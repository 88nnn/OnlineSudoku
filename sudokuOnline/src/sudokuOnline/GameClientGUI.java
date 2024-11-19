//게임 화면
//소요시간, 빈칸수, 칸별 답 입력, 칸별 값(NULL, Num) 출력
// MatchingClientGUI <--  --> ResultChatClientGUI

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

public class GameClientGUI extends JFrame {
	private JTextField[][] board = new JTextField[9][9];
	private JLabel myEmptyCellsLabel, opponentEmptyCellsLabel, elapsedTimeLabel;
	private int emptyCellsCount = 0;
	private int opponentEmptyCellsCount = 0;
	private int elapsedTime = 0;
	private Timer timer;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public GameClientGUI() {
	    super("온라인 스도쿠 게임");
	    
	    setSize(600, 700);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    setLayout(new BorderLayout());

	    // UI 패널 구성
	    JPanel displayPanel = createDisplayPanel();
	    JPanel boardPanel = createBoardPanel();

	    add(displayPanel, BorderLayout.NORTH);
	    add(boardPanel, BorderLayout.CENTER);

	    initializeServerConnection();
	    startGameTimer();
	}

	private JPanel createDisplayPanel() {
	    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
	    myEmptyCellsLabel = new JLabel("내 빈칸 수: 0");
	    opponentEmptyCellsLabel = new JLabel("상대 빈칸 수: 0");
	    elapsedTimeLabel = new JLabel("소요 시간: 0초");

	    panel.add(myEmptyCellsLabel);
	    panel.add(elapsedTimeLabel);
	    panel.add(opponentEmptyCellsLabel);
	    return panel;
	}

	private JPanel createBoardPanel() {
	    JPanel panel = new JPanel(new GridLayout(9, 9));
	    for (int i = 0; i < 9; i++) {
	        for (int j = 0; j < 9; j++) {
	            JTextField cell = new JTextField();
	            cell.setHorizontalAlignment(JTextField.CENTER);
	            cell.setFont(new Font("SansSerif", Font.BOLD, 20));

	            int row = i;
	            int col = j;

	            cell.addKeyListener(new KeyAdapter() {
	                @Override
	                public void keyTyped(KeyEvent e) {
	                    char c = e.getKeyChar();
	                    if (c < '1' || c > '9') {
	                        e.consume(); // 숫자 1~9 외의 입력 무시
	                    }
	                }

	                @Override
	                public void keyReleased(KeyEvent e) {
	                    String text = cell.getText();
	                    if (text.length() == 1 && text.matches("[1-9]")) {
	                        try {
								sendToServer(row, col, Integer.parseInt(text));
							} catch (NumberFormatException | IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                    }
	                }
	            });

	            board[i][j] = cell;
	            panel.add(cell);
	        }
	    }
	    return panel;
	}

	private void initializeServerConnection() {
	    try {
	        socket = new Socket("localhost", 54321);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out = new PrintWriter(socket.getOutputStream(), true);

	        receiveInitialBoard();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void receiveInitialBoard() {
	    try {
	        for (int i = 0; i < 9; i++) {
	            String line = in.readLine();
	            String[] values = line.split(" ");
	            for (int j = 0; j < 9; j++) {
	                int num = Integer.parseInt(values[j]);
	                if (num != 0) {
	                    board[i][j].setText(String.valueOf(num));
	                    board[i][j].setEditable(false);
	                } else {
	                    emptyCellsCount++;
	                }
	            }
	        }
	        updateEmptyCellsLabel();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void sendToServer(int row, int col, int value) throws UnknownHostException, IOException {
	    socket = new Socket();
		out.println(row + " " + col + " " + value);
	    try {
	        String response = in.readLine();
	        if (response.equals("correct")) {
	            board[row][col].setForeground(Color.BLUE);
	            board[row][col].setEditable(false);
	            emptyCellsCount--;
	            updateEmptyCellsLabel();
	        } else {
	            board[row][col].setText("");
	            JOptionPane.showMessageDialog(this, "잘못된 답입니다. 다시 입력해주세요.");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void startGameTimer() {
	    timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	            elapsedTime++;
	            elapsedTimeLabel.setText("소요 시간: " + elapsedTime + "초");
	            sendGameStateToServer();
	        }
	    }, 1000, 1000); // 1초마다 실행
	}

	private void sendGameStateToServer() {
	    out.println("update " + emptyCellsCount + " " + elapsedTime);
	    try {
	        String response = in.readLine();
	        if (response.startsWith("opponentEmptyCells")) {
	            opponentEmptyCellsCount = Integer.parseInt(response.split(" ")[1]);
	            opponentEmptyCellsLabel.setText("상대 빈칸 수: " + opponentEmptyCellsCount);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void updateEmptyCellsLabel() {
	    myEmptyCellsLabel.setText("내 빈칸 수: " + emptyCellsCount);
	}

	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	        GameClientGUI game = new GameClientGUI();
	        game.setVisible(true);
	    });
	}
}
