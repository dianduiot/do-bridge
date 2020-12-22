package com.github.dianduiot.vttp;

import com.github.dianduiot.util.ByteUtils;
import com.github.dianduiot.vttp.resource.VttpResource;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class VttpClient implements IoHandler, VttpHeartbeatClient {
    private static final int VTTP_CLIENT_PHASE_GENERATE_IV = 1;
    private static final int VTTP_CLIENT_PHASE_ENTERING_ENCRYPT_CHANNEL = 2;
    private static final int VTTP_CLIENT_PHASE_OK = 3;

    private String socketType = null;
    private VttpClientConfig vttpClientConfig = null;
    private NioSocketConnector nioSocketConnector = null;
    private NioDatagramConnector nioDatagramConnector = null;
    protected IoSession session = null;
    private int vttpClientPhase = 0;
    private int vttpLogicRetryCount = 0;

    private List<VttpResource> resources = new ArrayList<>();

    public void addResource(VttpResource resource) {
        this.resources.add(resource);
    }

    public void startClient(VttpClientConfig config, String socketType, VttpHeartbeatManager heartbeatManager) {
        this.socketType = socketType;
        this.vttpClientConfig = config;
        this.startClientByInnerSocketType();

        heartbeatManager.addClient(this.vttpClientConfig.getClientName(), this);
    }

    private void startClientByInnerSocketType() {
        if ("tcp".equalsIgnoreCase(this.socketType)) {
            this.startClientWithInnerTcp();
        } else if ("udp".equalsIgnoreCase(this.socketType)) {
            this.startClientWithInnerUdp();
        }
    }

    private void startClientWithInnerTcp() {
        VttpClientConfig config = this.vttpClientConfig;

        nioSocketConnector = new NioSocketConnector();
        DefaultIoFilterChainBuilder chainBuilder = nioSocketConnector.getFilterChain();

        ProtocolCodecFactory codecFactory = new VttpCodecFactory();
        chainBuilder.addLast("codec", new ProtocolCodecFilter(codecFactory));

        nioSocketConnector.setConnectTimeoutCheckInterval(10);
        nioSocketConnector.setHandler(this);

        InetSocketAddress socketAddress = new InetSocketAddress(config.getAcceptorHost(), config.getAcceptorPort());
        try {
            ConnectFuture future = nioSocketConnector.connect(socketAddress);
            future.awaitUninterruptibly();
            this.session = future.getSession();
            this.vttpClientPhase = VTTP_CLIENT_PHASE_GENERATE_IV;
            this.doIvGenerating(this.session);
            // LOGGER.info("Vttp client \"" + this.vttpClientConfig.getClientName() + "\" is linked to server.");
        } catch (RuntimeIoException e) {
            // Failed to connect.
            this.session = null;
            this.nioSocketConnector.dispose();
            this.nioSocketConnector = null;
            // LOGGER.info("Vttp client \"" + this.vttpClientConfig.getClientName() + "\" failed to linking to server, will retry in next tick.");
        }
    }

    private void startClientWithInnerUdp() {
        VttpClientConfig config = this.vttpClientConfig;

        nioDatagramConnector = new NioDatagramConnector();
        DefaultIoFilterChainBuilder chainBuilder = nioDatagramConnector.getFilterChain();

        ProtocolCodecFactory codecFactory = new VttpCodecFactory();
        chainBuilder.addLast("codec", new ProtocolCodecFilter(codecFactory));

        nioDatagramConnector.setHandler(this);
        nioDatagramConnector.setConnectTimeoutCheckInterval(10);

        InetSocketAddress socketAddress = new InetSocketAddress(config.getAcceptorHost(), config.getAcceptorPort());
        try {
            ConnectFuture future = nioDatagramConnector.connect(socketAddress);
            future.awaitUninterruptibly();
            this.session = future.getSession();
            this.vttpClientPhase = VTTP_CLIENT_PHASE_GENERATE_IV;
            this.doIvGenerating(this.session);
            // LOGGER.info("Vttp client \"" + this.vttpClientConfig.getClientName() + "\" is linked to server.");
        } catch (RuntimeIoException e) {
            // Failed to connect.
            this.session = null;
            this.nioDatagramConnector.dispose();
            this.nioDatagramConnector = null;
            // LOGGER.info("Vttp client \"" + this.vttpClientConfig.getClientName() + "\" failed to linking to server, will retry in next tick.");
        }
    }

    public void checkClientStatusAndRecovery() {
        if (this.vttpClientConfig == null) {
            return;
        }
        if (this.session == null) {
            // Not connect yet.
            // Do start again.
            this.startClientByInnerSocketType();
        } else {
            // Connected.
            // Check the heartbeat.
            this.checkHeartbeatAndDoLogic();
        }
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

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        // Init heartbeat.
        VttpHeartbeat heartbeat = new VttpHeartbeat();
        heartbeat.setLastReceivedTs(System.currentTimeMillis());
        heartbeat.setCycle(60);
        heartbeat.setSendCd(60);
        session.setAttribute(VttpHeartbeat.HEARTBEAT_ATTR_KEY, heartbeat);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        this.session = null;
        if (this.nioDatagramConnector != null) {
            this.nioDatagramConnector.dispose();
            this.nioDatagramConnector = null;
        }
        if (this.nioSocketConnector != null) {
            this.nioSocketConnector.dispose();
            this.nioSocketConnector = null;
        }
        // LOGGER.info("Vttp client \"" + this.vttpClientConfig.getClientName() + "\" was disconnected from vttp server, will reconnect in a moment.");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        try {
            VttpFrame frame = (VttpFrame) message;
            if (frame.isHeartbeat()) {
                // Update heartbeat.
                VttpHeartbeat heartbeat = (VttpHeartbeat) session.getAttribute(VttpHeartbeat.HEARTBEAT_ATTR_KEY);
                heartbeat.setLastReceivedTs(System.currentTimeMillis());
                // Send heartbeat back.
                session.write(frame);
            } else if (this.checkIsVttpLogicUri(frame.getUri())){
                this.doClientVttpLogic(session, frame);
            } else {
                byte[] uri = frame.getUri();
                for (VttpResource resource : this.resources) {
                    if (resource.isMatchUri(uri)) {
                        resource.handleFrame(session, frame);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        session.closeNow();
    }

    @Override
    public void event(IoSession ioSession, FilterEvent filterEvent) throws Exception {
    }

    protected void afterEncryptChannelOk() {
    }

    private void doClientVttpLogic(IoSession session, VttpFrame frame) {
        String uri = frame.getUriAsString();
        switch (uri) {
            case "/v/giv-ok":
                this.handleGeneratingIvOk(session, frame);
                break;
            case "/v/eec-ok":
                this.handleEnteringEncryptChannelOk(session);
                break;
        }
    }

    private void doVttpLogicTimeoutRecovering() {
        this.vttpLogicRetryCount++;
        if (this.vttpLogicRetryCount > 5) {
            // Timeout count to match.
            // May catch some error.
            // Disconnect the link.
            // LOGGER.info("Vttp client \"" + this.vttpClientConfig.getClientName() + "\" failed to enter the encrypt channel, link may catch error, will reconnect in a moment.");
            this.session.closeNow();
            return;
        }
        switch (this.vttpClientPhase) {
            case VTTP_CLIENT_PHASE_GENERATE_IV:
                this.doIvGenerating(this.session);
                break;
            case VTTP_CLIENT_PHASE_ENTERING_ENCRYPT_CHANNEL:
                this.doEnterEncryptChannel(this.session);
                break;
        }
    }

    private void doIvGenerating(IoSession session) {
        // Send generate iv request.
        VttpFrame request = new VttpFrame("/v/giv");
        request.appendParam(this.vttpClientConfig.getDeviceId());
        session.write(request);
    }

    private void handleGeneratingIvOk(IoSession session, VttpFrame frame) {
        // Iv generated.
        // Now generate encrypt obj.
        byte[] key = ByteUtils.hexStrToBytes(this.vttpClientConfig.getKey());
        byte[] iv = frame.getParam(0);
        VttpEncrypt vttpEncrypt = new VttpEncrypt(VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING, key, iv);
        session.setAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY, vttpEncrypt);
        // Send enter encrypt channel request.
        this.vttpClientPhase = VTTP_CLIENT_PHASE_ENTERING_ENCRYPT_CHANNEL;
        this.doEnterEncryptChannel(session);
    }

    private void doEnterEncryptChannel(IoSession session) {
        VttpEncrypt vttpEncrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);
        if (vttpEncrypt != null) {
            byte[] iv = vttpEncrypt.getIv();
            byte[] encryptedIv = vttpEncrypt.doEncrypt(iv, iv.length);
            // Build request frame.
            VttpFrame request = new VttpFrame("/v/eec");
            request.appendParam(encryptedIv);
            session.write(request);
        }
    }

    private void handleEnteringEncryptChannelOk(IoSession session) {
        this.vttpClientPhase = VTTP_CLIENT_PHASE_OK;
        // Enable encrypt encoding.
        VttpEncrypt vttpEncrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);
        vttpEncrypt.setEnable(true);
        // Channel build completed.
        this.vttpLogicRetryCount = 0;
        this.afterEncryptChannelOk();
    }

    private boolean checkIsVttpLogicUri(byte[] uri) {
        if (uri.length < 3) {
            // Not the vttp logic uri.
            return false;
        }
        if (uri[0] != '/' || uri[1] != 'v' || uri[2] != '/') {
            // Not the vttp logic uri.
            return false;
        }
        return true;
    }
}
