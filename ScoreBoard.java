package game;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class ScoreBoard {
    private JFrame frame;
    private JTable scoreTable;
    private DefaultTableModel tableModel;
    private ScoreManager scoreManager;

    public ScoreBoard(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        initUI();
    }

    // ��ʼ���û�����
    private void initUI() {
        frame = new JFrame("Game Scoreboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // ��ʼ�����ģ�ͣ������б��⣺��һ��Ϊ�غ�����ʣ�µ���Ϊ�������
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Round");

        // ������������Ϊ�б���
        for (Player player : scoreManager.getPlayers()) {
            tableModel.addColumn(player.getName());
        }

        // ����������ñ��ģ��
        scoreTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // ��ʾ����
        frame.setVisible(true);
    }

 // ���±������
    public void updateScores() {
        tableModel.setRowCount(0); // ��վ�����

        // ��ȡ���غ���
        int maxRounds = scoreManager.getGame().getTurnCounter();

        // ���ջغϸ��±������
        for (int round = 1; round <= maxRounds; round++) {
            Object[] rowData = new Object[tableModel.getColumnCount()];
            rowData[0] = "Round " + round; // ��һ���ǻغϺ�

            // ����������ң���ȡ�ûغϵķ���
            int columnIndex = 1; // �ӵ�2�п�ʼ�����ҵ÷�
            for (Player player : scoreManager.getPlayers()) {
                Integer score = scoreManager.getRoundScores(player.getId()).get(round);
                if (score != null) {
                    rowData[columnIndex] = score;
                } else {
                    rowData[columnIndex] = "-"; // ���û�з�������ʾ "-"
                }
                columnIndex++;
            }

            // ��������ݵ������
            tableModel.addRow(rowData);
        }

        // ������յ÷���
        Object[] finalScoreRow = new Object[tableModel.getColumnCount()];
        finalScoreRow[0] = "Final";
        int columnIndex = 1;
        for (Player player : scoreManager.getPlayers()) {
            finalScoreRow[columnIndex] = scoreManager.getFinalScore(player.getId());
            columnIndex++;
        }
        tableModel.addRow(finalScoreRow); // ������յ÷���
    }

}
