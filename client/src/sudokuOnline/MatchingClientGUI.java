// MainMenuGUI <--  --> GameClientGUI

package sudokuOnline;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import sudokuOnline.GameSessionManager;


public class MatchingClientGUI extends JFrame {
	GameSessionManager session = GameSessionManager.getInstance();

    private int serverPort = 54321; // 서버 포트 기본값
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread receiveThread;
	
    private JLabel statusLabel;
    private javax.swing.Timer timer;
    private int loadot = 0;
    private boolean Matched = false;

    public MatchingClientGUI() {
        super("대기 중");
    	String nickname = session.getNickname();
    	nickname = "유저";
    	//out.println("매칭 시작" + nickname);
        
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 매칭 대기 상태 표시 라벨
        statusLabel = new JLabel("매칭 중", SwingConstants.CENTER);
        add(statusLabel, BorderLayout.CENTER);

        // 로딩 애니메이션 시작
        startLoadingAnimation();

        // 서버로부터 매칭
        connectToServer();
    }

    private void startLoadingAnimation() {
    	timer = new javax.swing.Timer(500, e -> { // 로딩 상태를 주기적으로 갱신
        	loadot = (loadot + 1) % 4;
        	StringBuilder dots = new StringBuilder();
        	for (int i = 0; i < loadot; i++) {
                dots.append(".");
            }
            statusLabel.setText("매칭 중" + dots);
        }); // 0.5초마다 실행
        timer.start();
    }
    
    private void connectToServer() {
    	new Thread(() -> {
        try {
            socket = new Socket("localhost", serverPort);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 매칭 요청 보내기
            String nickname = GameSessionManager.getInstance().getNickname();
            out.println("매칭 시작 " + nickname);

            // 서버 응답 수신 시 매칭 완료 스레드 시작
            receiveThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        if (response.equals("matched")) {
                            SwingUtilities.invokeLater(this::matchingSuccess);
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "서버 연결 실패!", "오류", JOptionPane.ERROR_MESSAGE);
            if (timer != null) {timer.stop();}
        }
    	}).start();
    }

    private void matchingSuccess() {
        // 로딩 애니메이션 중지
    	if (timer != null) {timer.stop();}
        statusLabel.setText("매칭 성공! 게임 화면으로 전환합니다...");

        // 3초 후 게임 화면으로 이동
        /*timer = new javax.swing.Timer(3000, e -> {
        	GameClientGUI gameScreen = new GameClientGUI();
            gameScreen.setVisible(true);
            dispose();
        }).setRepeats(false).start();
    }*/
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MatchingClientGUI matchingScreen = new MatchingClientGUI();
            matchingScreen.setVisible(true);
        });
    }

}
