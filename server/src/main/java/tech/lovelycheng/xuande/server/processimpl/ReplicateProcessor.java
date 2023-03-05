package tech.lovelycheng.xuande.server.processimpl;

import tech.lovelycheng.xuande.server.Processor;
import tech.lovelycheng.xuande.transfer.Packet;
import tech.lovelycheng.xuande.transfer.TypeCode;

/**
 * @author chengtong
 * @date 2023/2/28 19:31
 */
public class ReplicateProcessor implements Processor {

    Processor next;

    @Override
    public void process(Packet.Body body) {
        if(!body.getType().equals(TypeCode.REPLICATE)){
            next.process(body);
        }
    }
}
