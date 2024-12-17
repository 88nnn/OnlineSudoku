package SudokuMaster;

import java.util.Random;

public class GameServerLogic {
    private int[][] solution;       // 스도쿠 정답 보드
    private int[][] puzzle;         // 플레이어에게 제공할 보드
    private int emptyCellsCount;    // 빈 칸 수
    private long startTime;         // 게임 시작 시간
    private long endTime;           // 게임 종료 시간

    public GameServerLogic() {
        solution = new int[9][9];
        puzzle = new int[9][9];
    }

    // 1. 스도쿠 문제 생성 알고리즘
    public int[][] generateSudokuPuzzle(int difficulty) {
        generateFullBoard();
        removeCellsBasedOnDifficulty(difficulty);
        return puzzle;
    }

    // 1-1. 정답 보드 생성 (기본 알고리즘)
    private void generateFullBoard() {
        Random random = new Random();
        solution = new int[9][9];

        // 기본 숫자 채우기
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                solution[i][j] = (i * 3 + i / 3 + j) % 9 + 1;
            }
        }

        // 행과 열 섞기
        for (int i = 0; i < 20; i++) {
            int r1 = random.nextInt(9);
            int r2 = random.nextInt(9);
            swapRows(r1, r2);
            swapColumns(r1, r2);
        }

        // 정답 보드 복사
        for (int i = 0; i < 9; i++) {
            System.arraycopy(solution[i], 0, puzzle[i], 0, 9);
        }
    }

    // 행 교환
    private void swapRows(int row1, int row2) {
        int[] temp = solution[row1];
        solution[row1] = solution[row2];
        solution[row2] = temp;
    }

    // 열 교환
    private void swapColumns(int col1, int col2) {
        for (int i = 0; i < 9; i++) {
            int temp = solution[i][col1];
            solution[i][col1] = solution[i][col2];
            solution[i][col2] = temp;
        }
    }

    // 1-2. 난이도에 따른 빈 칸 생성
    private void removeCellsBasedOnDifficulty(int difficulty) {
        Random random = new Random();
        emptyCellsCount = difficulty * 10; // 예: 난이도에 따라 빈 칸 수 설정 (쉬움=30, 보통=50, 어려움=70)
        for (int i = 0; i < emptyCellsCount; ) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if (puzzle[row][col] != 0) {
                puzzle[row][col] = 0; // 칸 비우기
                i++;
            }
        }
    }

    // 2. 정답 여부 확인
    public boolean validateAnswer(int row, int col, int value) {
        return solution[row][col] == value;
    }

    // 3. 유저 상태 관리 (소요 시간 및 빈 칸 수)
    public void startGame() {
        startTime = System.currentTimeMillis();
    }

    public void endGame() {
        endTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return (endTime - startTime) / 1000; // 초 단위 반환
    }

    public int getEmptyCellsCount() {
        return emptyCellsCount;
    }

    public void decrementEmptyCells() {
        if (emptyCellsCount > 0) emptyCellsCount--;
    }
}
