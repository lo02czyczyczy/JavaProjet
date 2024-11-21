package game;

import java.util.*;

public class HexBoard {
    private Map<Hex, Sector> board;
    private Map<Hex, OccupationInfo> occupationMap; // ӳ����ϵ��ռ����Ϣ

    public HexBoard() {
        board = new HashMap<>();
        occupationMap = new HashMap<>(); // ��ʼ��ռ����Ϣӳ��
        initializeBoard();
    }
    
    // �ڲ��ඨ��ռ����Ϣ
    public static class OccupationInfo {
        private int playerId; // ռ���ߵ���� ID
        private List<Ship> occupyingShips; // ռ�����ϵ�Ĵ�ֻ�б�

        public OccupationInfo(int playerId) {
            this.playerId = playerId;
            this.occupyingShips = new ArrayList<>();
        }

        public void addShip(Ship ship) {
            occupyingShips.add(ship);
        }

        public void removeShip(Ship ship) {
            occupyingShips.remove(ship);
        }

        public int getPlayerId() {
            return playerId;
        }

        public List<Ship> getOccupyingShips() {
            return new ArrayList<>(occupyingShips);
        }

        public int getNumberOfShips() {
            return occupyingShips.size();
        }
    }
    
    // ������ϵռ������ķ���
    public void updateOccupation(Hex hex, Ship ship) {
        OccupationInfo info = occupationMap.getOrDefault(hex, new OccupationInfo(ship.getOwner().getId()));
        info.addShip(ship);
        occupationMap.put(hex, info);
    }

    public void clearOccupation(Hex hex, Ship ship) {
        if (occupationMap.containsKey(hex)) {
            OccupationInfo info = occupationMap.get(hex);
            info.removeShip(ship);
            if (info.getNumberOfShips() == 0) {
                occupationMap.remove(hex); // ���û�д�ֻռ�죬�Ƴ�ռ����Ϣ
            }
        }
    }

    public OccupationInfo getOccupationInfo(Hex hex) {
        return occupationMap.get(hex);
    }

    // ��ʼ������
    private void initializeBoard() {
        // �̶��� Tri-Prime ��������
        Hex[] fixedHexes = {
            new Hex(0, 0, 0),
            new Hex(1, -1, 0),
            new Hex(0, -1, 1),
            new Hex(1, 0, -1)
        };
        for (Hex hex : fixedHexes) {
            board.put(hex, new Sector(3));
        }

        // ���ñ߽���ֻ����0��ϵͳ�ĸ���
        setBoundaryHexes();

        // Ϊÿ�����������������
        Map<String, List<Hex>> cardMap = generateCardHexes();
        cardMap.forEach((cardLabel, hexList) -> randomizeSectors(hexList));
    }

 // ���ñ߽���ֻ����0��ϵͳ�ĸ���
    private void setBoundaryHexes() {
        List<Hex> boundaryHexes = Arrays.asList(
            new Hex(0, -3, 3),  // ָ���ı߽���ӣ��˴��������������߽����
            new Hex(-2, -1, 3),
            new Hex(1, -2, 1),
            new Hex(-1, 0, 1),
            new Hex(2, -1, -1),
            new Hex(0, 1, -1),
            new Hex(3, 0, -3),
            new Hex(1, 2, -3)
        );
        for (Hex hex : boundaryHexes) {
            board.put(hex, new Sector(0));  // ��������һ�� board �������ҿ��Ե��� put ����
        }
    }

    // ����ÿ�ſ��Ƶ�Hex����
    public Map<String, List<Hex>> generateCardHexes() {
        Map<String, List<Hex>> cardHexes = new HashMap<>();

        // Ϊÿ�ſ��ƶ���Hex����
        cardHexes.put("a", Arrays.asList(new Hex(1, -5, 4), new Hex(0, -4, 4), new Hex(1, -4, 3), new Hex(2, -4, 2), new Hex(1, -3, 2)));
        cardHexes.put("b", Arrays.asList(new Hex(-1, -3, 4), new Hex(-2, -2, 4), new Hex(-1, -2, 3), new Hex(0, -2, 2), new Hex(-1, -1, 2)));
        cardHexes.put("c", Arrays.asList(new Hex(-3, -1, 4), new Hex(-4, 0, 4), new Hex(-3, 0, 3), new Hex(-2, 0, 2), new Hex(-3, 1, 2)));
        cardHexes.put("d", Arrays.asList(new Hex(2, -3, 1), new Hex(3, -3, 0), new Hex(2, -2, 0), new Hex(3, -2, -1)));
        cardHexes.put("e", Arrays.asList(new Hex(-2, 1, 1), new Hex(-1, 1, 0), new Hex(-2, 2, 0), new Hex(-1, 2, -1)));
        cardHexes.put("f", Arrays.asList(new Hex(4, -2, -2), new Hex(3, -1, -2), new Hex(4, -1, -3), new Hex(5, -1, -4), new Hex(4, 0,-4)));
        cardHexes.put("g", Arrays.asList(new Hex(2, 0, -2), new Hex(1, 1, -2), new Hex(2, 1, -3), new Hex(3, 1, -4), new Hex(2, 2, -4)));
        cardHexes.put("h", Arrays.asList(new Hex(0, 2, -2), new Hex(-1, 3, -2), new Hex(0, 3, -3), new Hex(1, 3, -4), new Hex(0, 4, -4)));

        return cardHexes;
    }

    // �����������
    private void randomizeSectors(List<Hex> hexes) {
        List<Sector> sectors = new ArrayList<>();
        // Ϊ�˼򻯣�ÿ�ſ����������һ��2��������1������������Ϊ0��
        sectors.add(new Sector(2));
        sectors.add(new Sector(1));
        sectors.add(new Sector(1));
        while (sectors.size() < hexes.size()) {
            sectors.add(new Sector(0));  // ���ʣ��Ϊ0��
        }

        Collections.shuffle(sectors);  // �������

        for (int i = 0; i < hexes.size(); i++) {
            Hex hex = hexes.get(i);
            if (!board.containsKey(hex)) {  // ȷ���������Ѿ����õı߽�Hex
                board.put(hex, sectors.get(i));  // ���䵽��Ӧ��Hex
            }
        }
    }
    
    public Map<Hex, Sector> getBoard() {
        return this.board;
    }
    
    public Map<Hex, OccupationInfo> getOccupationMap() {
        return occupationMap;
    }
    
    public String getCardSectorInfo() {
        Map<String, List<Hex>> cardHexes = generateCardHexes();  // Get all card Hex coordinates
        StringBuilder info = new StringBuilder();
        cardHexes.forEach((cardLabel, hexList) -> {
            info.append("Card ").append(cardLabel).append(" sector information:\n");
            Map<Integer, List<Hex>> sectorMap = new HashMap<>();
            for (Hex hex : hexList) {
                Sector sector = board.get(hex);
                int level = sector.getSector();  // Get the sector level
                if (!sectorMap.containsKey(level)) {
                    sectorMap.put(level, new ArrayList<>());
                }
                sectorMap.get(level).add(hex);
            }
            sectorMap.keySet().stream().sorted(Collections.reverseOrder()).forEach(level -> {
                info.append("  - ").append(sectorMap.get(level).size()).append(" coordinates are level ").append(level).append(" system:");
                sectorMap.get(level).forEach(hex -> info.append(" (").append(hex.q).append(", ").append(hex.r).append(", ").append(hex.s).append(")"));
                info.append("\n");
            });
            info.append("\n");
        });
        return info.toString();
    }

}
