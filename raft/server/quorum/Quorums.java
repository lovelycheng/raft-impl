package com.example.tests.raft.server.quorum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.tests.raft.config.Config;
import com.example.tests.raft.transfer.VoteFor;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.collection.ListUtil;
import lombok.Data;

/**
 * @author chengtong
 * @date 2023/3/3 20:54
 */
@Data
public class Quorums {

    private ConcurrentHashMap<Integer, QuorumPeer> quorum = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, List<VoteFor>> electionSet = new ConcurrentHashMap<>();

    private QuorumPeer self;

    public Quorums() {

    }
    public void addPeer(QuorumPeer quorumPeer) {
        quorum.put(quorumPeer.myid,quorumPeer);
    }



    /**
     * @param voteFor 选票
     * @return
     */
    public boolean addElectVotes(VoteFor voteFor) {

        electionSet.compute(voteFor.getLeaderPid(), (integer,val) -> {
            List<VoteFor> voteFors = electionSet.getOrDefault(voteFor.getLeaderPid(), new ArrayList<>());
            if (!voteFors.contains(voteFor)) {
                voteFors.add(voteFor);
            }
            return voteFors;
        });
        if (electionSet.size() > quorum.size() / 2) {
            for (Map.Entry<Integer, List<VoteFor>> entry : electionSet.entrySet()) {
                if (entry.getValue()
                    .size() > quorum.size() / 2) {

                    if (entry.getKey()
                        .equals(self.getMyid())) {
                        // todo PEER 状态变成 leader
                        self.changeToLead();
                    } else {
                        //todo follower
                        self.changeToFollower();
                    }
                    return true;
                }
            }
        } return false;
    }



}
