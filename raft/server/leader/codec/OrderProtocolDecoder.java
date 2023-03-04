package com.example.tests.raft.server.leader.codec;

import java.util.List;

import com.example.tests.raft.protocol.KryoProtocol;
import com.example.tests.raft.protocol.Protocol;
import com.example.tests.raft.transfer.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * @author chengtong
 * @date 2023/2/26 19:36
 */
public class OrderProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    Protocol protocol = new KryoProtocol();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Packet p = new Packet();
        p.decode(in);
        out.add(p);
    }
}
