package com.example.tests.raft.server.follower;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import com.example.tests.raft.server.follower.codec.OrderFrameDecoder;
import com.example.tests.raft.server.follower.codec.OrderFrameEncoder;
import com.example.tests.raft.server.follower.codec.OrderProtocolDecoder;
import com.example.tests.raft.server.follower.codec.OrderProtocolEncoder;
import com.example.tests.raft.protocol.KryoProtocol;
import com.example.tests.raft.protocol.Protocol;
import com.example.tests.raft.transfer.Packet;
import com.example.tests.raft.transfer.TypeCode;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author chengtong
 * @date 2023/2/25 01:18
 */
public class RaftTestClient {

    private ConcurrentHashMap<String, Integer> votes = new ConcurrentHashMap<>();
    private final static Protocol protocol = new KryoProtocol();

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        ClientHandler clientHandler = new ClientHandler();

        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //添加客户端通道的处理器
                ch.pipeline()
                    .addLast(new LoggingHandler(LogLevel.INFO))
                    .addLast(new OrderFrameDecoder())
                    .addLast(new OrderFrameEncoder())
                    .addLast(new OrderProtocolDecoder())
                    .addLast(new OrderProtocolEncoder())
                    .addLast(clientHandler);
            }
        };

        try {
            //创建bootstrap对象，配置参数
            Bootstrap bootstrap = new Bootstrap();
            //设置线程组
            bootstrap.group(eventExecutors)
                //设置客户端的通道实现类型
                .channel(NioSocketChannel.class)
                //使用匿名内部类初始化通道
                .handler(channelInitializer);
            //连接服务端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6699)
                .sync();
            channelFuture.channel().writeAndFlush(new Packet(1, TypeCode.REPLICATE,new Object())).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    System.err.println("SEND OK!!");
                }
            });
            //对通道关闭进行监听
            channelFuture.channel()
                .closeFuture()
                .sync();
        } finally {
            //关闭线程组
            eventExecutors.shutdownGracefully();
        }
    }

}
