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
        frame.setSize(600, 400); // ���ô��ڴ�С
        frame.setLayout(new BorderLayout());
        
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
    }

    public void displayOccupation() {
        StringBuilder display = new StringBuilder();

        // ��ȡ���е� Hex �Ͷ�Ӧ�� OccupationInfo
        Map<Hex, HexBoard.OccupationInfo> occupationMap = hexBoard.getOccupationMap();
        Map<Hex, Sector> allHexes = hexBoard.getBoard();

        // ����һ�� Map���Կ��Ʊ�ǩΪ����ֵΪ�ÿ����µ� Hex �б�
        Map<String, List<Hex>> cardHexesMap = new HashMap<>();

        // ���ȣ��������е� Hex���ҵ����ǵĿ��Ʊ�ǩ�������з���
        for (Hex hex : allHexes.keySet()) {
            String cardLabel = findCardLabelForHex(hex);
            cardHexesMap.computeIfAbsent(cardLabel, k -> new ArrayList<>()).add(hex);
        }

        // �Կ��Ʊ�ǩ��������
        List<String> sortedCardLabels = new ArrayList<>(cardHexesMap.keySet());
        Collections.sort(sortedCardLabels);

        // ���⴦�� Tri-Prime �� Boundary��ʹ�����б�Ŀ�ͷ���β
        sortedCardLabels.remove("Tri-Prime");
        sortedCardLabels.remove("Boundary");
        sortedCardLabels.add(0, "Tri-Prime");
        sortedCardLabels.add("Boundary");

        // ���������Ŀ��Ʊ�ǩ����һ����
        for (String cardLabel : sortedCardLabels) {
            List<Hex> hexes = cardHexesMap.get(cardLabel);

            // �� Hex �б�����������
            hexes.sort(Comparator.comparingInt((Hex h) -> h.q)
                    .thenComparingInt(h -> h.r)
                    .thenComparingInt(h -> h.s));

            // ������Ʊ�ǩ
            display.append("=== ").append(cardLabel).append(" ===\n");

            // ��һ����ÿ����µ� Hex ��Ϣ
            for (Hex hex : hexes) {
                HexBoard.OccupationInfo info = occupationMap.get(hex);
                display.append(formatOccupationInfo(cardLabel, hex, info)).append("\n");
            }
        }

        JTextArea textArea = (JTextArea)((JScrollPane)frame.getContentPane().getComponent(0)).getViewport().getView();
        textArea.setText(display.toString());
        EventQueue.invokeLater(() -> frame.setVisible(true));
    }

    // �޸� findCardLabelForHex ����
    private String findCardLabelForHex(Hex hex) {
        // ���ȼ���Ƿ��� Tri-Prime ����
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
        return "Boundary"; // ��������κο����У�����Ϊ�߽�
    }

    // �ж��Ƿ�Ϊ Tri-Prime ����
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
          .append("��sector ").append(hexBoard.getBoard().get(hex).getSector());

        if (info != null && info.getNumberOfShips() > 0) {
            sb.append("��Occupied by player ").append(info.getPlayerId())
              .append("��Ships: ");

            List<Ship> ships = info.getOccupyingShips();
            for (Ship ship : ships) {
                sb.append(ship.getIdShip()).append(", ");
            }

            sb.setLength(sb.length() - 2); // ɾ�����һ�����źͿո�

            sb.append("��Number of ships: ").append(info.getNumberOfShips());
        } else {
            sb.append("��Unoccupied");
        }

        return sb.toString();
    }
}
