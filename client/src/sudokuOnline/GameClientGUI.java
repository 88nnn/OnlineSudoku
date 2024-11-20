//게임 화면
//소요시간, 빈칸수, 칸별 답 입력, 칸별 값(NULL, Num) 출력
// MatchingClientGUI <--  --> ResultChatClientGUI

package sudokuOnline;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.*;

public class GameClientGUI extends JFrame {
	GameSessionManager session = GameSessionManager.getInstance();
	private static final Logger logger = Logger.getLogger(GameClientGUI.class.getName());
    private JTextField[][] board = new JTextField[9][9]; // 9x9 스도쿠 보드
    private JLabel myEmptyCellsLabel, opponentEmptyCellsLabel, elapsedTimeLabel;
    private int emptyCellsCount = 0; // 내가 풀어야 할 빈 칸 수
    private int opponentEmptyCellsCount = 0; // 상대방의 빈 칸 수
    private int elapsedTime = 0; // 경과 시간
    private Timer timer; // 게임 타이머
    private Socket socket; // 서버와의 통신용 소켓
    private BufferedReader in; // 서버로부터 입력 받는 스트림
    private PrintWriter out; // 서버로 데이터 전송용 출력 스트림
    String serverAddress = "localhost";    
    int port = 54321;

    public GameClientGUI() {
        // GUI 초기화
        super("온라인 스도쿠 게임");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 주요 UI 컴포넌트 추가
        JPanel displayPanel = createDisplayPanel(); // 상단 상태 표시 패널
        JPanel boardPanel = createBoardPanel(); // 스도쿠 보드 패널

        add(displayPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);

        // 서버 연결 초기화 및 게임 타이머 시작
        ConnectToServer();
        startTimer();
    }

    /**
     * 상단 상태 표시 패널 생성
     * - 내 빈칸 수, 경과 시간, 상대 빈칸 수를 표시.
     */
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

    /**
     * 스도쿠 보드 패널 생성
     * - 9x9 그리드의 텍스트 필드로 구성.
     * - 입력 검증 및 서버 전송 처리.
     */
    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 9));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER); // 텍스트 중앙 정렬
                cell.setFont(new Font("SansSerif", Font.BOLD, 20)); // 폰트 크기 설정

                int row = i;
                int col = j;

                // 입력 검증: 숫자만 입력 가능
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
                        if (text.matches("[1-9]")) { // 유효한 숫자 확인
                            try {
                                sendToServer(row, col, Integer.parseInt(text)); // 서버로 입력값 전송
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });

                board[i][j] = cell; // 보드에 셀 추가
                panel.add(cell);
            }
        }
        return panel;
    }

    /**
     * 서버 연결 초기화
     * - 소켓을 생성하고 서버와 연결.
     * - 초기 보드 데이터 수신.
     */
    private void ConnectToServer() {
    	int retries = 3;
        while (retries > 0) {
        	try {
            	socket = new Socket(); // 로컬 서버와 포트 54321로 연결
            	socket.connect(new InetSocketAddress(serverAddress, port), 5000); //5초 후 타임아웃
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                receiveBoard(); // 초기 보드 데이터를 서버에서 수신
                return; //접속 성공 시 루프에서 탈출
            } catch (IOException e) {
            	retries--;
                if (retries == 0) {
                    JOptionPane.showMessageDialog(this, "Failed to connect to server after multiple attempts.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        }
    }

    /**
     * 초기 보드 데이터 수신
     * - 서버에서 초기 스도쿠 보드 정보를 받아 채움.
     */
    private void receiveBoard() {
        try {
            for (int i = 0; i < 9; i++) {
                String line = in.readLine(); // 서버로부터 한 줄씩 수신
                String[] values = line.split(" "); // 공백으로 분리
                for (int j = 0; j < 9; j++) {
                    int num = Integer.parseInt(values[j]);
                    board[i][j].setToolTipText("1~9까지의 숫자만 입력 가능합니다.");
                    if (num != 0) {
                        board[i][j].setText(String.valueOf(num));
                        board[i][j].setEditable(false);
                        board[i][j].setBackground(Color.LIGHT_GRAY); // 초기값 강조(회색)
                    } else {
                        emptyCellsCount++;
                        board[i][j].setBackground(Color.WHITE); // 
                    }
                }
            }
            updateEmptyCellsLabel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 서버로 값 전송
     * - 입력된 값이 맞는지 서버로 전송 후 응답 확인.
     */
        @SuppressWarnings("CallToPrintStackTrace")
    private void sendToServer(int row, int col, int value) {
        out.println(row + " " + col + " " + value); // 좌표와 값을 전송
        try {
            String response = in.readLine(); // 서버 응답
            if ("correct".equals(response)) {
                board[row][col].setForeground(Color.BLUE); // 올바른 입력일 경우 색상 변경
                board[row][col].setEditable(false);
                emptyCellsCount--;
                updateEmptyCellsLabel();
            } else {
                board[row][col].setText(""); // 잘못된 입력일 경우 값 초기화
                JOptionPane.showMessageDialog(this, "잘못된 답입니다. 다시 입력해주세요.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 게임 타이머 시작
     * - 매 초마다 경과 시간 갱신 및 상태 업데이트.
     */
    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedTime++; // 경과 시간 증가
                SwingUtilities.invokeLater(() -> elapsedTimeLabel.setText("소요 시간: " + elapsedTime + "초"));
                sendState(); // 서버에 상태 업데이트
            }
        }, 1000, 1000); // 1초마다 실행
    }

    /**
     * 서버에 현재 상태 전송
     * - 남은 빈칸 수와 경과 시간을 전송.
     */
    private void sendState() {
    	new Thread(() -> {
    		out.println("update " + emptyCellsCount + " " + elapsedTime); // 상태 전송
            try {
                String response = in.readLine(); // 서버 응답
                if (response.startsWith("opponentEmptyCells")) {
                	/*
                	int opponentCount = Integer.parseInt(response.split(" ")[1]);
                    SwingUtilities.invokeLater(() -> opponentEmptyCellsLabel.setText("상대 빈칸 수: " + opponentCount));
                	 */
                    opponentEmptyCellsCount = Integer.parseInt(response.split(" ")[1]);
                    opponentEmptyCellsLabel.setText("상대 빈칸 수: " + opponentEmptyCellsCount);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 빈칸 수 라벨 업데이트
     */
    private void updateEmptyCellsLabel() {
        myEmptyCellsLabel.setText("내 빈칸 수: " + emptyCellsCount);
    }
    
    private void fetchOpponentData() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    String response = in.readLine();
                    publish(response); // Pass data to the `process` method
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                opponentEmptyCellsLabel.setText("상대 빈칸 수: " + chunks.get(0));
            }
        };
        worker.execute();
    }

    
    private void setupLogger() {
        try {
            FileHandler fh = new FileHandler("game_logs.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            //logger.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameClientGUI game = new GameClientGUI();
            game.setVisible(true);
        });
    }
}
