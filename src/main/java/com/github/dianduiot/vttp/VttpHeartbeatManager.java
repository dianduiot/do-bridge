package com.github.dianduiot.vttp;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.util.*;

public class VttpHeartbeatManager extends TimerTask {
    private static final long HEARTBEAT_CHECK_CYCLE_MS = 60000;
    private static final int HEARTBEAT_CHECK_CYCLE_SEC = (int) (HEARTBEAT_CHECK_CYCLE_MS / 1000);

    private List<NioSocketAcceptor> nioSocketAcceptorList = new ArrayList<>();
    private List<NioDatagramAcceptor> nioDatagramAcceptorList = new ArrayList<>();
    private Map<String, VttpHeartbeatClient> clientMap = new HashMap<>();

    private Timer timer = null;

    public void addAcceptor(NioSocketAcceptor acceptor) {
        nioSocketAcceptorList.add(acceptor);
    }

    public void addAcceptor(NioDatagramAcceptor acceptor) {
        nioDatagramAcceptorList.add(acceptor);
    }

    public void addClient(String name, VttpHeartbeatClient client) {
        this.clientMap.put(name, client);
    }

    public void removeClient(String name) {
        this.clientMap.remove(name);
    }

    public void enableHeartbeatManager() {
        this.timer = new Timer();
        this.timer.schedule(this, HEARTBEAT_CHECK_CYCLE_MS, HEARTBEAT_CHECK_CYCLE_MS);
    }

    public VttpHeartbeatManager withEnabled() {
        this.enableHeartbeatManager();
        return this;
    }

    @Override
    public void run() {
        // For servers.
        VttpFrame heartbeatFrame = VttpFrame.buildHeartbeatFrame();
        for (NioSocketAcceptor acceptor : this.nioSocketAcceptorList) {
            Collection<IoSession> sessions = acceptor.getManagedSessions().values();
            this.handleSessionHeartbeatCheckAndSend(sessions, heartbeatFrame);
        }
        for (NioDatagramAcceptor acceptor : this.nioDatagramAcceptorList) {
            Collection<IoSession> sessions = acceptor.getManagedSessions().values();
            this.handleSessionHeartbeatCheckAndSend(sessions, heartbeatFrame);
        }
        // For clients.
        Collection<VttpHeartbeatClient> clients = this.clientMap.values();
        for (VttpHeartbeatClient client : clients) {
            client.checkClientStatusAndRecovery();
        }
    }

    private void handleSessionHeartbeatCheckAndSend(Collection<IoSession> sessions, VttpFrame heartbeatFrame) {
        for (IoSession session : sessions) {
            VttpHeartbeat heartbeat = (VttpHeartbeat) session.getAttribute(VttpHeartbeat.HEARTBEAT_ATTR_KEY);
            if (heartbeat == null) {
                session.closeNow();
            } else {
                long deadline = heartbeat.getCycle() * 1000 * 3 + 5000 + heartbeat.getLastReceivedTs();
                if (deadline < System.currentTimeMillis()) {
                    session.closeNow();
                } else {
                    int sendHeartbeatCd = heartbeat.getSendCd() - HEARTBEAT_CHECK_CYCLE_SEC;
                    if (sendHeartbeatCd <= 0) {
                        sendHeartbeatCd = heartbeat.getCycle();
                        session.write(heartbeatFrame);
                    }
                    heartbeat.setSendCd(sendHeartbeatCd);
                }
            }
        }
    }
}
