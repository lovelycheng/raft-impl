package com.example.tests.raft.server.quorum;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.tests.raft.server.RaftServer;
import com.example.tests.raft.server.ServerCnxn;
import com.example.tests.raft.transfer.Packet;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.internal.ConcurrentSet;
import lombok.Data;
import lombok.extern.java.Log;

/**
 * @author chengtong
 * @date 2023/2/27 20:00
 */
@Log
@Data
public class QuorumPeerManager {

    private final RaftServer server;

    private QuorumPeer self;

    private ConcurrentSet<QuorumPeer> quorumPeers = new ConcurrentSet<>();

    public QuorumPeerManager(RaftServer server) {
        this.server = server;
    }

    public LinkedBlockingQueue<Packet> recvQueue = new LinkedBlockingQueue<>();




}
