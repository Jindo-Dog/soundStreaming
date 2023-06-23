package VoiceCall;

import baseCode.UDPMulticastServer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;

public class Server {
    private TargetDataLine targetDataLine;

    public Server() {
        System.out.println("Audio UDP Server Started");
        setupAudio();
        broadcastAudio();
        System.out.println("Audio UDP Server Terminated");
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

    private void broadcastAudio() {
        try {
            MulticastSocket multicastSocket = new MulticastSocket();
            InetAddress inetAddress = InetAddress.getByName("228.5.6.7");
            multicastSocket.joinGroup(inetAddress);

            final byte audioBuffer[] = new byte[10000];
            byte[] data;

            while (true) {
                int count = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
                if (count > 0) {
                    /*DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, 9786);
                    multicastSocket.send(packet);*/
                    Thread.sleep(1000);
                    String message = (new Date()).toString();
                    System.out.println("Sending: [" + message + "]");
                    data = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(data, message.length(), inetAddress, 9877);
                    multicastSocket.send(packet);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new Server();
    }
}


/*
 클릭으로 통화방 종료
 접속 시 콘솔에 접속자 표시

 */
