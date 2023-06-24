package VoiceCall;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Client2 {

    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;
    private TargetDataLine targetDataLine;

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

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

    private void transceiver() {

        try {
            // IPv6 설정
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan2");
            MulticastSocket multicastSocket = new MulticastSocket(9877);
            InetAddress inetAddress = InetAddress.getByName("FF01:0:0:0:0:0:0:FC");
            multicastSocket.setNetworkInterface(networkInterface);
            multicastSocket.joinGroup(inetAddress);

            // 패킷 설정
            byte[] audioBuffer = new byte[10000];
            DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length);

            while (true) {
                // 송수신 쓰레드 분리 필요

                // 멀티캐스트 송신
                try {
                    int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
                    if (count > 0) {
                        multicastSocket.send(packet);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    System.exit(0);
                }

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
                    System.exit(0);
                }

                /*String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Message from: " + packet.getAddress() + " Message: [" + message + "]");*/
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public Client2() {
        setupAudio();
        transceiver();
    }

    public static void main(String[] args) {
        new Client2();
    }
}
