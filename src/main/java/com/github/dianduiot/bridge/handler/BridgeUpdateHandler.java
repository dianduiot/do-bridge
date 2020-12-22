package com.github.dianduiot.bridge.handler;

import com.github.dianduiot.bridge.DoHardware;
import com.github.dianduiot.util.StringUtils;
import com.github.dianduiot.vttp.VttpFrame;
import org.apache.mina.core.session.IoSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class BridgeUpdateHandler extends BridgeSpeedUpHandler {
    // --------------------------------------------------
    // Should override methods BEGIN.

    /** @return The absolute io file full path for version parameter. */
    public String locateAbsIoUpdateFileFullPath(String version) {
        return null;
    }

    // Should override methods END.
    // --------------------------------------------------

    public static final String UPDATE_VERSION_SESSION_ATTR_KEY = "UV";

    public BridgeUpdateHandler() {
        super('u');
    }

    @Override
    public void handleFrame(DoHardware hardware, VttpFrame frame, String subUrl) {
        if ("/hv".equals(subUrl)) {
            this.onHv(hardware, frame);
        } else if ("/fs".equals(subUrl)) {
            this.onFs(hardware, frame);
        } else if ("/uv".equals(subUrl)) {
            this.onUv(hardware);
        }
    }

    protected void onHv(DoHardware hardware, VttpFrame frame) {
        String version = frame.getParamAsString(0);
        String fileIoFullPath = this.locateAbsIoUpdateFileFullPath(version);
        long fileSize = 0;
        if (!StringUtils.isEmpty(fileIoFullPath)) {
            File file = new File(fileIoFullPath);
            if (file.exists() && file.isFile()) {
                fileSize = file.length();
            }
        }

        IoSession session = hardware.getSession();
        if (fileSize == 0) {
            session.write(new VttpFrame(this.wrapSubUrl("/hv-err")));
            return;
        }

        session.setAttribute(UPDATE_VERSION_SESSION_ATTR_KEY, version);
        VttpFrame responseFrame = new VttpFrame(this.wrapSubUrl("/hv-ok"));
        responseFrame.appendParam((int) fileSize);
        session.write(responseFrame);
    }

    protected void onFs(DoHardware hardware, VttpFrame frame) {
        IoSession session = hardware.getSession();
        String version = (String) session.getAttribute(UPDATE_VERSION_SESSION_ATTR_KEY);
        if (StringUtils.isEmpty(version)) {
            session.write(new VttpFrame(this.wrapSubUrl("/fs-err")));
            return;
        }

        String fileIoFullPath = this.locateAbsIoUpdateFileFullPath(version);
        if (StringUtils.isEmpty(fileIoFullPath)) {
            session.write(new VttpFrame(this.wrapSubUrl("/fs-err")));
            return;
        }

        int fetchOffset = frame.getParamAsInt(0);
        int fetchSize = frame.getParamAsInt(1);

        int realFetchSize = 0;
        File file = new File(fileIoFullPath);
        if (file.exists() && file.isFile()) {
            if (fetchOffset + fetchSize > file.length()) {
                realFetchSize = (int) (file.length() - fetchOffset);
            } else {
                realFetchSize = fetchSize;
            }
        }
        if (realFetchSize == 0) {
            session.write(new VttpFrame(this.wrapSubUrl("/fs-err")));
            return;
        }

        byte[] fetchResultBytes = new byte[realFetchSize];
        InputStream in = null;
        boolean readSuccessFlag = false;
        long operatedBytes;
        try {
            in = new FileInputStream(file);
            if (fetchOffset > 0) {
                operatedBytes = in.skip(fetchOffset);
                if (operatedBytes == 0) {
                    throw new Exception();
                }
            }
            operatedBytes = in.read(fetchResultBytes);
            readSuccessFlag = operatedBytes == realFetchSize;
        } catch (Exception ignored) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                }
            }
        }

        if (readSuccessFlag) {
            VttpFrame responseFrame = new VttpFrame(this.wrapSubUrl("/fs-ok"));
            responseFrame.appendParam(fetchResultBytes);
            hardware.getSession().write(responseFrame);
        } else {
            session.write(new VttpFrame(this.wrapSubUrl("/fs-err")));
        }
    }

    protected void onUv(DoHardware hardware) {
        hardware.getSession().removeAttribute(UPDATE_VERSION_SESSION_ATTR_KEY);
    }

}
