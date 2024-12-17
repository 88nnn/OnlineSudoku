package SudokuMaster;

import java.util.*;

public class Sudoku {
    private int[][] board;
    private int[][] solution;
    private long startTime;

    public Sudoku() {
        board = new int[9][9];
        solution = new int[9][9];
    }

    // 스도쿠 보드 반환 메서드
    public int[][] getBoard() {
        return board;
    }

    // 스도쿠 규칙에 따라 숫자가 유효한지 확인
    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }
        int startRow = row / 3 * 3;
        int startCol = col / 3 * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    // 스도쿠 보드 채우기
    private boolean fillBoard() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    List<Integer> nums = new ArrayList<>();
                    for (int i = 1; i <= 9; i++) nums.add(i);
                    Collections.shuffle(nums);
                    for (int num : nums) {
                        if (isValid(row, col, num)) {
                            board[row][col] = num;
                            if (fillBoard()) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    // 퍼즐 생성
    public void generatePuzzle(String difficulty) {
        fillBoard();
        copySolution();

        int cellsToRemove;
        switch (difficulty.toLowerCase()) {
            case "easy":
                cellsToRemove = 41;
                break;
            case "hard":
                cellsToRemove = 71;
                break;
            default:
                cellsToRemove = 56;
        }

        Random rand = new Random();
        for (int i = 0; i < cellsToRemove; i++) {
            int row, col;
            do {
                row = rand.nextInt(9);
                col = rand.nextInt(9);
            } while (board[row][col] == 0);
            board[row][col] = 0;
        }
    }

    private void copySolution() {
        for (int i = 0; i < 9; i++) {
            System.arraycopy(board[i], 0, solution[i], 0, 9);
        }
    }

    // 정답 체크
    public Map<String, Boolean> checkSolution(int[][] userBoard) {
        Map<String, Boolean> correctness = new HashMap<>();
        boolean isCorrect = true;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                boolean cellCorrect = solution[row][col] == userBoard[row][col];
                correctness.put("(" + row + "," + col + ")", cellCorrect);
                if (!cellCorrect) {
                    isCorrect = false;
                }
            }
        }

        correctness.put("isCorrect", isCorrect);
        return correctness;
    }
}

