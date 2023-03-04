package com.example.tests.raft.server;

import com.example.tests.raft.config.Config;
import com.example.tests.raft.server.quorum.QuorumPeer;
import com.example.tests.raft.server.quorum.QuorumPeerManager;

/**
 * @author chengtong
 * @date 2023/2/25 19:36
 */
public class RaftServer {


    public static void main(String[] args) throws Exception {
        Config config = Config.getConfig(1);
        CnxnManager cnxnManager = new CnxnManager(config);
        cnxnManager.start();
        QuorumPeerManager quorumPeerManager = new QuorumPeerManager(new RaftServer());
        QuorumPeer self = new QuorumPeer(config);
        self.start();
    }
}
