package com.example.tests.raft.server.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.tests.raft.server.CnxnManager;
import com.example.tests.raft.server.ServerCnxn;
import com.example.tests.raft.server.quorum.QuorumPeer;
import com.example.tests.raft.server.quorum.Quorums;
import com.example.tests.raft.transfer.Packet;
import com.example.tests.raft.transfer.VoteFor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chengtong
 * @date 2023/3/3 23:08
 * election 使用netty 复杂度有点高，问题是netty的client端和server端是不同用的。
 * 如果server要连上4个client，那么就需要4个bootstrap。感觉不太行。
 *
 */
@Data
@Slf4j
public class Election extends Thread{


    private final QuorumPeer self;

    private AtomicBoolean isFinish = new AtomicBoolean(false);

    private Quorums quorums;
    RecvWorker rw;
    private final LinkedBlockingQueue<Packet.Body> recvQueue = new LinkedBlockingQueue<>();
    private Map<Integer, MsgSender> senderMap = new ConcurrentHashMap<>();


    public Election(QuorumPeer self,Quorums quorums) {
        this.self = self;
        this.quorums = quorums;
        this.rw = new RecvWorker(self.getMyid(),recvQueue, self);
        for(Map.Entry<Integer,QuorumPeer> entry: quorums.getQuorum().entrySet()){

        }
    }

    @Override
    public synchronized void start() {
        this.rw.start();
    }

    @Override
    public void run() {

    }

    /**
     * 以下是交互处理线程类
     */

    // public Packet
    public static class MsgSender extends Thread {
        private final int sid;
        private final LinkedBlockingQueue<Packet.Body> queue;
        private CnxnManager cnxnManager;
        private final QuorumPeer self;
        private final QuorumPeer peer;

        public MsgSender(int sid, LinkedBlockingQueue<Packet.Body> queue, QuorumPeer self, QuorumPeer quorumPeer,
            QuorumPeer peer) {
            this.sid = sid;
            this.queue = queue;
            this.self = self;
            this.peer = peer;
        }

        @Override
        public synchronized void start() {
            self.start();
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    Packet.Body body = queue.poll(40, TimeUnit.SECONDS);
                    if (body == null) {
                        continue;
                    }
                    Integer requestId = serverCnxn.getNextRequestId();
                    Packet packet = new Packet(body,requestId );
                    packet.setBody(body);
                    serverCnxn.getChannel()
                        .writeAndFlush(packet)
                        .addListener(future -> {
                            log.info("sid:{},选举信息发送成功", sid);
                            self.addPendingRequest(requestId,packet);
                        });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class RecvWorker extends Thread{

        private final int sid;
        private final LinkedBlockingQueue<Packet.Body> queue;
        private final QuorumPeer self;

        public RecvWorker(int sid, LinkedBlockingQueue<Packet.Body> queue,  QuorumPeer self) {
            this.sid = sid;
            this.queue = queue;
            this.self = self;
        }



        @Override
        public void run() {
            while (true){
                Packet.Body body = queue.poll();
                if(body == null){
                    continue;
                }
                VoteFor voteFor = (VoteFor) body.getData();
                if(quorums.addElectVotes(voteFor)){

                }
            }
        }
    }

}
