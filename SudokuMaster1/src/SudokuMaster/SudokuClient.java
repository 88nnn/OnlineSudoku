package SudokuMaster;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Map;

public class SudokuClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to Sudoku Server!");

            while (true) {
                System.out.println("Enter command (generate, check, exit): ");
                String command = scanner.nextLine();
                out.writeObject(command);
                out.flush();

                if ("generate".equalsIgnoreCase(command)) {
                    int[][] board = (int[][]) in.readObject();
                    printBoard(board);
                } else if ("check".equalsIgnoreCase(command)) {
                    int[][] userBoard = new int[9][9];
                    out.writeObject(userBoard);
                    out.flush();

                    Map<String, Boolean> response = (Map<String, Boolean>) in.readObject();
                    System.out.println("Solution correct: " + response.get("isCorrect"));
                } else if ("exit".equalsIgnoreCase(command)) {
                    System.out.println("Exiting...");
                    break;
                } else {
                    System.out.println((String) in.readObject());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printBoard(int[][] board) {
        for (int[] row : board) {
            for (int num : row) {
                System.out.print((num == 0 ? "." : num) + " ");
            }
            System.out.println();
        }
    }
}
