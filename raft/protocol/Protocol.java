package com.example.tests.raft.protocol;

/**
 * @author chengtong
 * @date 2023/2/25 04:34
 */
public interface Protocol {

    /**
     * 序列化
     *
     * @param object
     * @return
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     *
     * @param clazz target type
     * @param bytes data
     * @return
     */
    <T> T deserialize(Class<T> clazz,byte[] bytes);

}
