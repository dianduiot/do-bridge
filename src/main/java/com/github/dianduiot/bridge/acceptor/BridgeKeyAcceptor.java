package com.github.dianduiot.bridge.acceptor;

import com.github.dianduiot.bridge.DoHardware;
import com.github.dianduiot.util.ByteUtils;
import com.github.dianduiot.vttp.VttpEncrypt;
import com.github.dianduiot.vttp.VttpFrame;
import org.apache.mina.core.session.IoSession;

public abstract class BridgeKeyAcceptor extends BridgeAcceptor{
    @Override
    public boolean acceptHardware(DoHardware hardware, VttpFrame frame) {
        IoSession session = hardware.getSession();
        VttpEncrypt vttpEncrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);

        String keyStr = this.loadKey(hardware, VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING);
        if (keyStr == null || keyStr.length() != 64) {
            return false;
        }
        // Convert key str to bytes.
        byte[] key = ByteUtils.hexStrToBytes(keyStr);
        vttpEncrypt.setKey(key);

        byte[] receivedEncryptedIvBytes = frame.getParam(0);

        byte[] iv = vttpEncrypt.getIv();
        int oriEncryptType = vttpEncrypt.getEncryptType();
        vttpEncrypt.setEncryptType(VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING);
        byte[] realEncryptedIvBytes = vttpEncrypt.doEncrypt(iv, iv.length);
        vttpEncrypt.setEncryptType(oriEncryptType);

        // Compare the encrypt result.
        return ByteUtils.isSameBytes(realEncryptedIvBytes, receivedEncryptedIvBytes);
    }

    /** @return The encrypt key or keys str of the gateway. */
    public abstract String loadKey(DoHardware hardware, Integer encryptType);
}
