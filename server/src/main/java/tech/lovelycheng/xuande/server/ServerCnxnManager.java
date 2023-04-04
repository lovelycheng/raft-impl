package tech.lovelycheng.xuande.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import tech.lovelycheng.xuande.server.quorum.QuorumPeer;
import tech.lovelycheng.xuande.server.quorum.Quorums;
import tech.lovelycheng.xuande.server.quorum.transfer.RequestVote;
import tech.lovelycheng.xuande.server.quorum.transfer.VoteResponse;

/**
 * @author chengtong
 * @date 2023/3/8 21:22
 */
public class ServerCnxnManager {

    private QuorumPeer self;
    private Quorums quorums;

    private ConcurrentHashMap<Integer,Socket> socketConcurrentHashMap = new ConcurrentHashMap<>();
    private final LinkedBlockingQueue<VoteResponse> recvQueue = new LinkedBlockingQueue<>();
    private Map<Integer, Election.MsgSender> senderMap = new ConcurrentHashMap<>();
    private Map<Integer, LinkedBlockingQueue<RequestVote>> senderQueueMap = new ConcurrentHashMap<>();


    private Runnable acceptConnection = new Runnable() {
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            DataInputStream din = null;
            try{
                serverSocket = new ServerSocket();
                serverSocket.bind(serverSocket.getLocalSocketAddress());
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(false);
                socket.setSoTimeout(0);

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(serverSocket != null){
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(din!=null){
                    try {
                        din.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    class MsgSender extends Thread{
        QuorumPeer peer;
        int sid;
        MsgReceiver msgReceiver;

        public MsgSender(QuorumPeer peer) {
            super("MsgSender-"+peer.getMyid());
            this.peer = peer;
        }
    }

    class MsgReceiver extends Thread{
        QuorumPeer self;
        QuorumPeer peer;
        Socket socket;
        DataInputStream din;
        public MsgReceiver(QuorumPeer self, QuorumPeer peer,Socket socket,DataInputStream din) {
            super("MsgReceiver-"+peer.getMyid());
            this.self = self;
            this.peer = peer;
            this.socket = socket;
            this.din = din;
        }

        @Override
        public void run() {
            while (true){
                // din.read();
            }
        }


    }


}
