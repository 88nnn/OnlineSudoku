package SudokuMaster;

import java.io.*;
import java.util.*;

public class RankingSystem {
    private Map<String, Integer> playerScores; // 플레이어 이름과 점수
    private final String filePath = "ranking.txt"; // 파일에 랭킹 저장

    public RankingSystem() {
        playerScores = new HashMap<>();
        loadRankings();
    }

    public void addWin(String playerName) {
        playerScores.put(playerName, playerScores.getOrDefault(playerName, 0) + 3); // 승리 시 +3점
        saveRankings();
    }

    public void addLoss(String playerName) {
        playerScores.put(playerName, playerScores.getOrDefault(playerName, 0) - 1); // 패배 시 -1점
        saveRankings();
    }

    public void displayRankings() {
        System.out.println("===== 랭킹 =====");
        playerScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + "점"));
    }

    private void loadRankings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                playerScores.put(parts[0], Integer.parseInt(parts[1]));
            }
        } catch (IOException e) {
            System.out.println("랭킹 파일을 읽을 수 없습니다. 새로 생성합니다.");
        }
    }

    private void saveRankings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, Integer> entry : playerScores.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("랭킹 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
