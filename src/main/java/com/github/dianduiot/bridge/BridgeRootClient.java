package com.github.dianduiot.bridge;

import com.github.dianduiot.bridge.handler.BridgeRootHandler;
import com.github.dianduiot.bridge.model.DoSignUp;
import com.github.dianduiot.exception.DoException;
import com.github.dianduiot.util.ByteUtils;
import com.github.dianduiot.util.StringUtils;
import com.github.dianduiot.vttp.*;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Random;

public class BridgeRootClient implements IoHandler, VttpHeartbeatClient {
    public static final String BRIDGE_ROOT_CLIENT_DEFAULT_HOSTS = "127.0.0.1";
    public static final String BRIDGE_ROOT_CLIENT_NAME = "ROOT_CLIENT";
    public static final int BRIDGE_ROOT_CLIENT_REMOTE_DEFAULT_PORT = 6331;
    public static final int BRIDGE_ROOT_CLIENT_REMOTE_NODE_PORT = 6332;

    private static final int VTTP_CLIENT_PHASE_ENCRYPT_PERPARE= 1;
    private static final int VTTP_CLIENT_PHASE_REGISTERING = 2;
    private static final int VTTP_CLIENT_PHASE_OK = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(BridgeRootClient.class);

    private VttpClientConfig vttpClientConfig;
    private NioSocketConnector nioSocketConnector = null;
    protected IoSession session = null;
    private int vttpClientPhase = 0;
    private int vttpLogicRetryCount = 0;

    private boolean startFlag = false;

    private Random random = new Random();
    private String redirectingHost = null;
    private Integer redirectingPort = null;

    private BridgeRootHandler bridgeRootHandler;

    public void prepareClient(VttpClientConfig vttpClientConfig, VttpHeartbeatManager vttpHeartbeatManager) {
        this.vttpClientConfig = vttpClientConfig;

        // For root handler.
        if (this.bridgeRootHandler == null) {
            this.bridgeRootHandler = new BridgeRootHandler();
        }

        vttpHeartbeatManager.addClient(vttpClientConfig.getClientName(), this);
    }

    public void startClient() {
        this.startFlag = true;
        this.startOrRestartClientSession();
    }

    public void stopClient() {
        this.startFlag = false;
        if (this.session != null) {
            this.session.closeNow();
        }
    }

    private String chooseRandomInHosts(String hosts) {
        String[] sps = hosts.split(",");
        int length = sps.length;
        return sps[this.random.nextInt(length)];
    }

    private void startOrRestartClientSession() {
        if (!this.startFlag) {
            return;
        }

        VttpClientConfig config = this.vttpClientConfig;

        nioSocketConnector = new NioSocketConnector();
        DefaultIoFilterChainBuilder chainBuilder = nioSocketConnector.getFilterChain();

        ProtocolCodecFactory codecFactory = new VttpCodecFactory();
        chainBuilder.addLast("codec", new ProtocolCodecFilter(codecFactory));

        nioSocketConnector.setConnectTimeoutCheckInterval(10);
        nioSocketConnector.setHandler(this);

        // Override the hosts from database.
        InetSocketAddress socketAddress;
        if (this.redirectingHost != null && this.redirectingPort != null) {
            socketAddress = new InetSocketAddress(this.redirectingHost, BRIDGE_ROOT_CLIENT_REMOTE_NODE_PORT);
        } else {
            String resultHosts;
            DoSignUp dbSignUp = this.bridgeRootHandler.onHardwareFetching(BRIDGE_ROOT_CLIENT_NAME, BRIDGE_ROOT_CLIENT_NAME);
            if (dbSignUp == null || StringUtils.isEmpty(dbSignUp.getKey())) {
                resultHosts = this.vttpClientConfig.getAcceptorHost();
            } else {
                resultHosts = dbSignUp.getKey();
            }

            String anyCenterHost = this.chooseRandomInHosts(resultHosts);
            socketAddress = new InetSocketAddress(anyCenterHost, config.getAcceptorPort());
        }

        try {
            ConnectFuture future = nioSocketConnector.connect(socketAddress);
            future.awaitUninterruptibly();
            this.session = future.getSession();
            this.vttpClientPhase = VTTP_CLIENT_PHASE_ENCRYPT_PERPARE;
            this.doEncryptPrepare(this.session);
            LOGGER.info("Bridge remote root client \"" + config.getClientName() + "\" is linked to server.");
        } catch (RuntimeIoException e) {
            // Failed to connect.
            this.session = null;
            this.nioSocketConnector.dispose();
            this.nioSocketConnector = null;
            LOGGER.info("Bridge remote root client \"" + config.getClientName() + "\" failed to linking to server, will retry in next tick.");
        }
    }

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {
        // Init hardware obj.
        DoHardware hw = new DoHardware(ioSession, null);
        ioSession.setAttribute(DoHardware.SESSION_ATTR_KEY, hw);
        // Init heartbeat obj.
        VttpHeartbeat heartbeat = new VttpHeartbeat();
        heartbeat.setLastReceivedTs(System.currentTimeMillis());
        heartbeat.setCycle(60);
        heartbeat.setSendCd(60);
        ioSession.setAttribute(VttpHeartbeat.HEARTBEAT_ATTR_KEY, heartbeat);
    }

    @Override
    public void sessionOpened(IoSession ioSession) throws Exception {
    }

    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {
        this.session = null;
        if (this.nioSocketConnector != null) {
            this.nioSocketConnector.dispose();
            this.nioSocketConnector = null;
        }
        if (this.redirectingHost != null && this.redirectingPort != null) {
            LOGGER.info("Bridge remote root client \"" + this.vttpClientConfig.getClientName() + "\" redirecting to the target node.");
            this.checkClientStatusAndRecovery();
            this.redirectingHost = null;
            this.redirectingPort = null;
        } else {
            if (this.startFlag) {
                LOGGER.info("Bridge remote root client \"" + this.vttpClientConfig.getClientName() + "\" was disconnected from vttp server, will reconnect in a moment.");
            } else {
                LOGGER.info("Bridge remote root client \"" + this.vttpClientConfig.getClientName() + "\" was disconnected from vttp server, and stop the retry logic.");
            }
        }
    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {
    }

    @Override
    public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {
    }

    @Override
    public void messageReceived(IoSession ioSession, Object message) throws Exception {
        try {
            VttpFrame frame = (VttpFrame) message;
            if (frame.isHeartbeat()) {
                // Update heartbeat.
                VttpHeartbeat heartbeat = (VttpHeartbeat) session.getAttribute(VttpHeartbeat.HEARTBEAT_ATTR_KEY);
                heartbeat.setLastReceivedTs(System.currentTimeMillis());
                // Send heartbeat back.
                ioSession.write(frame);
            } else {
                this.speedUpProcessFrame(ioSession, frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DoException(e);
        }
    }

    @Override
    public void messageSent(IoSession ioSession, Object o) throws Exception {
    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {
        session.closeNow();
    }

    @Override
    public void event(IoSession ioSession, FilterEvent filterEvent) throws Exception {
    }

    private void speedUpProcessFrame(IoSession ioSession, VttpFrame frame) {
        byte[] uri = frame.getUri();
        if (uri.length < 3) {
            return;
        }
        if (uri[0] == '/' && uri[2] == '/') {
            byte speedUpHandlerSign = uri[1];
            switch (speedUpHandlerSign) {
                case 'h':
                    this.doHandshakeLogic(ioSession, frame);
                    break;
                case 'r':
                    this.bridgeRootHandler.handleSpeedUpFrame((DoHardware) ioSession.getAttribute(DoHardware.SESSION_ATTR_KEY), frame);
                    break;
            }
        }
    }

    private void doHandshakeLogic(IoSession ioSession, VttpFrame frame) {
        String uri = frame.getUriAsString();
        if ("/h/ct-lg".equals(uri)) {
            this.handleWholeHostsAndDoRedirectToNode(ioSession, frame);
        } else if ("/h/ep-ok".equals(uri)) {
            this.handleEncryptPrepareOk(ioSession, frame);
        } else if ("/h/reg-ok".equals(uri)) {
            this.handleRegisteringOk(ioSession);
        } else if ("/h/reg-err".equals(uri)) {
            this.handleRegisterError(ioSession);
        } else if ("/h/reg-denied".equals(uri)) {
            this.handleRegisterError(ioSession);
        }
    }

    @Override
    public void checkClientStatusAndRecovery() {
        if (this.vttpClientConfig == null) {
            return;
        }
        if (this.session == null) {
            // Not connect yet.
            // Do start again.
            this.startOrRestartClientSession();
        } else {
            // Connected.
            // Check the heartbeat.
            this.checkHeartbeatAndDoLogic();
        }
    }

    public IoSession getSession() {
        return session;
    }

    private void checkHeartbeatAndDoLogic() {
        VttpHeartbeat heartbeat = (VttpHeartbeat) this.session.getAttribute(VttpHeartbeat.HEARTBEAT_ATTR_KEY);
        if (heartbeat == null) {
            this.session.closeNow();
        } else {
            long deadline = heartbeat.getCycle() * 1000 * 3 + 5000 + heartbeat.getLastReceivedTs();
            if (deadline < System.currentTimeMillis()) {
                // Death.
                this.session.closeNow();
            } else {
                if (this.vttpClientPhase != VTTP_CLIENT_PHASE_OK) {
                    this.doVttpLogicTimeoutRecovering();
                }
            }
        }
    }

    private void doVttpLogicTimeoutRecovering() {
        this.vttpLogicRetryCount++;
        if (this.vttpLogicRetryCount > 5) {
            // Timeout count to match.
            // May catch some error.
            // Disconnect the link.
            LOGGER.info("Bridge remote root client \"" + this.vttpClientConfig.getClientName() + "\" failed to enter the encrypt channel, link may catch error, will reconnect in a moment.");
            this.session.closeNow();
            return;
        }
        switch (this.vttpClientPhase) {
            case VTTP_CLIENT_PHASE_ENCRYPT_PERPARE:
                this.doEncryptPrepare(this.session);
                break;
            case VTTP_CLIENT_PHASE_REGISTERING:
                this.doRegistering(this.session);
                break;
        }
    }

    private void doEncryptPrepare(IoSession session) {
        // Send generate iv request.
        VttpFrame request = new VttpFrame("/h/ep");
        request.appendParam(VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING);
        request.appendParam(this.vttpClientConfig.getDeviceId());
        request.appendParam(this.vttpClientConfig.getDeviceId());
        session.write(request);
    }

    private void handleWholeHostsAndDoRedirectToNode(IoSession session, VttpFrame frame) {
        // Handle whole received.
        String wholeCentersHost = frame.getParamAsString(0);
        DoSignUp dbSignUp = this.bridgeRootHandler.onHardwareFetching(BRIDGE_ROOT_CLIENT_NAME, BRIDGE_ROOT_CLIENT_NAME);
        if (dbSignUp == null || !wholeCentersHost.equals(dbSignUp.getKey())) {
            dbSignUp = new DoSignUp();
            dbSignUp.setHardwareId(BRIDGE_ROOT_CLIENT_NAME);
            dbSignUp.setGatewayId(BRIDGE_ROOT_CLIENT_NAME);
            dbSignUp.setEncryptType(VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING);
            dbSignUp.setKey(wholeCentersHost);
            this.bridgeRootHandler.onHardwareSignUp(dbSignUp);
        }

        // Handle redirect to node.
        this.redirectingHost = frame.getParamAsString(1);
        this.redirectingPort = frame.getParamAsInt(2);

        this.session.closeNow();
    }

    private void handleEncryptPrepareOk(IoSession session, VttpFrame frame) {
        // Iv generated.
        // Now generate encrypt obj.
        byte[] key = ByteUtils.hexStrToBytes(this.vttpClientConfig.getKey());
        byte[] iv = frame.getParam(0);
        VttpEncrypt vttpEncrypt = new VttpEncrypt(VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING, key, iv);
        session.setAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY, vttpEncrypt);
        // Send enter encrypt channel request.
        this.vttpClientPhase = VTTP_CLIENT_PHASE_ENCRYPT_PERPARE;
        this.doRegistering(session);
    }

    private void doRegistering(IoSession session) {
        VttpEncrypt vttpEncrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);
        if (vttpEncrypt != null) {
            byte[] iv = vttpEncrypt.getIv();
            byte[] encryptedIv = vttpEncrypt.doEncrypt(iv, iv.length);
            // Build request frame.
            VttpFrame request = new VttpFrame("/h/reg");
            request.appendParam(encryptedIv);
            session.write(request);
        }
    }

    private void handleRegisteringOk(IoSession session) {
        this.vttpClientPhase = VTTP_CLIENT_PHASE_OK;
        // Enable encrypt encoding.
        VttpEncrypt vttpEncrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);
        vttpEncrypt.setEnable(true);
        // Channel build completed.
        this.vttpLogicRetryCount = 0;
        this.afterEncryptChannelOk(session);
    }

    private void handleRegisterError(IoSession session) {
        LOGGER.info("Bridge remote root client \"" + this.vttpClientConfig.getClientName() + "\" failed to register the client to server, the server returns a refused error.");
    }

    protected void afterEncryptChannelOk(IoSession session) {
        this.bridgeRootHandler.doSyncItemChain(session);
    }

    public BridgeRootHandler getBridgeRootHandler() {
        return bridgeRootHandler;
    }

    public void setBridgeRootHandler(BridgeRootHandler bridgeRootHandler) {
        this.bridgeRootHandler = bridgeRootHandler;
    }
}
