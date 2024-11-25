// 
// --> MatchingClientGUI

package sudokuOnline;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import sudokuOnline.MatchingClientGUI;

public class MainMenuGUI extends JFrame {
	GameSessionManager session = GameSessionManager.getInstance();

	// private JCheckBox<String> levelBox; // 난이도 선택 콤보박스

	private JTextField t_input, t_userID;
	private JTextArea t_display;
	private JButton b_matchStart, b_setting, b_connect, b_disconnect, b_send, b_exit;
	private String nickname = "유저", serverAddress = "localhost"; // 사용자의 닉네임, 서버 주소 기본값

	// 서버 연결 관련 필드
	private int serverPort = 54321; // 서버 포트 기본값
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private Thread receiveThread;

	public MainMenuGUI() {
		super("메인 메뉴");
		
		
		GameSessionManager session = GameSessionManager.getInstance();
		// this.nickname = session.getNickname();
		

		setSize(400, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null); // 화면 중앙에 위치
		setLayout(new BorderLayout());

		// GUI 컴포넌트 초기화
		buildGUI();

		// 서버와 연결 시도
		try {
			connectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// GUI 컴포넌트 초기화 메서드
	private void buildGUI() {
		// 환영메시지
		// The newline character (\n) in the JLabel will not render correctly. To create
		// a multi-line message, a <html> tag should be used:
		JLabel welcomeLabel = new JLabel("<html>" + nickname + "님<br>환영합니다!</html>");
		add(welcomeLabel, BorderLayout.NORTH);

		// 난이도 선택
		JPanel levelPanel = new JPanel(new FlowLayout());
		JCheckBox easyCheck = new JCheckBox("쉬움");
		JCheckBox hardCheck = new JCheckBox("어려움");

		// 둘 중 하나만 선택 가능하게 토글 효과 부여
		ButtonGroup levelGroup = new ButtonGroup();
		levelGroup.add(easyCheck);
		levelGroup.add(hardCheck);

		// 패널에 난이도 체크박스 삽입
		levelPanel.add(easyCheck);
		levelPanel.add(hardCheck);

		// 난이도 선택 전까지 매칭 진입(버튼 클릭) 불가
		easyCheck.addActionListener(e -> b_matchStart.setEnabled(true));
		hardCheck.addActionListener(e -> b_matchStart.setEnabled(true));

		add(levelPanel, BorderLayout.CENTER);

		// 버튼 패널
		JPanel b_Panel = new JPanel(new FlowLayout());

		// 매칭 시작 버튼
		b_matchStart = new JButton("매칭 시작");
		b_matchStart.addActionListener(e -> matchingStart(easyCheck.isSelected(), hardCheck.isSelected()));
		b_Panel.add(b_matchStart);

		add(b_Panel, BorderLayout.SOUTH);
	}

	// 서버 연결 메서드
	private void connectToServer() throws IOException {
		for (int i = 0; i < 5; i++) {
			try {
				// 서버 소켓 연결 생성
				socket = new Socket(serverAddress, serverPort);
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				// System.out.println("서버에 연결되었습니다.");
				JOptionPane.showMessageDialog(this, "서버에 성공적으로 연결되었습니다.", "연결 성공", JOptionPane.INFORMATION_MESSAGE);
				return;
			} catch (IOException e) {
				// 서버 연결 실패 시 예외 처리 및 메시지 표시
				int retry = JOptionPane.showConfirmDialog(this, "서버 연결 실패. 재시도 하시겠습니까?", "오류",
						JOptionPane.YES_NO_OPTION);
				if (retry == JOptionPane.NO_OPTION) {
					/*connectToServer();
				} else {*/
					JOptionPane.showMessageDialog(this, "서버 연결을 종료합니다.", "오류", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			}
		}
		JOptionPane.showMessageDialog(this, "최대 시도 횟수를 초과했습니다. 프로그램을 종료합니다.", "오류", JOptionPane.ERROR_MESSAGE);
	    System.exit(0);
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
			// JOptionPane.showMessageDialog(this, "서버 연결 종료 중 오류 발생: " + e.getMessage(),
			// "오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 매칭 시작 메서드
	private void matchingStart(boolean easySelected, boolean hardSelected) {
		// 사용자가 선택한 난이도 가져오기
		// String selectedLevel = easySelected ? "쉬움" : hardSelected ? "어려움" : null; //
		// 사용자가 선택한 난이도 가져오기
		// 사용자가 선택한 난이도 가져오기
		final String selectedLevel;
		if (easySelected) {
			selectedLevel = "쉬움";
		} else if (hardSelected) {
			selectedLevel = "어려움";
		} else {
			JOptionPane.showMessageDialog(this, "난이도를 선택해 주세요.", "오류", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// 서버와 연결 상태 확인
	    if (socket == null || socket.isClosed() || out == null) {
	        JOptionPane.showMessageDialog(this, "서버와 연결되어 있지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

		/*
		 * if (selectedLevel == null) { JOptionPane.showMessageDialog(this,
		 * "난이도를 선택해 주세요.", "오류", JOptionPane.ERROR_MESSAGE); return; } else if
		 * (!easySelected && !hardSelected) { JOptionPane.showMessageDialog(this,
		 * "난이도가 선택되지 않었습니다.", "오류", JOptionPane.ERROR_MESSAGE); return; } else if
		 * (easySelected) { selectedLevel = "쉬움"; } else if (hardSelected) {
		 * selectedLevel = "어려움"; } else { JOptionPane.showMessageDialog(this,
		 * "올바르지 못한 접근입니다.", "오류", JOptionPane.ERROR_MESSAGE); return; }
		 */
		if (selectedLevel != null) {
			SwingWorker<Void, Void> worker = new SwingWorker<>() {
				@Override
				protected Void doInBackground() throws Exception {
					if (socket != null && socket.isConnected()) {
						out.println("닉네임:" + nickname + ", 난이도:" + selectedLevel + "\n");
						//out.flush();
						System.out.println("매칭 요청 전송: " + nickname + ", 난이도(" + selectedLevel + ")");
					} else {
						SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(MainMenuGUI.this,
								"매칭 요청 중 오류 발생:", "오류", JOptionPane.ERROR_MESSAGE));
					}

					// UI 업데이트
					SwingUtilities.invokeLater(() -> {
						MatchingClientGUI matchingClient = new MatchingClientGUI();
						matchingClient.setVisible(true);
						MainMenuGUI.this.dispose();
					});

					return null;
				}

				@Override
				protected void done() {
					MatchingClientGUI matchingClient = new MatchingClientGUI();
					matchingClient.setVisible(true);
					MainMenuGUI.this.dispose();
				}
			};
			worker.execute();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainMenuGUI mainMenu = new MainMenuGUI();
			mainMenu.setVisible(true);
		});
	}
}
