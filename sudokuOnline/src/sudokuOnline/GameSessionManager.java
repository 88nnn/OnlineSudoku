package sudokuOnline;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class GameSessionManager {
    private static GameSessionManager instance;

    // 공유 데이터
    //private String userId;
    //private String password;
    private String nickname;
    private int rankingScore;
    private int wins;
    private int losses;

    
    private GameSessionManager() {}

    public static GameSessionManager getInstance() {
        if (instance == null) {
            instance = new GameSessionManager();
        }
        return instance;
    }

    // 닉네임 설정 및 가져오기
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
    	if (nickname != null) {
    		return nickname;
    	} else {
    		return null;
    	}
    }

    // 랭킹 점수 관리
    public void setRankingScore(int score) {
        this.rankingScore = score;
    }

    public int getRankingScore() {
        return rankingScore;
    }

    // 승리/패배 기록 추가
    public void addWin() {
        wins++;
    }

    public void addLoss() {
        losses++;
    }
    
    // 승리/패배 기록 조회
    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    // 매치메이킹 상대 대상 데이터 초기화 (예: 게임 종료 후)
    public void resetSession() {
    	//userId = null;
    	//password = null;
    	nickname = null;
        rankingScore = 0;
        wins = 0;
        losses = 0;
    }
}
