package tech.lovelycheng.xuande.server.follower.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import tech.lovelycheng.xuande.protocol.KryoProtocol;
import tech.lovelycheng.xuande.protocol.Protocol;
import tech.lovelycheng.xuande.transfer.Packet;

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
