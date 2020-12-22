package com.github.dianduiot.bridge;

import com.github.dianduiot.bridge.acceptor.BridgeAcceptor;
import com.github.dianduiot.bridge.handler.*;
import com.github.dianduiot.util.ByteUtils;
import com.github.dianduiot.vttp.VttpEncrypt;
import com.github.dianduiot.vttp.VttpHeartbeatManager;
import com.github.dianduiot.vttp.VttpServerConfig;

public class BridgeServerFactory {
    private VttpServerConfig config = VttpServerConfig.genDefault();
    private int encryptTypeM = VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING;

    private VttpHeartbeatManager heartbeatManagerM;
    private DoHardwareManager hardwareManagerM;
    private BridgeAcceptor acceptorM;

    private BridgeHandshakeHandler doHandshakeHandler;
    private BridgeConfigHandler doConfigHandler;
    private BridgeIotHandler doIotHandler;
    private BridgeRootHandler doRootHandler;
    private BridgeUpdateHandler doUpdateHandler;
    private BridgeTransferHandler doTransferHandler;

    private String rootKeyHexStr = null;

    public BridgeServerFactory port(int port) {
        config.setPort(port);
        return this;
    }

    public BridgeServerFactory minThread(int minThread) {
        config.setMinThread(minThread);
        return this;
    }
    public BridgeServerFactory maxThread(int maxThread) {
        config.setMaxThread(maxThread);
        return this;
    }

    public BridgeServerFactory idleTime(int idleTime) {
        config.setIdleTime(idleTime);
        return this;
    }

    public BridgeServerFactory bufferSize(int bufferSize) {
        config.setBufferSize(bufferSize);
        return this;
    }

    public BridgeServerFactory encryptType(int encryptType) {
        this.encryptTypeM = encryptType;
        return this;
    }

    public BridgeServerFactory heartbeatManager(VttpHeartbeatManager heartbeatManager) {
        this.heartbeatManagerM = heartbeatManager;
        return this;
    }

    public BridgeServerFactory hardwareManager(DoHardwareManager hardwareManager) {
        this.hardwareManagerM = hardwareManager;
        return this;
    }

    public BridgeServerFactory hardwareAcceptor(BridgeAcceptor vttpAcceptor) {
        this.acceptorM = vttpAcceptor;
        return this;
    }

    public BridgeServerFactory handshakeHandler(BridgeHandshakeHandler doHandshakeHandler) {
        this.doHandshakeHandler = doHandshakeHandler;
        return this;
    }

    public BridgeServerFactory configHandler(BridgeConfigHandler doConfigHandler) {
        this.doConfigHandler = doConfigHandler;
        return this;
    }

    public BridgeServerFactory iotHandler(BridgeIotHandler doIotHandler) {
        this.doIotHandler = doIotHandler;
        return this;
    }

    public BridgeServerFactory rootHandler(BridgeRootHandler doRootHandler) {
        this.doRootHandler = doRootHandler;
        return this;
    }

    public BridgeServerFactory updateHandler(BridgeUpdateHandler doUpdateHandler) {
        this.doUpdateHandler = doUpdateHandler;
        return this;
    }

    public BridgeServerFactory transferHandler(BridgeTransferHandler doTransferHandler) {
        this.doTransferHandler = doTransferHandler;
        return this;
    }

    public BridgeServerFactory withRoot(String aes256KeyHex) {
        this.rootKeyHexStr = aes256KeyHex;
        return this;
    }

    public BridgeServer build() {
        // Set up server.
        BridgeServer server = new BridgeServer();

        server.setConfig(this.config.cloneOne());
        server.setEncryptType(this.encryptTypeM);

        server.setHeartbeatManager(this.heartbeatManagerM);
        server.setHardwareManager(this.hardwareManagerM);
        server.setBridgeAcceptor(this.acceptorM);

        server.setBridgeHandshakeHandler(this.doHandshakeHandler);
        server.setBridgeConfigHandler(this.doConfigHandler);
        server.setBridgeIotHandler(this.doIotHandler);
        server.setBridgeRootHandler(this.doRootHandler);
        server.setBridgeUpdateHandler(this.doUpdateHandler);
        server.setBridgeTransferHandler(this.doTransferHandler);

        // Pre.
        byte[] rootKey = null;
        if (this.rootKeyHexStr != null && this.rootKeyHexStr.length() == 64) {
            rootKey = ByteUtils.hexStrToBytes(this.rootKeyHexStr);
        }
        if (rootKey != null) {
            // Root client enabled.
            server.setRootKey(rootKey);
        }

        return server;
    }
}
