package VoiceCall;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatReceiveWorkerThread implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    MulticastSocket multicastSocket;

    public ChatReceiveWorkerThread() {
    }

    @Override
    public void run() {
//        System.out.println("Chat Worker Receive Thread Started");   // 추후 주석처리

        try {
            running.set(true);

            // 멀티캐스트 IPv6 설정
            // 네트워크 인터페이스 설정
//            NetworkInterface networkInterface = NetworkInterface.getByName("eth4");
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan2");
            // 멀티캐스트 소켓 설정
            multicastSocket = new MulticastSocket(9872);
            // IPv6 주소 설정
//            InetAddress inetAddress = InetAddress.getByName("FF01:0:0:0:0:0:0:FC");
            // IPv4 주소 설정
            InetAddress inetAddress = InetAddress.getByName("239.127.127.127");
            // 소켓에 네트워크 인터페이스 지정
            multicastSocket.setNetworkInterface(networkInterface);
            // 멀티캐스트 그룹에 조인
            multicastSocket.joinGroup(inetAddress);

            byte[] receiveBuffer = new byte[10000];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length, inetAddress, 9872);

            while (running.get()) {
                try {
                    // 멀티캐스트 수신
                    multicastSocket.receive(receivePacket);
                    String receiveMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    // 본인 패킷 드랍
//                    if (!Objects.equals(receivePacket.getAddress().toString(), "/" + InetAddress.getLocalHost().getHostAddress())) {
                        System.out.println("Message from: " + receivePacket.getAddress() + "\nMessage: " + receiveMessage);
//                    }

                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        multicastSocket.close();
//        System.out.println("Chat Worker Receive Thread Stopped");   // 추후 주석처리
    }

    // 쓰레드 종료
    public void stop() {
        running.set(false);
    }
}
