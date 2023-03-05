package tech.lovelycheng.xuande.server;

import tech.lovelycheng.xuande.transfer.Packet;

/**
 * @author chengtong
 * @date 2023/2/28 19:30
 */
public interface Processor {

    void process(Packet.Body body);

}
