package VoiceCall;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Client2 {

    private void transceiver() {

        // 채팅을 위한 또다른 멀티캐스트
        //

        // 쓰레드풀 생성
        ThreadPoolExecutor receiveExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor sendExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        // 쓰레드 생성
        VoiceCallReceiveWorkerThread receiveTask = new VoiceCallReceiveWorkerThread();
        VoiceCallSendWorkerThread sendTask = new VoiceCallSendWorkerThread();

        //쓰레드 실행
        receiveExecutor.execute(receiveTask);
        sendExecutor.execute(sendTask);

    }

    public Client2() {
        transceiver();
    }

    public static void main(String[] args) {
        new Client2();
    }
}
