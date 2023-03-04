package com.example.tests.raft.server.common;

import com.example.tests.raft.transfer.Packet;

/**
 * @author chengtong
 * @date 2023/2/28 19:30
 */
public interface Processor {

    void process(Packet.Body body);

}
