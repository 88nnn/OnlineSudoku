package SudokuMaster;
import javax.swing.*;
import java.awt.*;

public class GameResultScreen extends JFrame {
    public GameResultScreen(String result, int myTime, int myEmptyCells) {
        setTitle("결과 화면");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        // 결과 정보 표시
        JLabel resultLabel = new JLabel("결과: " + result, SwingConstants.CENTER);
        JLabel timeLabel = new JLabel("소요 시간: " + myTime + "초", SwingConstants.CENTER);
        JLabel emptyCellsLabel = new JLabel("남은 빈 칸 수: " + myEmptyCells, SwingConstants.CENTER);

        add(resultLabel);
        add(timeLabel);
        add(emptyCellsLabel);

        // 버튼 패널 추가
        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("다시 시작");
        JButton menuButton = new JButton("메인 메뉴");

        // "다시 시작" 버튼 동작
        restartButton.addActionListener(e -> {
            new GameClientGUI().setVisible(true);
            dispose();
        });

        // "메인 메뉴" 버튼 동작
        menuButton.addActionListener(e -> {
            new.action().setVisible(true);
            dispose();
        });

        buttonPanel.add(restartButton);
        buttonPanel.add(menuButton);
        add(buttonPanel);
    }

    public static void main(String[] args) {
        // 테스트용 실행 코드
        SwingUtilities.invokeLater(() -> {
            new GameResultScreen("승리", 120, 5).setVisible(true);
        });
    }
}
