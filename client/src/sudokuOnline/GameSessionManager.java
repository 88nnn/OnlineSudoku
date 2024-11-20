//클라이언트 내 전역변수 관리용 세션
package sudokuOnline;


public class GameSessionManager {
    private static volatile GameSessionManager instance;

    // 사용자 정보
    private String nickname;      // 사용자 닉네임
    private int score;     // 랭킹 점수
    private int wins;             // 승리 횟수
    private int losses;           // 패배 횟수

    // 게임 상태 관리
    private boolean inGame; // 현재 게임 진행 여부
    private String level;        // 게임 난이도 (쉬움/어려움)
    private long startTime;       // 게임 시작 시간 (ms)
    private long elapsedTime;     // 게임 소요 시간 (ms)

    // 상대 정보
    private String opponentNickname;  // 매칭된 상대 닉네임
    private int opponentScore; // 상대의 랭킹 점수


    private GameSessionManager() {}

    public static GameSessionManager getInstance() {
        if (instance == null) {
            instance = new GameSessionManager();
        }
        return instance;
    }

    // 닉네임 설정 및 가져오기
    public void setNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임이 인식되지 않고 있거나, 공백입니다.");
        }
        this.nickname = nickname;
    }

    public String getNickname() {
        if (nickname == null) {
            throw new IllegalStateException("닉네임이 없어 서버에 접속이 되지 않습니다. 닉네임을 섧정해 주세요.");
        }
        return nickname;
    }

    // 랭킹 점수 관리
    public void setScore(int rs) {
    	if (score < 0) {
            throw new IllegalArgumentException("Ranking score cannot be negative.");
        }
        this.score = rs;
    }

    public int getScore() {
        return score;
    }

    // 승리/패배 기록 관리
    public void addWin() {
        wins++;
    }

    public void addLoss() {
        losses++;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    // 게임 상태 관리
    public void startGame(String level) {
        this.inGame = true;
        this.level = level;
        this.startTime = System.currentTimeMillis();
    }

    public void endGame() {
        this.inGame = false;
        this.elapsedTime = System.currentTimeMillis() - startTime;
    }

    public boolean inGame() {
        return inGame;
    }

    public String getLevel() {
        return level;
    }

    public long getelapsedTime() {
        return elapsedTime;
    }

    // 매칭 상대 정보 관리
    public void setOpponent(String nickname, int score) {
        this.opponentNickname = nickname;
        this.opponentScore = score;
    }

    public String getOpponentNickname() {
        return opponentNickname;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    // 세션 초기화
    public void resetSession() {
        nickname = null;
        score = 0;
        wins = 0;
        losses = 0;
        inGame = false;
        level = null;
        startTime = 0;
        elapsedTime = 0;
        opponentNickname = null;
        opponentScore = 0;
    }
}
