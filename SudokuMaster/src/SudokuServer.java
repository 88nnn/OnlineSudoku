
import java.io.*;
import java.net.*;
import java.util.Map;

public class SudokuServer {
    private Sudoku sudoku;

    public SudokuServer() {
        sudoku = new Sudoku(); // Sudoku 클래스 사용
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Sudoku Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            System.out.println("Client connected: " + clientSocket.getInetAddress());

            while (true) {
                String request = (String) in.readObject();

                if ("generate".equalsIgnoreCase(request)) {
                    sudoku.generatePuzzle("hard");
                    out.writeObject(sudoku.getBoard()); // getBoard() 호출
                    out.flush();
                } else if ("check".equalsIgnoreCase(request)) {
                    int[][] userBoard = (int[][]) in.readObject();
                    Map<String, Boolean> correctness = sudoku.checkSolution(userBoard);
                    out.writeObject(correctness);
                    out.flush();
                } else if ("exit".equalsIgnoreCase(request)) {
                    System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                    break;
                } else {
                    out.writeObject("Invalid request: " + request);
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
