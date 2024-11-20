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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ResultChatClientGUI extends JFrame {
    private JLabel resultLabel, rankLabel, myStatsLabel, opponentStatsLabel;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton emojiButton, sendButton, exitButton, nextGameButton;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Timer updateTimer;
    
    //BuildGUI: 초기화된 GUI와 서버 연결 생성
    public ResultChatClientGUI(String serverAddress, int serverPort) {
    	GameSessionManager session = GameSessionManager.getInstance();
    	
        super("게임 결과");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLayout(new BorderLayout());

        try {
            socket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            initializeUI();
            startUpdateTimer(); //타이머 시작. (정기적인 인게임 데이터 갱신 기준)

        } catch (IOException e) {
            //log.error("Exception [Err_Msg]: {}", e.geStxakTrace()[0]); //e.getMessage()
            //thow new RuntimeException(e);
        	e.printStackTrace();
        }
    }

    private void initializeUI() {
        // 상단: 결과 정보 패널
        JPanel resultPanel = new JPanel(new GridLayout(3, 1));
        myStatsLabel = new JLabel("내 빈칸 수: 0 | 소요 시간: 0초");
        opponentStatsLabel = new JLabel("상대 빈칸 수: 0 | 소요 시간: 0초");
        resultLabel = new JLabel("결과: 승리!", SwingConstants.CENTER);
        rankLabel = new JLabel("랭킹 점수: 0 (+0)", SwingConstants.CENTER);

        resultPanel.add(myStatsLabel);
        resultPanel.add(resultLabel);
        resultPanel.add(rankLabel);

        add(resultPanel, BorderLayout.NORTH);

        // 중앙: 채팅 패널
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        chatInput = new JTextField();
        chatInput.addActionListener(e -> sendChatMessage());

        emojiButton = new JButton("😀");
        emojiButton.addActionListener(e -> showEmojiOptions());

        sendButton = new JButton("전송");
        sendButton.addActionListener(e -> sendChatMessage());

        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.add(emojiButton, BorderLayout.WEST);
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.CENTER);

        // 하단: 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        exitButton = new JButton("나가기");
        exitButton.addActionListener(e -> exitToMainMenu());

        nextGameButton = new JButton("다음 게임");
        nextGameButton.addActionListener(e -> moveToMatchingScreen());

        buttonPanel.add(exitButton);
        buttonPanel.add(nextGameButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void startUpdateTimer() {
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGameStatus();
            }
        }, 1000, 1000); // 1초마다 갱신
    }

    private void updateGameStatus() {
        String response = in.readLine();
        try {
        	out.println("status_update");
            String response = in.readLine();
            if (response != null && response.startsWith("stats")) {
                String[] stats = response.split(" ");
                myStatsLabel.setText("내 빈칸 수: " + stats[1] + " | 소요 시간: " + stats[2] + "초");
                opponentStatsLabel.setText("상대 빈칸 수: " + stats[3] + " | 소요 시간: " + stats[4] + "초");
                if (stats[5].equals("win")) {
                    resultLabel.setText("결과: 승리!");
                    rankLabel.setText("랭킹 점수: " + stats[6] + " (+1)");
                    session.recordWin();
                	session.setRankingScore(session.getRankingScore() + 1);
                } else if (stats[5].equals("lose")) {
                    resultLabel.setText("결과: 패배!");
                    rankLabel.setText("랭킹 점수: " + stats[6] + " (+0)");
                    session.recordLoss();
                }
            } response != null && response.startsWith("chat")) {
                chatArea.append(response.substring(5) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("게임 결과 업데이트 오류: " + e.getMessage());
            System.exit(-1);
        }
    }

    private void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            out.println("chat " + message); //서버로 채팅 메시지 전송(객체화해 구현)
            chatInput.setText("");
        }
    }

    private void showEmojiOptions() {
        // 이모티콘 목록
        String[] emojis = {"😀", "😂", "😎", "😢", "😡", "🎉", "👍", "👎"};

        // 이모티콘 팝업 프레임
        JFrame emojiFrame = new JFrame("이모티콘 선택");
        emojiFrame.setSize(300, 200);
        emojiFrame.setLayout(new GridLayout(2, 4, 10, 10)); // 2행 4열, 간격 10px
        emojiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //이모지 전송 관련 기능만 관리하는 함수이므로, 모든 창을 닫아버리는 exit이 아니라 선택된 프레임 하나만 닫는 dispose 선택.

        for (String emoji : emojis) {
            JButton emojiButton = new JButton(emoji);
            emojiButton.addActionListener(e -> {
                out.println("chat " + emoji);
                emojiFrame.dispose(); // 선택 후 이모지 창만 닫기
            });
            emojiFrame.add(emojiButton);
        }

        emojiFrame.setLocationRelativeTo(this);
        emojiFrame.setVisible(true);
    }


    private void exitToMainMenu() {
    	GameSessionManager session = GameSessionManager.getInstance(); //게임 세션 매니저로 인스턴스 관리
    	session.resetSession();
    	// 메인 메뉴로 이동
    	dispose();
        updateTimer.cancel(); //화면 갱신할 필요 없어졌으니 타이머 종료
        try {
            out.println("exit");
            socket.close(); //해당 매칭과의 연결 종료
        } catch (IOException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MainMenuGUI("내 닉네임").setVisible(true)); // 닉네임 전달
        dispose();
        exit(-1);
    }


    private void connectToMatchingScreen() {
    	GameSessionManager session = GameSessionManager.getInstance();
    	// 매칭 화면으로 이동
        updateTimer.cancel(); //화면 갱신할 필요 없어졌으니 타이머 종료
        try {
            out.println("next_game");
            socket.close(); //해당 매칭과의 연결 종료
        } catch (IOException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MatchingClientGUI("내 닉네임").setVisible(true)); // 닉네임 전달
        //dispose();
        exit(-1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResultChatClientGUI("localhost", 54321).setVisible(true));
    }
}

}
