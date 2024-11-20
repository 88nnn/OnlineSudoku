import java.io.*;
import java.net.*;
import java.util.Map;

public class SudokuServer {
    private Sudoku sudoku;

    public SudokuServer() {
        sudoku = new Sudoku(); // 기존 Sudoku 클래스 활용
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) { // 포트 12345로 서버 시작
            System.out.println("Sudoku Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                new Thread(() -> handleClient(clientSocket)).start(); // 멀티스레드 처리
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // 클라이언트 요청 처리
            while (true) {
                String request = (String) in.readObject(); // 클라이언트 요청 읽기

                if ("generate".equalsIgnoreCase(request)) {
                    // 스도쿠 문제 생성
                    sudoku.generatePuzzle("hard"); // 난이도: hard
                    out.writeObject(sudoku.getBoard()); // Getter 메서드를 사용해 생성된 보드 전송
                    out.flush();
                } else if ("check".equalsIgnoreCase(request)) {
                    // 클라이언트 제출 보드 수신
                    int[][] userBoard = (int[][]) in.readObject();

                    // 정답 검증 및 결과 반환
                    Map<String, Boolean> correctness = sudoku.checkSolution(userBoard);
                    out.writeObject(correctness); // 결과 전송
                    out.flush();
                } else if ("exit".equalsIgnoreCase(request)) {
                    System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                    break; // 연결 종료
                } else {
                    out.writeObject("Invalid request: " + request); // 잘못된 요청 처리
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SudokuServer server = new SudokuServer();
        server.startServer();
    }
}
