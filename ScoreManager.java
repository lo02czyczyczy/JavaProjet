package game;

import java.util.*;

public class ScoreManager {
    private Map<Integer, Player> players; // �洢���ID����Ҷ���
    private Map<Integer, Map<Integer, Integer>> roundScores; // ÿ����ҵ�ÿ�ֵ÷�
    private Game game; // ��Ϸʵ�������ڷ��ʵ�ǰ�غ���

    public ScoreManager(List<Player> players, Game game) {
        this.players = new HashMap<>();
        this.roundScores = new HashMap<>();
        this.game = game;
        players.forEach(player -> {
            this.players.put(player.getId(), player);
            roundScores.put(player.getId(), new HashMap<>());
        });
    }

    // ÿ�ֽ���ʱ���㲢��¼����
    public void calculateRoundScores() {
        int currentRound = game.getTurnCounter(); // ��Game�����ȡ��ǰ�غϺ�
        for (Player player : players.values()) {
            int roundScore = player.getRoundScore(); // ����Player���з������ظ��ֵĵ÷�
            roundScores.get(player.getId()).put(currentRound, roundScore);
            player.addScore(roundScore); // �ۼӵ���ҵ��ܷ�
        }
    }

    // ������ʾ������ҵĵ÷����
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
