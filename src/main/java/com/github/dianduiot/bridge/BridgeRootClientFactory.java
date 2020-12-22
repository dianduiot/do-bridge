package com.github.dianduiot.bridge;

import com.github.dianduiot.bridge.handler.BridgeRootHandler;
import com.github.dianduiot.vttp.VttpClientConfig;
import com.github.dianduiot.vttp.VttpEncrypt;
import com.github.dianduiot.vttp.VttpHeartbeatManager;

public class BridgeRootClientFactory {
    private VttpClientConfig config = new VttpClientConfig();
    private VttpHeartbeatManager heartbeatManagerM;
    private BridgeRootHandler bridgeRootHandler;

    public BridgeRootClientFactory() {
        config.setClientName(BridgeRootClient.BRIDGE_ROOT_CLIENT_NAME);
        config.setDeviceId("NONE");
        config.setEncryptType(VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING);
        config.setKey("");
        config.setAcceptorHost(BridgeRootClient.BRIDGE_ROOT_CLIENT_DEFAULT_HOSTS);
        config.setAcceptorPort(BridgeRootClient.BRIDGE_ROOT_CLIENT_REMOTE_DEFAULT_PORT);
    }

    public BridgeRootClientFactory withServerId(String serverId) {
        config.setDeviceId(serverId);
        return this;
    }

    public BridgeRootClientFactory withServerKey(String serverKey) {
        config.setKey(serverKey);
        return this;
    }

    public BridgeRootClientFactory heartbeatManager(VttpHeartbeatManager heartbeatManager) {
        this.heartbeatManagerM = heartbeatManager;
        return this;
    }

    public BridgeRootClientFactory rootHandler(BridgeRootHandler bridgeRootHandler) {
        this.bridgeRootHandler = bridgeRootHandler;
        return this;
    }

    public BridgeRootClientFactory acceptorHost(String hosts) {
        this.config.setAcceptorHost(hosts);
        return this;
    }

    public BridgeRootClientFactory acceptorPort(int port) {
        this.config.setAcceptorPort(port);
        return this;
    }

    public BridgeRootClient build() {
        // Set up server.
        BridgeRootClient client = new BridgeRootClient();

        client.prepareClient(this.config.cloneOne(), this.heartbeatManagerM);
        client.setBridgeRootHandler(this.bridgeRootHandler);

        return client;
    }

}
