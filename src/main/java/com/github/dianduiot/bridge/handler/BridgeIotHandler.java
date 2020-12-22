package com.github.dianduiot.bridge.handler;

import com.github.dianduiot.bridge.DoHardware;
import com.github.dianduiot.bridge.model.DoActionResult;
import com.github.dianduiot.bridge.model.DoTrigger;
import com.github.dianduiot.bridge.model.DoValue;
import com.github.dianduiot.vttp.VttpFrame;

public class BridgeIotHandler extends BridgeSpeedUpHandler {
    // --------------------------------------------------
    // Should override methods BEGIN.
    /** This method will invoke if a normal hardware registered success. */
    public void onConnected(DoHardware hardware) {
    }

    public void onValueReceived(DoHardware hardware, DoValue doValue) {
    }

    public void onTriggerTouched(DoHardware hardware, DoTrigger doTrigger) {
    }

    public void onActionResult(DoHardware hardware, DoActionResult actionResult) {
    }

    /** This method will invoke if the normal registered hardware disconnected. */
    public void onDisconnected(DoHardware hardware) {
    }

    // Should override methods END.
    // --------------------------------------------------

    public BridgeIotHandler() {
        super('i');
    }

    @Override
    public void handleFrame(DoHardware hardware, VttpFrame frame, String subUrl) {
        if (!frame.isEncrypted()) {
            return;
        }

        if ("/v".equals(subUrl)) {
            this.handleValueFrame(hardware, frame);
        } else if ("/t".equals(subUrl)) {
            this.handleTriggerFrame(hardware, frame);
        } else if ("/ar".equals(subUrl)) {
            this.handleActionResultFrame(hardware, frame);
        }
    }

    private void handleValueFrame(DoHardware hardware, VttpFrame frame) {
        DoValue value = new DoValue();
        String propertyId = frame.getParamAsString(0);
        int valueType = frame.getParamAsByte(1);
        value.setPropertyId(propertyId);
        value.setValueType(valueType);
        if (valueType == DoValue.VALUE_TYPE_INT) {
            value.setValueInt(frame.getParamAsInt(2));
        } else if (valueType == DoValue.VALUE_TYPE_FLOAT) {
            value.setValueFloat(frame.getParamAsFloat(2));
        } else {
            value.setValueType(DoValue.VALUE_TYPE_NULL);
        }

        this.onValueReceived(hardware, value);
    }

    private void handleTriggerFrame(DoHardware hardware, VttpFrame frame) {
        String propertyId = frame.getParamAsString(0);
        String triggerId = frame.getParamAsString(1);
        Integer touchMode = frame.getParamAsByte(2).intValue();
        Float value = frame.getParamAsFloat(3);

        DoTrigger trigger = new DoTrigger();
        trigger.setPropertyId(propertyId);
        trigger.setTriggerId(triggerId);
        trigger.setTouchMode(touchMode);
        trigger.setValue(value);

        this.onTriggerTouched(hardware, trigger);
    }

    private void handleActionResultFrame(DoHardware hardware, VttpFrame frame) {
        String actionId = frame.getParamAsString(0);
        Integer resultCode = frame.getParamAsInt(1);
        String writeValue = frame.getParamAsString(2);

        DoActionResult actionResult = new DoActionResult();
        actionResult.setActionId(actionId);
        actionResult.setResultCode(resultCode);
        actionResult.setWriteValue(writeValue);

        this.onActionResult(hardware, actionResult);
    }
}
