package com.example.tests.raft.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author chengtong
 * @date 2023/2/25 05:00
 */
public class Serializer {

    ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<>();

    public byte[] serialize(Object o){
        Kryo kryo = kryoThreadLocal.get();
        if(kryo == null){
            kryo = new Kryo();
            kryoThreadLocal.set(kryo);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output,o);
        return output.getBuffer();
    }

    public <T> T deserialize(byte[] data,Class<T> c){
        Kryo kryo = kryoThreadLocal.get();
        if(kryo == null){
            kryo = new Kryo();
            kryoThreadLocal.set(kryo);
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input output = new Input(byteArrayInputStream);
        return kryo.readObject(output,c);
    }

}
