package tech.lovelycheng.xuande.server.quorum;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;



import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import tech.lovelycheng.xuande.server.StateEnum;
import tech.lovelycheng.xuande.transfer.Packet;
import tech.lovelycheng.xuande.transfer.VoteFor;

/**
 * @author chengtong
 * @date 2023/3/1 15:48
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class Follower extends QuorumPeer {

    private QuorumPeer self;

    /**
     * 是否支持当前leader
     */
    private AtomicBoolean supportive = new AtomicBoolean(false);

    private Timer timer = Timer.simpleTimer("leaderShipChangeTimer", 5, TimeUnit.SECONDS, this::changeToCandidate);


    public Follower() {

    }

    public void start(){
        timer.start();
    }

    public void follow() {

    }

    public void changeToCandidate() {
        log.info("peer:{}开始选举", this.self.myid);
        if (!fieldUpdater.compareAndSet(this, StateEnum.FOLLOWER, StateEnum.CANDIDATE)) {
            log.warn("当前peer状态 not follower");
            return;
        }
        this.supportive.set(false);
    }

    public void refresh() {
        this.timer.refresh();
    }

    public boolean supportElect(VoteFor voteFor){
        return !StateEnum.LEADER.equals(self.getStateEnum()) && self.isAlive.get()
            && !follower.getSupportive()
            .get();
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



}
