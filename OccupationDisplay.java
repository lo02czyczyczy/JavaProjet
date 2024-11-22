package game;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import java.util.*;
import java.util.stream.Collectors;

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

        // 获取所有的 Hex 和对应的 OccupationInfo
        Map<Hex, HexBoard.OccupationInfo> occupationMap = hexBoard.getOccupationMap();
        Map<Hex, Sector> allHexes = hexBoard.getBoard();

        // 构建一个 Map，以卡牌标签为键，值为该卡牌下的 Hex 列表
        Map<String, List<Hex>> cardHexesMap = new HashMap<>();

        // 首先，处理所有的 Hex，找到它们的卡牌标签，并进行分组
        for (Hex hex : allHexes.keySet()) {
            String cardLabel = findCardLabelForHex(hex);
            cardHexesMap.computeIfAbsent(cardLabel, k -> new ArrayList<>()).add(hex);
        }

        // 对卡牌标签进行排序
        List<String> sortedCardLabels = new ArrayList<>(cardHexesMap.keySet());
        Collections.sort(sortedCardLabels);

        // 特殊处理 Tri-Prime 和 Boundary，使其在列表的开头或结尾
        sortedCardLabels.remove("Tri-Prime");
        sortedCardLabels.remove("Boundary");
        sortedCardLabels.add(0, "Tri-Prime");
        sortedCardLabels.add("Boundary");

        // 按照排序后的卡牌标签，逐一处理
        for (String cardLabel : sortedCardLabels) {
            List<Hex> hexes = cardHexesMap.get(cardLabel);

            // 对 Hex 列表按照坐标排序
            hexes.sort(Comparator.comparingInt((Hex h) -> h.q)
                    .thenComparingInt(h -> h.r)
                    .thenComparingInt(h -> h.s));

            // 输出卡牌标签
            display.append("=== ").append(cardLabel).append(" ===\n");

            // 逐一输出该卡牌下的 Hex 信息
            for (Hex hex : hexes) {
                HexBoard.OccupationInfo info = occupationMap.get(hex);
                display.append(formatOccupationInfo(cardLabel, hex, info)).append("\n");
            }
        }

        JTextArea textArea = (JTextArea)((JScrollPane)frame.getContentPane().getComponent(0)).getViewport().getView();
        textArea.setText(display.toString());
        EventQueue.invokeLater(() -> frame.setVisible(true));
    }

    // 修改 findCardLabelForHex 方法
    private String findCardLabelForHex(Hex hex) {
        // 首先检查是否是 Tri-Prime 区域
        if (isTriPrimeHex(hex)) {
            return "Tri-Prime";
        }

        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();

        for (Map.Entry<String, List<Hex>> entry : cardHexes.entrySet()) {
            String cardLabel = entry.getKey();
            List<Hex> hexes = entry.getValue();
            if (hexes.contains(hex)) {
                return cardLabel;
            }
        }
        return "Boundary"; // 如果不在任何卡牌中，则标记为边界
    }

    // 判断是否为 Tri-Prime 区域
    private boolean isTriPrimeHex(Hex hex) {
        List<Hex> triPrimeHexes = Arrays.asList(
            new Hex(0, 0, 0),
            new Hex(1, -1, 0),
            new Hex(0, -1, 1),
            new Hex(1, 0, -1)
        );
        return triPrimeHexes.contains(hex);
    }

    private String formatOccupationInfo(String cardLabel, Hex hex, HexBoard.OccupationInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hex ").append(hex)
          .append("，sector ").append(hexBoard.getBoard().get(hex).getSector());

        if (info != null && info.getNumberOfShips() > 0) {
            sb.append("，Occupied by player ").append(info.getPlayerId())
              .append("，Ships: ");

            List<Ship> ships = info.getOccupyingShips();
            for (Ship ship : ships) {
                sb.append(ship.getIdShip()).append(", ");
            }

            sb.setLength(sb.length() - 2); // 删除最后一个逗号和空格

            sb.append("，Number of ships: ").append(info.getNumberOfShips());
        } else {
            sb.append("，Unoccupied");
        }

        return sb.toString();
    }
}
