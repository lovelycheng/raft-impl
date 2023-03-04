package com.example.tests.raft.server.leader.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author chengtong
 * @date 2023/2/25 19:55
 */
public class OrderFrameEncoder extends LengthFieldPrepender {

    public OrderFrameEncoder() {
        super( 2);
    }
}
