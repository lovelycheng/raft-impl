package tech.lovelycheng.xuande.test.startup;

import java.util.Objects;

import tech.lovelycheng.xuande.common.config.Config;
import tech.lovelycheng.xuande.server.Election;
import tech.lovelycheng.xuande.server.quorum.QuorumPeer;

/**
 * @author chengtong
 * @date 2023/3/5 18:33
 */
public class Election3 {
    public static void main(String[] args) throws InterruptedException {
        QuorumPeer quorumPeer = new QuorumPeer();
        quorumPeer.parseConfig(Objects.requireNonNull(Config.getConfig(3)));
        Election election = new Election(quorumPeer);

        election.start();
        election.join();
    }
}
