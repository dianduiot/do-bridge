package com.github.dianduiot.bridge;

import com.github.dianduiot.bridge.model.*;
import com.github.dianduiot.util.StringUtils;
import com.github.dianduiot.vttp.VttpFrame;
import org.apache.mina.core.session.IoSession;

public class DoHardware {
    public static final String SESSION_ATTR_KEY = "_H";
    public static final String SESSION_ATTR_MANAGED_TS = "_MT";

    public static final String TYPE_HARDWARE = "HW";
    public static final String TYPE_ROOT = "ROOT";

    private IoSession session;
    private BridgeServer server;

    private String hardwareId;
    private String gatewayId;
    private String hardwareType;
    private Integer redirectCount;

    private boolean registered = false;

    public DoHardware(IoSession session, BridgeServer server) {
        this.session = session;
        this.server = server;
    }

    @Deprecated
    public void signInfo(String hardwareId, String gatewayId, String hardwareType, Integer redirectCount) {
        this.hardwareId = hardwareId;
        this.gatewayId = gatewayId;
        this.hardwareType = hardwareType;
        this.redirectCount = redirectCount;
    }

    @Deprecated
    public void release() {
        this.session = null;
    }

    @Deprecated
    public void signRegOk() {
        this.registered = true;
    }

    public void sendFrame(VttpFrame frame) {
        this.session.write(frame);
    }

    // For function codes.
    public void sendLinkRedirect(String host) {
        this.sendLinkRedirect(host, 0);
    }

    public void sendLinkRedirect(String host, Integer port) {
        VttpFrame frame = new VttpFrame("/h/rd");
        frame.appendParam(host);
        frame.appendParam(port);
        this.session.write(frame);
    }

    public void executeAction(DoAction action) {
        VttpFrame frame = new VttpFrame("/i/a");
        frame.appendParam(action.getActionId());
        frame.appendParam(action.getValueStr());
        this.session.write(frame);
    }

    public void enableConfigServer() {
        VttpFrame frame = new VttpFrame("/c/ecs");
        frame.appendParamAsByte(1);
        this.session.write(frame);
    }

    public void disableConfigServer() {
        VttpFrame frame = new VttpFrame("/c/ecs");
        frame.appendParamAsByte(0);
        this.session.write(frame);
    }

    public void reboot() {
        this.session.write(new VttpFrame("/c/rb"));
    }

    public void transferBegin() {
        this.session.write(new VttpFrame("/t/h"));
    }

    public void transferEnd() {
        this.session.write(new VttpFrame("/t/r"));
    }

    public void transferPacket(byte[] bytes) {
        VttpFrame frame = new VttpFrame("/t/p");
        frame.appendParam(bytes);
        this.session.write(frame);
    }

    public void triggerOnceCollection() {
        this.session.write(new VttpFrame("/i/t-oc"));
    }

    public void requestNetStatus() {
        this.session.write(new VttpFrame("/c/fns"));
    }

    public void requestNetInfo() {
        this.session.write(new VttpFrame("/c/fn"));
    }

    public void requestVersionInfo() {
        this.session.write(new VttpFrame("/c/fv"));
    }

    public void requestAppInfo() {
        this.session.write(new VttpFrame("/c/fa"));
    }

    public void requestAppProtocol() {
        this.session.write(new VttpFrame("/c/fap"));
    }

    public void requestCollectionInfo() {
        this.session.write(new VttpFrame("/c/fc"));
    }

    public void requestDevicePortInfo() {
        this.session.write(new VttpFrame("/c/fd"));
    }

    public void writeNetInfo(DoBytes netBytes) {
        VttpFrame frame = new VttpFrame("/c/wn");
        frame.appendParam(netBytes.getBytesType());
        frame.appendParam(netBytes.getBytes());
        this.session.write(frame);
    }

    public void writeVersionInfo(DoVersionInfo versionInfo) {
        VttpFrame frame = new VttpFrame("/c/wv");
        frame.appendEmptyParam();
        frame.appendEmptyParam();
        frame.appendParam(versionInfo.getTargetMasterVersion());
        frame.appendParam(versionInfo.getTargetSlaveVersion());
        this.session.write(frame);
    }

    public void writeAppInfo(DoAppInfo appInfo) {
        VttpFrame frame = new VttpFrame("/c/wa");
        frame.appendParam(appInfo.getGatewayId());
        if (Boolean.TRUE.equals(appInfo.getDdwlLinkEnableFlag())) {
            frame.appendParam(1);
        } else {
            frame.appendParam(0);
        }

        if (Boolean.TRUE.equals(appInfo.getAppLinkEnableFlag())) {
            frame.appendParam(1);
        } else {
            frame.appendParam(0);
        }

        frame.appendParam(appInfo.getAppLinkProtocolType());
        frame.appendParam(appInfo.getAppLinkHosts());
        this.session.write(frame);
    }

    public void writeAppProtocol(DoBytes appProtocolBytes) {
        VttpFrame frame = new VttpFrame("/c/wap");
        frame.appendParam(appProtocolBytes.getBytesType());
        frame.appendParam(appProtocolBytes.getBytes());
        this.session.write(frame);
    }

    public void writeCollectionInfo(DoCollectionInfo collectionInfo) {
        int resetAfterWrote = 0;
        if (Boolean.TRUE.equals(collectionInfo.getResetAfterWrote())) {
            resetAfterWrote = 1;
        }

        VttpFrame frame = new VttpFrame("/c/wc");
        frame.appendParam(collectionInfo.getMode());
        frame.appendParam(collectionInfo.getCycleMs());
        frame.appendParam(collectionInfo.getResponseMs());
        frame.appendParam(collectionInfo.getReadInterval());
        frame.appendParam(collectionInfo.getWriteInterval());
        frame.appendParam(collectionInfo.getMaxPropertyCombine());
        frame.appendParamAsByte(resetAfterWrote);

        this.session.write(frame);
    }

    public void writeDevicePortInfo(DoBytes devicePortBytes) {
        VttpFrame frame = new VttpFrame("/c/wd");
        frame.appendParam(devicePortBytes.getBytesType());
        frame.appendParam(devicePortBytes.getBytes());
        this.session.write(frame);
    }

    public void requestPropertyCount() {
        this.session.write(new VttpFrame("/c/p-c"));
    }

    public void requestPropertyItem(int itemIndex) {
        VttpFrame frame = new VttpFrame("/c/p-i");
        frame.appendParam(itemIndex);
        this.session.write(frame);
    }

    public void requestPropertySave(DoPropertyInfo propertyInfo) {
        VttpFrame frame = new VttpFrame("/c/p-s");


        int sendFlag = 0;
        if (Boolean.TRUE.equals(propertyInfo.getSendFlag())) {
            sendFlag = 1;
        }
        int enableFlag = 0;
        if (Boolean.TRUE.equals(propertyInfo.getEnableFlag())) {
            enableFlag = 1;
        }

        frame.appendParamAsByte(enableFlag);
        frame.appendParamAsByte(sendFlag);
        frame.appendParam(propertyInfo.getPropertyId());
        frame.appendParam(propertyInfo.getPropertyName());
        frame.appendParamAsByte(propertyInfo.getPrecision());
        frame.appendParam(propertyInfo.getAfpr());
        frame.appendParam(propertyInfo.getIgnoreValue());
        frame.appendParamAsUint(propertyInfo.getCountDownMs());
        frame.appendParamAsUint(propertyInfo.getForceUploadMs());
        frame.appendParam(propertyInfo.getSort());

        frame.appendParam(propertyInfo.getCollectionBytes());

        this.session.write(frame);
    }

    public void requestPropertyRemove(String propertyId) {
        VttpFrame frame = new VttpFrame("/c/p-r");
        frame.appendParam(propertyId);
        this.session.write(frame);
    }

    public void requestActionCount() {
        this.session.write(new VttpFrame("/c/a-c"));
    }

    public void requestActionItem(int itemIndex) {
        VttpFrame frame = new VttpFrame("/c/a-i");
        frame.appendParam(itemIndex);
        this.session.write(frame);
    }

    public void requestActionSave(DoActionInfo actionInfo) {
        VttpFrame frame = new VttpFrame("/c/a-s");

        int enableFlag = 0;
        if (Boolean.TRUE.equals(actionInfo.getEnableFlag())) {
            enableFlag = 1;
        }

        frame.appendParamAsByte(enableFlag);
        frame.appendParam(actionInfo.getActionId());
        frame.appendParam(actionInfo.getActionName());
        frame.appendParamAsByte(actionInfo.getInputType());
        frame.appendParamAsByte(actionInfo.getOperatorType());
        frame.appendParam(actionInfo.getValueStr());
        frame.appendParam(actionInfo.getBfpr());
        frame.appendParam(actionInfo.getBitMask());
        frame.appendParam(actionInfo.getSort());

        frame.appendParam(actionInfo.getCollectionBytes());

        this.session.write(frame);
    }

    public void requestActionRemove(String actionId) {
        VttpFrame frame = new VttpFrame("/c/a-r");
        frame.appendParam(actionId);
        this.session.write(frame);
    }

    public void requestExtensionCount() {
        this.session.write(new VttpFrame("/c/e-c"));
    }

    public void requestExtensionItem(int itemIndex) {
        VttpFrame frame = new VttpFrame("/c/e-i");
        frame.appendParam(itemIndex);
        this.session.write(frame);
    }

    public void requestExtensionSave(DoExtensionInfo extensionInfo) {
        VttpFrame frame = new VttpFrame("/c/e-s");

        frame.appendParam(extensionInfo.getExtensionId());
        frame.appendParam(extensionInfo.getExtensionName());
        frame.appendParam(extensionInfo.getExtensionValue());
        frame.appendParam(extensionInfo.getSort());

        this.session.write(frame);
    }

    public void requestExtensionRemove(String extensionId) {
        VttpFrame frame = new VttpFrame("/c/e-r");
        frame.appendParam(extensionId);
        this.session.write(frame);
    }

    public void requestTriggerCount() {
        this.session.write(new VttpFrame("/c/t-c"));
    }

    public void requestTriggerItem(int itemIndex) {
        VttpFrame frame = new VttpFrame("/c/t-i");
        frame.appendParam(itemIndex);
        this.session.write(frame);
    }

    public void requestTriggerSave(DoTriggerInfo triggerInfo) {
        VttpFrame frame = new VttpFrame("/c/t-s");

        int enableFlag = 0;
        if (Boolean.TRUE.equals(triggerInfo.getEnableFlag())) {
            enableFlag = 1;
        }
        int sendFlag = 0;
        if (Boolean.TRUE.equals(triggerInfo.getSendFlag())) {
            sendFlag = 1;
        }

        frame.appendParamAsByte(enableFlag);
        frame.appendParamAsByte(sendFlag);
        frame.appendParam(triggerInfo.getTriggerId());
        frame.appendParam(triggerInfo.getPropertyId());
        frame.appendParam(triggerInfo.getTriggerName());
        frame.appendParam(triggerInfo.getLevel());
        frame.appendParamAsByte(triggerInfo.getConditionType());
        frame.appendParam(triggerInfo.getXstr());
        frame.appendParam(triggerInfo.getYstr());
        frame.appendParamAsUint(triggerInfo.getFpds());
        frame.appendParamAsUint(triggerInfo.getCpus());
        frame.appendParamAsUint(triggerInfo.getCpmf());
        frame.appendParam(triggerInfo.getSort());

        this.session.write(frame);
    }

    public void requestTriggerRemove(String triggerId) {
        VttpFrame frame = new VttpFrame("/c/t-r");
        frame.appendParam(triggerId);
        this.session.write(frame);
    }

    public void requestReactCount() {
        this.session.write(new VttpFrame("/c/r-c"));
    }

    public void requestReactItem(int itemIndex) {
        VttpFrame frame = new VttpFrame("/c/r-i");
        frame.appendParam(itemIndex);
        this.session.write(frame);
    }

    public void requestReactSave(DoReactInfo reactInfo) {
        VttpFrame frame = new VttpFrame("/c/r-s");

        frame.appendParam(reactInfo.getReactId());
        frame.appendParam(reactInfo.getTriggerId());
        frame.appendParam(reactInfo.getActionId());
        frame.appendParamAsByte(reactInfo.getTriggerTouchMode());
        frame.appendParam(reactInfo.getSort());

        this.session.write(frame);
    }

    public void requestReactRemove(String reactId) {
        VttpFrame frame = new VttpFrame("/c/r-r");
        frame.appendParam(reactId);
        this.session.write(frame);
    }

    public IoSession getSession() {
        return session;
    }

    public BridgeServer getServer() {
        return server;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public String getGatewayId() {
        if (StringUtils.isEmpty(gatewayId)) {
            return hardwareId;
        } else {
            return gatewayId;
        }
    }

    public String getHardwareType() {
        return hardwareType;
    }

    public Integer getRedirectCount() {
        return redirectCount;
    }

    public boolean isRegistered() {
        return registered;
    }

    public Long linkTs() {
        return (Long) this.session.getAttribute(SESSION_ATTR_MANAGED_TS);
    }
}
