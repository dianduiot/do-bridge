package com.github.dianduiot.bridge;

import com.github.dianduiot.bridge.acceptor.BridgeAcceptor;
import com.github.dianduiot.bridge.handler.*;
import com.github.dianduiot.exception.DoException;
import com.github.dianduiot.exception.InstanceExistException;
import com.github.dianduiot.vttp.*;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.ExpiringSessionRecycler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;

public class BridgeServer implements IoHandler {
    private static final int MINA_UDP_FORCE_THROW_SESSION_SECONDS = 330;

    protected boolean startedFlag = false;
    protected String currentSocketType = null;
    protected NioSocketAcceptor socketAcceptor = null;
    protected NioDatagramAcceptor datagramAcceptor = null;

    private VttpServerConfig config;
    private int encryptType = VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING;

    private VttpHeartbeatManager heartbeatManager;
    private DoHardwareManager hardwareManager;
    private BridgeAcceptor bridgeAcceptor;

    private BridgeHandshakeHandler bridgeHandshakeHandler;
    private BridgeConfigHandler bridgeConfigHandler;
    private BridgeIotHandler bridgeIotHandler;
    private BridgeRootHandler bridgeRootHandler;
    private BridgeUpdateHandler bridgeUpdateHandler;
    private BridgeTransferHandler bridgeTransferHandler;

    private byte[] rootKey = null;

    public void startServer(String socketType) throws Exception {
        if (this.startedFlag) {
            throw new InstanceExistException();
        }

        // Fill missing items.
        // For server config.
        if (this.config == null) {
            this.config = VttpServerConfig.genDefault();
        }
        // For heartbeat manager.
        if (this.heartbeatManager == null) {
            this.heartbeatManager = new VttpHeartbeatManager();
            this.heartbeatManager.enableHeartbeatManager();
        }
        // For hardware manager.
        if (this.hardwareManager == null) {
            this.hardwareManager = new DoHardwareManager();
        }
        // For vttp acceptor.
        if (this.bridgeAcceptor == null) {
            this.bridgeAcceptor = new BridgeAcceptor();
        }
        // For handshake handler.
        if (this.bridgeHandshakeHandler == null) {
            this.bridgeHandshakeHandler = new BridgeHandshakeHandler();
        }
        // For config handler.
        if (this.bridgeConfigHandler == null) {
            this.bridgeConfigHandler = new BridgeConfigHandler();
        }
        // For iot handler.
        if (this.bridgeIotHandler == null) {
            this.bridgeIotHandler = new BridgeIotHandler();
        }
        // For root handler.
        if (this.bridgeRootHandler == null) {
            this.bridgeRootHandler = new BridgeRootHandler();
        }
        // For update handler.
        if (this.bridgeUpdateHandler == null) {
            this.bridgeUpdateHandler = new BridgeUpdateHandler();
        }
        // For transfer handler.
        if (this.bridgeTransferHandler == null) {
            this.bridgeTransferHandler = new BridgeTransferHandler();
        }
        // Ok.
        // Now try to bind the server.
        this.currentSocketType = socketType;
        if ("tcp".equalsIgnoreCase(socketType)) {
            this.startServerWithTcp(this.config);
        } else if ("udp".equalsIgnoreCase(socketType)) {
            this.startServerWithUdp(this.config);
        }
        this.startedFlag = true;
    }

    private void startServerWithTcp(VttpServerConfig config) throws Exception {
        this.socketAcceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder chainBuilder = this.socketAcceptor.getFilterChain();

        ProtocolCodecFactory codecFactory = new VttpCodecFactory();
        chainBuilder.addLast("codec", new ProtocolCodecFilter(codecFactory));
        chainBuilder.addLast("executor", new ExecutorFilter(config.getMinThread(), config.getMaxThread()));

        this.socketAcceptor.setHandler(this);
        this.socketAcceptor.getSessionConfig().setReadBufferSize(config.getBufferSize());
        this.socketAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, config.getIdleTime());

        // Run it.
        this.socketAcceptor.bind(new InetSocketAddress(config.getPort()));
        // Add server to heartbeat manager.
        this.heartbeatManager.addAcceptor(this.socketAcceptor);
    }

    private void startServerWithUdp(VttpServerConfig config) throws Exception {
        this.datagramAcceptor = new NioDatagramAcceptor();
        DefaultIoFilterChainBuilder chainBuilder = this.datagramAcceptor.getFilterChain();

        ProtocolCodecFactory codecFactory = new VttpCodecFactory();
        chainBuilder.addLast("codec", new ProtocolCodecFilter(codecFactory));
        chainBuilder.addLast("executor", new ExecutorFilter(config.getMinThread(), config.getMaxThread()));

        this.datagramAcceptor.setHandler(this);
        this.datagramAcceptor.setSessionRecycler(new ExpiringSessionRecycler(MINA_UDP_FORCE_THROW_SESSION_SECONDS));
        this.datagramAcceptor.getSessionConfig().setReadBufferSize(config.getBufferSize());
        this.datagramAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, config.getIdleTime());

        // Run it.
        this.datagramAcceptor.bind(new InetSocketAddress(config.getPort()));
        // Add server to heartbeat manager.
        this.heartbeatManager.addAcceptor(this.datagramAcceptor);
    }

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {
        // Init hardware obj.
        DoHardware hw = new DoHardware(ioSession, this);
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
        DoHardware hardware = (DoHardware) ioSession.getAttribute(DoHardware.SESSION_ATTR_KEY);
        if (this.hardwareManager.ifManagedHardware(hardware)) {
            String hardwareType = hardware.getHardwareType();
            if (hardwareType == null || hardwareType.equals(DoHardware.TYPE_HARDWARE)) {
                // Remove from manager.
                boolean unManageSuccessFlag = this.hardwareManager.removeHardware(hardware.getGatewayId(), hardware);
                if (unManageSuccessFlag) {
                    this.bridgeIotHandler.onDisconnected(hardware);
                }
            }
        }
        hardware.release();
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
                this.heartbeatReceived(ioSession);
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
        ioSession.closeNow();
    }

    @Override
    public void event(IoSession ioSession, FilterEvent filterEvent) throws Exception {
    }

    private void heartbeatReceived(IoSession session) {
        // Update heartbeat.
        VttpHeartbeat heartbeat = (VttpHeartbeat) session.getAttribute(VttpHeartbeat.HEARTBEAT_ATTR_KEY);
        heartbeat.setLastReceivedTs(System.currentTimeMillis());
    }

    private void speedUpProcessFrame(IoSession ioSession, VttpFrame frame) {
        byte[] uri = frame.getUri();
        if (uri.length < 3) {
            return;
        }
        if (uri[0] == '/' && uri[2] == '/') {
            byte speedUpHandlerSign = uri[1];
            switch (speedUpHandlerSign) {
                case 't':
                    this.bridgeTransferHandler.handleSpeedUpFrame((DoHardware) ioSession.getAttribute(DoHardware.SESSION_ATTR_KEY), frame);
                    break;
                case 'i':
                    this.bridgeIotHandler.handleSpeedUpFrame((DoHardware) ioSession.getAttribute(DoHardware.SESSION_ATTR_KEY), frame);
                    break;
                case 'h':
                    this.bridgeHandshakeHandler.handleSpeedUpFrame((DoHardware) ioSession.getAttribute(DoHardware.SESSION_ATTR_KEY), frame);
                    break;
                case 'c':
                    this.bridgeConfigHandler.handleSpeedUpFrame((DoHardware) ioSession.getAttribute(DoHardware.SESSION_ATTR_KEY), frame);
                    break;
                case 'r':
                    this.bridgeRootHandler.handleSpeedUpFrame((DoHardware) ioSession.getAttribute(DoHardware.SESSION_ATTR_KEY), frame);
                    break;
                case 'u':
                    this.bridgeUpdateHandler.handleSpeedUpFrame((DoHardware) ioSession.getAttribute(DoHardware.SESSION_ATTR_KEY), frame);
                    break;
            }
        }
    }

    public VttpServerConfig getConfig() {
        return config;
    }

    public void setConfig(VttpServerConfig config) {
        this.config = config;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }

    public VttpHeartbeatManager getHeartbeatManager() {
        return heartbeatManager;
    }

    public void setHeartbeatManager(VttpHeartbeatManager heartbeatManager) {
        this.heartbeatManager = heartbeatManager;
    }

    public DoHardwareManager getHardwareManager() {
        return hardwareManager;
    }

    public void setHardwareManager(DoHardwareManager hardwareManager) {
        this.hardwareManager = hardwareManager;
    }

    public BridgeAcceptor getBridgeAcceptor() {
        return bridgeAcceptor;
    }

    public void setBridgeAcceptor(BridgeAcceptor bridgeAcceptor) {
        this.bridgeAcceptor = bridgeAcceptor;
    }

    public BridgeHandshakeHandler getBridgeHandshakeHandler() {
        return bridgeHandshakeHandler;
    }

    public void setBridgeHandshakeHandler(BridgeHandshakeHandler bridgeHandshakeHandler) {
        this.bridgeHandshakeHandler = bridgeHandshakeHandler;
    }

    public BridgeConfigHandler getBridgeConfigHandler() {
        return bridgeConfigHandler;
    }

    public void setBridgeConfigHandler(BridgeConfigHandler bridgeConfigHandler) {
        this.bridgeConfigHandler = bridgeConfigHandler;
    }

    public BridgeIotHandler getBridgeIotHandler() {
        return bridgeIotHandler;
    }

    public void setBridgeIotHandler(BridgeIotHandler bridgeIotHandler) {
        this.bridgeIotHandler = bridgeIotHandler;
    }

    public BridgeRootHandler getBridgeRootHandler() {
        return bridgeRootHandler;
    }

    public void setBridgeRootHandler(BridgeRootHandler bridgeRootHandler) {
        this.bridgeRootHandler = bridgeRootHandler;
    }

    public BridgeUpdateHandler getBridgeUpdateHandler() {
        return bridgeUpdateHandler;
    }

    public void setBridgeUpdateHandler(BridgeUpdateHandler bridgeUpdateHandler) {
        this.bridgeUpdateHandler = bridgeUpdateHandler;
    }

    public BridgeTransferHandler getBridgeTransferHandler() {
        return bridgeTransferHandler;
    }

    public void setBridgeTransferHandler(BridgeTransferHandler bridgeTransferHandler) {
        this.bridgeTransferHandler = bridgeTransferHandler;
    }

    public byte[] getRootKey() {
        return rootKey;
    }

    public void setRootKey(byte[] rootKey) {
        this.rootKey = rootKey;
    }
}
