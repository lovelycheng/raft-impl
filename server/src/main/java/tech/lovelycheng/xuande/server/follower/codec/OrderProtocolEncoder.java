package tech.lovelycheng.xuande.server.follower.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import tech.lovelycheng.xuande.protocol.KryoProtocol;
import tech.lovelycheng.xuande.protocol.Protocol;
import tech.lovelycheng.xuande.transfer.Packet;

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
