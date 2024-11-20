package sudokuOnline;
import sudokuOnline.GameSessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;



public class MatchingClientGUI extends JFrame {
	GameSessionManager session = GameSessionManager.getInstance();

    private int serverPort = 54321; // 서버 포트 기본값
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private Thread receiveThread;

	
    private JLabel statusLabel;
    private Timer timer;
    private int dotCount = 0;
    private boolean isMatched = false;

    public MatchingClientGUI() {
        super("매칭 대기 중");
    	GameSessionManager session = GameSessionManager.getInstance();
    	String nickname = session.getNickname();
    	out.PrintWriter("start_matching " + nickname);
        
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 매칭 대기 상태 표시 라벨
        statusLabel = new JLabel("매칭 중", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(statusLabel, BorderLayout.CENTER);

        // 로딩 애니메이션 시작
        startLoadingAnimation();

        // 서버로부터 매칭 완료 상태를 가정
        simulateServerMatching();
    }

    private void startLoadingAnimation() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 로딩 상태를 주기적으로 갱신
                dotCount = (dotCount + 1) % 4; // 0, 1, 2, 3 반복
                StringBuilder dots = new StringBuilder();
                for (int i = 0; i < dotCount; i++) {
                    dots.append(".");
                }
                statusLabel.setText("매칭 중" + dots);
            }
        }, 0, 500); // 0.5초마다 실행
    }

    private void simulateServerMatching() {
        // 서버 매칭 상태 시뮬레이션 (3초 후 매칭 완료)
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                isMatched = true;
                onMatchingSuccess();
            }
        }, 3000);
    }

    private void onMatchingSuccess() {
        // 로딩 애니메이션 중지
        timer.cancel();
        statusLabel.setText("매칭 성공! 게임 화면으로 전환합니다...");

        // 3초 후 게임 화면으로 이동
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    GameClientGUI gameScreen = new GameClientGUI();
                    gameScreen.setVisible(true);
                    dispose();
                });
            }
        }, 3000); // 준비 시간 3초
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MatchingClientGUI matchingScreen = new MatchingClientGUI();
            matchingScreen.setVisible(true);
        });
    }

}
