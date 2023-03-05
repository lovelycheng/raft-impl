package tech.lovelycheng.xuande.server;

import java.util.Objects;

import tech.lovelycheng.xuande.common.config.Config;
import tech.lovelycheng.xuande.server.quorum.QuorumPeer;
import tech.lovelycheng.xuande.server.quorum.QuorumPeerManager;

/**
 * @author chengtong
 * @date 2023/2/25 19:36
 */
public class RaftServer {


    public static void main(String[] args) throws Exception {
        QuorumPeer quorumPeer2 = new QuorumPeer();
        quorumPeer2.parseConfig(Objects.requireNonNull(Config.getConfig(2)));
        Election election2 = new Election(quorumPeer2);
        election2.start();
        election2.join();
    }
}
