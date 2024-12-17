package SudokuMaster;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class MatchingScreen extends JFrame {
    public MatchingScreen() {
        setTitle("매칭 화면");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 매칭 상태 표시 레이블
        JLabel statusLabel = new JLabel("매칭 중...", SwingConstants.CENTER);
        add(statusLabel, BorderLayout.CENTER);

        // 서버에 매칭 요청을 별도의 스레드에서 처리
        new Thread(() -> {
            try {
                // 서버에 매칭 요청
                Socket socket = new Socket("localhost", 54321);

                // 매칭 성공 → 게임 화면으로 이동
                SwingUtilities.invokeLater(() -> {
                    new GameClientGUI().setVisible(true); // 게임 클라이언트 화면
                    dispose(); // 매칭 화면 종료
                });
            } catch (IOException e) {
                // 매칭 실패 시 레이블에 메시지 표시
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("매칭 실패. 다시 시도해주세요.");
                    statusLabel.setForeground(Color.RED);
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        // 매칭 화면 테스트 실행 코드
        SwingUtilities.invokeLater(() -> {
            new MatchingScreen().setVisible(true);
        });
    }
}
