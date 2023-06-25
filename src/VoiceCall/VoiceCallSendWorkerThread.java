package VoiceCall;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.text.NumberFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceCallSendWorkerThread implements Runnable {
    //    private final MulticastSocket multicastSocket;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private TargetDataLine targetDataLine;
    MulticastSocket multicastSocket;

    /*public VoiceCallSendWorkerThread(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }*/

    public VoiceCallSendWorkerThread() {
    }

    @Override
    public void run() {
//        System.out.println("VoiceCall Send Worker Thread Started");   // 추후 주석처리

        try {
            running.set(true);

            // 멀티캐스트 IPv6 설정
            // 네트워크 인터페이스 설정
            NetworkInterface networkInterface = NetworkInterface.getByName("eth4");
//            NetworkInterface networkInterface = NetworkInterface.getByName("wlan2");
            // 멀티캐스트 소켓 설정
            multicastSocket = new MulticastSocket(9871);
//            multicastSocket = new MulticastSocket();
            // IPv6 주소 설정
            InetAddress inetAddress = InetAddress.getByName("FF01:0:0:0:0:0:0:FC");
            // 소켓에 네트워크 인터페이스 지정
            multicastSocket.setNetworkInterface(networkInterface);
            // 멀티캐스트 그룹에 조인
            multicastSocket.joinGroup(inetAddress);

            byte[] audioBuffer = new byte[10000];

            setupAudio();

            while (running.get()) {
                // 멀티캐스트 송신
                try {
                    int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);

                    if (count > 0) {
                        DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, 9871);
                        multicastSocket.send(packet);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        multicastSocket.close();
//        System.out.println("VoiceCall Send Worker Thread Stopped");   // 추후 주석처리
    }

    // 쓰레드 종료
    public void stop() {
        running.set(false);
    }

    // 오디오 형식 지정
    private AudioFormat getAudioFormat() {
        float sampleRate = 16000F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    // 음성 입력
    private void setupAudio() {
        try {
            AudioFormat audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
}
