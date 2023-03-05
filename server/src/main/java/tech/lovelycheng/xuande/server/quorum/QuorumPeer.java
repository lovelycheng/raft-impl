package tech.lovelycheng.xuande.server.quorum;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tech.lovelycheng.xuande.common.config.Config;
import tech.lovelycheng.xuande.server.Election;
import tech.lovelycheng.xuande.server.StateEnum;
import tech.lovelycheng.xuande.transfer.Packet;
import static tech.lovelycheng.xuande.server.StateEnum.CANDIDATE;
import static tech.lovelycheng.xuande.server.StateEnum.FOLLOWER;
import static tech.lovelycheng.xuande.server.StateEnum.LEADER;

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
    @Getter
    @Setter
    private AtomicInteger nextIndex;

    /**
     * 已经提交的最新的日志记录
     */
    @Getter
    @Setter
    private AtomicInteger committedIndex = new AtomicInteger(0);

    /**
     * 任期
     */
    @Getter
    @Setter
    protected int term = 0;

    private long lastConnect;

    Leader leader;
    Follower follower;
    Candidate candidate;

    AtomicInteger lastRequestId = new AtomicInteger();


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


    @Getter
    @Setter
    private Quorums quorums;

    private Config config;

    public QuorumPeer() {

    }

    public void parseConfig(Config config){
        this.inetSocketAddress = config.getLocalAddr();
        this.myid = config.getSid();
        this.quorums = new Quorums();
        this.lastConnect = System.currentTimeMillis();
        this.isAlive = new AtomicBoolean(true);
        this.follower = new Follower();
        this.follower.setSelf(this);
        for(Config other:config.getOthers()){
            QuorumPeer peer= QuorumPeer.parseFromConfig(other.getSid(),other.getLocalAddr());
            quorums.addPeer(peer);
        }
        quorums.addPeer(this);
        this.follower.start();
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
                    //理论上会在此等待 其他peer接入 或者在这个状态超时进入candidate；或者等leader的append entry消息
                case CANDIDATE:
                    Election election = new Election(this);
                    election.start();
                case LEADER:
                default:
                    break;
            }
        }
    }


    public void changeToLead(){
        if(!this.getStateEnum().equals(CANDIDATE)){
            log.info("无法从非candidate状态转变成leader");
        }else if(this.getStateEnum().equals(LEADER)){
            log.info("已经是leader");
        }
        this.setStateEnum(LEADER);
    }

    public void changeToFollower(){
        if(this.getStateEnum().equals(FOLLOWER)){
            log.info("已经是 follower");
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
