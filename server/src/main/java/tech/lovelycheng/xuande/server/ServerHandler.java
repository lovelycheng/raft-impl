package tech.lovelycheng.xuande.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tech.lovelycheng.xuande.transfer.OpCode;
import tech.lovelycheng.xuande.transfer.Operation;
import tech.lovelycheng.xuande.transfer.Packet;
import tech.lovelycheng.xuande.transfer.TypeCode;

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
