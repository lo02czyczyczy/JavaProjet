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
    private ScoreManager scoreManager; // ��Ӷ�ScoreManager������
    private ScoreBoard scoreBoard;
    private HexBoard hexBoard;  
    private HexBoardDisplay hexBoardDisplay;  // ��� HexBoardDisplay
    private OccupationDisplay occupationDisplay;
    private int[][] commandMatrix = new int[3][3]; // 3x3��������0��ӦExpand��1��ӦExplore��2��ӦExterminate


 // ���캯��
    public Game(int maxTurns, Player startPlayer, List<Player> players) {
        this.players = players;  // ʹ�ô��������б�
        this.sectors = new ArrayList<>();
        this.turnCounter = 0;
        this.maxTurns = maxTurns;
        this.startPlayer = startPlayer;  // ������֮������
        this.hexBoard = new HexBoard();  // ���� HexBoard
        this.hexBoardDisplay = new HexBoardDisplay(hexBoard);
        this.occupationDisplay = new OccupationDisplay(hexBoard);
        this.scoreManager = new ScoreManager(players, this); // ��ʼ��ScoreManager
    }

    
    // ����
    public void startGame() {
        EventQueue.invokeLater(() -> hexBoardDisplay.setVisible(true));
        EventQueue.invokeLater(() -> occupationDisplay.displayOccupation()); // ��ʾռ�����
        String cardInfo = hexBoard.getCardSectorInfo();
        System.out.println(cardInfo);
        this.turnCounter = 1;  // ��Ϸ��ʼ�����ûغϼ���Ϊ1
        this.scoreBoard = new ScoreBoard(scoreManager); // ��ʼ��������
        
        // ����Ϸ��ʼʱ���ý���
        System.out.println("Starting ship placement...");

        // ˳ʱ����ý���
        Set<String> occupiedSectors = new HashSet<>();  // �洢��ռ�õ���ϵ
        for (Player player : players) {
            System.out.println("It's " + player.getName() + "'s turn to place ships.");
            player.placeShips(occupiedSectors);
            occupationDisplay.displayOccupation(); // ������ʾռ�����
        }

        // ��ʱ����ý���
        for (int i = players.size() - 1; i >= 0; i--) {
            Player player = players.get(i);
            System.out.println("It's " + player.getName() + "'s turn to place ships.");
            player.placeShips(occupiedSectors);
            occupationDisplay.displayOccupation(); // ������ʾռ�����
        }
        
        summarizeShipPlacement();

        System.out.println("Game started. It's " + startPlayer.getName() + "'s turn.");
        nextTurn();  // ��ʼ��һ�غϵ�ִ��
        scoreManager.calculateRoundScores(this); // ��ʼ��Ϸʱ�����һ�ַ���
    }

    public void nextTurn() {
        System.out.println("Turn " + turnCounter + " starts.");
        // ȷ��Ϊÿ�������������˳��
        for (Player player : players) {
            player.setCommandOrder();
            player.endTurn();
        }
        this.updateCommandMatrix();
       
        executeRound();  // ��ʼִ��ÿ����ҵ�����
        scoreManager.calculateRoundScores(this); // ÿ���غϼ������
        scoreBoard.updateScores(); // ���·�����
        endRound();  // ����endRound����غϽ������߼�
    }

    
    public void endRound() {
    	
        sustainShips();          // ά�ַɴ����Ƴ��޷�ά�ֵķɴ�
        chooseSectorCards();     // �����ѡ���������ƽ��е÷�
        
        // ��ʾ�����㵱ǰ�غϵĵ÷�
        calculateScores();
        scoreManager.calculateRoundScores(this); // ���㲢����ÿ����ҵķ���
        scoreBoard.updateScores(); // ���·�������ʾ
        System.out.println("**************************************************************************************************************************");

        // ����Ƿ�ﵽ�����غ���
        if (turnCounter >= maxTurns) {
            endGame();  // ����ﵽ���غ�����������Ϸ
        } else {
        	turnCounter++;  // �غϼ�����һ
            nextTurn();  // ���򣬿�ʼ��һ���غ�
        }
    }

    
    public void summarizeShipPlacement() {
    	System.out.println("\n**************************************************************************************************************************");
        System.out.println("\nSummary of Ship Placement:");

        for (Player player : players) {
            System.out.println(player.getName() + " placed ships at the following locations:");
            Map<String, Map<Hex, List<String>>> shipPlacementSummary = new HashMap<>();

            // ����ÿ����ҵĽ���
            for (Ship ship : player.getShips()) {
                String sector = getSectorLabelForHex(ship.getShipLocation()); // ��ȡ��Ӧ����ϵ����
                Hex position = ship.getShipLocation();
                String shipId = ship.getIdShip();

                shipPlacementSummary.putIfAbsent(sector, new HashMap<>());
                Map<Hex, List<String>> sectorMap = shipPlacementSummary.get(sector);
                sectorMap.putIfAbsent(position, new ArrayList<>());
                sectorMap.get(position).add(shipId);
            }

            // �����ҵĽ���������Ϣ
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

    // ��ȡĳ��������������ϵ��ǩ�ķ���
    private String getSectorLabelForHex(Hex hex) {
        // ����������ϵ���Ƽ����Ӧ�������ҵ�ƥ�����ϵ����
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();
        for (Map.Entry<String, List<Hex>> entry : cardHexes.entrySet()) {
            if (entry.getValue().contains(hex)) {
                return entry.getKey();
            }
        }
        return "Unknown";  // �Ҳ�����Ӧ��ϵ�����
    }
    
    // ���¾���
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

    // ��ȡ�ض��������ض��ִε�ʹ�ô���
    public int getCommandUsage(int round, int command) {
    	return commandMatrix[command - 1][round - 1];
    }

    
    public void executeRound() {
        System.out.println("Executing round for all players");

        for (int commandPos = 0; commandPos < 3; commandPos++) { // ����λ�ô�0��2����Ӧ��ÿλ��ҵ�����1��2��3
            for (Player player : players) {
                int commandIndex = player.getCurrentCommandOrder().get(commandPos); // ��ȡ��ǰ��Ҵ�λ�õ���������
                System.out.println("Executing command " + commandIndex + " for " + player.getName());
                executeCommands(player, commandIndex); // ִ��ָ��������
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

    
    //ά�ִ�ֻ
    public void sustainShips() {
        Map<Hex, HexBoard.OccupationInfo> occupationMap = hexBoard.getOccupationMap();

        // ����ÿ�������κ�����ռ����Ϣ
        Iterator<Map.Entry<Hex, HexBoard.OccupationInfo>> it = occupationMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Hex, HexBoard.OccupationInfo> entry = it.next();
            Hex hex = entry.getKey();
            HexBoard.OccupationInfo occupationInfo = entry.getValue();
            Sector sector = hexBoard.getBoard().get(hex);
            int maxShipsSustained = 1 + sector.getSector();  // ����getSector()���ص���ϵͳ�ȼ�

            List<Ship> ships = occupationInfo.getOccupyingShips();
            if (ships.size() > maxShipsSustained) {
                // ��������������������ά�������ض��б�
                List<Ship> toRemove = new ArrayList<>(ships.subList(maxShipsSustained, ships.size()));
                ships.removeAll(toRemove);  // ��ȫ�Ƴ��������ֵĽ���
                // �˴����Դ��������ص����������߼�
            }

            // ���û�н���ռ�ݴ������Σ����ռ����Ϣ
            if (ships.isEmpty()) {
                it.remove();  // ��ȫ�ش�ӳ�����Ƴ���ǰ��
                hexBoard.clearOccupation(hex, null);  // ��������������Դ������ռ��
            }
        }
    }


    //���ѡ�÷�����
    public void chooseSectorCards() {
        List<String> availableCards = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));

        System.out.println("Now pick a sector card to calculate the score for this round.");
        for (Player player : players) {
            // ����Ƿ��п��õ���������
            if (availableCards.isEmpty()) {
                System.out.println("No available sectors to choose for scoring.");
                break;
            }

            String chosenCard = player.chooseSectorCard(availableCards);

            availableCards.remove(chosenCard);
            player.calculatePlayerScore(chosenCard, hexBoard);  // ���������ѡ��������еĵ÷�
        }
    }


    
    public void calculateScores() {
        System.out.println("Scores for round " + turnCounter + ":");
        
        // ��ʾÿλ�������һ�ֵĵ÷�
        for (Player player : players) {
            System.out.println("Player " + player.getName() + " scored " + player.getRoundScore() + " in round " + turnCounter);
        }

        // ��ʾ��Ϸ���е�Ŀǰ���ܷ�
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
                                  .orElse(Integer.MIN_VALUE);  // �ҳ���ߵ÷�

        List<Player> winners = players.stream()
                                      .filter(p -> p.getScore() == highestScore)
                                      .collect(Collectors.toList());  // �ҳ����е÷���ߵ����

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
        // ��ʼ������б�
        List<Player> players = new ArrayList<>();
        System.out.println("Welcome to the game! Please enter player details.");

        // ���� Game ����
        Game game = new Game(9, null, players);  // ��ʼʱû�� startPlayer����ʱ�� null

        Set<Integer> availableIds = new HashSet<>(Arrays.asList(1, 2, 3));

        // ��ʾ�û��������������2 �� 3��
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

        // ������Ҳ�������ȷ�� game ����
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for player " + (i + 1) + ": ");
            String name = scanner.nextLine().trim();

            int id = getAvailableId(availableIds, scanner);

            if (name.equalsIgnoreCase("VirtualPlayer")) {
                // ���� VirtualPlayer
                VirtualPlayer virtualPlayer = new VirtualPlayer("VirtualPlayer" + id, id, game);
                players.add(virtualPlayer);
            } else {
                // ������ͨ���
                Player player = new Player(name, id, game);
                players.add(player);
            }
        }

        // �Զ����������ң�ֱ����������ﵽ3��
        while (players.size() < 3) {
            int id = availableIds.iterator().next();
            availableIds.remove(id);
            VirtualPlayer virtualPlayer = new VirtualPlayer("VirtualPlayer" + id, id, game);
            players.add(virtualPlayer);
            System.out.println("Added VirtualPlayer" + id + " to the game.");
        }

        // ������Ϸ������б�
        game.setPlayers(players);

        // ���� ID Ϊ 1 �������Ϊ��ʼ���
        Player startPlayer = players.stream()
                .filter(p -> p.getId() == 1)
                .findFirst()
                .orElse(null);

        if (startPlayer == null) {
            System.out.println("No player with ID 1 found, please ensure one of the players has ID 1.");
            return; // ���û���ҵ� ID Ϊ 1 ����ң���������
        }

        // ������Ϸ�Ŀ�ʼ���
        game.setStartPlayer(startPlayer);

        // ��ʼ��Ϸ
        game.startGame();  // ��ʼ��Ϸ�߼�

        // ģ����Ϸ�ĻغϽ���
        while (game.getTurnCounter() <= game.getMaxTurns()) {
            game.nextTurn();
        }
    }


    // ��ȡ���õ���� ID
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