package game;

import java.util.*;

public class ScoreManager {
    private Map<Integer, Player> players; // 存储玩家ID和玩家对象
    private Map<Integer, Map<Integer, Integer>> roundScores; // 每个玩家的每轮得分
    private Game game; // 游戏实例，用于访问当前回合数

    public ScoreManager(List<Player> players, Game game) {
        this.players = new HashMap<>();
        this.roundScores = new HashMap<>();
        this.game = game;
        players.forEach(player -> {
            this.players.put(player.getId(), player);
            roundScores.put(player.getId(), new HashMap<>());
        });
    }

    // 每轮结束时计算并记录分数
    public void calculateRoundScores() {
        int currentRound = game.getTurnCounter(); // 从Game对象获取当前回合号
        for (Player player : players.values()) {
            int roundScore = player.getRoundScore(); // 假设Player类有方法返回该轮的得分
            roundScores.get(player.getId()).put(currentRound, roundScore);
            player.addScore(roundScore); // 累加到玩家的总分
        }
    }

    // 用于显示所有玩家的得分情况
    public void displayScores() {
        System.out.println("Scores after round " + game.getTurnCounter() + ":");
        players.values().forEach(player -> 
            System.out.println("Player " + player.getName() + " (ID: " + player.getId() + ") has total score: " + player.getScore())
        );
    }

    public Map<Integer, Integer> getRoundScores(int playerId) {
        return roundScores.get(playerId);
    }

    public int getFinalScore(int playerId) {
        return players.get(playerId).getScore();
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public Game getGame() {
        return game;
    }
}
