package com.example.tests.raft.transfer;

import java.io.Serializable;

import com.example.tests.raft.protocol.Serializer;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @author chengtong
 * @date 2023/2/25 15:40
 *
 * 0-4: magicNum
 * 4-8: version
 * 8-12: 序列化类型type
 * 12-16: data长度
 * 16: body
 * |  magicNum(4)  | version   |序列化类型type| data长度|
 * +-------------------------------------------------+
 * |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
 * +-------------------------------------------------+
 * |         requestId         |
 * +--------+-------------------------------------------------+----------------+
 * |00000000| ba be ca fe 11 11 00 01 aa aa bb bb 00 00 10 00 |................|
 */
@Data
public class Packet implements Serializable {

    private static final int MAGIC_NUM = 0xBABECAFE;
    private static final int VERSION = 0x11110001;
    private static final int SERIAL_TYPE = 0xABAD;

    private Serializer serializer = new Serializer();

    @Data
    public static class Header {
        private int magicNum = MAGIC_NUM;//8 bit max 32
        private int version = VERSION;
        private int serialType = SERIAL_TYPE;
        private long requestId = -1L;
        //is this needed?
        private int dataLength;

        public Header() {

        }
    }

    private Header header;
    private Body body;

    @Data
    public static class Body {
        /**
         * 索引
         */
        private Integer index;
        /**
         * REPLICATE,
         * VOTES,
         * RECONFIG,
         */
        private TypeCode type;

        /**
         * ping : ping||pong
         * election : votes
         */
        private Object data;

        /**
         * vote result
         */
        private boolean result;

    }

    public Packet() {
        this.header = new Header();
        this.body = new Body();
    }

    public Packet(Integer id, TypeCode type, Object data, long requestId) {
        this.header = new Header();
        this.header.requestId = requestId;
        this.body = new Body();
        this.body.index = id;
        this.body.type = type;
        this.body.data = data;
    }

    public Packet(Packet.Body body, long requestId) {
        this.header = new Header();
        this.header.requestId = requestId;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Packet{" + "header=" + header + ", body=" + body + '}';
    }

    public void encode(ByteBuf msg) {
        msg.writeInt(this.getHeader().magicNum);
        msg.writeInt(this.getHeader().version);
        msg.writeInt(this.getHeader().serialType);
        byte[] bytes = serializer.serialize(this.body);
        this.header.dataLength = bytes.length;
        msg.writeInt(this.getHeader().dataLength);
        msg.writeLong(this.getHeader().requestId);
        msg.writeBytes(bytes);
    }

    public void decode(ByteBuf msg) {
        this.getHeader().magicNum = msg.readInt();
        this.getHeader().version = msg.readInt();
        this.getHeader().serialType = msg.readInt();
        this.getHeader().dataLength = msg.readInt();
        this.getHeader().requestId = msg.readLong();
        byte[] data = new byte[this.getHeader().dataLength];
        msg.readBytes(data, 0, data.length);
        this.body = serializer.deserialize(data, this.body.getClass());
    }

    public static boolean verify(Packet p) {
        return p.header.getMagicNum() != MAGIC_NUM || p.header.getVersion() != VERSION
            || SERIAL_TYPE != p.header.serialType;
    }
}
