
import java.io.*;
import java.net.*;
import java.util.*;

public class GameServerGUI {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients; // 연결된 클라이언트 목록
    private int[][] sudokuBoard; // 스도쿠 보드
    private int[][] solution; // 스도쿠 정답 보드

    public GameServerGUI(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clients = new ArrayList<>();
            initializeSudoku(); // 스도쿠 보드 및 정답 초기화
            System.out.println("게임 서버가 포트 " + port + "에서 시작되었습니다.");
        } catch (IOException e) {
            System.err.println("서버 소켓 생성 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 서버 실행
     */
    public void start() {
        try {
            while (true) {
                System.out.println("클라이언트 연결 대기 중...");
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                System.out.println("새 클라이언트 연결됨: " + clientSocket.getInetAddress());

                // 새 클라이언트를 처리할 핸들러 생성
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler); // 클라이언트 목록에 추가

                // 핸들러를 별도의 스레드로 실행
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("서버 실행 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 스도쿠 보드 초기화
     */
    private void initializeSudoku() {
        sudokuBoard = new int[9][9];
        solution = new int[9][9];
        // 기본 스도쿠 문제 및 정답 설정 (여기서는 예제 데이터)
        // 실제 구현에서는 문제 생성 알고리즘 적용 가능
        int[][] exampleBoard = {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        int[][] exampleSolution = {
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        sudokuBoard = exampleBoard;
        solution = exampleSolution;
    }

    /**
     * 클라이언트 핸들러 클래스
     */
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private int emptyCellsCount;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                emptyCellsCount = calculateEmptyCells();
            } catch (IOException e) {
                System.err.println("클라이언트 입출력 스트림 설정 중 오류 발생: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                sendInitialBoard(); // 초기 스도쿠 보드 전송

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("클라이언트 요청: " + request);

                    if (request.startsWith("update")) {
                        handleGameStateUpdate(request);
                    } else {
                        handleCellInput(request);
                    }
                }
            } catch (IOException e) {
                System.err.println("클라이언트 처리 중 오류 발생: " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        /**
         * 초기 스도쿠 보드 전송
         */
        private void sendInitialBoard() {
            for (int[] row : sudokuBoard) {
                StringBuilder line = new StringBuilder();
                for (int cell : row) {
                    line.append(cell).append(" ");
                }
                out.println(line.toString().trim());
            }
        }

        /**
         * 클라이언트로부터의 게임 상태 업데이트 요청 처리
         */
        private void handleGameStateUpdate(String request) {
            String[] parts = request.split(" ");
            emptyCellsCount = Integer.parseInt(parts[1]); // 클라이언트가 남은 빈칸 수 전달
            int elapsedTime = Integer.parseInt(parts[2]); // 클라이언트가 경과 시간 전달
            System.out.println("클라이언트 상태: 빈칸 수=" + emptyCellsCount + ", 경과 시간=" + elapsedTime);

            // 상대 클라이언트 상태 전송
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.out.println("opponentEmptyCells " + emptyCellsCount);
                }
            }
        }

        /**
         * 클라이언트로부터의 입력 요청 처리
         */
        private void handleCellInput(String request) {
            String[] parts = request.split(" ");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            int value = Integer.parseInt(parts[2]);

            if (solution[row][col] == value) {
                out.println("correct"); // 정답
            } else {
                out.println("incorrect"); // 오답
            }
        }

        /**
         * 남은 빈칸 수 계산
         */
        private int calculateEmptyCells() {
            int count = 0;
            for (int[] row : sudokuBoard) {
                for (int cell : row) {
                    if (cell == 0) count++;
                }
            }
            return count;
        }

        /**
         * 클라이언트 연결 해제
         */
        private void disconnect() {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                    System.out.println("클라이언트 연결 해제됨.");
                }
            } catch (IOException e) {
                System.err.println("클라이언트 연결 해제 중 오류 발생: " + e.getMessage());
            }
            clients.remove(this);
        }
    }

    public static void main(String[] args) {
        int port = 54321; // 클라이언트와 동일한 포트 설정
        GameServerGUI server = new GameServerGUI(port);
        server.start();
    }
}
