package com.github.dianduiot.bridge.handler;

import com.github.dianduiot.bridge.DoHardware;
import com.github.dianduiot.bridge.model.DoSignUp;
import com.github.dianduiot.vttp.VttpEncrypt;
import com.github.dianduiot.vttp.VttpFrame;
import org.apache.mina.core.session.IoSession;

public class BridgeRootHandler extends BridgeSpeedUpHandler {
    // --------------------------------------------------
    // Should override methods BEGIN.

    /** @return true if sign up success. false if sign up failed. */
    public boolean onHardwareSignUp(DoSignUp signUp) {
        return false;
    }

    /** @return The hardware sign up info for giving hardware and gateway id, if database not found the gateway info, just return null.*/
    public DoSignUp onHardwareFetching(String hardwareId, String gatewayId) {
        return null;
    }

    // Should override methods END.
    // --------------------------------------------------

    private boolean syncing = false;

    public BridgeRootHandler() {
        super('r');
    }

    @Override
    public void handleFrame(DoHardware hardware, VttpFrame frame, String subUrl) {
        if (!frame.isEncrypted()) {
            return;
        }
        if ("/sign-up".equals(subUrl)) {
            this.handleSignUpFrame(hardware, frame);
        } else if ("/get-sign-up".equals(subUrl)) {
            this.handleGetSignUpFrame(hardware, frame);
        } else if ("/sync-do".equals(subUrl)) {
            this.doSyncItemChain(hardware.getSession());
        } else if ("/sync-fi-ok".equals(subUrl)) {
            this.handleSyncItemFrameReceived(hardware, frame);
        } else if ("/sync-all-ok".equals(subUrl)) {
            this.handleSyncAllOkFrameReceived(hardware);
        }
    }

    private void handleSignUpFrame(DoHardware hardware, VttpFrame frame) {
        DoSignUp signUp = new DoSignUp();
        signUp.setHardwareId(frame.getParamAsString(0));
        signUp.setGatewayId(frame.getParamAsString(1));
        signUp.setEncryptType(VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING);
        signUp.setKey(frame.getParamAsString(3));
        signUp.setModel(frame.getParamAsString(4));
        signUp.setPassword(frame.getParamAsString(5));

        if (this.onHardwareSignUp(signUp)) {
            // Sign up success.
            hardware.sendFrame(new VttpFrame(this.wrapSubUrl("/sign-up-ok")));
        } else {
            // Sign up failed.
            hardware.sendFrame(new VttpFrame(this.wrapSubUrl("/sign-up-err")));
        }
    }

    private void handleGetSignUpFrame(DoHardware hardware, VttpFrame frame) {
        String hardwareId = frame.getParamAsString(0);
        String gatewayId = frame.getParamAsString(1);
        DoSignUp signUp = this.onHardwareFetching(hardwareId, gatewayId);

        VttpFrame responseFrame;
        if (signUp == null) {
            responseFrame = new VttpFrame(this.wrapSubUrl("/no-sign-up"));
            responseFrame.appendParam(hardwareId);
            responseFrame.appendParam(gatewayId);
        } else {
            responseFrame = new VttpFrame(this.wrapSubUrl("/rt-sign-up"));
            responseFrame.appendParam(hardwareId);
            responseFrame.appendParam(gatewayId);

            Integer encryptType = signUp.getEncryptType();
            if (encryptType != null) {
                responseFrame.appendParam(encryptType);
            } else {
                responseFrame.appendParam(0);
            }

            this.checkStringIfNullToFrameParam(responseFrame, signUp.getKey());
            this.checkStringIfNullToFrameParam(responseFrame, signUp.getModel());
            this.checkStringIfNullToFrameParam(responseFrame, signUp.getPassword());
        }
        hardware.sendFrame(responseFrame);
    }

    private void checkStringIfNullToFrameParam(VttpFrame frame, String param) {
        if (param != null) {
            frame.appendParam(param);
        } else {
            frame.appendParam("");
        }
    }

    public void doSyncItemChain(IoSession session) {
        if (this.syncing) {
            return;
        }
        this.syncing = true;
        session.write(new VttpFrame("/r/sync-fi"));
    }

    private void handleSyncItemFrameReceived(DoHardware hardware, VttpFrame frame) {
        String itemType = frame.getParamAsString(0);
        String operator = frame.getParamAsString(1);
        String itemId = frame.getParamAsString(2);

        DoSignUp signUp = new DoSignUp();
        signUp.setHardwareId(frame.getParamAsString(3));
        signUp.setGatewayId(frame.getParamAsString(4));
        signUp.setEncryptType(VttpEncrypt.ENCRYPT_TYPE_AES256_CBC_PKCS5PADDING);
        signUp.setKey(frame.getParamAsString(6));
        signUp.setModel(frame.getParamAsString(7));
        signUp.setPassword(frame.getParamAsString(8));

        if (this.onHardwareSignUp(signUp)) {
            // Sign up success.
            // Fetch next.
            VttpFrame responseFrame = new VttpFrame(this.wrapSubUrl("/sync-fi"));
            responseFrame.appendParam(itemType);
            responseFrame.appendParam(operator);
            responseFrame.appendParam(itemId);
            hardware.sendFrame(responseFrame);
        } else {
            // Sign up failed.
            hardware.sendFrame(new VttpFrame(this.wrapSubUrl("/sync-fi-err")));
        }
    }

    private void handleSyncAllOkFrameReceived(DoHardware hardware) {
        this.syncing = false;
    }

}
