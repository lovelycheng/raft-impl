package tech.lovelycheng.xuande.server.follower;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tech.lovelycheng.xuande.protocol.KryoProtocol;
import tech.lovelycheng.xuande.protocol.Protocol;
import tech.lovelycheng.xuande.transfer.Packet;

/**
 * @author chengtong
 * @date 2023/2/25 21:29
 */
public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

    Protocol protocol = new KryoProtocol();

/*
    ConcurrentHashMap<Integer, follower.OperationFuture> futureConcurrentHashMap = new ConcurrentHashMap<>();

    public void addFuture(follower.OperationFuture future,Integer id){
        futureConcurrentHashMap.put(id,future);
    }
*/

    LinkedBlockingQueue<Packet> toSend = new LinkedBlockingQueue<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        System.err.println(packet.toString());
        // channelHandlerContext.writeAndFlush(packet);
        // OperationFuture future = futureConcurrentHashMap.get(packet.getBody().get);
        // future.setSuccess(packet);

    }
}
