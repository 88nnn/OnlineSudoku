// --> MainMenuGUI (-->)

package sudokuOnline;
import sudokuOnline.GameSessionManager;

import javax.swing.*;
import java.awt.*;
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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class GameMenuGUI extends JFrame {
	GameSessionManager session = GameSessionManager.getInstance();
    private JLabel rankLabel;
    private JTextField nicknameField;
    private JLabel userIdLabel;
    private JPasswordField passwordField;
    private JButton nicknameChangeButton;
    private JButton passwordChangeButton;
    private JButton fontSizeButton;
    private boolean isFontLarge = false;
    private Timer rankUpdateTimer;

    private String userId = "user123";  // Example user ID
    private int rankPercentage = 0;    // Initial percentage (updated via server)
    private String nickname = "Player"; // Example nickname
	private String password = "abcdef!2";

    /**
     * 
     */
    public GameMenuGUI() {
    	setLayout(new BorderLayout());
        super("게임 설정");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 랭킹 패널
        JPanel rankPanel = new JPanel();
        rankLabel = new JLabel("랭킹 점수: 상위 " + rankPercentage + "%");
        rankPanel.add(rankLabel);

     // 사용자 정보 패널
        JPanel userInfoPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("사용자 정보"));
        
        // 닉네임
        userInfoPanel.add(new JLabel("닉네임:"));
        nicknameField = new JTextField(nickname);
        userInfoPanel.add(nicknameField);
        nicknameChangeButton = new JButton("변경");
        userInfoPanel.add(nicknameChangeButton);

     // 아이디 (수정 불가)
        userInfoPanel.add(new JLabel("아이디:"));
        userIdLabel = new JLabel(userId);
        userInfoPanel.add(userIdLabel);
        userInfoPanel.add(new JLabel()); // Placeholder

     // 비밀번호
        userInfoPanel.add(new JLabel("비밀번호:"));
        passwordField = new JPasswordField();
        userInfoPanel.add(passwordField);
        passwordChangeButton = new JButton("변경");
        userInfoPanel.add(passwordChangeButton);

        // 설정 패널
        JPanel settingsPanel = new JPanel();
        fontSizeButton = new JButton("글자 키우기");
        settingsPanel.add(fontSizeButton);

        // 기능 연결
        nicknameChangeButton.addActionListener(e -> changeNickname());
        passwordChangeButton.addActionListener(e -> changePassword());
        fontSizeButton.addActionListener(e -> toggleFontSize());

     // 랭킹 주기적 갱신
        //startRankUpdate();
        
        // 랭킹 업데이트 타이머 (서버와 통신)
        rankUpdateTimer = new Timer(5000, e -> updateRankPercentage());
        rankUpdateTimer.start();

     // 패널 추가
        add(rankPanel, BorderLayout.NORTH);
        add(userInfoPanel, BorderLayout.CENTER);
        add(settingsPanel, BorderLayout.SOUTH);

    }

    private void changeNickname() {
        String newNickname = nicknameField.getText().trim();
        if (!newNickname.isEmpty() && !newNickname.equals(nickname)) {
            nickname = newNickname;
            JOptionPane.showMessageDialog(this, "닉네임이 변경되었습니다.");
            sendToServer(nickname, newNickname);
        } else {
            JOptionPane.showMessageDialog(this, "닉네임이 변경되지 않았습니다.");
        }
    }

    private void changePassword() {
        String newPassword = new String(passwordField.getPassword());
        if (isValidPassword(newPassword)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 변경되었습니다.");
            sendToServer(password , newPassword);
        } else {
            JOptionPane.showMessageDialog(this, "비밀번호는 8~16자이며 숫자와 특수문자가 2개 이상 포함되어야 합니다.");
        }
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z0-9!@#$%^&*(),.?\":{}|<>]{8,16}$";
        return Pattern.matches(passwordPattern, password);
    }

    private void toggleFontSize() {
        isFontLarge = !isFontLarge;
        Font font = isFontLarge ? new Font("Arial", Font.BOLD, 16) : new Font("Arial", Font.PLAIN, 12);
        rankLabel.setFont(font);
        nicknameField.setFont(font);
        userIdLabel.setFont(font);
        passwordField.setFont(font);

        fontSizeButton.setText(isFontLarge ? "글자 줄이기" : "글자 키우기");
    }
    
    private void startRankUpdate() {
        rankUpdateTimer = new Timer();
        rankUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateRankPercentage();
            }
        }, 0, 5000); // 5초마다 갱신
    }

    private void updateRankPercentage() {
        // Simulate server rank update
        rankPercentage = (int) (Math.random() * 100); // Replace with server data
        rankLabel.setText("랭킹 점수: 상위 " + rankPercentage + "%");
    }

    /*
     * private void sendNicknameToServer(String nickname) {
        // 서버로 닉네임 전송 구현
        System.out.println("서버로 닉네임 전송: " + nickname);
    }

    private void sendPasswordToServer(String password) {
        // 서버로 비밀번호 전송 구현
        System.out.println("서버로 비밀번호 전송: " + password);
    }
    */
    
    private void sendToServer(String key, String value) {
        // 서버와의 통신 구현
        System.out.println("서버 전송: " + key + " = " + value);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("게임 메뉴");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new GameMenuGUI());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
