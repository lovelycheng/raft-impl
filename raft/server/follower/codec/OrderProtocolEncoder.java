package com.example.tests.raft.server.follower.codec;

import java.util.List;

import com.example.tests.raft.protocol.KryoProtocol;
import com.example.tests.raft.protocol.Protocol;
import com.example.tests.raft.transfer.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * @author chengtong
 * @date 2023/2/26 19:36
 */
public class OrderProtocolEncoder extends MessageToMessageEncoder<Packet> {

    Protocol protocol = new KryoProtocol();

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out) throws Exception {
        System.err.println("OrderProtocolEncoder");
        ByteBuf buffer = ctx.alloc().buffer();
        packet.encode(buffer);
        out.add(buffer);
    }
}
