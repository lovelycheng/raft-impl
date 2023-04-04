package tech.lovelycheng.xuande.test.startup;

import java.util.Objects;

import tech.lovelycheng.xuande.common.config.Config;
import tech.lovelycheng.xuande.server.Election;
import tech.lovelycheng.xuande.server.quorum.QuorumPeer;

/**
 * @author chengtong
 * @date 2023/3/5 19:02
 */
public class ServerOne {
    public static void main(String[] args) throws InterruptedException {
        QuorumPeer quorumPeer = new QuorumPeer();
        quorumPeer.parseConfig(Objects.requireNonNull(Config.getConfig(1)));
        Election election = new Election(quorumPeer);

        election.start();
        election.join();
    }
}
