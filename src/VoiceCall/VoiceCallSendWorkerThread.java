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

public class VoiceCallSendWorkerThread implements Runnable {
//    private final MulticastSocket multicastSocket;
    private TargetDataLine targetDataLine;

    /*public VoiceCallSendWorkerThread(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }*/

    public VoiceCallSendWorkerThread() {
    }

    @Override
    public void run() {
        System.out.println("Worker Thread Started");

        try {
            // 멀티캐스트 IPv6 설정
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan2");
            MulticastSocket multicastSocket = new MulticastSocket(9877);
            InetAddress inetAddress = InetAddress.getByName("FF01:0:0:0:0:0:0:FC");
            multicastSocket.setNetworkInterface(networkInterface);
            multicastSocket.joinGroup(inetAddress);

            byte[] audioBuffer = new byte[10000];

            setupAudio();

            while (true) {
                // 멀티캐스트 송신
                try {
                    int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);

                    if (count > 0) {
                        DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, 9877);
                        multicastSocket.send(packet);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


//        clientSocket.close();
//        System.out.println("Client Connection Terminated");

    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

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
