package game;

import java.util.*;
import game.HexBoard.OccupationInfo;
import java.util.stream.Collectors;


public class Player {
    private String name;
    public List<Ship> ships;
    private List<CommandCard> commandCards;
    private List<Sector> controlledSectors;
    private int score;
    private int roundScore; // 当轮得分
    public int id;
    private String color;  // 新增属性：颜色
    private static final Scanner scanner = new Scanner(System.in); // 共享 Scanner 实例
    public HexBoard hexBoard;  
    public Game game; 
    public List<Integer> currentCommandOrder;  // 存储当前回合的命令顺序

    // 构造函数
    public Player(String name, int id, Game game) {
        this.game = game;
        this.ships = new ArrayList<>();
        this.commandCards = new ArrayList<>();
        this.controlledSectors = new ArrayList<>();
        this.score = 0;
        this.name = name;
        this.id = id;
        this.color = assignColor(id);  // 根据 id 分配颜色
        this.hexBoard = game.getHexBoard(); // 从 Game 对象获取 hexBoard
        System.out.println("Player created: " + name + " with id: " + id);
    }

//    // 方法从用户获取名字
//    private void setNameFromUser() {
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Please enter your name: ");
//        name = scanner.nextLine();
//    }
//    
//    // 方法从用户获取id
//    private void setIdFromUser() {
//        boolean validId = false;
//        while (!validId) {
//            try {
//                System.out.print("Please enter your id (choose from 1,2,3): ");
//                this.id = Integer.parseInt(scanner.nextLine());
//                if (this.id < 1 || this.id > 3) {
//                    System.out.println("Invalid ID. Please choose 1, 2, or 3.");
//                } else {
//                    validId = true;
//                }
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid input. Please enter numeric values only.");
//            }
//        }
//    }
    
    // 方法
    
    // 分配颜色的方法
    private String assignColor(int id) {
        switch (id) {
            case 1:
                return "Red";
            case 2:
                return "Blue";
            case 3:
                return "Green";
            default:
                throw new IllegalArgumentException("Invalid player ID");
        }
    }

    public String getColor() {
        return color;
    }
    
    
    // placeShips 方法
    public void placeShips(Set<String> occupiedSectors) {
        int shipsToPlace = 2; // 玩家初始放置2艘船
        Map<String, List<Hex>> cardHexes = this.hexBoard.generateCardHexes(); // 使用 this.hexBoard

        System.out.println("Player " + name + " is placing ships with id: " + id);

        // 显示可用的星区卡牌
        List<String> availableSectors = new ArrayList<>(cardHexes.keySet());
        availableSectors.removeAll(occupiedSectors);
        if (availableSectors.isEmpty()) {
            System.out.println("No available sectors to choose for ship placement.");
            return;
        }

        System.out.println("Available sectors for ship placement: " + availableSectors);
        System.out.println("Please choose a sector card from the available sectors:");
        String selectedSector = scanner.nextLine().trim(); // 使用共享的 scanner

        while (!availableSectors.contains(selectedSector)) {
            System.out.println("Invalid sector card. Please choose again from: " + availableSectors);
            selectedSector = scanner.nextLine().trim(); // 使用共享的 scanner
        }

        occupiedSectors.add(selectedSector); // 标记该星区卡牌已被占用

        // 获取所选星区中的 Level I 系统
        List<Hex> levelIHexes = cardHexes.get(selectedSector).stream()
                .filter(hex -> this.hexBoard.getBoard().get(hex).getSector() == 1)
                .collect(Collectors.toList());

        if (levelIHexes.isEmpty()) {
            System.out.println("No Level I systems in the selected sector.");
            return;
        }

        System.out.println("Available Level I hexes for ship placement: " + levelIHexes);
        System.out.println("Please enter the coordinates of the Level I hex to place your ships (e.g., '1 -5 4'):");
        String input = scanner.nextLine().trim(); // 使用共享的 scanner
        String[] parts = input.split("\\s+");

        while (parts.length != 3) {
            System.out.println("Invalid input. Please enter three coordinates (e.g., '1 -5 4'):");
            input = scanner.nextLine().trim(); // 使用共享的 scanner
            parts = input.split("\\s+");
        }

        try {
            int q = Integer.parseInt(parts[0]);
            int r = Integer.parseInt(parts[1]);
            int s = Integer.parseInt(parts[2]);

            if (q + r + s != 0) {
                System.out.println("Invalid coordinates. q + r + s must equal 0.");
                return;
            }

            Hex selectedHex = new Hex(q, r, s);

            if (!levelIHexes.contains(selectedHex)) {
                System.out.println("The selected hex is not a Level I system in the chosen sector.");
                return;
            }

            // 放置舰船并更新占领信息
            for (int i = 1; i <= shipsToPlace; i++) {
                String shipId = generateShipId(ships.size() + 1);
                Ship newShip = new Ship(this, this.hexBoard.getBoard().get(selectedHex), selectedHex, shipId);
                ships.add(newShip);
                this.hexBoard.updateOccupation(selectedHex, newShip); // 更新占领信息
                System.out.println("Placed ship with ID " + shipId + " at Hex coordinates " + selectedHex);
            }

            // 添加调试信息
            System.out.println(name + " now controls the following hexes after placement:");
            this.hexBoard.getOccupationMap().forEach((hex, occupationInfo) -> {
                if (occupationInfo.getPlayerId() == this.id) {
                    System.out.println("Controlled hex: " + hex);
                }
            });

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid integers for coordinates.");
        }
    }




    // 生成舰船 ID 的方法
    public String generateShipId(int shipNumber) {
        return id + "-" + shipNumber;  // 返回格式为 "玩家ID-编号"，例如 "1-1"
    }
    
    public void endTurn() {
        ships.forEach(ship -> ship.setMoved(false));
    }
    
    //选指令卡
    public List<Integer> chooseCommandOrder() {
    	System.out.println("Choosing command order for " + getName());
        List<Integer> order = new ArrayList<>();
        boolean validInput = false;

        System.out.println("Enter the order of command cards (1 for EXPAND, 2 for EXPLORE, 3 for EXTERMINATE):");
        System.out.println("Available commands: 1 - EXPAND, 2 - EXPLORE, 3 - EXTERMINATE :");

        while (!validInput) {
            try {
                for (int i = 1; i <= 3; i++) {
                    System.out.print("Enter command " + i + ": ");
                    String commandChoice = scanner.nextLine(); // 改为 nextLine
                    if (commandChoice.equalsIgnoreCase("CHEN")) {
                    	System.out.print("Congratulations on discovering the Easter egg in this game!");
                        order.clear();
                        order.add(999); // 使用999作为特殊标志
                        validInput = true;
                        break;
                    }
                    int cmd = Integer.parseInt(commandChoice);
                    if (cmd < 1 || cmd > 3) {
                        throw new IllegalArgumentException("Invalid command choice. Please enter 1, 2, or 3.");
                    }
                    order.add(cmd);
                }
                validInput = true; // 如果所有输入都是有效的，设置 validInput 为 true，结束循环
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numeric values only.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage()); // 输出具体的错误信息
            }
        }
        return order;
    }
    

    
    // 方法用于关闭 Scanner，只有当整个程序结束时才调用
    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    
    
    
    
    
    
    
    
    //EXPAND卡牌
    public void expand(Game game) {
        int round = currentCommandOrder.indexOf(1) + 1;
        int usage = game.getCommandUsage(round, 1); // 获取该轮"Expand"命令的使用次数
        int shipsToAdd = Math.max(4 - usage, 1); // 计算可以添加的船只数量
        System.out.println(name + " can add " + shipsToAdd + " ships using Expand command in round " + round);

        // 显示玩家当前控制的系统和他们可用来放置新船只的位置
        List<Hex> controlledHexes = new ArrayList<>();
        hexBoard.getOccupationMap().forEach((hex, occupationInfo) -> {
            System.out.println("Hex: " + hex + ", occupationInfo.getPlayerId(): " + occupationInfo.getPlayerId() + ", player id: " + this.id);
            if (occupationInfo.getPlayerId() == this.id) {
                controlledHexes.add(hex);
            }
        });

        System.out.println("You control the following hexes:");
        for (Hex hex : controlledHexes) {
            System.out.println("Controlled hex: " + hex);
        }

        System.out.println("You can distribute " + shipsToAdd + " ships among the controlled hexes or type 'skip' to skip adding ships.");
        // 使用类中共享的 scanner
        while (shipsToAdd > 0) {
            System.out.println("Enter hex coordinates (q r s) and number of ships to add, e.g., '0 0 0 2', or type 'skip':");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("skip")) {
                System.out.println("Skipping ship addition for this round.");
                break;
            }
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 4) {
                System.out.println("Invalid input. Please enter three coordinates and a number.");
                continue;
            }
            try {
                int q = Integer.parseInt(parts[0]);
                int r = Integer.parseInt(parts[1]);
                int s = Integer.parseInt(parts[2]);
                int count = Integer.parseInt(parts[3]);

                if (q + r + s != 0) {
                    System.out.println("Invalid coordinates. q + r + s must equal 0.");
                    continue;
                }

                if (count <= 0) {
                    System.out.println("Invalid ship count. Must be greater than 0.");
                    continue;
                }

                Hex targetHex = new Hex(q, r, s);
                System.out.println("You entered hex: " + targetHex);

                // 直接比较 q, r, s 值
                boolean controlsHex = false;
                for (Hex hex : controlledHexes) {
                    if (hex.q == targetHex.q && hex.r == targetHex.r && hex.s == targetHex.s) {
                        controlsHex = true;
                        break;
                    }
                }

                if (controlledHexes.contains(targetHex) && count <= shipsToAdd) {
                    // 添加舰船到指定的 hex
                    for (int i = 0; i < count; i++) {
                        Ship newShip = new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, generateShipId(ships.size() + 1));
                        ships.add(newShip);
                        hexBoard.updateOccupation(targetHex, newShip); // 更新占领信息
                    }
                    shipsToAdd -= count;
                    System.out.println("Added " + count + " ships to hex " + targetHex);
                } else {
                    System.out.println("Invalid hex or ship count. Make sure the hex is controlled and the ship count is correct.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter valid integers for coordinates and ship count.");
            }
        }

        System.out.println("All ships placed or skipped for this round.");
        System.out.println("**************************************************************************************************************************");
    }



    
    
    
    
    
    
    
    

    //EXPLORE卡牌
    public void explore(Game game) {
        int round = currentCommandOrder.indexOf(2) + 1;
        int usage = game.getCommandUsage(round, 2); // 从Game获取该轮explore命令的使用次数

        int movesPerShip = Math.max(4 - usage, 1); // 每艘船的可移动次数
        System.out.println(name + " can move each ship " + movesPerShip + " times using Explore command in round " + round);

        // 显示所有飞船及其位置
        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        // 玩家选择飞船和移动操作
        for (Ship ship : ships) {
            int remainingMoves = movesPerShip;
            Hex currentHex = ship.getShipLocation();

            System.out.println("Move Ship ID: " + ship.getIdShip() + " starting at Hex coordinates " + currentHex);

            while (remainingMoves > 0) {
                System.out.println("Enter new coordinates for move (or type 'skip'):");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("skip")) {
                    break; // 跳过此船的移动
                }

                String[] parts = input.trim().split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Invalid input. Please enter three coordinates.");
                    continue;
                }

                try {
                    int q = Integer.parseInt(parts[0]);
                    int r = Integer.parseInt(parts[1]);
                    int s = Integer.parseInt(parts[2]);

                    if (q + r + s != 0) {
                        System.out.println("Invalid coordinates. q + r + s must equal 0.");
                        continue;
                    }

                    Hex targetHex = new Hex(q, r, s);

                    if (isValidMove(currentHex, targetHex, remainingMoves)) {
                        // 移动船只
                        ship.move(targetHex, hexBoard.getBoard().get(targetHex)); // 移动船只
                        hexBoard.updateOccupation(targetHex, ship); // 更新占领信息
                        hexBoard.clearOccupation(currentHex, ship); // 清除原位置的占领信息

                        currentHex = targetHex; // 更新当前坐标
                        remainingMoves--;

                        System.out.println("Ship moved to " + targetHex);

//                        // 如果进入了Tri-Prime系统六边形，必须停止移动
//                        if (isTriPrimeHex(targetHex)) {
//                            System.out.println("Ship has entered Tri-Prime system hex and must stop moving.");
//                            break;
//                        }
                    } else {
                        System.out.println("Invalid move. Cannot move to the specified hex.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter valid integers for coordinates.");
                }
            }
        }
        System.out.println("**************************************************************************************************************************");
    }


    private boolean isValidMove(Hex from, Hex to, int remainingMoves) {
        // 检查是否相邻
        if (!from.isNeighbor(to)) {
            System.out.println("Target hex is not adjacent.");
            return false;
        }

        // 检查目标hex是否被其他玩家占领
        HexBoard.OccupationInfo occupationInfo = hexBoard.getOccupationInfo(to);
        if (occupationInfo != null && occupationInfo.getPlayerId() != this.id) {
            System.out.println("Cannot move to a hex occupied by another player.");
            return false;
        }

        // 检查是否是边缘的“半个”六边形格（sector等级为0）
        if (hexBoard.getBoard().get(to).getSector() == 0) {
            System.out.println("Cannot move to an invalid hex.");
            return false;
        }

        // 检查是否经过了Tri-Prime系统六边形
        boolean fromIsTriPrime = isTriPrimeHex(from);
        boolean toIsTriPrime = isTriPrimeHex(to);

        if (toIsTriPrime) {
            // 如果目标是Tri-Prime系统六边形，必须停止移动
            if (remainingMoves > 1) {
                System.out.println("Must stop moving after entering Tri-Prime system hex.");
                return false;
            }
        } else if (fromIsTriPrime) {
            // 从Tri-Prime系统六边形移动出来，正常处理
            // 不需要特殊处理
        } else if (isMovingThroughTriPrime(from, to)) {
            // 如果是在同一移动中试图经过Tri-Prime系统六边形，则不允许
            System.out.println("Cannot move through Tri-Prime system hex.");
            return false;
        }

        return true;
    }

    // 检查是否为Tri-Prime系统六边形
    private boolean isTriPrimeHex(Hex hex) {
        List<Hex> triPrimeHexes = Arrays.asList(
            new Hex(0, 0, 0),
            new Hex(1, -1, 0),
            new Hex(0, -1, 1),
            new Hex(1, 0, -1)
        );
        for (Hex triHex : triPrimeHexes) {
            if (hex.q == triHex.q && hex.r == triHex.r && hex.s == triHex.s) {
                return true;
            }
        }
        return false;
    }

    // 检查是否在同一移动中试图经过Tri-Prime系统六边形
    private boolean isMovingThroughTriPrime(Hex from, Hex to) {
        // 获取from和to之间的所有可能路径（由于相邻，只需检查from和to是否都邻近Tri-Prime）
        return isNeighborToTriPrime(from) && isNeighborToTriPrime(to);
    }

    // 检查一个hex是否邻近Tri-Prime系统六边形
    private boolean isNeighborToTriPrime(Hex hex) {
        for (Hex neighbor : hex.getNeighbors(hexBoard.getBoard())) {
            if (isTriPrimeHex(neighbor)) {
                return true;
            }
        }
        return false;
    }

    
    
    
    
    
    
    


    //EXTERMINATE卡牌
    public void exterminate(Game game) {
        int round = currentCommandOrder.indexOf(3) + 1;
        int usage = game.getCommandUsage(round, 3);
        int attacksAllowed = Math.max(4 - usage, 1);
        System.out.println(name + " has " + attacksAllowed + " total attacks using Exterminate command in round " + round);

        // 显示所有飞船及其位置
        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        // 玩家选择进攻操作
        while (attacksAllowed > 0) {
            System.out.println("Enter ship ID and target hex coordinates to attack (e.g., '1 0 0 0') or type 'skip' to end attacks:");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("skip")) {
                break; // 玩家选择结束进攻
            }

            String[] parts = input.trim().split("\\s+");
            if (parts.length != 4) {
                System.out.println("Invalid input. Please enter ship ID and three coordinates.");
                continue;
            }

            try {
                int shipId = Integer.parseInt(parts[0]);
                int q = Integer.parseInt(parts[1]);
                int r = Integer.parseInt(parts[2]);
                int s = Integer.parseInt(parts[3]);

                if (q + r + s != 0) {
                    System.out.println("Invalid coordinates. q + r + s must equal 0.");
                    continue;
                }

                Ship attackingShip = findShipById(shipId);
                if (attackingShip == null) {
                    System.out.println("Invalid ship ID.");
                    continue;
                }

                Hex targetHex = new Hex(q, r, s);

                if (isValidAttack(attackingShip.getShipLocation(), targetHex)) {
                    resolveAttack(attackingShip, targetHex); // 执行进攻
                    attacksAllowed--;
                } else {
                    System.out.println("Invalid attack. Cannot attack the specified hex.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter valid integers for ship ID and hex coordinates.");
            }
        }
        System.out.println("All attacks have been executed.");
        System.out.println("**************************************************************************************************************************");
    }

    private Ship findShipById(int shipId) {
        for (Ship ship : ships) {
            // 假设 ship.getIdShip() 的格式为 "playerId-shipNumber"，例如 "1-1"
            String[] idParts = ship.getIdShip().split("-");
            if (idParts.length == 2) {
                try {
                    int id = Integer.parseInt(idParts[1]);
                    if (id == shipId) {
                        return ship;
                    }
                } catch (NumberFormatException e) {
                    // 忽略格式不正确的 shipId
                }
            }
        }
        return null; // 未找到匹配的船只
    }

    private boolean isValidAttack(Hex from, Hex to) {
        if (!from.isNeighbor(to)) {
            System.out.println("Target hex is not adjacent.");
            return false;
        }

        HexBoard.OccupationInfo occupationInfo = hexBoard.getOccupationInfo(to);
        if (occupationInfo == null) {
            System.out.println("Cannot attack an unoccupied hex.");
            return false;
        }

        if (occupationInfo.getPlayerId() == this.id) {
            System.out.println("Cannot attack your own hex.");
            return false;
        }

        return true;
    }

    private void resolveAttack(Ship attackingShip, Hex targetHex) {
        OccupationInfo targetInfo = hexBoard.getOccupationInfo(targetHex);
        List<Ship> defendingShips = targetInfo.getOccupyingShips();

        // 移除防守方的一艘舰船
        if (!defendingShips.isEmpty()) {
            Ship shipToRemove = defendingShips.get(0);
            defendingShips.remove(shipToRemove);
            hexBoard.clearOccupation(targetHex, shipToRemove);
            shipToRemove.getOwner().getShips().remove(shipToRemove); // 从防守方的舰船列表中移除
            System.out.println("Defending ship " + shipToRemove.getIdShip() + " has been destroyed.");
        }

        // 移除攻击方的舰船
        ships.remove(attackingShip);
        hexBoard.clearOccupation(attackingShip.getShipLocation(), attackingShip);
        System.out.println("Attacking ship " + attackingShip.getIdShip() + " has been destroyed.");

        // 如果防守方没有剩余舰船，攻击方占领该六边形
        if (defendingShips.isEmpty()) {
            hexBoard.clearOccupation(targetHex, null); // 清除原防守方的占领信息

            // 添加一艘新的攻击方舰船到目标六边形
            Ship newShip = new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, generateShipId(ships.size() + 1));
            ships.add(newShip);
            hexBoard.updateOccupation(targetHex, newShip);

            System.out.println(name + " has taken control of hex " + targetHex);
        } else {
            // 更新防守方的占领信息
            hexBoard.updateOccupation(targetHex, defendingShips.get(0));
        }
    }


    
    
    
    
    
    
    
    
    //选扇区
    public String chooseSectorCard(List<String> availableCards) {
        System.out.println(getName() + ", choose a sector card from: " + availableCards);
        String chosenCard = scanner.nextLine().trim().toLowerCase();

        while (!availableCards.contains(chosenCard)) {
            System.out.println("Invalid card. Please choose again from: " + availableCards);
            chosenCard = scanner.nextLine().trim().toLowerCase();
        }

        return chosenCard;
    }
    
    //得分扇区计分
    public void calculatePlayerScore(String sectorCard, HexBoard hexBoard) {
        int score = 0;
        List<Hex> hexesInSector = hexBoard.generateCardHexes().get(sectorCard); // 获取选定卡牌的所有Hex坐标

        for (Hex hex : hexesInSector) {
            OccupationInfo occupationInfo = hexBoard.getOccupationInfo(hex);
            if (occupationInfo != null && occupationInfo.getPlayerId() == this.id) {
                int systemLevel = hexBoard.getBoard().get(hex).getSector();  // 获取系统等级
                score += systemLevel;  // 累加系统等级作为得分
            }
        }

        // 检查玩家是否控制了至少一个Tri-Prime的六边形，并增加额外得分
        List<Hex> triPrimeHexes = Arrays.asList(
            new Hex(0, 0, 0), 
            new Hex(1, -1, 0), 
            new Hex(0, -1, 1), 
            new Hex(1, 0, -1)
        );

        boolean controlsTriPrime = false;
        for (Hex hex : triPrimeHexes) {
            OccupationInfo occupationInfo = hexBoard.getOccupationInfo(hex);
            if (occupationInfo != null && occupationInfo.getPlayerId() == this.id) {
                controlsTriPrime = true;
                break;  // 一旦发现玩家控制了一个Tri-Prime的六边形，立即跳出循环
            }
        }

        if (controlsTriPrime) {
            score += 3;  // 玩家控制了至少一个Tri-Prime的六边形，获得3分加成
        }

        this.addScore(score);  // 更新玩家的总得分
        System.out.println("Player " + getName() + " scored " + score + " points from sector card " + sectorCard);
    }

    
    public void setRoundScore(int score) {
        this.roundScore = score;
    }

    public int getRoundScore() {
        return this.roundScore;
    }

    public void addScore(int score) {
        this.score += score;
        this.roundScore = score; // 设置当轮得分
    }

    
    
    
    
    
    
    
    
    // Getters 
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
    
    public List<Ship> getShips() {
        return ships;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public List<CommandCard> getCommandCards() {
        return commandCards;
    }

    public void setCommandCards(List<CommandCard> commandCards) {
        this.commandCards = commandCards;
    }

    public List<Sector> getControlledSectors() {
        return controlledSectors;
    }

    public void setControlledSectors(List<Sector> controlledSectors) {
        this.controlledSectors = controlledSectors;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    public void setCommandOrder() {
        this.currentCommandOrder = chooseCommandOrder();
    }

    public List<Integer> getCurrentCommandOrder() {
        return this.currentCommandOrder;
    }
}