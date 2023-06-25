package VoiceCall;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Client2 {

    private void transceiver() {

        // 채팅을 위한 또다른 멀티캐스트
        //
        // 연결 전에 이름, 입력받아서 채팅에서 닉네임으로 활용
        // 쓰레드 생성 전에 접속을 멀티캐스트로 notify

        // 접속 여부를 묻는 팝업
        if (JOptionPane.showConfirmDialog(null, "접속하시겠습니까?", "ALERT", JOptionPane.YES_NO_OPTION) == 0) {

            // 쓰레드풀 생성
            ThreadPoolExecutor receiveExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            ThreadPoolExecutor sendExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            ThreadPoolExecutor chatReceiveExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            ThreadPoolExecutor chatSendExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

            // 쓰레드 생성
            VoiceCallReceiveWorkerThread receiveTask = new VoiceCallReceiveWorkerThread();
            VoiceCallSendWorkerThread sendTask = new VoiceCallSendWorkerThread();
            ChatWorkerReceiveThread chatReceiveTask = new ChatWorkerReceiveThread();
            ChatWorkerSendThread chatSendTask = new ChatWorkerSendThread();

            //쓰레드 실행
            receiveExecutor.execute(receiveTask);
            sendExecutor.execute(sendTask);
            chatReceiveExecutor.execute(chatReceiveTask);
            chatSendExecutor.execute(chatSendTask);

            // 버튼 클릭 시 쓰레드 종료(접속 종료)
            int confirm = JOptionPane.showConfirmDialog(null, "접속을 종료하시겠습니까?", "ALERT", JOptionPane.DEFAULT_OPTION);
            if (confirm == 0 || confirm == -1) {
                // while문 정지
                receiveTask.stop();
                sendTask.stop();
                chatReceiveTask.stop();
                chatSendTask.stop();

                // 쓰레드 정지
                receiveExecutor.shutdown();
                sendExecutor.shutdown();
                chatReceiveExecutor.shutdown();
                chatSendExecutor.shutdown();
            }

        } else {
            JOptionPane.showMessageDialog(null, "접속이 종료되었습니다", "ALERT", JOptionPane.WARNING_MESSAGE);
        }


    }

    public Client2() {
        transceiver();
    }

    public static void main(String[] args) {
        new Client2();
    }
}
