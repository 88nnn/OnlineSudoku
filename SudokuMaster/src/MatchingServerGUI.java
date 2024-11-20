import java.io.*;
import java.net.*;
import java.util.*;

public class MatchingServerGUI {
    private ServerSocket serverSocket;
    private List<ClientHandler> waitingClients; // 매칭 대기 중인 클라이언트 목록

    public MatchingServerGUI(int port) {
        try {
            serverSocket = new ServerSocket(port);
            waitingClients = new ArrayList<>();
            System.out.println("매칭 서버가 포트 " + port + "에서 시작되었습니다.");
        } catch (IOException e) {
            System.err.println("서버 소켓 생성 중 오류 발생: " + e.getMessage());
        }
    }

    // 서버 실행
    public void start() {
        try {
            while (true) {
                System.out.println("클라이언트 연결 대기 중...");
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                System.out.println("새 클라이언트 연결됨: " + clientSocket.getInetAddress());

                // 새 클라이언트를 처리할 핸들러 생성
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                waitingClients.add(clientHandler); // 클라이언트 목록에 추가

                // 핸들러를 별도의 스레드로 실행
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("서버 실행 중 오류 발생: " + e.getMessage());
        }
    }

    // 클라이언트 핸들러 클래스
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private BufferedWriter out;
        private String nickname; // 클라이언트 닉네임

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            } catch (IOException e) {
                System.err.println("클라이언트 입출력 스트림 설정 중 오류 발생: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                // 클라이언트로부터 데이터 수신
                String request = in.readLine(); // 매칭 요청 수신
                System.out.println("클라이언트 요청 수신: " + request);

                if (request.startsWith("start_matching")) {
                    this.nickname = request.split(" ")[1]; // 닉네임 추출
                    System.out.println("매칭 요청 닉네임: " + nickname);

                    // 매칭 처리 시작
                    matchClient();
                } else {
                    out.write("알 수 없는 요청입니다: " + request + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("클라이언트 처리 중 오류 발생: " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        // 클라이언트 매칭 처리
        private void matchClient() throws IOException {
            try {
                // 매칭 대기 (3초 대기 후 매칭 성공 시뮬레이션)
                Thread.sleep(3000);
                out.write("matching_success\n"); // 매칭 성공 신호 전송
                out.flush();
                System.out.println("매칭 성공 신호 전송 완료: " + nickname);
            } catch (InterruptedException e) {
                System.err.println("매칭 처리 중 오류 발생: " + e.getMessage());
            }
        }

        // 클라이언트 연결 해제
        private void disconnect() {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                    System.out.println("클라이언트 연결 해제됨: " + nickname);
                }
            } catch (IOException e) {
                System.err.println("클라이언트 연결 해제 중 오류 발생: " + e.getMessage());
            }
            waitingClients.remove(this);
        }
    }

    public static void main(String[] args) {
        int port = 54321; // 클라이언트와 동일한 포트 설정
        MatchingServerGUI server = new MatchingServerGUI(port);
        server.start();
    }
}
