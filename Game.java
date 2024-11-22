package game;

import java.util.*;
import java.awt.EventQueue; 
import java.util.stream.Collectors;


public class Game {
    private List<Player> players;
    private List<Sector> sectors;
    private int turnCounter;
    private int maxTurns; 
    private Player startPlayer;
    private ScoreManager scoreManager; // 添加对ScoreManager的引用
    private ScoreBoard scoreBoard;
    private HexBoard hexBoard;  
    private HexBoardDisplay hexBoardDisplay;  // 添加 HexBoardDisplay
    private OccupationDisplay occupationDisplay;
    private int[][] commandMatrix = new int[3][3]; // 3x3矩阵，索引0对应Expand，1对应Explore，2对应Exterminate


 // 构造函数
    public Game(int maxTurns, Player startPlayer, List<Player> players) {
        this.players = players;  // 使用传入的玩家列表
        this.sectors = new ArrayList<>();
        this.turnCounter = 0;
        this.maxTurns = maxTurns;
        this.startPlayer = startPlayer;  // 可以在之后设置
        this.hexBoard = new HexBoard();  // 创建 HexBoard
        this.hexBoardDisplay = new HexBoardDisplay(hexBoard);
        this.occupationDisplay = new OccupationDisplay(hexBoard);
        this.scoreManager = new ScoreManager(players, this); // 初始化ScoreManager
    }

    
    // 方法
    public void startGame() {
        EventQueue.invokeLater(() -> hexBoardDisplay.setVisible(true));
        EventQueue.invokeLater(() -> occupationDisplay.displayOccupation()); // 显示占领情况
        String cardInfo = hexBoard.getCardSectorInfo();
        System.out.println(cardInfo);
        this.turnCounter = 1;  // 游戏开始，设置回合计数为1
        this.scoreBoard = new ScoreBoard(scoreManager); // 初始化分数板
        
        // 在游戏开始时放置舰船
        System.out.println("Starting ship placement...");

        // 顺时针放置舰船
        Set<String> occupiedSectors = new HashSet<>();  // 存储已占用的星系
        for (Player player : players) {
            System.out.println("It's " + player.getName() + "'s turn to place ships.");
            player.placeShips(occupiedSectors);
            occupationDisplay.displayOccupation(); // 更新显示占领情况
        }

        // 逆时针放置舰船
        for (int i = players.size() - 1; i >= 0; i--) {
            Player player = players.get(i);
            System.out.println("It's " + player.getName() + "'s turn to place ships.");
            player.placeShips(occupiedSectors);
            occupationDisplay.displayOccupation(); // 更新显示占领情况
        }
        
        summarizeShipPlacement();

        System.out.println("Game started. It's " + startPlayer.getName() + "'s turn.");
        nextTurn();  // 开始第一回合的执行
        scoreManager.calculateRoundScores(this); // 开始游戏时计算第一轮分数
    }

    public void nextTurn() {
        System.out.println("Turn " + turnCounter + " starts.");
        // 确保为每个玩家设置命令顺序
        for (Player player : players) {
            player.setCommandOrder();
            player.endTurn();
        }
        this.updateCommandMatrix();
       
        executeRound();  // 开始执行每个玩家的命令
        scoreManager.calculateRoundScores(this); // 每个回合计算分数
        scoreBoard.updateScores(); // 更新分数板
        endRound();  // 调用endRound处理回合结束的逻辑
    }

    
    public void endRound() {
    	
        sustainShips();          // 维持飞船并移除无法维持的飞船
        chooseSectorCards();     // 让玩家选择星区卡牌进行得分
        
        // 显示并计算当前回合的得分
        calculateScores();
        scoreManager.calculateRoundScores(this); // 计算并更新每个玩家的分数
        scoreBoard.updateScores(); // 更新分数板显示
        System.out.println("**************************************************************************************************************************");

        // 检查是否达到了最大回合数
        if (turnCounter >= maxTurns) {
            endGame();  // 如果达到最大回合数，结束游戏
        } else {
        	turnCounter++;  // 回合计数加一
            nextTurn();  // 否则，开始下一个回合
        }
    }

    
    public void summarizeShipPlacement() {
    	System.out.println("\n**************************************************************************************************************************");
        System.out.println("\nSummary of Ship Placement:");

        for (Player player : players) {
            System.out.println(player.getName() + " placed ships at the following locations:");
            Map<String, Map<Hex, List<String>>> shipPlacementSummary = new HashMap<>();

            // 遍历每个玩家的舰船
            for (Ship ship : player.getShips()) {
                String sector = getSectorLabelForHex(ship.getShipLocation()); // 获取对应的星系名称
                Hex position = ship.getShipLocation();
                String shipId = ship.getIdShip();

                shipPlacementSummary.putIfAbsent(sector, new HashMap<>());
                Map<Hex, List<String>> sectorMap = shipPlacementSummary.get(sector);
                sectorMap.putIfAbsent(position, new ArrayList<>());
                sectorMap.get(position).add(shipId);
            }

            // 输出玩家的舰船放置信息
            for (String sector : shipPlacementSummary.keySet()) {
                Map<Hex, List<String>> sectorMap = shipPlacementSummary.get(sector);
                for (Hex hex : sectorMap.keySet()) {
                    List<String> shipsAtHex = sectorMap.get(hex);
                    System.out.println("  - In sector " + sector + " at coordinates " + hex + ": " + String.join(", ", shipsAtHex));
                }
            }
            System.out.println();
        }
        System.out.println("**************************************************************************************************************************");
    }

    // 获取某个坐标所属的星系标签的方法
    private String getSectorLabelForHex(Hex hex) {
        // 遍历所有星系卡牌及其对应坐标来找到匹配的星系名称
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();
        for (Map.Entry<String, List<Hex>> entry : cardHexes.entrySet()) {
            if (entry.getValue().contains(hex)) {
                return entry.getKey();
            }
        }
        return "Unknown";  // 找不到对应星系的情况
    }
    
    // 更新矩阵
    public void updateCommandMatrix() {
        for (int i = 0; i < 3; i++) {
            Arrays.fill(commandMatrix[i], 0);
        }

        for (Player player : players) {
            List<Integer> commands = player.getCurrentCommandOrder();
            for (int position = 0; position < 3; position++) {
                int command = commands.get(position) - 1;
                commandMatrix[command][position]++;
            }
        }

        System.out.println("Command Usage Matrix (The rows represent the types of cards, and the columns represent the rounds.):");
        for (int i = 0; i < 3; i++) {
            System.out.println(Arrays.toString(commandMatrix[i]));
        }
    }

    // 获取特定命令在特定轮次的使用次数
    public int getCommandUsage(int round, int command) {
    	return commandMatrix[command - 1][round - 1];
    }

    
    public void executeRound() {
        System.out.println("Executing round for all players");

        for (int commandPos = 0; commandPos < 3; commandPos++) { // 命令位置从0到2，对应于每位玩家的命令1、2、3
            for (Player player : players) {
                int commandIndex = player.getCurrentCommandOrder().get(commandPos); // 获取当前玩家此位置的命令索引
                System.out.println("Executing command " + commandIndex + " for " + player.getName());
                executeCommands(player, commandIndex); // 执行指定的命令
            }
        }
    }

    private void executeCommands(Player player, Integer commandIndex) {
        switch (commandIndex) {
            case 1:
                player.expand(this);
                break;
            case 2:
                player.explore(this);
                break;
            case 3:
                player.exterminate(this);
                break;
            default:
                System.out.println("Unknown command.");
                break;
        }
    }

    
    //维持船只
    public void sustainShips() {
        Map<Hex, HexBoard.OccupationInfo> occupationMap = hexBoard.getOccupationMap();

        // 迭代每个六边形和它的占领信息
        Iterator<Map.Entry<Hex, HexBoard.OccupationInfo>> it = occupationMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Hex, HexBoard.OccupationInfo> entry = it.next();
            Hex hex = entry.getKey();
            HexBoard.OccupationInfo occupationInfo = entry.getValue();
            Sector sector = hexBoard.getBoard().get(hex);
            int maxShipsSustained = 1 + sector.getSector();  // 假设getSector()返回的是系统等级

            List<Ship> ships = occupationInfo.getOccupyingShips();
            if (ships.size() > maxShipsSustained) {
                // 如果舰船数量超出了最大维持量，截断列表
                List<Ship> toRemove = new ArrayList<>(ships.subList(maxShipsSustained, ships.size()));
                ships.removeAll(toRemove);  // 安全移除超出部分的舰船
                // 此处可以处理舰船返回到供给区的逻辑
            }

            // 如果没有舰船占据此六边形，清空占领信息
            if (ships.isEmpty()) {
                it.remove();  // 安全地从映射中移除当前项
                hexBoard.clearOccupation(hex, null);  // 假设这个方法可以处理清除占领
            }
        }
    }


    //玩家选得分扇区
    public void chooseSectorCards() {
        List<String> availableCards = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));

        System.out.println("Now pick a sector card to calculate the score for this round.");
        for (Player player : players) {
            // 检查是否有可用的星区卡牌
            if (availableCards.isEmpty()) {
                System.out.println("No available sectors to choose for scoring.");
                break;
            }

            String chosenCard = player.chooseSectorCard(availableCards);

            availableCards.remove(chosenCard);
            player.calculatePlayerScore(chosenCard, hexBoard);  // 计算玩家在选择的星区中的得分
        }
    }


    
    public void calculateScores() {
        System.out.println("Scores for round " + turnCounter + ":");
        
        // 显示每位玩家在这一轮的得分
        for (Player player : players) {
            System.out.println("Player " + player.getName() + " scored " + player.getRoundScore() + " in round " + turnCounter);
        }

        // 显示游戏进行到目前的总分
        System.out.println("Total scores after " + turnCounter + " rounds:");
        for (Player player : players) {
            System.out.println("Player " + player.getName() + ": " + player.getScore());
        }
    }

    
    public void endGame() {
        System.out.println("Game ends after " + maxTurns + " turns.");
        determineWinner();
    }

    public void determineWinner() {
        int highestScore = players.stream()
                                  .mapToInt(Player::getScore)
                                  .max()
                                  .orElse(Integer.MIN_VALUE);  // 找出最高得分

        List<Player> winners = players.stream()
                                      .filter(p -> p.getScore() == highestScore)
                                      .collect(Collectors.toList());  // 找出所有得分最高的玩家

        if (winners.size() == 1) {
            System.out.println("The winner is " + winners.get(0).getName() + " with a score of " + highestScore + "!");
        } else {
            System.out.println("It's a tie!");
            for (Player winner : winners) {
                System.out.println(winner.getName() + " scored " + winner.getScore());
            }
        }
    }


    // Getters and Setters
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }

    public Player getStartPlayer() {
        return startPlayer;
    }

    public void setStartPlayer(Player startPlayer) {
        this.startPlayer = startPlayer;
    }
    
    public HexBoard getHexBoard() {
        return this.hexBoard;
    }

    
    
    
    
    
    
    //main
    public static void main(String[] args) {
        // 初始化玩家列表
        List<Player> players = new ArrayList<>();
        System.out.println("Welcome to the game! Please enter player details.");

        // 创建 Game 对象
        Game game = new Game(9, null, players);  // 初始时没有 startPlayer，暂时传 null

        Set<Integer> availableIds = new HashSet<>(Arrays.asList(1, 2, 3));

        // 提示用户输入玩家数量（2 或 3）
        Scanner scanner = new Scanner(System.in);
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 3) {
            try {
                System.out.print("Enter number of players (2 or 3): ");
                numPlayers = Integer.parseInt(scanner.nextLine());
                if (numPlayers < 2 || numPlayers > 3) {
                    System.out.println("Please enter a valid number of players (2 or 3).");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }

        // 创建玩家并传入正确的 game 对象
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for player " + (i + 1) + ": ");
            String name = scanner.nextLine().trim();

            int id = getAvailableId(availableIds, scanner);

            if (name.equalsIgnoreCase("VirtualPlayer")) {
                // 创建 VirtualPlayer
                VirtualPlayer virtualPlayer = new VirtualPlayer("VirtualPlayer" + id, id, game);
                players.add(virtualPlayer);
            } else {
                // 创建普通玩家
                Player player = new Player(name, id, game);
                players.add(player);
            }
        }

        // 自动添加虚拟玩家，直到玩家数量达到3人
        while (players.size() < 3) {
            int id = availableIds.iterator().next();
            availableIds.remove(id);
            VirtualPlayer virtualPlayer = new VirtualPlayer("VirtualPlayer" + id, id, game);
            players.add(virtualPlayer);
            System.out.println("Added VirtualPlayer" + id + " to the game.");
        }

        // 设置游戏的玩家列表
        game.setPlayers(players);

        // 查找 ID 为 1 的玩家作为开始玩家
        Player startPlayer = players.stream()
                .filter(p -> p.getId() == 1)
                .findFirst()
                .orElse(null);

        if (startPlayer == null) {
            System.out.println("No player with ID 1 found, please ensure one of the players has ID 1.");
            return; // 如果没有找到 ID 为 1 的玩家，结束程序
        }

        // 设置游戏的开始玩家
        game.setStartPlayer(startPlayer);

        // 开始游戏
        game.startGame();  // 开始游戏逻辑

        // 模拟游戏的回合进行
        while (game.getTurnCounter() <= game.getMaxTurns()) {
            game.nextTurn();
        }
    }


    // 获取可用的玩家 ID
    private static int getAvailableId(Set<Integer> availableIds, Scanner scanner) {
        int id = 0;
        while (id == 0) {
            try {
                System.out.print("Available IDs: " + availableIds + "\nEnter ID for this player: ");
                id = Integer.parseInt(scanner.nextLine());
                if (!availableIds.contains(id)) {
                    System.out.println("ID not available. Please choose from " + availableIds);
                    id = 0;
                } else {
                    availableIds.remove(id);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }
        return id;
    }

}