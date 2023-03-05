package tech.lovelycheng.xuande.server.quorum.transfer;

import lombok.Data;
import tech.lovelycheng.xuande.protocol.KryoProtocol;
import tech.lovelycheng.xuande.protocol.Protocol;

/**
 * @author chengtong
 * @date 2023/3/4 22:14
 */
@Data
public class RequestVote {

    private int term;
    private int candidateId;
    private int lastLogIndex;
    private int lastLogTerm;

    public RequestVote(int term, int candidateId, int lastLogIndex, int lastLogTerm) {
        this.term = term;
        this.candidateId = candidateId;
        this.lastLogIndex = lastLogIndex;
        this.lastLogTerm = lastLogTerm;
    }

    public RequestVote() {
    }

    public static void main(String[] args) {
        Protocol k = new KryoProtocol();
        byte[] bytes =k.serialize(new RequestVote(1,1,1,1));
        RequestVote vote = k.deserialize(RequestVote.class,bytes);

    }
}
