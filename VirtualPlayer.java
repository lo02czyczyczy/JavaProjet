package game;

import java.util.*;
import java.util.stream.Collectors;

public class VirtualPlayer extends Player {
    // ���캯��
    public VirtualPlayer(String name, int id, Game game) {
        super(name, id, game);
    }

    // ���� placeShips ����
    @Override
    public void placeShips(HexBoard hexBoard, Set<String> occupiedSectors) {
        int shipsToPlace = 2;  // ÿ�η������Ҵ�
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();  // ��ȡ���п��Ƽ����Ӧ������

        // ������ռ�õ���ϵ
        List<String> availableSectors = cardHexes.keySet().stream()
                .filter(sector -> !occupiedSectors.contains(sector))
                .collect(Collectors.toList());

        if (availableSectors.isEmpty()) {
            System.out.println("No available sectors for VirtualPlayer to place ships.");
            return;
        }

        // ���ѡ��һ����ϵ
        Random rand = new Random();
        String selectedSector = availableSectors.get(rand.nextInt(availableSectors.size()));
        occupiedSectors.add(selectedSector);  // ��Ǹ���ϵ�ѱ�ռ��

        // ��ȡ��ѡ��ϵ�е� Level I ϵͳ
        List<Hex> levelIHexes = cardHexes.get(selectedSector).stream()
                .filter(hex -> hexBoard.getBoard().get(hex).getSector() == 1)
                .collect(Collectors.toList());

        if (levelIHexes.isEmpty()) {
            System.out.println("No Level I systems in selected sector for VirtualPlayer.");
            return;
        }

        // ���ѡ��һ������
        Hex selectedHex = levelIHexes.get(rand.nextInt(levelIHexes.size()));

        // ���ý���������ռ����Ϣ
        for (int i = 1; i <= shipsToPlace; i++) {
            String shipId = generateShipId(ships.size() + 1);
            Ship newShip = new Ship(this, hexBoard.getBoard().get(selectedHex), selectedHex, shipId);
            ships.add(newShip);
            hexBoard.updateOccupation(selectedHex, newShip); // ����ռ����Ϣ
            System.out.println("VirtualPlayer " + getName() + " placed a ship with ID " + shipId + " at Hex coordinates " + selectedHex);
        }
    }


    // ���� chooseCommandOrder ����
    @Override
    public List<Integer> chooseCommandOrder() {
        List<Integer> commands = Arrays.asList(1, 2, 3);
        Collections.shuffle(commands);  // �����������
        System.out.println("VirtualPlayer " + getName() + " chose command order: " + commands);
        return commands;
    }


    
    
    
    // ���� expand ����
    @Override
    public void expand(Game game) {
        int round = currentCommandOrder.indexOf(1) + 1;
        int usage = game.getCommandUsage(round, 1); // ��ȡ���� "Expand" �����ʹ�ô���
        int shipsToAdd = Math.max(4 - usage, 1); // ���������ӵĴ�ֻ����
        System.out.println(getName() + " can add " + shipsToAdd + " ships using Expand command in round " + round);

        // ��ȡ��ҵ�ǰ���Ƶ�ϵͳ
        List<Hex> controlledHexes = hexBoard.getOccupationMap().entrySet().stream()
                .filter(entry -> entry.getValue().getPlayerId() == this.id)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (controlledHexes.isEmpty()) {
            System.out.println(getName() + " controls no hexes to expand.");
            return;
        }

        Random rand = new Random();

        // ����ڿ��Ƶ�ϵͳ�з����µĽ���
        while (shipsToAdd > 0) {
            Hex targetHex = controlledHexes.get(rand.nextInt(controlledHexes.size()));
            String shipId = generateShipId(ships.size() + 1);
            Ship newShip = new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, shipId);
            ships.add(newShip);
            hexBoard.updateOccupation(targetHex, newShip); // ����ռ����Ϣ
            System.out.println(getName() + " added ship " + shipId + " to hex " + targetHex);
            shipsToAdd--;
        }
    }


    
    
    
    // ���� explore ����
    @Override
    public void explore(Game game) {
        int round = currentCommandOrder.indexOf(2) + 1;
        int usage = game.getCommandUsage(round, 2);
        int fleetMovements = Math.max(4 - usage, 1); // ���Խ��еĽ����ƶ�����
        System.out.println(getName() + " can make " + fleetMovements + " fleet movements using Explore command in round " + round);

        Random rand = new Random();

        // ��ȡ���п����ƶ��Ľ��ӣ�ͬһλ�õĽ�����
        Map<Hex, List<Ship>> fleets = ships.stream()
                .filter(ship -> !ship.isMoved()) // ֻѡ����δ�ƶ��Ľ���
                .collect(Collectors.groupingBy(Ship::getShipLocation));

        List<Hex> triPrimeHexes = Arrays.asList(new Hex(0, 0, 0), new Hex(1, -1, 0), new Hex(0, -1, 1), new Hex(1, 0, -1));

        while (fleetMovements > 0 && !fleets.isEmpty()) {
            // ���ѡ��һ������
            List<Hex> fleetLocations = new ArrayList<>(fleets.keySet());
            Hex currentHex = fleetLocations.get(rand.nextInt(fleetLocations.size()));
            List<Ship> fleetShips = fleets.get(currentHex);

            // �Ƴ��Ѿ��ƶ����Ľ���
            fleetShips = fleetShips.stream().filter(ship -> !ship.isMoved()).collect(Collectors.toList());
            if (fleetShips.isEmpty()) {
                fleets.remove(currentHex);
                continue;
            }

            // ��ȡ���ƶ���λ�ã�·���������Ϊ2��
            List<List<Hex>> possiblePaths = getPossiblePaths(currentHex, 2);

            // ���˵������Ϲ����·��
            List<List<Hex>> validPaths = possiblePaths.stream()
                    .filter(path -> isValidPath(path, triPrimeHexes))
                    .collect(Collectors.toList());

            if (validPaths.isEmpty()) {
                fleets.remove(currentHex);
                continue;
            }

            // ���ѡ��һ��·��
            List<Hex> path = validPaths.get(rand.nextInt(validPaths.size()));
            Hex targetHex = path.get(path.size() - 1);

            // �ƶ�����
            for (Ship ship : fleetShips) {
                Hex previousHex = ship.getShipLocation();
                ship.move(targetHex, hexBoard.getBoard().get(targetHex));
                hexBoard.updateOccupation(targetHex, ship);
                hexBoard.clearOccupation(previousHex, ship);
                ship.setMoved(true); // ���Ϊ���ƶ�
            }

            System.out.println(getName() + " moved fleet from " + currentHex + " to " + targetHex);

            // ���½�����Ϣ
            fleets.remove(currentHex);
            fleetMovements--;
        }

        // ���ý������ƶ�״̬�������Ҫ�ڻغϽ�����
        // ships.forEach(ship -> ship.setMoved(false));
    }

 // ��ȡ���ܵ�·��
    private List<List<Hex>> getPossiblePaths(Hex startHex, int maxDistance) {
        List<List<Hex>> paths = new ArrayList<>();
        Queue<List<Hex>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(startHex));

        while (!queue.isEmpty()) {
            List<Hex> path = queue.poll();
            Hex lastHex = path.get(path.size() - 1);

            if (path.size() > maxDistance + 1) {
                continue;
            }

            if (path.size() > 1) {
                paths.add(new ArrayList<>(path));
            }

            List<Hex> neighbors = lastHex.getNeighbors(hexBoard.getBoard());
            for (Hex neighbor : neighbors) {
                if (!path.contains(neighbor)) {
                    List<Hex> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }

        return paths;
    }

    // ���·���Ƿ���Ч
    private boolean isValidPath(List<Hex> path, List<Hex> triPrimeHexes) {
        for (int i = 1; i < path.size(); i++) {
            Hex hex = path.get(i);
            // ����Ƿ��������ռ��
            HexBoard.OccupationInfo info = hexBoard.getOccupationInfo(hex);
            if (info != null && info.getPlayerId() != this.id) {
                return false;
            }
            // ����Ƿ��Ǳ�Ե�ġ�����������θ�sector �ȼ�Ϊ 0��
            if (hexBoard.getBoard().get(hex).getSector() == 0) {
                return false;
            }
            // ����Ƿ񾭹� Tri-Prime ϵͳ������ǣ���Ҫֹͣ
            if (triPrimeHexes.contains(hex) && i != path.size() - 1) {
                return false;
            }
        }
        return true;
    }

    
    
    
    

    
    // ���� exterminate ����
    @Override
    public void exterminate(Game game) {
        int round = currentCommandOrder.indexOf(3) + 1;
        int usage = game.getCommandUsage(round, 3);
        int invasionsAllowed = Math.max(4 - usage, 1);
        System.out.println(getName() + " can perform " + invasionsAllowed + " invasions using Exterminate command in round " + round);

        Random rand = new Random();

        Set<Ship> usedShips = new HashSet<>();

        while (invasionsAllowed > 0) {
            // ���ҿ������Ե�Ŀ��ϵͳ
            List<Hex> potentialTargets = ships.stream()
                    .filter(ship -> !usedShips.contains(ship))
                    .flatMap(ship -> ship.getShipLocation().getNeighbors(hexBoard.getBoard()).stream()
                            .filter(hex -> {
                                HexBoard.OccupationInfo info = hexBoard.getOccupationInfo(hex);
                                return (info == null || info.getPlayerId() != this.id) && hexBoard.getBoard().get(hex).getSector() > 0;
                            })
                    )
                    .distinct()
                    .collect(Collectors.toList());

            if (potentialTargets.isEmpty()) {
                System.out.println(getName() + " has no targets to invade.");
                break;
            }

            // ���ѡ��һ��Ŀ��ϵͳ
            Hex targetHex = potentialTargets.get(rand.nextInt(potentialTargets.size()));
            HexBoard.OccupationInfo targetInfo = hexBoard.getOccupationInfo(targetHex);

            // �����ڵĸ�����ѡ����õĽ���
            List<Ship> availableShips = ships.stream()
                    .filter(ship -> !usedShips.contains(ship) && ship.getShipLocation().isNeighbor(targetHex))
                    .collect(Collectors.toList());

            if (availableShips.isEmpty()) {
                invasionsAllowed--;
                continue;
            }

            // ����Ҫ�ƶ��Ľ�������������һ�ң�
            int shipsToMove = rand.nextInt(availableShips.size()) + 1;
            List<Ship> invadingShips = availableShips.subList(0, shipsToMove);

            // �ƶ�������Ŀ��ϵͳ
            for (Ship ship : invadingShips) {
                Hex currentHex = ship.getShipLocation();
                ship.move(targetHex, hexBoard.getBoard().get(targetHex));
                hexBoard.updateOccupation(targetHex, ship);
                hexBoard.clearOccupation(currentHex, ship);
                usedShips.add(ship); // ���Ϊ��ʹ��
            }

            System.out.println(getName() + " is invading hex " + targetHex + " with " + invadingShips.size() + " ships.");

            // �������Խ��
            resolveInvasion(invadingShips.size(), targetInfo, targetHex);

            invasionsAllowed--;
        }
    }

    private void resolveInvasion(int invadingShipsCount, HexBoard.OccupationInfo targetInfo, Hex targetHex) {
        int defendingShipsCount = (targetInfo != null) ? targetInfo.getNumberOfShips() : 0;

        int shipsToRemove = Math.min(invadingShipsCount, defendingShipsCount);

        // �Ƴ����ط��Ľ���
        if (targetInfo != null) {
            List<Ship> defendingShips = targetInfo.getOccupyingShips();
            for (int i = 0; i < shipsToRemove; i++) {
                Ship shipToRemove = defendingShips.get(0);
                defendingShips.remove(shipToRemove);
                hexBoard.clearOccupation(targetHex, shipToRemove);
                System.out.println("Defending ship " + shipToRemove.getIdShip() + " has been destroyed.");
            }

            // ������ط�����ʣ�ར��������ռ����Ϣ
            if (!defendingShips.isEmpty()) {
                hexBoard.updateOccupation(targetHex, defendingShips.get(0));
            }
        }

        // �Ƴ��������Ľ���
        List<Ship> invadingShips = hexBoard.getOccupationInfo(targetHex).getOccupyingShips().stream()
                .filter(ship -> ship.getOwner() == this)
                .collect(Collectors.toList());

        for (int i = 0; i < shipsToRemove; i++) {
            Ship shipToRemove = invadingShips.get(0);
            invadingShips.remove(shipToRemove);
            ships.remove(shipToRemove);
            hexBoard.clearOccupation(targetHex, shipToRemove);
            System.out.println("Invading ship " + shipToRemove.getIdShip() + " has been destroyed.");
        }

        // ������Խ��
        int remainingInvadingShips = invadingShips.size();
        int remainingDefendingShips = (targetInfo != null) ? targetInfo.getOccupyingShips().size() : 0;

        if (remainingInvadingShips > 0 && remainingDefendingShips == 0) {
            // ������ռ���ϵͳ
            System.out.println(getName() + " has taken control of hex " + targetHex);
        } else if (remainingDefendingShips > 0 && remainingInvadingShips == 0) {
            // ���ط���������Ȩ
            System.out.println("Defending player retains control of hex " + targetHex);
        } else {
            // ϵͳ���˿���
            hexBoard.clearOccupation(targetHex, null);
            System.out.println("Hex " + targetHex + " is now unoccupied.");
        }
    }


//    // ʵ�� resolveAttack ����
//    private void resolveAttack(Ship attackingShip, Hex targetHex) {
//        HexBoard.OccupationInfo targetInfo = hexBoard.getOccupationInfo(targetHex);
//        List<Ship> targetShips = targetInfo.getOccupyingShips();
//
//        // �Ƴ�˫����ͬ�����Ĵ�ֻ��ֱ��һ��û�д�ֻ
//        int attackingShipsCount = 1; // ����ֻ���ǵ��ҹ�����
//        int defendingShipsCount = targetShips.size();
//
//        int shipsToRemove = Math.min(attackingShipsCount, defendingShipsCount);
//
//        // �Ƴ����ط��Ĵ�ֻ
//        for (int i = 0; i < shipsToRemove; i++) {
//            Ship shipToRemove = targetShips.get(0);
//            targetShips.remove(shipToRemove);
//            hexBoard.clearOccupation(targetHex, shipToRemove);
//            System.out.println("Defending ship " + shipToRemove.getIdShip() + " has been destroyed.");
//        }
//
//        // �Ƴ��������Ĵ�ֻ
//        ships.remove(attackingShip);
//        hexBoard.clearOccupation(attackingShip.getShipLocation(), attackingShip);
//        System.out.println("Attacking ship " + attackingShip.getIdShip() + " has been destroyed.");
//
//        // ������ط�����ʣ�ബֻ������ռ����Ϣ
//        if (!targetShips.isEmpty()) {
//            hexBoard.updateOccupation(targetHex, targetShips.get(0));
//        } else {
//            // ������ط�û��ʣ�ബֻ��������ռ���λ��
//            Ship newShip = new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, generateShipId(ships.size() + 1));
//            ships.add(newShip);
//            hexBoard.updateOccupation(targetHex, newShip);
//            System.out.println(getName() + " has taken control of hex " + targetHex);
//        }
//    }


    
}
