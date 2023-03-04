package com.example.tests.raft.server.follower;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.example.tests.raft.protocol.KryoProtocol;
import com.example.tests.raft.protocol.Protocol;
import com.example.tests.raft.transfer.Packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author chengtong
 * @date 2023/2/25 21:29
 */
public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

    Protocol protocol = new KryoProtocol();

    ConcurrentHashMap<Integer,OperationFuture> futureConcurrentHashMap = new ConcurrentHashMap<>();

    public void addFuture(OperationFuture future,Integer id){
        futureConcurrentHashMap.put(id,future);
    }

    LinkedBlockingQueue<Packet> toSend = new LinkedBlockingQueue<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        System.err.println(packet.toString());
        // channelHandlerContext.writeAndFlush(packet);
        // OperationFuture future = futureConcurrentHashMap.get(packet.getBody().get);
        // future.setSuccess(packet);

    }
}
