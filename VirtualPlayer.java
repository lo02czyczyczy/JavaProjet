package game;

import java.util.*;
import java.util.stream.Collectors;

public class VirtualPlayer extends Player {
    // 构造函数
    public VirtualPlayer(String name, int id, Game game) {
        super(name, id, game);
    }

    // 覆盖 placeShips 方法
    @Override
    public void placeShips(HexBoard hexBoard, Set<String> occupiedSectors) {
        int shipsToPlace = 2;  // 每次放置两艘船
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();  // 获取所有卡牌及其对应的坐标

        // 过滤已占用的星系
        List<String> availableSectors = cardHexes.keySet().stream()
                .filter(sector -> !occupiedSectors.contains(sector))
                .collect(Collectors.toList());

        if (availableSectors.isEmpty()) {
            System.out.println("No available sectors for VirtualPlayer to place ships.");
            return;
        }

        // 随机选择一个星系
        Random rand = new Random();
        String selectedSector = availableSectors.get(rand.nextInt(availableSectors.size()));
        occupiedSectors.add(selectedSector);  // 标记该星系已被占用

        // 获取所选星系中的 Level I 系统
        List<Hex> levelIHexes = cardHexes.get(selectedSector).stream()
                .filter(hex -> hexBoard.getBoard().get(hex).getSector() == 1)
                .collect(Collectors.toList());

        if (levelIHexes.isEmpty()) {
            System.out.println("No Level I systems in selected sector for VirtualPlayer.");
            return;
        }

        // 随机选择一个坐标
        Hex selectedHex = levelIHexes.get(rand.nextInt(levelIHexes.size()));

        // 放置舰船并更新占领信息
        for (int i = 1; i <= shipsToPlace; i++) {
            String shipId = generateShipId(ships.size() + 1);
            Ship newShip = new Ship(this, hexBoard.getBoard().get(selectedHex), selectedHex, shipId);
            ships.add(newShip);
            hexBoard.updateOccupation(selectedHex, newShip); // 更新占领信息
            System.out.println("VirtualPlayer " + getName() + " placed a ship with ID " + shipId + " at Hex coordinates " + selectedHex);
        }
    }


    // 覆盖 chooseCommandOrder 方法
    @Override
    public List<Integer> chooseCommandOrder() {
        List<Integer> commands = Arrays.asList(1, 2, 3);
        Collections.shuffle(commands);  // 随机排序命令
        System.out.println("VirtualPlayer " + getName() + " chose command order: " + commands);
        return commands;
    }


    
    
    
    // 覆盖 expand 方法
    @Override
    public void expand(Game game) {
        int round = currentCommandOrder.indexOf(1) + 1;
        int usage = game.getCommandUsage(round, 1); // 获取该轮 "Expand" 命令的使用次数
        int shipsToAdd = Math.max(4 - usage, 1); // 计算可以添加的船只数量
        System.out.println(getName() + " can add " + shipsToAdd + " ships using Expand command in round " + round);

        // 获取玩家当前控制的系统
        List<Hex> controlledHexes = hexBoard.getOccupationMap().entrySet().stream()
                .filter(entry -> entry.getValue().getPlayerId() == this.id)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (controlledHexes.isEmpty()) {
            System.out.println(getName() + " controls no hexes to expand.");
            return;
        }

        Random rand = new Random();

        // 随机在控制的系统中放置新的舰船
        while (shipsToAdd > 0) {
            Hex targetHex = controlledHexes.get(rand.nextInt(controlledHexes.size()));
            String shipId = generateShipId(ships.size() + 1);
            Ship newShip = new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, shipId);
            ships.add(newShip);
            hexBoard.updateOccupation(targetHex, newShip); // 更新占领信息
            System.out.println(getName() + " added ship " + shipId + " to hex " + targetHex);
            shipsToAdd--;
        }
    }


    
    
    
    // 覆盖 explore 方法
    @Override
    public void explore(Game game) {
        int round = currentCommandOrder.indexOf(2) + 1;
        int usage = game.getCommandUsage(round, 2);
        int fleetMovements = Math.max(4 - usage, 1); // 可以进行的舰队移动次数
        System.out.println(getName() + " can make " + fleetMovements + " fleet movements using Explore command in round " + round);

        Random rand = new Random();

        // 获取所有可以移动的舰队（同一位置的舰船）
        Map<Hex, List<Ship>> fleets = ships.stream()
                .filter(ship -> !ship.isMoved()) // 只选择尚未移动的舰船
                .collect(Collectors.groupingBy(Ship::getShipLocation));

        List<Hex> triPrimeHexes = Arrays.asList(new Hex(0, 0, 0), new Hex(1, -1, 0), new Hex(0, -1, 1), new Hex(1, 0, -1));

        while (fleetMovements > 0 && !fleets.isEmpty()) {
            // 随机选择一个舰队
            List<Hex> fleetLocations = new ArrayList<>(fleets.keySet());
            Hex currentHex = fleetLocations.get(rand.nextInt(fleetLocations.size()));
            List<Ship> fleetShips = fleets.get(currentHex);

            // 移除已经移动过的舰船
            fleetShips = fleetShips.stream().filter(ship -> !ship.isMoved()).collect(Collectors.toList());
            if (fleetShips.isEmpty()) {
                fleets.remove(currentHex);
                continue;
            }

            // 获取可移动的位置（路径长度最多为2）
            List<List<Hex>> possiblePaths = getPossiblePaths(currentHex, 2);

            // 过滤掉不符合规则的路径
            List<List<Hex>> validPaths = possiblePaths.stream()
                    .filter(path -> isValidPath(path, triPrimeHexes))
                    .collect(Collectors.toList());

            if (validPaths.isEmpty()) {
                fleets.remove(currentHex);
                continue;
            }

            // 随机选择一条路径
            List<Hex> path = validPaths.get(rand.nextInt(validPaths.size()));
            Hex targetHex = path.get(path.size() - 1);

            // 移动舰队
            for (Ship ship : fleetShips) {
                Hex previousHex = ship.getShipLocation();
                ship.move(targetHex, hexBoard.getBoard().get(targetHex));
                hexBoard.updateOccupation(targetHex, ship);
                hexBoard.clearOccupation(previousHex, ship);
                ship.setMoved(true); // 标记为已移动
            }

            System.out.println(getName() + " moved fleet from " + currentHex + " to " + targetHex);

            // 更新舰队信息
            fleets.remove(currentHex);
            fleetMovements--;
        }

        // 重置舰船的移动状态（如果需要在回合结束后）
        // ships.forEach(ship -> ship.setMoved(false));
    }

 // 获取可能的路径
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

    // 检查路径是否有效
    private boolean isValidPath(List<Hex> path, List<Hex> triPrimeHexes) {
        for (int i = 1; i < path.size(); i++) {
            Hex hex = path.get(i);
            // 检查是否被其他玩家占据
            HexBoard.OccupationInfo info = hexBoard.getOccupationInfo(hex);
            if (info != null && info.getPlayerId() != this.id) {
                return false;
            }
            // 检查是否是边缘的“半个”六边形格（sector 等级为 0）
            if (hexBoard.getBoard().get(hex).getSector() == 0) {
                return false;
            }
            // 检查是否经过 Tri-Prime 系统，如果是，需要停止
            if (triPrimeHexes.contains(hex) && i != path.size() - 1) {
                return false;
            }
        }
        return true;
    }

    
    
    
    

    
    // 覆盖 exterminate 方法
    @Override
    public void exterminate(Game game) {
        int round = currentCommandOrder.indexOf(3) + 1;
        int usage = game.getCommandUsage(round, 3);
        int invasionsAllowed = Math.max(4 - usage, 1);
        System.out.println(getName() + " can perform " + invasionsAllowed + " invasions using Exterminate command in round " + round);

        Random rand = new Random();

        Set<Ship> usedShips = new HashSet<>();

        while (invasionsAllowed > 0) {
            // 查找可以侵略的目标系统
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

            // 随机选择一个目标系统
            Hex targetHex = potentialTargets.get(rand.nextInt(potentialTargets.size()));
            HexBoard.OccupationInfo targetInfo = hexBoard.getOccupationInfo(targetHex);

            // 从相邻的格子中选择可用的舰船
            List<Ship> availableShips = ships.stream()
                    .filter(ship -> !usedShips.contains(ship) && ship.getShipLocation().isNeighbor(targetHex))
                    .collect(Collectors.toList());

            if (availableShips.isEmpty()) {
                invasionsAllowed--;
                continue;
            }

            // 决定要移动的舰船数量（至少一艘）
            int shipsToMove = rand.nextInt(availableShips.size()) + 1;
            List<Ship> invadingShips = availableShips.subList(0, shipsToMove);

            // 移动舰船到目标系统
            for (Ship ship : invadingShips) {
                Hex currentHex = ship.getShipLocation();
                ship.move(targetHex, hexBoard.getBoard().get(targetHex));
                hexBoard.updateOccupation(targetHex, ship);
                hexBoard.clearOccupation(currentHex, ship);
                usedShips.add(ship); // 标记为已使用
            }

            System.out.println(getName() + " is invading hex " + targetHex + " with " + invadingShips.size() + " ships.");

            // 处理侵略结果
            resolveInvasion(invadingShips.size(), targetInfo, targetHex);

            invasionsAllowed--;
        }
    }

    private void resolveInvasion(int invadingShipsCount, HexBoard.OccupationInfo targetInfo, Hex targetHex) {
        int defendingShipsCount = (targetInfo != null) ? targetInfo.getNumberOfShips() : 0;

        int shipsToRemove = Math.min(invadingShipsCount, defendingShipsCount);

        // 移除防守方的舰船
        if (targetInfo != null) {
            List<Ship> defendingShips = targetInfo.getOccupyingShips();
            for (int i = 0; i < shipsToRemove; i++) {
                Ship shipToRemove = defendingShips.get(0);
                defendingShips.remove(shipToRemove);
                hexBoard.clearOccupation(targetHex, shipToRemove);
                System.out.println("Defending ship " + shipToRemove.getIdShip() + " has been destroyed.");
            }

            // 如果防守方还有剩余舰船，更新占领信息
            if (!defendingShips.isEmpty()) {
                hexBoard.updateOccupation(targetHex, defendingShips.get(0));
            }
        }

        // 移除进攻方的舰船
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

        // 检查侵略结果
        int remainingInvadingShips = invadingShips.size();
        int remainingDefendingShips = (targetInfo != null) ? targetInfo.getOccupyingShips().size() : 0;

        if (remainingInvadingShips > 0 && remainingDefendingShips == 0) {
            // 攻击方占领该系统
            System.out.println(getName() + " has taken control of hex " + targetHex);
        } else if (remainingDefendingShips > 0 && remainingInvadingShips == 0) {
            // 防守方保留控制权
            System.out.println("Defending player retains control of hex " + targetHex);
        } else {
            // 系统无人控制
            hexBoard.clearOccupation(targetHex, null);
            System.out.println("Hex " + targetHex + " is now unoccupied.");
        }
    }


//    // 实现 resolveAttack 方法
//    private void resolveAttack(Ship attackingShip, Hex targetHex) {
//        HexBoard.OccupationInfo targetInfo = hexBoard.getOccupationInfo(targetHex);
//        List<Ship> targetShips = targetInfo.getOccupyingShips();
//
//        // 移除双方相同数量的船只，直到一方没有船只
//        int attackingShipsCount = 1; // 这里只考虑单艘攻击船
//        int defendingShipsCount = targetShips.size();
//
//        int shipsToRemove = Math.min(attackingShipsCount, defendingShipsCount);
//
//        // 移除防守方的船只
//        for (int i = 0; i < shipsToRemove; i++) {
//            Ship shipToRemove = targetShips.get(0);
//            targetShips.remove(shipToRemove);
//            hexBoard.clearOccupation(targetHex, shipToRemove);
//            System.out.println("Defending ship " + shipToRemove.getIdShip() + " has been destroyed.");
//        }
//
//        // 移除攻击方的船只
//        ships.remove(attackingShip);
//        hexBoard.clearOccupation(attackingShip.getShipLocation(), attackingShip);
//        System.out.println("Attacking ship " + attackingShip.getIdShip() + " has been destroyed.");
//
//        // 如果防守方还有剩余船只，更新占领信息
//        if (!targetShips.isEmpty()) {
//            hexBoard.updateOccupation(targetHex, targetShips.get(0));
//        } else {
//            // 如果防守方没有剩余船只，攻击方占领该位置
//            Ship newShip = new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, generateShipId(ships.size() + 1));
//            ships.add(newShip);
//            hexBoard.updateOccupation(targetHex, newShip);
//            System.out.println(getName() + " has taken control of hex " + targetHex);
//        }
//    }


    
}
