package com.example.tests.raft.server.quorum;

import java.net.InetSocketAddress;

import com.example.tests.raft.server.ServerCnxn;

/**
 * @author chengtong
 * @date 2023/3/1 15:48
 */
public class Candidate extends QuorumPeer{
    /**
     * 两个计时器：
     * 维持leadership，每次心跳重新计时。
     * 等待自己从follower变成candidate，超时之后变化状态
     *
     * @param inetSocketAddress
     * @param cnxn
     */
    public Candidate(InetSocketAddress inetSocketAddress, ServerCnxn cnxn) {

    }
}
