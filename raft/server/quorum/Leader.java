package com.example.tests.raft.server.quorum;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.example.tests.raft.server.ServerCnxn;
import com.example.tests.raft.server.common.StateEnum;

import lombok.Data;

/**
 * @author chengtong
 * @date 2023/3/1 15:48
 */
@Data
public class Leader extends QuorumPeer{

    private QuorumPeer self;
    private int sid;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    public Leader() {

    }

    public void appendLog(){

    }

    public void commit(){
       // todo
    }

    public void heartbeatStart(){
        scheduledThreadPoolExecutor.scheduleAtFixedRate(()->{

        },0,5000, TimeUnit.MILLISECONDS);
    }

    public void heartBeatToFollowers(){
        if(!this.self.getStateEnum().equals(StateEnum.LEADER)){
            return;
        }

    }

}
