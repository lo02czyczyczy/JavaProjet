package game;

public class RoundScore {
    private int score;
    private String playerName;
    private int playerId;
    private int roundNumber; // »ØºÏ±àºÅ

    public RoundScore(int score, String playerName, int playerId, int roundNumber) {
        this.score = score;
        this.playerName = playerName;
        this.playerId = playerId;
        this.roundNumber = roundNumber;
    }

    public int getScore() {
        return score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}
