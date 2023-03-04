package com.example.tests.raft.config;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author chengtong
 * @date 2023/2/28 18:22
 */
@Data
public class Config {
    private int sid;
    private InetSocketAddress localAddr;
    private List<Config> others = new ArrayList<>();

    private static Config config1 = new Config(1);
    private static Config config2 = new Config(2);
    private static Config config3 = new Config(3);

    private static InetSocketAddress address1 = InetSocketAddress.createUnresolved("127.0.0.1",6970);
    private static InetSocketAddress address2 = InetSocketAddress.createUnresolved("127.0.0.1",6971);
    private static InetSocketAddress address3 = InetSocketAddress.createUnresolved("127.0.0.1",6972);


    private Config(int index){
        if(index == 1){
            this.sid =1;
            this.localAddr = address1;
            others.add(config2);
            others.add(config3);
        }else if(index == 2){
            this.sid =2;
            this.localAddr = address2;
            others.add(config1);
            others.add(config3);
        }else{
            this.sid =3;
            this.localAddr = address3;
            others.add(config2);
            others.add(config1);
        }
    }

    public static Config getConfig(int index) throws Exception {
        switch (index){
            case 1:
                return config1;
            case 2:
                return config2;
            case 3:
                return config3;
            default:
                return null;
        }

    }



}
