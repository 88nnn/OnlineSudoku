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

public class MainMenuGUI extends JFrame {
    private String nickname;
    private JComboBox<String> difficultyComboBox;
    private JButton startMatchingButton;
    private JButton settingsButton;

    public MainMenuGUI(String nickname) {
        //this.nickname = nickname;
    	GameSessionManager session = GameSessionManager.getInstance();
        String nickname = session.getNickname();
        
        setTitle("Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        
        int rankingScore = session.getRankingScore();
        labelUsername.setText(nickname + "님, 환영합니다!");
        labelRanking.setText("현재 랭킹 점수: " + rankingScore);


        // Welcome Label
        JLabel welcomeLabel = new JLabel("<html>" + nickname + "님<br>환영합니다!</html>", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        // Difficulty Selection
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new FlowLayout());
        JLabel difficultyLabel = new JLabel("난이도: ");
        difficultyComboBox = new JComboBox<>(new String[]{"쉬움", "어려움"});
        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(difficultyComboBox);
        add(difficultyPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());

        // Settings Button
        settingsButton = new JButton("⚙️ 설정");
        settingsButton.addActionListener(e -> openSettings());
        buttonsPanel.add(settingsButton);

        // Start Matching Button
        startMatchingButton = new JButton("매칭 시작");
        startMatchingButton.addActionListener(e -> startMatching());
        buttonsPanel.add(startMatchingButton);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void openSettings() {
        // Transition to GameMenuGUI
        GameMenuGUI gameMenu = new GameMenuGUI();
        gameMenu.setVisible(true);
        this.dispose();
    }

    private void startMatching() {
        // Gather user selection
        String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();

        // Server communication logic
        // Send nickname and difficulty to the server
        // Example: Server.send(nickname, selectedDifficulty);

        // Transition to MatchingClientGUI
        MatchingClientGUI matchingClient = new MatchingClientGUI();
        matchingClient.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenuGUI mainMenu = new MainMenuGUI("테스트유저");
            mainMenu.setVisible(true);
        });
    }
}
