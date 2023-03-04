package com.example.tests.raft.server.follower.codec;

import java.util.List;

import com.example.tests.raft.transfer.Packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.java.Log;

/**
 * @author chengtong
 * @date 2023/2/28 21:32
 */
@Log
public class OrderRpcDecoder extends MessageToMessageDecoder<Packet> {
    @Override
    protected void decode(ChannelHandlerContext ctx, Packet packet, List<Object> out) throws Exception {
        if(Packet.verify(packet)){
            log.warning("packet verify denied");
        }else {
            Packet.Body body = packet.getBody();
            out.add(body);
        }

    }
}
