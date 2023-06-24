package VoiceCall;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Client2 {

    private void transceiver() {

        try {
            // 멀티캐스트 IPv6 설정
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan2");
            MulticastSocket multicastSocket = new MulticastSocket(9877);
            InetAddress inetAddress = InetAddress.getByName("FF01:0:0:0:0:0:0:FC");
            multicastSocket.setNetworkInterface(networkInterface);
            multicastSocket.joinGroup(inetAddress);

            // 채팅을 위한 또다른 멀티캐스트
            //

            // 쓰레드풀 생성
            ThreadPoolExecutor receiveExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            ThreadPoolExecutor sendExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

//            VoiceCallReceiveWorkerThread receiveTask = new VoiceCallReceiveWorkerThread(multicastSocket);
//            VoiceCallSendWorkerThread sendTask = new VoiceCallSendWorkerThread(multicastSocket);
            VoiceCallReceiveWorkerThread receiveTask = new VoiceCallReceiveWorkerThread();
            VoiceCallSendWorkerThread sendTask = new VoiceCallSendWorkerThread();

            receiveExecutor.execute(receiveTask);
            sendExecutor.execute(sendTask);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public Client2() {
        transceiver();
    }

    public static void main(String[] args) {
        new Client2();
    }
}
