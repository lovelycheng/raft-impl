package com.example.tests.raft.transfer;

import lombok.Data;

/**
 * @author chengtong
 * @date 2023/2/25 15:48
 */
@Data
public class VoteFor {
    /**
     *
     */
    private int pid;
    /**
     * 支持的leadePid
     */
    private int leaderPid;
    /**
     * 任期
     */
    private int term;
    /**
     * 最新的logIndex
     */
    private int lastLogIndex;
    /**
     * 上一个log的term 用于match
     */
    private int lastLogTerm;

}
