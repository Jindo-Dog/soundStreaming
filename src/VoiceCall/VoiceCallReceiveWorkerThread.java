package VoiceCall;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceCallReceiveWorkerThread implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;
    MulticastSocket multicastSocket;
    public VoiceCallReceiveWorkerThread() {
    }

    @Override
    public void run() {
//        System.out.println("VoiceCall Receive Worker Thread Started");   // 추후 주석처리

        try {
            running.set(true);

            // 멀티캐스트 IPv6 설정
            // 네트워크 인터페이스 설정
//            NetworkInterface networkInterface = NetworkInterface.getByName("eth4");
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan2");
            // 멀티캐스트 소켓 설정
            multicastSocket = new MulticastSocket(9871);
            // IPv6 주소 설정
//            InetAddress inetAddress = InetAddress.getByName("FF01:0:0:0:0:0:0:FC");
            // IPv4 주소 설정
            InetAddress inetAddress = InetAddress.getByName("239.127.127.127");
            // 소켓에 네트워크 인터페이스 지정
            multicastSocket.setNetworkInterface(networkInterface);
            // 멀티캐스트 그룹에 조인
            multicastSocket.joinGroup(inetAddress);

            // 패킷 설정
            byte[] audioBuffer = new byte[10000];
            DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, 9871);

            while (running.get()) {
                // 멀티캐스트 수신
                multicastSocket.receive(packet);
                try {
                    byte audioData[] = packet.getData();

                    // 본인 패킷 드랍
//                    if (!Objects.equals(packet.getAddress().toString(), "/" + InetAddress.getLocalHost().getHostAddress())) {
                        InputStream byteInputStream = new ByteArrayInputStream(audioData);
                        AudioFormat audioFormat = getAudioFormat();
                        audioInputStream = new AudioInputStream(byteInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());
                        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                        sourceDataLine.open(audioFormat);
                        sourceDataLine.start();
                        playAudio();
//                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        multicastSocket.close();
//        System.out.println("VoiceCall Receive Worker Thread Stopped");   // 추후 주석처리
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

    // 음성 재생
    private void playAudio() {
        byte[] buffer = new byte[10000];
        try {
            int count;
            while ((count = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                if (count > 0) {
                    sourceDataLine.write(buffer, 0, count);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

}
