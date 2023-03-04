package com.example.tests.raft.server.common.processimpl;

import com.example.tests.raft.server.common.Processor;
import com.example.tests.raft.transfer.Packet;
import com.example.tests.raft.transfer.TypeCode;

/**
 * @author chengtong
 * @date 2023/2/28 19:31
 */
public class HeartbeatProcessor implements Processor {

    Processor next;

    @Override
    public void process(Packet.Body body) {
        if (!body.getType().equals(TypeCode.HEARTBEAT)){
            next.process(body);
        }
    }

}
