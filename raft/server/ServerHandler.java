package com.example.tests.raft.server;

import com.example.tests.raft.protocol.KryoProtocol;
import com.example.tests.raft.protocol.Protocol;
import com.example.tests.raft.transfer.OpCode;
import com.example.tests.raft.transfer.Operation;
import com.example.tests.raft.transfer.Packet;
import com.example.tests.raft.transfer.TypeCode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author chengtong
 * @date 2023/2/25 01:25
 */
public class ServerHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet o) throws Exception {
        System.err.println("read");
        System.err.println(o);

        Packet response = new Packet(99, TypeCode.REPLICATE,new Operation(OpCode.ADD,"auth","pass"),1L);
        channelHandlerContext.writeAndFlush(response);
    }


}
