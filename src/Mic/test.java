package Mic;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class test {
    public static void main(String[] args) throws Exception {
        MicrophoneClient mr = new MicrophoneClient(new AudioFormat(16000, 16, 1, true, false));
        mr.start();
        Thread.sleep(10 * 1000);
        mr.stop();
        Thread.sleep(1000);

//        //save
//        WaveData wd = new WaveData();
//        Thread.sleep(3000);
//        wd.saveToFile("~tmp", AudioFileFormat.Type.WAVE, mr.getAudioInputStream());
        File file = new File("test.wav");
        AudioSystem.write(mr.getAudioInputStream(), AudioFileFormat.Type.WAVE, file);
    }
}
