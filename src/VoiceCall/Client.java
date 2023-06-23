package VoiceCall;

import baseCode.UDPMulticastClient;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class Client {

    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;

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

    public Client() {
        System.out.println("UDP Multicast Time Client Started");
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName("wlan2");
            MulticastSocket multicastSocket = new MulticastSocket(9877);
            InetAddress inetAddress = InetAddress.getByName("FF01:0:0:0:0:0:0:FC");
            multicastSocket.setNetworkInterface(networkInterface);
            multicastSocket.joinGroup(inetAddress);

            byte[] audioBuffer = new byte[10000];
            DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length);

            while (true) {
                multicastSocket.receive(packet);

                try {
                    byte audioData[]= packet.getData();
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

        System.out.println("UDP Multicast Time Client Terminated");
    }

    public static void main(String[] args) {
        new Client();
    }
}
