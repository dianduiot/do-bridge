package com.github.dianduiot.bridge.handler;

import com.github.dianduiot.bridge.DoHardware;
import com.github.dianduiot.bridge.acceptor.BridgeAcceptor;
import com.github.dianduiot.util.ByteUtils;
import com.github.dianduiot.util.StringUtils;
import com.github.dianduiot.vttp.VttpEncrypt;
import com.github.dianduiot.vttp.VttpFrame;
import org.apache.mina.core.session.IoSession;

import java.util.Random;

public class BridgeHandshakeHandler extends BridgeSpeedUpHandler {
    private Random random = new Random();

    public BridgeHandshakeHandler() {
        super('h');
    }

    @Override
    public void handleFrame(DoHardware hardware, VttpFrame frame, String subUrl) {
        if ("/ep".equals(subUrl)) {
            this.handleEp(hardware, frame);
        } else if ("/reg".equals(subUrl)) {
            this.handleReg(hardware, frame);
        } else if ("/close".equals(subUrl)) {
            this.handleClose(hardware);
        }
    }

    protected void handleEp(DoHardware hardware, VttpFrame frame) {
        IoSession session = hardware.getSession();

        String hardwareType = frame.getParamAsString(0);
        String hardwareId = frame.getParamAsString(1);
        String gatewayId = frame.getParamAsString(2);
        Integer redirectCount = frame.getParamAsInt(3);
        Integer handshakeEncryptType = frame.getParamAsInt(4);

        if (StringUtils.isEmpty(hardwareId) || StringUtils.isEmpty(hardwareType)) {
            // No hardware id receiving.
            // May catch any exception.
            session.write(new VttpFrame(this.wrapSubUrl("/ep-err")));
            return;
        }
        if (StringUtils.isEmpty(gatewayId)) {
            gatewayId = hardwareId;
        }
        hardware.signInfo(hardwareId, gatewayId, hardwareType, redirectCount);

        // Now do gateway accept logic.
        BridgeAcceptor acceptor = hardware.getServer().getBridgeAcceptor();
        if (DoHardware.TYPE_ROOT.equals(hardwareType)) {
            if (!(acceptor.acceptRoot(hardware))) {
                // Not accept the root.
                // May sent a redirect server link to the hardware.
                // Ignore current ep.
                return;
            }
        } else {
            if (!(acceptor.acceptHandshake(hardware))) {
                // Not accept the handshake.
                // May sent a redirect server link to the hardware.
                // Ignore current ep.
                return;
            }
        }

        // In current version, handshake is using AES encrypt.
        // Generate the iv for encrypt preparing.
        byte[] iv = new byte[16];
        this.random.nextBytes(iv);
        // Now generate the encrypt info for the next encrypt phase.
        VttpEncrypt vttpEncrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);
        if (vttpEncrypt == null) {
            vttpEncrypt = new VttpEncrypt(handshakeEncryptType, new byte[32], iv);
            session.setAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY, vttpEncrypt);
        } else {
            vttpEncrypt.setIv(iv);
            vttpEncrypt.setEncryptType(handshakeEncryptType);
            vttpEncrypt.refreshRealKey();
        }
        // Now, response the prepared encrypt info.
        VttpFrame responseFrame = new VttpFrame(this.wrapSubUrl("/ep-ok"));
        responseFrame.appendParam(iv);
        responseFrame.appendParam(vttpEncrypt.getEncryptType());
        session.write(responseFrame);
    }

    protected void handleReg(DoHardware hardware, VttpFrame frame) {
        IoSession session = hardware.getSession();
        VttpEncrypt vttpEncrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);
        if (vttpEncrypt == null) {
            // Encrypt info not prepare, please prepare first.
            session.write(new VttpFrame(this.wrapSubUrl("/reg-err")));
            return;
        }
        // Now do the register link logic.
        String hardwareType = hardware.getHardwareType();
        boolean acceptResult = false;
        boolean hardwareFlag = false;
        BridgeAcceptor acceptor = hardware.getServer().getBridgeAcceptor();
        if (hardwareType.equals(DoHardware.TYPE_ROOT)) {
            acceptResult = this.acceptRootReg(hardware, frame);
        } else {
            hardwareFlag = true;
            acceptResult = acceptor.acceptHardware(hardware, frame);
        }

        // Judge the accept result.
        Integer gatewayEncryptType = frame.getParamAsInt(1);
        if (gatewayEncryptType == null) {
            gatewayEncryptType = VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING;
        }
        if (acceptResult) {
            // Accept ok.
            // Response the hardware before entering the encrypt channel.
            session.write(new VttpFrame(this.wrapSubUrl("/reg-ok")));
            // Enable the encrypt channel or do the ignore hooking.
            if (gatewayEncryptType.equals(VttpEncrypt.ENCRYPT_TYPE_NONE)) {
                // Ignore the encrypt channel entering.
                vttpEncrypt.setEnable(false);
                vttpEncrypt.setNecHook(true);
            } else {
                vttpEncrypt.setEncryptType(gatewayEncryptType);
                vttpEncrypt.setEnable(true);
                vttpEncrypt.setNecHook(false);
            }
            // Manager the register ok hardware.
            hardware.signRegOk();
            // Do the linked emit logic.
            if (hardwareFlag) {
                // Emit linked success handler.
                boolean newLinkFlag = hardware.getServer().getHardwareManager().putHardware(hardware.getGatewayId(), hardware);
                if (newLinkFlag) {
                    hardware.getServer().getBridgeIotHandler().onConnected(hardware);
                }
            }
        } else {
            // Hardware had been denied.
            session.write(new VttpFrame(this.wrapSubUrl("/reg-denied")));
        }
    }

    protected void handleClose(DoHardware hardware) {
        hardware.getSession().closeNow();
    }

    private boolean acceptRootReg(DoHardware hardware, VttpFrame frame) {
        byte[] rootKey = hardware.getServer().getRootKey();
        if (rootKey == null || rootKey.length != 32) {
            return false;
        }

        IoSession session = hardware.getSession();
        VttpEncrypt vttpEncrypt = (VttpEncrypt) session.getAttribute(VttpEncrypt.ENCRYPT_ATTR_KEY);
        vttpEncrypt.setKey(rootKey);

        byte[] iv = vttpEncrypt.getIv();
        byte[] realEncryptedIvBytes = vttpEncrypt.doEncrypt(iv, iv.length);

        // Compare the encrypt result.
        byte[] receivedEncryptedIvBytes = frame.getParam(0);
        // Integer gatewayEncryptType = frame.getParamAsInt(1);
        return ByteUtils.isSameBytes(realEncryptedIvBytes, receivedEncryptedIvBytes);
    }
}
