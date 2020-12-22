package com.github.dianduiot.bridge.handler;

import com.github.dianduiot.bridge.DoHardware;
import com.github.dianduiot.bridge.model.*;
import com.github.dianduiot.vttp.VttpFrame;

public class BridgeConfigHandler extends BridgeSpeedUpHandler {
    // --------------------------------------------------
    // Should override methods BEGIN.

    /** If DoHardware.requestNetStatus() method invoked, this method will be invoked after version fetched. */
    public void onNetStatusFetched(DoHardware hardware, DoBytes configBytes) {
    }
    /** If DoHardware.requestNetInfo() method invoked, this method will be invoked after info fetched. */
    public void onNetInfoFetched(DoHardware hardware, DoBytes configBytes) {
    }
    /** If DoHardware.requestVersionInfo() method invoked, this method will be invoked after version fetched. */
    public void onVersionInfoFetched(DoHardware hardware, DoVersionInfo versionInfo) {
    }
    /** If DoHardware.requestAppInfo() method invoked, this method will be invoked after info fetched. */
    public void onAppInfoFetched(DoHardware hardware, DoAppInfo appInfo) {
    }
    /** If DoHardware.requestAppProtocol() method invoked, this method will be invoked after info fetched. */
    public void onAppProtocolFetched(DoHardware hardware, DoBytes configBytes) {
    }
    /** If DoHardware.requestCollectionInfo() method invoked, this method will be invoked after info fetched. */
    public void onCollectionInfoFetched(DoHardware hardware, DoCollectionInfo collectionInfo) {
    }
    /** If DoHardware.requestDevicePortInfo() method invoked, this method will be invoked after info fetched. */
    public void onDevicePortInfoFetched(DoHardware hardware, DoBytes configBytes) {
    }
    /** If DoHardware.writeNetInfo() method invoked, this method will be invoked after info wrote. */
    public void onNetInfoWrote(DoHardware hardware) {
    }
    /** If DoHardware.writeVersionInfo() method invoked, this method will be invoked after version wrote. */
    public void onVersionInfoWrote(DoHardware hardware) {
    }
    /** If DoHardware.writeAppInfo() method invoked, this method will be invoked after info wrote. */
    public void onAppInfoWrote(DoHardware hardware) {
    }
    /** If DoHardware.writeAppProtocol() method invoked, this method will be invoked after info wrote. */
    public void onAppProtocolWrote(DoHardware hardware) {
    }
    /** If DoHardware.writeCollectionInfo() method invoked, this method will be invoked after info wrote. */
    public void onCollectionInfoWrote(DoHardware hardware) {
    }
    /** If DoHardware.writeDevicePortInfo() method invoked, this method will be invoked after info wrote. */
    public void onDevicePortInfoWrote(DoHardware hardware) {
    }

    public void onPropertyCountFetched(DoHardware hardware, DoCount count) {
    }
    public void onPropertyItemFetched(DoHardware hardware, DoPropertyInfo propertyInfo) {
    }
    public void onPropertyItemSaved(DoHardware hardware, DoId id) {
    }
    public void onPropertyItemRemoved(DoHardware hardware, DoId id) {
    }

    public void onActionCountFetched(DoHardware hardware, DoCount count) {
    }
    public void onActionItemFetched(DoHardware hardware, DoActionInfo actionInfo) {
    }
    public void onActionItemSaved(DoHardware hardware, DoId id) {
    }
    public void onActionItemRemoved(DoHardware hardware, DoId id) {
    }

    public void onExtensionCountFetched(DoHardware hardware, DoCount count) {
    }
    public void onExtensionItemFetched(DoHardware hardware, DoExtensionInfo extensionInfo) {
    }
    public void onExtensionItemSaved(DoHardware hardware, DoId id) {
    }
    public void onExtensionItemRemoved(DoHardware hardware, DoId id) {
    }

    public void onTriggerCountFetched(DoHardware hardware, DoCount count) {
    }
    public void onTriggerItemFetched(DoHardware hardware, DoTriggerInfo triggerInfo) {
    }
    public void onTriggerItemSaved(DoHardware hardware, DoId id) {
    }
    public void onTriggerItemRemoved(DoHardware hardware, DoId id) {
    }

    public void onReactCountFetched(DoHardware hardware, DoCount count) {
    }
    public void onReactItemFetched(DoHardware hardware, DoReactInfo reactInfo) {
    }
    public void onReactItemSaved(DoHardware hardware, DoId id) {
    }
    public void onReactItemRemoved(DoHardware hardware, DoId id) {
    }

    public void onRebootAccepted(DoHardware hardware) {
    }
    public void onEnableConfigServerOk(DoHardware hardware) {
    }

    // Should override methods END.
    // --------------------------------------------------

    public BridgeConfigHandler() {
        super('c');
    }

    @Override
    public void handleFrame(DoHardware hardware, VttpFrame frame, String subUrl) {
        if (!frame.isEncrypted()) {
            return;
        }

        if ("/rns".equals(subUrl)) {
            this.handleNetStatusFetchedFrame(hardware, frame);
        } else if ("/rn".equals(subUrl)) {
            this.handleNetInfoFetchedFrame(hardware, frame);
        } else if ("/rv".equals(subUrl)) {
            this.handleVersionInfoFetchedFrame(hardware, frame);
        } else if ("/ra".equals(subUrl)) {
            this.handleAppInfoFetchedFrame(hardware, frame);
        } else if ("/rap".equals(subUrl)) {
            this.handleAppProtocolFetchedFrame(hardware, frame);
        } else if ("/rc".equals(subUrl)) {
            this.handleCollectionInfoFetchedFrame(hardware, frame);
        } else if ("/rd".equals(subUrl)) {
            this.handleDevicePortInfoFetchedFrame(hardware, frame);
        } else if ("/wn-ok".equals(subUrl)) {
            this.onNetInfoWrote(hardware);
        } else if ("/wv-ok".equals(subUrl)) {
            this.onVersionInfoWrote(hardware);
        } else if ("/wa-ok".equals(subUrl)) {
            this.onAppInfoWrote(hardware);
        } else if ("/wap-ok".equals(subUrl)) {
            this.onAppProtocolWrote(hardware);
        } else if ("/wc-ok".equals(subUrl)) {
            this.onCollectionInfoWrote(hardware);
        } else if ("/wd-ok".equals(subUrl)) {
            this.onDevicePortInfoWrote(hardware);
        } else if ("/p-c-ok".equals(subUrl)) {
            this.handlePropertyCountFrame(hardware, frame);
        } else if ("/p-i-ok".equals(subUrl)) {
            this.handlePropertyItemFrame(hardware, frame);
        } else if ("/p-s-ok".equals(subUrl)) {
            this.handlePropertySavedFrame(hardware, frame);
        } else if ("/p-r-ok".equals(subUrl)) {
            this.handlePropertyRemovedFrame(hardware, frame);
        } else if ("/a-c-ok".equals(subUrl)) {
            this.handleActionCountFrame(hardware, frame);
        } else if ("/a-i-ok".equals(subUrl)) {
            this.handleActionItemFrame(hardware, frame);
        } else if ("/a-s-ok".equals(subUrl)) {
            this.handleActionSavedFrame(hardware, frame);
        } else if ("/a-r-ok".equals(subUrl)) {
            this.handleActionRemovedFrame(hardware, frame);
        } else if ("/e-c-ok".equals(subUrl)) {
            this.handleExtensionCountFrame(hardware, frame);
        } else if ("/e-i-ok".equals(subUrl)) {
            this.handleExtensionItemFrame(hardware, frame);
        } else if ("/e-s-ok".equals(subUrl)) {
            this.handleExtensionSavedFrame(hardware, frame);
        } else if ("/e-r-ok".equals(subUrl)) {
            this.handleExtensionRemovedFrame(hardware, frame);
        } else if ("/t-c-ok".equals(subUrl)) {
            this.handleTriggerCountFrame(hardware, frame);
        } else if ("/t-i-ok".equals(subUrl)) {
            this.handleTriggerItemFrame(hardware, frame);
        } else if ("/t-s-ok".equals(subUrl)) {
            this.handleTriggerSavedFrame(hardware, frame);
        } else if ("/t-r-ok".equals(subUrl)) {
            this.handleTriggerRemovedFrame(hardware, frame);
        } else if ("/r-c-ok".equals(subUrl)) {
            this.handleReactCountFrame(hardware, frame);
        } else if ("/r-i-ok".equals(subUrl)) {
            this.handleReactItemFrame(hardware, frame);
        } else if ("/r-s-ok".equals(subUrl)) {
            this.handleReactSavedFrame(hardware, frame);
        } else if ("/r-r-ok".equals(subUrl)) {
            this.handleReactRemovedFrame(hardware, frame);
        } else if ("/rb-ok".equals(subUrl)) {
            this.onRebootAccepted(hardware);
        } else if ("/ecs-ok".equals(subUrl)) {
            this.onEnableConfigServerOk(hardware);
        }
    }

    private void handleNetStatusFetchedFrame(DoHardware hardware, VttpFrame frame) {
        DoBytes bytes = new DoBytes();
        bytes.setObjType("C-NS");
        bytes.setBytesType(frame.getParamAsInt(0));
        bytes.setBytes(frame.getParam(1));
        this.onNetStatusFetched(hardware, bytes);
    }

    private void handleNetInfoFetchedFrame(DoHardware hardware, VttpFrame frame) {
        DoBytes bytes = new DoBytes();
        bytes.setObjType("C-N");
        bytes.setBytesType(frame.getParamAsInt(0));
        bytes.setBytes(frame.getParam(1));
        this.onNetInfoFetched(hardware, bytes);
    }

    private void handleVersionInfoFetchedFrame(DoHardware hardware, VttpFrame frame) {
        DoVersionInfo versionInfo = new DoVersionInfo();
        versionInfo.setCurrMasterVersion(frame.getParamAsString(0));
        versionInfo.setCurrSlaveVersion(frame.getParamAsString(1));
        versionInfo.setTargetMasterVersion(frame.getParamAsString(2));
        versionInfo.setTargetSlaveVersion(frame.getParamAsString(3));
        this.onVersionInfoFetched(hardware, versionInfo);
    }

    private void handleAppInfoFetchedFrame(DoHardware hardware, VttpFrame frame) {
        DoAppInfo appInfo = new DoAppInfo();
        appInfo.setGatewayId(frame.getParamAsString(0));
        appInfo.setDdwlLinkEnableFlag(frame.getParamAsInt(1) > 0);
        appInfo.setAppLinkEnableFlag(frame.getParamAsInt(2) > 0);
        appInfo.setAppLinkProtocolType(frame.getParamAsInt(3));
        appInfo.setAppLinkHosts(frame.getParamAsString(4));
        this.onAppInfoFetched(hardware, appInfo);
    }

    private void handleAppProtocolFetchedFrame(DoHardware hardware, VttpFrame frame) {
        DoBytes bytes = new DoBytes();
        bytes.setObjType("C-AP");
        bytes.setBytesType(frame.getParamAsInt(0));
        bytes.setBytes(frame.getParam(1));
        this.onAppProtocolFetched(hardware, bytes);
    }

    private void handleCollectionInfoFetchedFrame(DoHardware hardware, VttpFrame frame) {
        DoCollectionInfo collectionInfo = new DoCollectionInfo();
        collectionInfo.setMode(frame.getParamAsInt(0));
        collectionInfo.setCycleMs(frame.getParamAsInt(1));
        collectionInfo.setResponseMs(frame.getParamAsInt(2));
        collectionInfo.setReadInterval(frame.getParamAsInt(3));
        collectionInfo.setWriteInterval(frame.getParamAsInt(4));
        collectionInfo.setMaxPropertyCombine(frame.getParamAsInt(5));
        collectionInfo.setResetAfterWrote(frame.getParamAsByte(6) > 0);

        this.onCollectionInfoFetched(hardware, collectionInfo);
    }

    private void handleDevicePortInfoFetchedFrame(DoHardware hardware, VttpFrame frame) {
        DoBytes bytes = new DoBytes();
        bytes.setObjType("C-DP");
        bytes.setBytesType(frame.getParamAsInt(0));
        bytes.setBytes(frame.getParam(1));
        this.onDevicePortInfoFetched(hardware, bytes);
    }

    private void handlePropertyCountFrame(DoHardware hardware, VttpFrame frame) {
        DoCount doCount = new DoCount("C-P-C");
        doCount.setValue(frame.getParamAsInt(0));
        doCount.setProtocol(frame.getParamAsInt(1));
        this.onPropertyCountFetched(hardware, doCount);
    }
    private void handlePropertyItemFrame(DoHardware hardware, VttpFrame frame) {
        DoPropertyInfo propertyInfo = new DoPropertyInfo();

        propertyInfo.setIndex(frame.getParamAsInt(0));

        propertyInfo.setEnableFlag(frame.getParamAsByte(1) > 0);
        propertyInfo.setSendFlag(frame.getParamAsByte(2) > 0);
        propertyInfo.setPropertyId(frame.getParamAsString(3));
        propertyInfo.setPropertyName(frame.getParamAsString(4));
        propertyInfo.setPrecision(frame.getParamAsByte(5).intValue());
        propertyInfo.setAfpr(frame.getParamAsString(6));
        propertyInfo.setIgnoreValue(frame.getParamAsString(7));
        propertyInfo.setCountDownMs(frame.getParamAsUint(8));
        propertyInfo.setForceUploadMs(frame.getParamAsUint(9));
        propertyInfo.setSort(frame.getParamAsInt(10));

        propertyInfo.setCollectionBytes(frame.getParam(11));

        this.onPropertyItemFetched(hardware, propertyInfo);
    }
    private void handlePropertySavedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-P-S");
        doId.setId(frame.getParamAsString(0));
        this.onPropertyItemSaved(hardware, doId);
    }
    private void handlePropertyRemovedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-P-R");
        doId.setId(frame.getParamAsString(0));
        this.onPropertyItemRemoved(hardware, doId);
    }

    private void handleActionCountFrame(DoHardware hardware, VttpFrame frame) {
        DoCount doCount = new DoCount("C-A-C");
        doCount.setValue(frame.getParamAsInt(0));
        doCount.setProtocol(frame.getParamAsInt(1));
        this.onActionCountFetched(hardware, doCount);
    }
    private void handleActionItemFrame(DoHardware hardware, VttpFrame frame) {
        DoActionInfo actionInfo = new DoActionInfo();

        actionInfo.setIndex(frame.getParamAsInt(0));

        actionInfo.setEnableFlag(frame.getParamAsByte(1) > 0);
        actionInfo.setActionId(frame.getParamAsString(2));
        actionInfo.setActionName(frame.getParamAsString(3));
        actionInfo.setInputType(frame.getParamAsByte(4).intValue());
        actionInfo.setOperatorType(frame.getParamAsByte(5).intValue());
        actionInfo.setValueStr(frame.getParamAsString(6));
        actionInfo.setBfpr(frame.getParamAsString(7));
        actionInfo.setBitMask(frame.getParamAsString(8));
        actionInfo.setSort(frame.getParamAsInt(9));

        actionInfo.setCollectionBytes(frame.getParam(10));

        this.onActionItemFetched(hardware, actionInfo);
    }
    private void handleActionSavedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-A-S");
        doId.setId(frame.getParamAsString(0));
        this.onActionItemSaved(hardware, doId);
    }
    private void handleActionRemovedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-A-R");
        doId.setId(frame.getParamAsString(0));
        this.onActionItemRemoved(hardware, doId);
    }

    private void handleExtensionCountFrame(DoHardware hardware, VttpFrame frame) {
        DoCount doCount = new DoCount("C-E-C");
        doCount.setValue(frame.getParamAsInt(0));
        doCount.setProtocol(frame.getParamAsInt(1));
        this.onExtensionCountFetched(hardware, doCount);
    }
    private void handleExtensionItemFrame(DoHardware hardware, VttpFrame frame) {
        DoExtensionInfo extensionInfo = new DoExtensionInfo();

        extensionInfo.setIndex(frame.getParamAsInt(0));

        extensionInfo.setExtensionId(frame.getParamAsString(1));
        extensionInfo.setExtensionName(frame.getParamAsString(2));
        extensionInfo.setExtensionValue(frame.getParamAsString(3));
        extensionInfo.setSort(frame.getParamAsInt(4));

        this.onExtensionItemFetched(hardware, extensionInfo);
    }
    private void handleExtensionSavedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-E-S");
        doId.setId(frame.getParamAsString(0));
        this.onExtensionItemSaved(hardware, doId);
    }
    private void handleExtensionRemovedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-E-R");
        doId.setId(frame.getParamAsString(0));
        this.onExtensionItemRemoved(hardware, doId);
    }

    private void handleTriggerCountFrame(DoHardware hardware, VttpFrame frame) {
        DoCount doCount = new DoCount("C-T-C");
        doCount.setValue(frame.getParamAsInt(0));
        doCount.setProtocol(frame.getParamAsInt(1));
        this.onTriggerCountFetched(hardware, doCount);
    }
    private void handleTriggerItemFrame(DoHardware hardware, VttpFrame frame) {
        DoTriggerInfo triggerInfo = new DoTriggerInfo();

        triggerInfo.setIndex(frame.getParamAsInt(0));

        triggerInfo.setEnableFlag(frame.getParamAsByte(1) > 0);
        triggerInfo.setSendFlag(frame.getParamAsByte(2) > 0);
        triggerInfo.setTriggerId(frame.getParamAsString(3));
        triggerInfo.setPropertyId(frame.getParamAsString(4));
        triggerInfo.setTriggerName(frame.getParamAsString(5));
        triggerInfo.setLevel(frame.getParamAsInt(6));
        triggerInfo.setConditionType(frame.getParamAsByte(7).intValue());
        triggerInfo.setXstr(frame.getParamAsString(8));
        triggerInfo.setYstr(frame.getParamAsString(9));
        triggerInfo.setFpds(frame.getParamAsUint(10));
        triggerInfo.setCpus(frame.getParamAsUint(11));
        triggerInfo.setCpmf(frame.getParamAsUint(12));
        triggerInfo.setSort(frame.getParamAsInt(13));

        this.onTriggerItemFetched(hardware, triggerInfo);
    }
    private void handleTriggerSavedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-T-S");
        doId.setId(frame.getParamAsString(0));
        this.onTriggerItemSaved(hardware, doId);
    }
    private void handleTriggerRemovedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-T-R");
        doId.setId(frame.getParamAsString(0));
        this.onTriggerItemRemoved(hardware, doId);
    }

    private void handleReactCountFrame(DoHardware hardware, VttpFrame frame) {
        DoCount doCount = new DoCount("C-R-C");
        doCount.setValue(frame.getParamAsInt(0));
        doCount.setProtocol(frame.getParamAsInt(1));
        this.onReactCountFetched(hardware, doCount);
    }
    private void handleReactItemFrame(DoHardware hardware, VttpFrame frame) {
        DoReactInfo reactInfo = new DoReactInfo();

        reactInfo.setIndex(frame.getParamAsInt(0));

        reactInfo.setReactId(frame.getParamAsString(1));
        reactInfo.setTriggerId(frame.getParamAsString(2));
        reactInfo.setActionId(frame.getParamAsString(3));
        reactInfo.setTriggerTouchMode(frame.getParamAsByte(4).intValue());
        reactInfo.setSort(frame.getParamAsInt(5));

        this.onReactItemFetched(hardware, reactInfo);
    }
    private void handleReactSavedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-R-S");
        doId.setId(frame.getParamAsString(0));
        this.onReactItemSaved(hardware, doId);
    }
    private void handleReactRemovedFrame(DoHardware hardware, VttpFrame frame) {
        DoId doId = new DoId("C-R-R");
        doId.setId(frame.getParamAsString(0));
        this.onReactItemRemoved(hardware, doId);
    }
}
