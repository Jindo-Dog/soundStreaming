package VoiceCall;

import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatSendWorkerThread implements Runnable {
    //    private final MulticastSocket multicastSocket;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private TargetDataLine targetDataLine;
    MulticastSocket multicastSocket;

    /*public VoiceCallSendWorkerThread(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }*/

    public ChatSendWorkerThread() {
    }

    @Override
    public void run() {
//        System.out.println("Chat Worker Send Thread Started");   // 추후 주석처리

        try {
            running.set(true);

            // 멀티캐스트 IPv6 설정
            // 네트워크 인터페이스 설정
//            NetworkInterface networkInterface = NetworkInterface.getByName("eth4");
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan2");
            // 멀티캐스트 소켓 설정
            multicastSocket = new MulticastSocket(9872);
//            multicastSocket = new MulticastSocket();
            // IPv6 주소 설정
//            InetAddress inetAddress = InetAddress.getByName("FF01:0:0:0:0:0:0:FC");
            // IPv4 주소 설정
            InetAddress inetAddress = InetAddress.getByName("239.127.127.127");
            // 소켓에 네트워크 인터페이스 지정
            multicastSocket.setNetworkInterface(networkInterface);
            // 멀티캐스트 그룹에 조인
            multicastSocket.joinGroup(inetAddress);

            String sendMessage;
            Scanner scanner = new Scanner(System.in);

            byte[] sendBuffer = new byte[10000];
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddress, 9872);

            // 접속했음을 멀티캐스트로 알림
            String hostName = InetAddress.getLocalHost().getHostName();
            sendMessage = hostName + "에서 접속했습니다.";
            sendBuffer = sendMessage.getBytes();
            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddress, 9872);
            multicastSocket.send(sendPacket);

            while (running.get()) {
                try {
                    // 멀티캐스트 송신
                    if (System.in.available() > 0) {
                        sendMessage = scanner.nextLine();
                        sendBuffer = sendMessage.getBytes();
                        sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddress, 9872);
                        multicastSocket.send(sendPacket);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            // 접속 종료를 멀티캐스트로 알림
            sendMessage = hostName + "에서 접속을 종료했습니다.";
            sendBuffer = sendMessage.getBytes();
            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddress, 9872);
            multicastSocket.send(sendPacket);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        multicastSocket.close();
//        System.out.println("Chat Worker Send Thread Stopped");   // 추후 주석처리
    }

    // 쓰레드 종료
    public void stop() {
        running.set(false);
    }
}
