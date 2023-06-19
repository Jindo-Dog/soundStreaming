package Mic;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class RecordTest {
    public static void main(String[] args) {

        try {
//            44100
//            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 1, 2, 8000, false);

            DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            if (!AudioSystem.isLineSupported(dataInfo)) {
                System.out.println("Not Supported");
            }

            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(dataInfo);
            System.out.println(targetLine.getFormat());
            targetLine.open();

            JOptionPane.showMessageDialog(null, "Hit ok to start recording");
            targetLine.start();

            Thread audioRecorderThread = new Thread() {
                @Override
                public void run() {
                    AudioInputStream recordingStream = new AudioInputStream(targetLine);
                    File outputFile = new File("record.wav");
                    try {
                        AudioSystem.write(recordingStream, AudioFileFormat.Type.WAVE, outputFile);
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }

                    System.out.println("Stopped recording");
                }
            };

            audioRecorderThread.start();
            JOptionPane.showMessageDialog(null, "Hit ok to stop recording");
            targetLine.stop();
            targetLine.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
