package game;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class HexBoardDisplay extends JFrame {
    private Map<Hex, Sector> board;

    public HexBoardDisplay(HexBoard hexBoard) {
        this.board = hexBoard.getBoard();
        setTitle("Hex Board Visualization");
        setSize(700, 600);  // �������ڳߴ磬���ӿ������ʾע��
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // ������ʾ����
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // ���ÿ����
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawHexBoard(g2d);
        drawLegend(g2d);  // ����ע��
    }

    private void drawHexBoard(Graphics g) {
        int size = 30;  // �����εĴ�С
        int offsetX = getWidth() / 3;  // ����ƫ��X������Ϊ�������
        int offsetY = getHeight() / 2;  // ����ƫ��Y

        for (Map.Entry<Hex, Sector> entry : board.entrySet()) {
            Hex hex = entry.getKey();
            Sector sector = entry.getValue();

            int x = offsetX + (int) ((3.0 / 2 * hex.q) * size);
            int y = offsetY + (int) ((Math.sqrt(3) * (hex.r + hex.q / 2.0)) * size);

            drawHex(g, x, y, size, sector.getSector(), hex);
        }
    }

    private void drawHex(Graphics g, int x, int y, int size, int level, Hex hex) {
        int[] cx = new int[6];
        int[] cy = new int[6];

        for (int i = 0; i < 6; i++) {
            cx[i] = (int) (x + size * Math.cos(i * Math.PI / 3));
            cy[i] = (int) (y + size * Math.sin(i * Math.PI / 3));
        }

        // ����������
        g.setColor(Color.BLACK);
        g.drawPolygon(cx, cy, 6);

        // ���������ȼ������ɫ
        Color color;
        switch (level) {
            case 0: color = Color.LIGHT_GRAY; break;
            case 1: color = Color.BLUE; break;
            case 2: color = Color.ORANGE; break;
            case 3: color = Color.GREEN; break;
            default: color = Color.BLACK;
        }
        g.setColor(color);
        g.fillPolygon(cx, cy, 6);

        // �����������Ļ�������
        g.setColor(Color.WHITE);  // ��ɫ����
        String coord = String.format("(%d,%d,%d)", hex.q, hex.r, hex.s);
        g.drawString(coord, x - g.getFontMetrics().stringWidth(coord) / 2, y + 5);
    }

    // �����Ҳ��ע����Ϣ
    private void drawLegend(Graphics g) {
        int legendX = getWidth() - 250;  // ע��λ�õ�X����
        int legendY = 100;  // ע����ʼλ�õ�Y����
        int boxSize = 30;  // ɫ��Ĵ�С

        // ����ÿ�������ȼ���ע��
        g.setColor(Color.BLACK);
        g.drawString("Sector Levels:", legendX, legendY - 20);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(legendX, legendY, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(legendX, legendY, boxSize, boxSize);
        g.drawString("Level 0", legendX + boxSize + 10, legendY + boxSize / 2 + 5);

        g.setColor(Color.BLUE);
        g.fillRect(legendX, legendY + 50, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(legendX, legendY + 50, boxSize, boxSize);
        g.drawString("Level 1", legendX + boxSize + 10, legendY + 50 + boxSize / 2 + 5);

        g.setColor(Color.ORANGE);
        g.fillRect(legendX, legendY + 100, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(legendX, legendY + 100, boxSize, boxSize);
        g.drawString("Level 2", legendX + boxSize + 10, legendY + 100 + boxSize / 2 + 5);

        g.setColor(Color.GREEN);
        g.fillRect(legendX, legendY + 150, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(legendX, legendY + 150, boxSize, boxSize);
        g.drawString("Level 3", legendX + boxSize + 10, legendY + 150 + boxSize / 2 + 5);
    }

    public static void main(String[] args) {
        HexBoard hexBoard = new HexBoard();
        EventQueue.invokeLater(() -> {
            new HexBoardDisplay(hexBoard).setVisible(true);
        });
    }
}
