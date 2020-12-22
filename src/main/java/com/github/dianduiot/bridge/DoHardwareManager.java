package com.github.dianduiot.bridge;

import org.apache.mina.core.session.IoSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DoHardwareManager {
    private Map<String, DoHardware> hardwareMap = new HashMap<>();
    private boolean doAllOnHardwareIdFlag = false;

    /** @return If the hardware with gatewayId had been linked to current server,
     *      this method will return the DoHardware instance that rel to gatewayId param.
     *      If the hardware with gatewayId not linked to current server, will return null. */
    public DoHardware linkedHardware(String gatewayId) {
        return hardwareMap.get(gatewayId);
    }

    public Collection<DoHardware> linkedAllHardwares() {
        return hardwareMap.values();
    }

    public boolean putHardware(String gatewayId, DoHardware hardware) {
        if (doAllOnHardwareIdFlag) {
            gatewayId = hardware.getHardwareId();
        }

        // If exist last managed session, dispose it.
        DoHardware lastManagedHardware = this.hardwareMap.get(gatewayId);
        Long resultManagedTs = System.currentTimeMillis();
        if (lastManagedHardware != null) {
            resultManagedTs = (Long) lastManagedHardware.getSession().getAttribute(DoHardware.SESSION_ATTR_MANAGED_TS);
            if (resultManagedTs == null) {
                resultManagedTs = System.currentTimeMillis();
            }
            lastManagedHardware.getSession().removeAttribute(DoHardware.SESSION_ATTR_MANAGED_TS);
        }

        // Make hardware as managed.
        IoSession session = hardware.getSession();
        session.setAttribute(DoHardware.SESSION_ATTR_MANAGED_TS, resultManagedTs);
        hardwareMap.put(gatewayId, hardware);

        if (lastManagedHardware == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean ifManagedHardware(DoHardware hardware) {
        return hardware.getSession().getAttribute(DoHardware.SESSION_ATTR_MANAGED_TS) != null;
    }

    public boolean removeHardware(String gatewayId, DoHardware hardware) {
        if (doAllOnHardwareIdFlag) {
            gatewayId = hardware.getHardwareId();
        }

        // If exist last managed session, dispose it.
        DoHardware removingHardware = this.hardwareMap.get(gatewayId);
        if (removingHardware != null) {
            Long managedTs = (Long) removingHardware.getSession().getAttribute(DoHardware.SESSION_ATTR_MANAGED_TS);
            if (managedTs != null && managedTs > 0) {
                // Managed.
                // Remove it.
                this.hardwareMap.remove(gatewayId);
                return true;
            }
        }
        return false;
    }

    public boolean isDoAllOnHardwareIdFlag() {
        return doAllOnHardwareIdFlag;
    }

    public void setDoAllOnHardwareIdFlag(boolean doAllOnHardwareIdFlag) {
        this.doAllOnHardwareIdFlag = doAllOnHardwareIdFlag;
    }
}
