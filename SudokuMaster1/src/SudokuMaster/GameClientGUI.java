package SudokuMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class GameClientGUI extends JFrame {
    private JTextField[][] board = new JTextField[9][9];
    private JLabel myEmptyCellsLabel, opponentEmptyCellsLabel, elapsedTimeLabel;
    private int myEmptyCells = 0;
    private int opponentEmptyCells = 0;
    private int elapsedTime = 0;
    private Timer timer;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // 채팅 관련 UI
    private JTextArea chatArea;
    private JTextField chatInputField;

    public GameClientGUI() {
        super("온라인 스도쿠 게임");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 패널 추가
        JPanel displayPanel = createDisplayPanel();
        JPanel boardPanel = createBoardPanel();
        JPanel chatPanel = createChatPanel();

        add(displayPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(chatPanel, BorderLayout.SOUTH);

        connectToServer();
        startTimer();
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
                int row = i, col = j;

                cell.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        String text = cell.getText();
                        if (text.matches("[1-9]")) {
                            int value = Integer.parseInt(text);
                            sendToServer(row, col, value);
                        } else {
                            cell.setText("");
                        }
                    }
                });

                board[i][j] = cell;
                panel.add(cell);
            }
        }
        return panel;
    }

    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());

        // 채팅 메시지 표시 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        // 채팅 입력 필드
        chatInputField = new JTextField();
        chatInputField.addActionListener(e -> sendChatMessage());
        chatPanel.add(chatInputField, BorderLayout.SOUTH);

        return chatPanel;
    }

    private void sendChatMessage() {
        String message = chatInputField.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println("CHAT " + message); // 서버로 채팅 메시지 전송
            chatInputField.setText("");
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 54321);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            loadInitialBoard();

            // 서버에서 메시지 수신
            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        if (response.startsWith("CHAT")) {
                            String chatMessage = response.substring(5);
                            chatArea.append(chatMessage + "\n");
                        } else if (response.startsWith("opponentEmptyCells")) {
                            String[] parts = response.split(" ");
                            opponentEmptyCells = Integer.parseInt(parts[1]);
                            opponentEmptyCellsLabel.setText("상대 빈칸 수: " + opponentEmptyCells);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "서버 연결 실패!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadInitialBoard() throws IOException {
        for (int i = 0; i < 9; i++) {
            String[] line = in.readLine().split(" ");
            for (int j = 0; j < 9; j++) {
                if (!line[j].equals("0")) {
                    board[i][j].setText(line[j]);
                    board[i][j].setEnabled(false);
                } else {
                    myEmptyCells++;
                }
            }
        }
        myEmptyCellsLabel.setText("내 빈칸 수: " + myEmptyCells);
    }

    private void sendToServer(int row, int col, int value) {
        if (out != null) {
            out.println(row + " " + col + " " + value);
            try {
                String response = in.readLine();
                if ("correct".equalsIgnoreCase(response)) {
                    board[row][col].setBackground(Color.GREEN);
                    myEmptyCells--;
                    myEmptyCellsLabel.setText("내 빈칸 수: " + myEmptyCells);
                    out.println("update " + myEmptyCells + " " + elapsedTime);
                } else if ("incorrect".equalsIgnoreCase(response)) {
                    board[row][col].setBackground(Color.RED);
                    board[row][col].setText("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            elapsedTime++;
            elapsedTimeLabel.setText("소요 시간: " + elapsedTime + "초");
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameClientGUI client = new GameClientGUI();
            client.setVisible(true);
        });
    }
}
