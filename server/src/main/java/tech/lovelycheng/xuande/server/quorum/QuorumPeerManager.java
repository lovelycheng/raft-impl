package tech.lovelycheng.xuande.server.quorum;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;



import io.netty.util.internal.ConcurrentSet;
import lombok.Data;
import lombok.extern.java.Log;
import tech.lovelycheng.xuande.server.RaftServer;
import tech.lovelycheng.xuande.transfer.Packet;

/**
 * @author chengtong
 * @date 2023/2/27 20:00
 */
@Log
@Data
public class QuorumPeerManager {

    private final RaftServer server;

    private QuorumPeer self;

    private ConcurrentSet<QuorumPeer> quorumPeers = new ConcurrentSet<>();

    public QuorumPeerManager(RaftServer server) {
        this.server = server;
    }

    public LinkedBlockingQueue<Packet> recvQueue = new LinkedBlockingQueue<>();

    /**
     * sid-queue pair
     */
    private Map<Integer, LinkedBlockingQueue<Packet.Body>> queueMap = new ConcurrentHashMap<>();




}
