package tech.lovelycheng.xuande.common.config;


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

    private static InetSocketAddress address1 = new InetSocketAddress("localhost",6970);
    private static InetSocketAddress address2 = new InetSocketAddress("localhost",6971);
    private static InetSocketAddress address3 = new InetSocketAddress("localhost",6972);

    private static Config config1 = new Config(1);
    private static Config config2 = new Config(2);
    private static Config config3 = new Config(3);



    private Config(int index){
        if(index == 1){
            this.sid =1;
            this.localAddr = address1;
        }else if(index == 2){
            this.sid =2;
            this.localAddr = address2;
        }else{
            this.sid =3;
            this.localAddr = address3;
        }
    }

    public static Config getConfig(int index) {
        switch (index){
            case 1:
                config1.getOthers().add(config2);
                config1.getOthers().add(config3);
                return config1;
            case 2:
                config2.getOthers().add(config1);
                config2.getOthers().add(config3);
                return config2;
            case 3:
                config3.getOthers().add(config1);
                config3.getOthers().add(config2);
                return config3;
            default:
                return null;
        }

    }



}
