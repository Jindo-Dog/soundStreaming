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
        // 연결 전에 이름, 입력받아서 채팅에서 닉네임으로 활용
        // 쓰레드 생성 전에 접속을 멀티캐스트로 notify

        //하단 쓰레드들에 채팅 쓰레드 추가

        // 쓰레드풀 생성
        ThreadPoolExecutor receiveExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        ThreadPoolExecutor sendExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        // 쓰레드 생성
        VoiceCallReceiveWorkerThread receiveTask = new VoiceCallReceiveWorkerThread();
        VoiceCallSendWorkerThread sendTask = new VoiceCallSendWorkerThread();

        //쓰레드 실행
        receiveExecutor.execute(receiveTask);
        sendExecutor.execute(sendTask);

        // 버튼 클릭 시 쓰레드 종료(접속 종료)
        /*receiveExecutor.shutdown();
        sendExecutor.shutdown();*/

        // 쓰레드 종료 후에 접속 종료를 멀티캐스트로 notify




    }

    public Client2() {
        transceiver();
    }

    public static void main(String[] args) {
        new Client2();
    }
}
