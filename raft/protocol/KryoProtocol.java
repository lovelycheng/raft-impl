package com.example.tests.raft.protocol;

import com.esotericsoftware.kryo.Kryo;

/**
 * @author chengtong
 * @date 2023/2/25 04:47
 */
public class KryoProtocol implements Protocol{

    Serializer serializer = new Serializer();

    @Override
    public byte[] serialize(Object object) {
        return serializer.serialize(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return serializer.deserialize(bytes,clazz);
    }
}
