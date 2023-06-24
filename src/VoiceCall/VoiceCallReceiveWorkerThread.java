package VoiceCall;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.text.NumberFormat;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceCallReceiveWorkerThread implements Runnable {

//    private final MulticastSocket clientSocket;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;

    /*public VoiceCallReceiveWorkerThread(MulticastSocket clientSocket) {
        this.clientSocket = clientSocket;
    }*/
    public VoiceCallReceiveWorkerThread() {
    }

    @Override
    public void run() {
        System.out.println("Worker Thread Started");

        try {
            // 멀티캐스트 IPv6 설정
            // 네트워크 인터페이스 설정
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan2");
            // 멀티캐스트 소켓 설정
            MulticastSocket multicastSocket = new MulticastSocket(9877);
            // IPv6 주소 설정
            InetAddress inetAddress = InetAddress.getByName("FF01:0:0:0:0:0:0:FC");
            // 소켓에 네트워크 인터페이스 지정
            multicastSocket.setNetworkInterface(networkInterface);
            // 멀티캐스트 그룹에 조인
            multicastSocket.joinGroup(inetAddress);

            // 패킷 설정
            byte[] audioBuffer = new byte[10000];
            DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length);

            while (true) {
                // 멀티캐스트 수신
                multicastSocket.receive(packet);
                try {
                    byte audioData[] = packet.getData();
                    InputStream byteInputStream = new ByteArrayInputStream(audioData);
                    AudioFormat audioFormat = getAudioFormat();
                    audioInputStream = new AudioInputStream(byteInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());
                    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                    sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    sourceDataLine.open(audioFormat);
                    sourceDataLine.start();
                    playAudio();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
