package game;

import java.util.*;

public class ScoreManager {
    private List<Player> players; // ʹ�� List ������ Map
    private Map<Integer, Map<Integer, Integer>> roundScores; // ÿ����ҵ�ÿ�ֵ÷�
    private Game game; // ��Ϸʵ�������ڷ��ʵ�ǰ�غ���

    public ScoreManager(List<Player> players, Game game) {
        this.players = players;
        this.roundScores = new HashMap<>();
        this.game = game;
        players.forEach(player -> {
            roundScores.put(player.getId(), new HashMap<>());
        });
    }

    // ÿ�ֽ���ʱ���㲢��¼����
    public void calculateRoundScores(Game game) {
        int currentRound = game.getTurnCounter(); // ��Game�����ȡ��ǰ�غϺ�
        for (Player player : players) {
            int roundScore = player.getRoundScore(); // ��ȡ���ֵ÷�
            roundScores.get(player.getId()).put(currentRound, roundScore);
            // �����������ۼ�����ܷ֣���Ϊ�Ѿ��� Player ���д���
        }
    }

    // ������ʾ������ҵĵ÷����
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
