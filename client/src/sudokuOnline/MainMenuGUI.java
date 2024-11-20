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

public class MainMenuGUI extends JFrame {
	GameSessionManager session = GameSessionManager.getInstance();
	
    private JComboBox<String> levelComboBox; // 난이도 선택 콤보박스   
    private JTextField t_input, t_userID;
    private JTextArea t_display;
    private JButton b_matchStart, b_setting, b_connect, b_disconnect, b_send, b_exit;
    private String nickname, serverAddress = "localhost"; // 사용자의 닉네임, 서버 주소 기본값
    
 // 서버 연결 관련 필드
    private int serverPort = 54321; // 서버 포트 기본값
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private Thread receiveThread;

    /**
     * 
     */
    public MainMenuGUI() {
        this.nickname = nickname;
    	GameSessionManager session = GameSessionManager.getInstance();
        //String nickname = session.getNickname();
        
        super("메인 메뉴");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 위치
        setLayout(new BorderLayout());
        
     // GUI 컴포넌트 초기화
        buildGUI();
        
     // 서버와 연결 시도
        connectToServer();
    }
    
 // GUI 컴포넌트 초기화 메서드
    private void buildGUI() {
        //int rankingScore = session.getRankingScore();
        //labelUsername.setText(nickname + "님, 환영합니다!");
        //labelRanking.setText("현재 랭킹 점수: " + rankingScore);

        // 환영메시지
        JLabel welcomeLabel = new JLabel("<html>" + nickname + "님<br>환영합니다!</html>", SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        // 난이도 선택
        JPanel levelPanel = new JPanel(new FlowLayout());
        JLabel levelLabel = new JLabel("난이도:");
        levelComboBox = new JComboBox<>(new String[]{"쉬움", "어려움"});
        levelPanel.add(levelLabel);
        levelPanel.add(levelComboBox);
        add(levelPanel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel b_Panel = new JPanel(new FlowLayout());

     // 설정 버튼
        b_setting = new JButton("⚙️ 설정");
        b_setting.addActionListener(e -> openSettings());
        b_Panel.add(b_setting);

        // 매칭 시작 버튼
        b_matchStart = new JButton("매칭 시작");
        b_matchStart.addActionListener(e -> matchingStart());
        b_Panel.add(b_matchStart);

        add(b_Panel, BorderLayout.SOUTH);
    }

    
 // 서버 연결 메서드
    private void connectToServer() {
        try {
            // 서버 소켓 연결 생성
            socket = new Socket(serverAddress, serverPort);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            System.out.println("서버에 연결되었습니다.");
        } catch (IOException e) {
            // 서버 연결 실패 시 예외 처리 및 메시지 표시
        	System.err.println("서버 연결 실패: " + e.getMessage());
            //JOptionPane.showMessageDialog(this, "서버 연결 실패: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
 // 서버 연결 종료 메서드
    private void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("서버 연결이 종료되었습니다.");
            }
        } catch (IOException e) {
            // 소켓 종료 실패 시 예외 처리
            System.err.println("서버 연결 종료 중 오류 발생: " + e.getMessage());
        }
    }
    
    // 설정 창 열기 메서드
    private void openSettings() {
        // GameMenuGUI로 전환
        GameMenuGUI gameMenu = new GameMenuGUI();
        gameMenu.setVisible(true);
        disconnect(); // 설정 화면으로 이동할 때 서버 연결 종료
        this.dispose();
    }

 // 매칭 시작 메서드
    private void matchingStart() {
        // 사용자가 선택한 난이도 가져오기
        String selectedLevel = (String) levelComboBox.getSelectedItem(); // 사용자가 선택한 난이도 가져오기

        // 서버로 닉네임과 난이도 데이터 전송
        try {
            if (socket != null && socket.isConnected()) {
                out.write("닉네임:" + nickname + ", 난이도:" + selectedLevel + "\n");
                out.flush();
                System.out.println("매칭 요청 전송: \n" + nickname + ", 난이도(" + selectedLevel + ")");
            } else {
            	System.err.println("서버 연결 실패: " + e.getMessage());
                //JOptionPane.showMessageDialog(this, "서버와 연결되어 있지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (IOException e) {
        	System.err.println("서버 전송 중 오류 발생: " + e.getMessage());
            //JOptionPane.showMessageDialog(this, "서버 전송 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // MatchingClientGUI로 전환
        MatchingClientGUI matchingClient = new MatchingClientGUI();
        matchingClient.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenuGUI mainMenu = new MainMenuGUI();
            mainMenu.setVisible(true);
        });
    }
}
