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

public class SignupClientGUI extends JFrame {
    private JTextField userIdField;
    private JTextField nicknameField;
    private JPasswordField passwordField;
    private JButton userIdCheckButton;
    private JButton nicknameCheckButton;
    private JButton signupButton;
    private JLabel userIdStatusLabel;
    private JLabel nicknameStatusLabel;
    private JLabel passwordStatusLabel;

    private boolean isUserIdValid = false;
    private boolean isNicknameValid = false;

    public SignupClientGUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));

        // 메인 패널
        JPanel mainPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder("Sudoku Online - 회원가입"));

        // 아이디 입력
        mainPanel.add(new JLabel("아이디:"));
        userIdField = new JTextField();
        mainPanel.add(userIdField);
        userIdCheckButton = new JButton("중복 확인");
        userIdCheckButton.setEnabled(false);
        mainPanel.add(userIdCheckButton);
        userIdStatusLabel = new JLabel(" ");
        mainPanel.add(userIdStatusLabel);

        // 닉네임 입력
        mainPanel.add(new JLabel("닉네임:"));
        nicknameField = new JTextField();
        nicknameField.setEnabled(false);
        mainPanel.add(nicknameField);
        nicknameCheckButton = new JButton("중복 확인");
        nicknameCheckButton.setEnabled(false);
        mainPanel.add(nicknameCheckButton);
        nicknameStatusLabel = new JLabel(" ");
        mainPanel.add(nicknameStatusLabel);

        // 비밀번호 입력
        mainPanel.add(new JLabel("비밀번호:"));
        passwordField = new JPasswordField();
        passwordField.setEnabled(false);
        mainPanel.add(passwordField);
        signupButton = new JButton("회원가입");
        signupButton.setEnabled(false);
        mainPanel.add(signupButton);
        passwordStatusLabel = new JLabel(" ");
        mainPanel.add(passwordStatusLabel);

        // 이벤트 연결
        userIdField.getDocument().addDocumentListener((SimpleDocumentListener) e -> validateUserId());
        nicknameField.getDocument().addDocumentListener((SimpleDocumentListener) e -> validateNickname());
        passwordField.getDocument().addDocumentListener((SimpleDocumentListener) e -> validatePassword());
        userIdCheckButton.addActionListener(e -> checkUserId());
        nicknameCheckButton.addActionListener(e -> checkNickname());
        signupButton.addActionListener(e -> signup());

        // 패널 추가
        add(mainPanel, BorderLayout.CENTER);
    }

    private void validateUserId() {
        String userId = userIdField.getText().trim();
        if (!userId.isEmpty() && userId.length() <= 8) {
            userIdCheckButton.setEnabled(true);
            userIdStatusLabel.setText("유효한 입력입니다.");
        } else {
            userIdCheckButton.setEnabled(false);
            userIdStatusLabel.setText("아이디는 8자 이내여야 합니다.");
        }
    }

    private void validateNickname() {
        String nickname = nicknameField.getText().trim();
        if (!nickname.isEmpty() && nickname.length() <= 16) {
            nicknameCheckButton.setEnabled(true);
            nicknameStatusLabel.setText("유효한 입력입니다.");
        } else {
            nicknameCheckButton.setEnabled(false);
            nicknameStatusLabel.setText("닉네임은 16자 이내여야 합니다.");
        }
    }

    private void validatePassword() {
        String password = new String(passwordField.getPassword());
        if (isValidPassword(password)) {
            passwordStatusLabel.setText("유효한 비밀번호입니다.");
            if (isUserIdValid && isNicknameValid) {
                signupButton.setEnabled(true);
            }
        } else {
            passwordStatusLabel.setText("비밀번호는 6~16자이며, 특수문자/숫자 2개 이상 포함해야 합니다.");
            signupButton.setEnabled(false);
        }
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z0-9!@#$%^&*(),.?\":{}|<>]{6,16}$";
        return Pattern.matches(passwordPattern, password);
    }

    private void checkUserId() {
        String userId = userIdField.getText().trim();
        // 서버로 userId 중복 확인 요청
        boolean isAvailable = sendToServer("checkUserId", userId);
        if (isAvailable) {
            isUserIdValid = true;
            userIdStatusLabel.setText("미중복 확인되었습니다!");
            nicknameField.setEnabled(true);
        } else {
            isUserIdValid = false;
            userIdStatusLabel.setText("이미 존재하는 아이디입니다.");
        }
    }

    private void checkNickname() {
        String nickname = nicknameField.getText().trim();
        // 서버로 nickname 중복 확인 요청
        boolean isAvailable = sendToServer("checkNickname", nickname);
        if (isAvailable) {
            isNicknameValid = true;
            nicknameStatusLabel.setText("미중복 확인되었습니다!");
            passwordField.setEnabled(true);
        } else {
            isNicknameValid = false;
            nicknameStatusLabel.setText("이미 존재하는 닉네임입니다.");
        }
    }

    private void signup() {
        String userId = userIdField.getText().trim();
        String nickname = nicknameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (isUserIdValid && isNicknameValid && isValidPassword(password)) {
            boolean success = sendToServer("signup", userId, nickname, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "회원가입되셨습니다!");
                moveToLoginScreen();
            } else {
                JOptionPane.showMessageDialog(this, "회원가입 중 문제가 발생했습니다.");
            }
        }
    }

    private boolean sendToServer(String action, String... data) {
        // 서버와의 통신 구현
        System.out.println("서버 전송: " + action + " -> " + String.join(", ", data));
        // 예제: 항상 성공으로 반환
        return true;
    }

    private void moveToLoginScreen() {
        // LoginClientGUI로 화면 전환 로직 구현
        System.out.println("로그인 화면으로 이동합니다.");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("회원가입");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new SignupClientGUI());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
