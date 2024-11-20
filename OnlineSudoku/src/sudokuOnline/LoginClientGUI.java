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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginClientGUI extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel statusLabel;

    public LoginClientGUI() {
        setTitle("Sudoku Online - 로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // 메인 패널 설정
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 아이디 입력
        JPanel userIdPanel = new JPanel(new BorderLayout());
        userIdPanel.add(new JLabel("아이디: "), BorderLayout.WEST);
        userIdField = new JTextField();
        userIdPanel.add(userIdField, BorderLayout.CENTER);
        mainPanel.add(userIdPanel);

        // 비밀번호 입력
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(new JLabel("비밀번호: "), BorderLayout.WEST);
        passwordField = new JPasswordField();
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        mainPanel.add(passwordPanel);

        // 상태 표시
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        mainPanel.add(statusLabel);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("로그인");
        signupButton = new JButton("회원가입");
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        mainPanel.add(buttonPanel);

        // 이벤트 연결
        loginButton.addActionListener(e -> attemptLogin());
        signupButton.addActionListener(e -> moveToSignup());

        // 프레임 구성
        add(mainPanel);
    }

    private void attemptLogin() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (userId.isEmpty() || password.isEmpty()) {
            statusLabel.setText("아이디와 비밀번호를 입력하세요.");
            return;
        }

        // 서버로 회원정보 전송
        boolean loginSuccess = sendToServer("login", userId, password);
        if (loginSuccess) {
            JOptionPane.showMessageDialog(this, "로그인 성공!");
            moveToMainMenu();
        } else {
            statusLabel.setText("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    private void moveToMainMenu() {
        MainMenuGUI mainMenu = new MainMenuGUI(); // MainMenuGUI 인스턴스 생성
        mainMenu.setVisible(true);
        dispose(); // 현재 창 닫기
    }

    private void moveToSignup() {
        SignupClientGUI signupScreen = new SignupClientGUI(); // SignUpClientGUI 인스턴스 생성
        JFrame signupFrame = new JFrame("회원가입");
        signupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        signupFrame.setSize(400, 300);
        signupFrame.setContentPane(signupScreen);
        signupFrame.setLocationRelativeTo(this);
        signupFrame.setVisible(true);
    }

    private boolean sendToServer(String action, String... data) {
        // 서버와의 통신 구현
        System.out.println("서버 전송: " + action + " -> " + String.join(", ", data));
        // 예제: 로그인 성공 반환
        return "testUser".equals(data[0]) && "password123!".equals(data[1]);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginClientGUI loginScreen = new LoginClientGUI();
            loginScreen.setVisible(true);
        });
    }
}
