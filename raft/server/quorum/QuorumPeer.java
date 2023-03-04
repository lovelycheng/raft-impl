package com.example.tests.raft.server.quorum;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import com.example.tests.raft.config.Config;
import com.example.tests.raft.server.ServerCnxn;
import com.example.tests.raft.server.common.StateEnum;
import com.example.tests.raft.transfer.Packet;
import com.example.tests.raft.transfer.VoteFor;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static com.example.tests.raft.server.common.StateEnum.*;

/**
 * @author chengtong
 * @date 2023/2/28 21:57
 */
@Slf4j
public class QuorumPeer extends PeerThread {
    /**
     * 是否存活的标志
     */
    protected AtomicBoolean isAlive;

    protected static final AtomicReferenceFieldUpdater<QuorumPeer, StateEnum> fieldUpdater
        = AtomicReferenceFieldUpdater.newUpdater(QuorumPeer.class, StateEnum.class, "stateEnum");

    @Getter
    @Setter
    private volatile StateEnum stateEnum = FOLLOWER;
    /**
     * 当前地址
     */
    @Getter
    @Setter
    private InetSocketAddress inetSocketAddress;

    /**
     * 下一个log entry的索引
     */
    private AtomicInteger nextIndex;

    /**
     * 已经提交的最新的日志记录
     */
    private AtomicInteger committedIndex;

    /**
     * 任期
     */
    protected int term = 0;

    private long lastConnect;

    Leader leader;
    Follower follower;
    Candidate candidate;

    AtomicInteger lastRequestId = new AtomicInteger();




    /**
     *
     */

    private Map<Integer,Packet> pendingRequest = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private Integer leaderPid;

    @Getter
    @Setter
    protected Integer myid;
    /**
     * 抽象的数据池
     */
    private ArrayList<Packet> data = new ArrayList<>(100);



    private Quorums quorums;

    private Config config;

    public QuorumPeer() {

    }

    /**
     * 两个计时器：
     * 维持leadership，每次心跳重新计时。
     * 等待自己从follower变成candidate，超时之后变化状态
     */
    public QuorumPeer(Config config) {
        this.inetSocketAddress = config.getLocalAddr();
        this.quorums = new Quorums();
        this.lastConnect = System.currentTimeMillis();
        this.isAlive = new AtomicBoolean(true);
        this.follower = new Follower();
        this.follower.setSelf(this);
        for(Config other:config.getOthers()){
           QuorumPeer peer= QuorumPeer.parseFromConfig(other.getSid(),other.getLocalAddr());
           quorums.addPeer(peer);
        }
    }


    /**
     * peer 存在两种状态：
     * follower
     * candidate
     * 接受来自leader的logEntry
     */
    public Packet acceptLogEntry(Packet packet) {
        return null;
    }

    public Packet pong(Packet packet) {
        return null;
    }

    @Override
    public synchronized void start() {
        //todo connect to all peers
        //maybe socket
    }

    @Override
    public void run() {
        while (isAlive.get()) {
            StateEnum state = this.getStateEnum();
            switch (state) {
                case FOLLOWER:
                    try{
                        this.follower = new Follower();
                    }catch (Exception e){
                        this.setStateEnum(FOLLOWER);
                    }

                case CANDIDATE:
                case LEADER:
                default:
                    break;
            }
        }
    }

    public void addPendingRequest(Integer requestId,Packet packet){
        this.pendingRequest.put(requestId,packet);
    }


    public void changeToLead(){
        if(!this.getStateEnum().equals(CANDIDATE)){
            log.warn("无法从非candidate状态转变成leader");
        }else if(this.getStateEnum().equals(LEADER)){
            log.warn("已经是leader");
        }
        this.setStateEnum(LEADER);
    }

    public void changeToFollower(){
        if(this.getStateEnum().equals(FOLLOWER)){
            log.warn("已经是 follower");
        }
        this.setStateEnum(FOLLOWER);
    }

    public void lead(){
        Leader leader = new Leader();
        leader.setSid(myid);
        this.leader = leader;
    }

    public static QuorumPeer parseFromConfig(int sid,InetSocketAddress inetSocketAddress){
        QuorumPeer quorumPeer = new QuorumPeer();
        quorumPeer.setMyid(sid);
        quorumPeer.setInetSocketAddress(inetSocketAddress);
        return quorumPeer;
    }

}
