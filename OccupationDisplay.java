package game;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


public class OccupationDisplay {
    private HexBoard hexBoard;
    private JFrame frame;

    public OccupationDisplay(HexBoard hexBoard) {
        this.hexBoard = hexBoard;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Occupation Details");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400); // 设置窗口大小
        frame.setLayout(new BorderLayout());
        
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
    }

    public void displayOccupation() {
        StringBuilder display = new StringBuilder();
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();
        Set<Hex> coveredHexes = new HashSet<>();  // 用于记录已经处理过的六边形格
        
        // 首先处理卡牌相关的六边形格
        for (Map.Entry<String, List<Hex>> entry : cardHexes.entrySet()) {
            String cardLabel = entry.getKey();
            List<Hex> hexes = entry.getValue();
            
            for (Hex hex : hexes) {
                coveredHexes.add(hex);  // 标记为已处理
                HexBoard.OccupationInfo info = hexBoard.getOccupationInfo(hex);
                display.append(formatOccupationInfo(cardLabel, hex, info)).append("\n");
            }
        }

        // 然后处理未被卡牌覆盖的六边形格
        Map<Hex, Sector> allHexes = hexBoard.getBoard();
        for (Map.Entry<Hex, Sector> entry : allHexes.entrySet()) {
            Hex hex = entry.getKey();
            if (!coveredHexes.contains(hex)) {  // 只处理未被卡牌覆盖的六边形格
                HexBoard.OccupationInfo info = hexBoard.getOccupationInfo(hex);
                display.append(formatOccupationInfo("Boundary", hex, info)).append("\n");
            }
        }

        JTextArea textArea = (JTextArea)((JScrollPane)frame.getContentPane().getComponent(0)).getViewport().getView();
        textArea.setText(display.toString());
        EventQueue.invokeLater(() -> frame.setVisible(true));
    }

    private String formatOccupationInfo(String cardLabel, Hex hex, HexBoard.OccupationInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append(cardLabel).append("Card，(")
          .append(hex.q).append(", ").append(hex.r).append(", ").append(hex.s)
          .append(")，sector").append(hexBoard.getBoard().get(hex).getSector());

        if (info != null && info.getNumberOfShips() > 0) {
            sb.append("，By player : ").append(info.getPlayerId()).append(" Occupy")
              .append("，ships occupied：");

            List<Ship> ships = info.getOccupyingShips();
            for (Ship ship : ships) {
                sb.append(ship.getIdShip()).append(",");
            }

            sb.setLength(sb.length() - 1); // 删除最后一个逗号

            sb.append("，number of ships occupied：").append(info.getNumberOfShips());
        } else {
            sb.append("，Unoccupied, ships occupied: 0, number of ships occupied: 0");
        }

        return sb.toString();
    }
}
