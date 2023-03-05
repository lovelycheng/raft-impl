package tech.lovelycheng.xuande.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import tech.lovelycheng.xuande.common.config.Config;
import tech.lovelycheng.xuande.protocol.KryoProtocol;
import tech.lovelycheng.xuande.server.quorum.QuorumPeer;
import tech.lovelycheng.xuande.server.quorum.Quorums;
import tech.lovelycheng.xuande.server.quorum.transfer.RequestVote;
import tech.lovelycheng.xuande.server.quorum.transfer.VoteResponse;

/**
 * @author chengtong
 * @date 2023/3/3 23:08
 * election 使用netty 复杂度有点高，问题是netty的client端和server端是不同用的。
 * 如果server要连上4个client，那么就需要4个bootstrap。感觉不太行。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class Election extends Thread {

    private final QuorumPeer self;
    private final KryoProtocol protocol = new KryoProtocol();
    private AtomicBoolean isFinish = new AtomicBoolean(false);

    private Quorums quorums;
    RecvWorker rw;
    private final LinkedBlockingQueue<VoteResponse> recvQueue = new LinkedBlockingQueue<>();
    private Map<Integer, MsgSender> senderMap = new ConcurrentHashMap<>();
    private Map<Integer, LinkedBlockingQueue<RequestVote>> senderQueueMap = new ConcurrentHashMap<>();

    public Election(QuorumPeer self) {
        this.self = self;
        this.quorums = self.getQuorums();
        this.rw = new RecvWorker(self.getMyid(), recvQueue, self);
        for (Map.Entry<Integer, QuorumPeer> entry : quorums.getQuorum().entrySet()) {
            LinkedBlockingQueue<RequestVote> linkedBlockingQueue = new LinkedBlockingQueue<>();
            senderMap.put(entry.getKey(), new MsgSender(entry.getKey(), linkedBlockingQueue, self, entry.getValue()));
            senderQueueMap.put(entry.getKey(), linkedBlockingQueue);
        }
    }

    @Override
    public synchronized void start() {
        this.rw.start();
        for (Map.Entry<Integer, QuorumPeer> entry : quorums.getQuorum()
            .entrySet()) {
            if(entry.getKey() == self.getMyid()){
                continue;
            }
            senderMap.get(entry.getKey()).start();
            senderMap.get(entry.getKey()).queue.offer(new RequestVote(self.getTerm(),self.getMyid(),self.getCommittedIndex().get(),self.getTerm()));
        }
        super.start();
    }

    @Override
    public void run() {

    }

    /**
     * 以下是交互处理线程类
     */

    // public Packet
    class MsgSender extends Thread {
        private final int sid;
        private final LinkedBlockingQueue<RequestVote> queue;
        private CnxnManager cnxnManager;
        private final QuorumPeer self;
        private final QuorumPeer peer;
        DataOutputStream dout = null;
        DataInputStream din = null;
        Socket socket = null;

        public MsgSender(int sid, LinkedBlockingQueue<RequestVote> queue, QuorumPeer self, QuorumPeer peer) {
            this.sid = sid;
            this.queue = queue;
            this.self = self;
            this.peer = peer;
        }


        private void initConnection() {
            try {
                socket = new Socket();
                socket.connect(peer.getInetSocketAddress());
                log.info("{} connect to {} succeed",self.getMyid(),peer.getMyid());
                BufferedOutputStream buf = new BufferedOutputStream(socket.getOutputStream());
                dout = new DataOutputStream(buf);
                din = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            } catch (IOException e) {
                log.error("{} connect to {} failed",self.getMyid(),this.sid);
                e.printStackTrace();
                try {
                    socket.close();
                    dout.close();
                    din.close();
                } catch (IOException ie) {
                    log.error("Exception while closing", ie);
                }
            }
        }

        @Override
        public void run() {

            if(this.socket == null || this.socket.isClosed()){
                initConnection();
            }
            if(this.socket == null ||this.socket.isClosed()){
                log.error("sender线程 socket创建失败，连接地址:{}:{}",this.peer.getInetSocketAddress().getAddress(),this.peer.getInetSocketAddress().getPort());
                return;
            }

            while (!this.isInterrupted()) {
                try {
                    RequestVote body = queue.poll(40, TimeUnit.SECONDS);
                    if (body == null) {
                        continue;
                    }
                    dout.write(protocol.serialize(body));
                    dout.flush();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class RecvWorker extends Thread {

        private final int sid;
        private final LinkedBlockingQueue<VoteResponse> queue;
        private final QuorumPeer self;

        public RecvWorker(int sid, LinkedBlockingQueue<VoteResponse> queue, QuorumPeer self) {
            this.sid = sid;
            this.queue = queue;
            this.self = self;
        }

        @Override
        public void run() {

            ServerSocket serverSocket = null;
            InputStream inputStream = null;
            try{
                serverSocket = new ServerSocket(self.getInetSocketAddress().getPort());
                log.info("Server {} listen... ",self.getMyid());
                Socket socket = serverSocket.accept();
                log.info("socket accepted");
                socket.setSoTimeout(0);
                inputStream = socket.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                DataInputStream dis = new DataInputStream(bis);
                byte[] bytes = new byte[dis.available()];
                dis.read(bytes);
                RequestVote response = protocol.deserialize(RequestVote.class,bytes);
                log.info(response.toString());
            }catch (Exception e){
                e.printStackTrace();
                log.error("{} connect to {} failed",self.getMyid(),this.sid);
            }finally {
                try {
                    if(serverSocket != null){
                        serverSocket.close();
                    }
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            while (true) {
                VoteResponse body = null;
                try {
                    body = queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (body == null) {
                    log.info("data readed is null");
                    continue;
                }

            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        QuorumPeer quorumPeer = new QuorumPeer();
        quorumPeer.parseConfig(Objects.requireNonNull(Config.getConfig(1)));
        Election election = new Election(quorumPeer);

        election.start();
        election.join();
    }
}
