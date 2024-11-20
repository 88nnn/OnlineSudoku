import java.util.*;

public class Sudoku {
    private int[][] board;
    private int[][] solution;
    private long startTime;

    public Sudoku() {
        board = new int[9][9];
        solution = new int[9][9];
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

    // 타이머 시작
    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    // 경과 시간 반환
    public String getTimestamp() {
        if (startTime == 0) {
            return "Timer not started.";
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        long minutes = elapsedTime / 60000;
        long seconds = (elapsedTime % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // 현재 보드 출력 (디버깅용)
    public void printBoard() {
        for (int[] row : board) {
            for (int num : row) {
                System.out.print((num == 0 ? "." : num) + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();

        // 퍼즐 생성
        sudoku.generatePuzzle("hard");
        System.out.println("Generated Sudoku Puzzle:");
        sudoku.printBoard();

        // 정답 출력
        System.out.println("\nSolution:");
        for (int[] row : sudoku.solution) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }

        // 타이머 시작
        sudoku.startTimer();

        // 사용자 입력 시뮬레이션
        int[][] userBoard = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(sudoku.board[i], 0, userBoard[i], 0, 9);
        }
        userBoard[0][0] = sudoku.solution[0][0]; // 첫 번째 칸에 정답 넣기 (예시)

        // 타임스탬프 확인
        System.out.println("\nElapsed Time: " + sudoku.getTimestamp());

        // 정답 체크
        Map<String, Boolean> correctness = sudoku.checkSolution(userBoard);
        System.out.println("\nCorrectness:");
        correctness.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
