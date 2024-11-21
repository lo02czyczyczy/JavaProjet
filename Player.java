package game;

import java.util.*;
import game.HexBoard.OccupationInfo;

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
    public void placeShips(HexBoard hexBoard, Set<String> occupiedSectors) {
        int shipsToPlace = 2;  // 每次放置两艘船
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();  // 获取所有卡牌及其对应的坐标

        String selectedSector = null;
        boolean validSector = false;

        // 玩家选择星系
        while (!validSector) {
            System.out.println("Sectors: " + cardHexes.keySet());
            System.out.print("Player " + name + ", choose a sector to place ships: ");
            selectedSector = scanner.nextLine().toLowerCase();

            if (cardHexes.containsKey(selectedSector) && !occupiedSectors.contains(selectedSector)) {
                validSector = true;
                occupiedSectors.add(selectedSector);  // 标记该星系已被占用
            } else {
                System.out.println("Invalid sector or sector already occupied. Please choose another.");
            }
        }

        // 玩家选择具体坐标
        List<Hex> availableHexes = cardHexes.get(selectedSector);
        List<Hex> levelIHexes = new ArrayList<>();
        for (Hex hex : availableHexes) {
            if (hexBoard.getBoard().get(hex).getSector() == 1) {  // 只选择 I 级系统
                levelIHexes.add(hex);
            }
        }

        if (levelIHexes.isEmpty()) {
            System.out.println("No available Level I systems in the chosen sector. Cannot place ships.");
            return;
        }

        Hex selectedHex = null;
        boolean validHex = false;

        while (!validHex) {
            System.out.println("Available Level I systems: ");
            for (Hex hex : levelIHexes) {
                System.out.println(hex);
            }

            System.out.print("Player " + name + ", choose coordinates to place 2 ships: ");
            String[] coordinates = scanner.nextLine().split(",");
            if (coordinates.length == 3) {  // 确保输入了三个部分
                try {
                    int q = Integer.parseInt(coordinates[0].trim());
                    int r = Integer.parseInt(coordinates[1].trim());
                    int s = Integer.parseInt(coordinates[2].trim());

                    if (q + r + s != 0) {  // 验证坐标是否满足六边形坐标系条件
                        System.out.println("Invalid coordinates. Coordinates must satisfy q + r + s = 0.");
                        continue;  // 如果不满足条件，继续循环，让用户重新输入
                    }

                    Hex hex = new Hex(q, r, s);
                    if (levelIHexes.contains(hex)) {
                        selectedHex = hex;
                        validHex = true;
                    } else {
                        System.out.println("Invalid coordinates. Please choose from available options.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter numeric values for coordinates.");
                }
            } else {
                System.out.println("Invalid input. Please enter coordinates in the format: q, r, s.");
            }
        }

        // 放置舰船并更新占领信息
        for (int i = 1; i <= shipsToPlace; i++) {
            String shipId = generateShipId(ships.size() + 1);
            Ship newShip = new Ship(this, hexBoard.getBoard().get(selectedHex), selectedHex, shipId);
            ships.add(newShip);
            hexBoard.updateOccupation(selectedHex, newShip); // 更新占领信息
            System.out.println("Player " + name + " placed a ship with ID " + shipId + " at Hex coordinates " + selectedHex);
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
            if (occupationInfo.getPlayerId() == this.id) {
                controlledHexes.add(hex);
                System.out.println("Controlled hex: " + hex + ", Number of ships: " + occupationInfo.getOccupyingShips().size());
            }
        });

        // 提示玩家选择在哪些系统中放置新的舰船
        System.out.println("You can distribute " + shipsToAdd + " ships among the controlled hexes.");
        int shipsPlaced = 0;
        Scanner scanner = new Scanner(System.in);

        while (shipsToAdd > 0) {
            System.out.println("Enter hex coordinates and number of ships to add (e.g., 0 0 0 2 to add 2 ships at (0,0,0)):");
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            try {
                int q = Integer.parseInt(parts[0]);
                int r = Integer.parseInt(parts[1]);
                int s = Integer.parseInt(parts[2]);
                int count = Integer.parseInt(parts[3]);
                
                Hex targetHex = new Hex(q, r, s);
                if (controlledHexes.contains(targetHex) && count <= shipsToAdd && count > 0) {
                    // Add ships to the specified hex
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
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter valid hex coordinates and a number.");
            }
        }

        System.out.println("All ships placed for this round.");
        System.out.println("**************************************************************************************************************************");
    }


    //EXPLORE卡牌
    public void explore(Game game) {
    	int round = currentCommandOrder.indexOf(2) + 1;
        int usage = game.getCommandUsage(round,2); // 从Game获取该轮explore命令的使用次数

        int movesPerShip = Math.max(4 - usage, 1); // 每艘船的可移动次数
        System.out.println(name + " can move each ship " + movesPerShip + " times using Explore command in round " + round);

        // 显示所有飞船及其位置
        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        // 玩家选择飞船和移动操作
        for (Ship ship : ships) {
            System.out.println("Move Ship ID: " + ship.getIdShip() + " at Hex coordinates " + ship.getShipLocation());

            for (int i = 0; i < movesPerShip; i++) {
                System.out.println("Enter new coordinates for move " + (i+1) + " (or skip):");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("skip")) {
                    break; // 跳过此船的移动
                }

                String[] parts = input.split(" ");
                try {
                    int q = Integer.parseInt(parts[0]);
                    int r = Integer.parseInt(parts[1]);
                    int s = Integer.parseInt(parts[2]);
                    Hex targetHex = new Hex(q, r, s);

                    // 检查目标Hex是否为有效移动选项
                    if (isValidMove(ship.getShipLocation(), targetHex)) {
                        ship.move(targetHex, hexBoard.getBoard().get(targetHex)); // 移动船只
                        hexBoard.updateOccupation(targetHex, ship); // 更新占领信息
                        hexBoard.clearOccupation(ship.getShipLocation(), ship); // 清除原位置的占领信息
                    } else {
                        System.out.println("Invalid move. Hex occupied by other player or out of reach.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter valid hex coordinates.");
                }
            }
        }
        System.out.println("**************************************************************************************************************************");
    }

    private boolean isValidMove(Hex from, Hex to) {
        // 检查是否相邻以及目标hex是否被其他玩家占领
        if (from.isNeighbor(to) && hexBoard.getOccupationInfo(to) == null) {
            return true;
        } else {
            return false;
        }
    }


    //EXTERMINATE卡牌
    public void exterminate(Game game) {
    	int round = currentCommandOrder.indexOf(3) + 1;
        int usage = game.getCommandUsage(round,3); // 从Game获取该轮exterminate命令的使用次数

        int attacksAllowed = Math.max(4 - usage, 1); // 玩家的总进攻次数
        System.out.println(name + " has " + attacksAllowed + " total attacks using Exterminate command in round " + round);

        // 显示所有飞船及其位置
        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        // 玩家选择飞船和进攻操作
        while (attacksAllowed > 0) {
            System.out.println("Select a ship and an adjacent hex to attack (or type 'skip' to end attacks):");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("skip")) {
                break; // 玩家选择结束进攻
            }

            String[] parts = input.split(" ");
            try {
                int shipId = Integer.parseInt(parts[0]);
                int q = Integer.parseInt(parts[1]);
                int r = Integer.parseInt(parts[2]);
                int s = Integer.parseInt(parts[3]);
                Ship attackingShip = findShipById(shipId);
                Hex targetHex = new Hex(q, r, s);

                if (attackingShip != null && isValidAttack(attackingShip.getShipLocation(), targetHex)) {
                    resolveAttack(attackingShip, targetHex); // 执行进攻
                    attacksAllowed--;
                } else {
                    System.out.println("Invalid attack. Check if target is adjacent and occupied by enemy ships.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter valid ship ID and hex coordinates.");
            }
        }
        System.out.println("All attacks have been executed.");
        System.out.println("**************************************************************************************************************************");
    }

    private Ship findShipById(int shipId) {
        return ships.stream()
                    .filter(ship -> Integer.parseInt(ship.getIdShip().split("-")[1]) == shipId)
                    .findFirst()
                    .orElse(null);
    }

    private boolean isValidAttack(Hex from, Hex to) {
        return from.isNeighbor(to) && hexBoard.getOccupationInfo(to) != null &&
               hexBoard.getOccupationInfo(to).getPlayerId() != this.id;
    }

    private void resolveAttack(Ship attackingShip, Hex targetHex) {
    	OccupationInfo targetInfo = hexBoard.getOccupationInfo(targetHex);
        // Assume that both the attacker and the target lose one ship
        if (targetInfo.getNumberOfShips() > 1) {
            targetInfo.removeShip(targetInfo.getOccupyingShips().get(0)); // Remove one ship from target
            System.out.println("One ship from " + targetHex + " has been destroyed.");
        } else {
            hexBoard.clearOccupation(targetHex, targetInfo.getOccupyingShips().get(0)); // Clear occupation if only one ship
            System.out.println(targetHex + " is now unoccupied.");
        }
        // Removing one ship of the attacker
        ships.remove(attackingShip);
        System.out.println("Attacking ship " + attackingShip.getIdShip() + " has been destroyed.");
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
