package com.example.tests.raft.server;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author chengtong
 * @date 2023/2/25 19:55
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {

    public OrderFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 2,0,2);
    }


}
