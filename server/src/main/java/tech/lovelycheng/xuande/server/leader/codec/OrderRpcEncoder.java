package tech.lovelycheng.xuande.server.leader.codec;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.java.Log;
import tech.lovelycheng.xuande.transfer.Packet;

/**
 * @author chengtong
 * @date 2023/2/28 21:32
 */
@Log
public class OrderRpcEncoder extends MessageToMessageDecoder<Packet.Body> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Packet.Body body, List<Object> out) throws Exception {
        Packet packet = new Packet();
        packet.setBody(body);
        out.add(packet);
    }
}
