package game;

import java.util.*;

public class ScoreManager {
    private List<Player> players; // 使用 List 而不是 Map
    private Map<Integer, Map<Integer, Integer>> roundScores; // 每个玩家的每轮得分
    private Game game; // 游戏实例，用于访问当前回合数

    public ScoreManager(List<Player> players, Game game) {
        this.players = players;
        this.roundScores = new HashMap<>();
        this.game = game;
        players.forEach(player -> {
            roundScores.put(player.getId(), new HashMap<>());
        });
    }

    // 每轮结束时计算并记录分数
    public void calculateRoundScores(Game game) {
        int currentRound = game.getTurnCounter(); // 从Game对象获取当前回合号
        for (Player player : players) {
            int roundScore = player.getRoundScore(); // 获取该轮得分
            roundScores.get(player.getId()).put(currentRound, roundScore);
            // 不再在这里累加玩家总分，因为已经在 Player 类中处理
        }
    }

    // 用于显示所有玩家的得分情况
    public void displayScores() {
        System.out.println("Scores after round " + game.getTurnCounter() + ":");
        players.forEach(player ->
                System.out.println("Player " + player.getName() + " (ID: " + player.getId() + ") has total score: " + player.getScore())
        );
    }

    public Map<Integer, Integer> getRoundScores(int playerId) {
        return roundScores.get(playerId);
    }

    public int getFinalScore(int playerId) {
        return players.stream()
                .filter(player -> player.getId() == playerId)
                .findFirst()
                .map(Player::getScore)
                .orElse(0);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Game getGame() {
        return game;
    }
}
