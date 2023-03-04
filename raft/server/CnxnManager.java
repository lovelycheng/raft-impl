package com.example.tests.raft.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.example.tests.raft.config.Config;
import com.example.tests.raft.server.follower.codec.OrderFrameDecoder;
import com.example.tests.raft.server.follower.codec.OrderFrameEncoder;
import com.example.tests.raft.server.follower.codec.OrderProtocolDecoder;
import com.example.tests.raft.server.follower.codec.OrderProtocolEncoder;
import com.example.tests.raft.server.leader.codec.OrderRpcDecoder;
import com.example.tests.raft.server.leader.codec.OrderRpcEncoder;
import com.example.tests.raft.transfer.Packet;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultEventExecutor;
import lombok.extern.java.Log;

/**
 * @author chengtong
 * @date 2023/2/27 00:05
 * 管理所有的channel，在这层封装channel的读写接口；
 */
@Log
public class CnxnManager {
    /**
     * 用于存放所有active的channel
     */
    private final ChannelGroup allChannels = new DefaultChannelGroup("serverCnxns", new DefaultEventExecutor());
    private ConcurrentHashMap<Integer,ServerCnxn> cnxnMap = new ConcurrentHashMap<>();

    private final AttributeKey<ServerCnxn> channelKey = AttributeKey.valueOf("ChannelKey");
    /**
     * sid-queue pair
     */
    private Map<Integer, LinkedBlockingQueue<Packet.Body>> queueMap = new ConcurrentHashMap<>();

    private ServerBootstrap serverBootstrap;
    private InetSocketAddress localAddress;

    /*
    * 本服务器的channel
    * */
    private Channel parentChannel;
    private Config config;

    /**
     * 需要一个处理器，处理读取和写入、在channel active时加入 allChannels；inactive 移除；
     * 使用默认的双工通信的处理器
     */
    @ChannelHandler.Sharable
    class Handler extends ChannelDuplexHandler {

        /**
         * 我希望通过cnxnmanager管理所有的channel，所以在channel就绪的时候注册这个channel；
         * 在这个处理器做出站操作时，能够直接获取channel
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("channel active");
            final Channel channel = ctx.channel();

            InetAddress inetAddress = ((InetSocketAddress) channel.remoteAddress()).getAddress();
            log.info(inetAddress.getHostAddress() + " connected");
            allChannels.add(channel);
            ServerCnxn cnxn = new ServerCnxn();
            //channel CNXN 的绑定关系
            channel.attr(channelKey)
                .set(cnxn);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.info("channel inactive");
            final Channel channel = ctx.channel();
            allChannels.remove(channel);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                System.err.println("read");
                System.err.println(msg);

                ctx.fireChannelRead(msg);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);
            ctx.fireChannelReadComplete();
        }
    }

    public void start(){
        //对关闭通道进行监听
        this.parentChannel = this.serverBootstrap.bind(config.getLocalAddr().getPort()).channel();
        this.localAddress = (InetSocketAddress)parentChannel.localAddress();
    }

    public CnxnManager(Config config) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //创建服务端的启动对象，设置参数
        this.serverBootstrap = new ServerBootstrap();
        //设置两个线程组boosGroup和workerGroup
        serverBootstrap.group(bossGroup, workerGroup)
            .handler(new LoggingHandler(LogLevel.INFO))
            //设置服务端通道实现类型
            .channel(NioServerSocketChannel.class)
            //设置线程队列得到连接个数
            .option(ChannelOption.SO_BACKLOG, 128)
            //设置保持活动连接状态
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            //使用匿名内部类的形式初始化通道对象
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    //给pipeline管道设置处理器
                    socketChannel.pipeline()
                        .addLast(new OrderFrameDecoder())
                        .addLast(new OrderFrameEncoder())
                        .addLast(new OrderProtocolDecoder())
                        .addLast(new OrderProtocolEncoder())
                        .addLast(new OrderRpcEncoder())
                        .addLast(new OrderRpcDecoder())
                        .addLast(new ServerHandler());
                }
            });//给workerGroup的EventLoop对应的管道设置处理器
        //绑定端口号，启动服务端
        this.config = config;
    }



}
