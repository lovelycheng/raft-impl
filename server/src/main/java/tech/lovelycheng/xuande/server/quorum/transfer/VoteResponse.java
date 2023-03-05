package tech.lovelycheng.xuande.server.quorum.transfer;

import lombok.Data;

/**
 * @author chengtong
 * @date 2023/3/4 22:16
 */
@Data
public class VoteResponse {

    int term;
    boolean voteGranted;

}
