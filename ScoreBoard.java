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

    // 初始化用户界面
    private void initUI() {
    	frame = new JFrame("Game Scoreboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 100);  // 宽度变窄
        frame.setLayout(new BorderLayout());

        // 使窗口出现在屏幕顶部中间
        frame.setLocationRelativeTo(null);
        frame.setLocation(frame.getX(), 0);
        frame.setAlwaysOnTop(true);  // 窗口始终在最前面

        // 初始化表格模型，创建列标题：第一列为回合数，剩下的列为玩家姓名
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Round");

        // 添加玩家姓名作为列标题
        for (Player player : scoreManager.getPlayers()) {
            tableModel.addColumn(player.getName());
        }

        // 创建表格并设置表格模型
        scoreTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 显示窗口
        frame.setVisible(true);
    }

 // 更新表格内容
    public void updateScores() {
        tableModel.setRowCount(0); // 清空旧数据

        // 获取最大回合数
        int maxRounds = scoreManager.getGame().getTurnCounter();

        // 按照回合更新表格内容
        for (int round = 1; round <= maxRounds; round++) {
            Object[] rowData = new Object[tableModel.getColumnCount()];
            rowData[0] = "Round " + round; // 第一列是回合号

            // 遍历所有玩家，获取该回合的分数
            int columnIndex = 1; // 从第2列开始填充玩家得分
            for (Player player : scoreManager.getPlayers()) {
                Integer score = scoreManager.getRoundScores(player.getId()).get(round);
                if (score != null) {
                    rowData[columnIndex] = score;
                } else {
                    rowData[columnIndex] = "-"; // 如果没有分数则显示 "-"
                }
                columnIndex++;
            }

            // 添加行数据到表格中
            tableModel.addRow(rowData);
        }

        // 添加最终得分行
        Object[] finalScoreRow = new Object[tableModel.getColumnCount()];
        finalScoreRow[0] = "Final";
        int columnIndex = 1;
        for (Player player : scoreManager.getPlayers()) {
            finalScoreRow[columnIndex] = scoreManager.getFinalScore(player.getId());
            columnIndex++;
        }
        tableModel.addRow(finalScoreRow); // 添加最终得分行
    }

}
