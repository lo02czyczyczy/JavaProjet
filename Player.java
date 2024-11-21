package game;

import java.util.*;
import game.HexBoard.OccupationInfo;

public class Player {
    private String name;
    public List<Ship> ships;
    private List<CommandCard> commandCards;
    private List<Sector> controlledSectors;
    private int score;
    private int roundScore; // ���ֵ÷�
    public int id;
    private String color;  // �������ԣ���ɫ
    private static final Scanner scanner = new Scanner(System.in); // ���� Scanner ʵ��
    public HexBoard hexBoard;  
    public Game game; 
    public List<Integer> currentCommandOrder;  // �洢��ǰ�غϵ�����˳��

    // ���캯��
    public Player(String name, int id, Game game) {
        this.game = game;
        this.ships = new ArrayList<>();
        this.commandCards = new ArrayList<>();
        this.controlledSectors = new ArrayList<>();
        this.score = 0;
        this.name = name;
        this.id = id;
        this.color = assignColor(id);  // ���� id ������ɫ
        this.hexBoard = game.getHexBoard(); // �� Game �����ȡ hexBoard
    }

//    // �������û���ȡ����
//    private void setNameFromUser() {
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Please enter your name: ");
//        name = scanner.nextLine();
//    }
//    
//    // �������û���ȡid
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
    
    // ����
    
    // ������ɫ�ķ���
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
    
    
 // placeShips ����
    public void placeShips(HexBoard hexBoard, Set<String> occupiedSectors) {
        int shipsToPlace = 2;  // ÿ�η������Ҵ�
        Map<String, List<Hex>> cardHexes = hexBoard.generateCardHexes();  // ��ȡ���п��Ƽ����Ӧ������

        String selectedSector = null;
        boolean validSector = false;

        // ���ѡ����ϵ
        while (!validSector) {
            System.out.println("Sectors: " + cardHexes.keySet());
            System.out.print("Player " + name + ", choose a sector to place ships: ");
            selectedSector = scanner.nextLine().toLowerCase();

            if (cardHexes.containsKey(selectedSector) && !occupiedSectors.contains(selectedSector)) {
                validSector = true;
                occupiedSectors.add(selectedSector);  // ��Ǹ���ϵ�ѱ�ռ��
            } else {
                System.out.println("Invalid sector or sector already occupied. Please choose another.");
            }
        }

        // ���ѡ���������
        List<Hex> availableHexes = cardHexes.get(selectedSector);
        List<Hex> levelIHexes = new ArrayList<>();
        for (Hex hex : availableHexes) {
            if (hexBoard.getBoard().get(hex).getSector() == 1) {  // ֻѡ�� I ��ϵͳ
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
            if (coordinates.length == 3) {  // ȷ����������������
                try {
                    int q = Integer.parseInt(coordinates[0].trim());
                    int r = Integer.parseInt(coordinates[1].trim());
                    int s = Integer.parseInt(coordinates[2].trim());

                    if (q + r + s != 0) {  // ��֤�����Ƿ���������������ϵ����
                        System.out.println("Invalid coordinates. Coordinates must satisfy q + r + s = 0.");
                        continue;  // �������������������ѭ�������û���������
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

        // ���ý���������ռ����Ϣ
        for (int i = 1; i <= shipsToPlace; i++) {
            String shipId = generateShipId(ships.size() + 1);
            Ship newShip = new Ship(this, hexBoard.getBoard().get(selectedHex), selectedHex, shipId);
            ships.add(newShip);
            hexBoard.updateOccupation(selectedHex, newShip); // ����ռ����Ϣ
            System.out.println("Player " + name + " placed a ship with ID " + shipId + " at Hex coordinates " + selectedHex);
        }
    }


    // ���ɽ��� ID �ķ���
    public String generateShipId(int shipNumber) {
        return id + "-" + shipNumber;  // ���ظ�ʽΪ "���ID-���"������ "1-1"
    }
    
    public void endTurn() {
        ships.forEach(ship -> ship.setMoved(false));
    }
    
    //ѡָ�
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
                    String commandChoice = scanner.nextLine(); // ��Ϊ nextLine
                    if (commandChoice.equalsIgnoreCase("CHEN")) {
                    	System.out.print("Congratulations on discovering the Easter egg in this game!");
                        order.clear();
                        order.add(999); // ʹ��999��Ϊ�����־
                        validInput = true;
                        break;
                    }
                    int cmd = Integer.parseInt(commandChoice);
                    if (cmd < 1 || cmd > 3) {
                        throw new IllegalArgumentException("Invalid command choice. Please enter 1, 2, or 3.");
                    }
                    order.add(cmd);
                }
                validInput = true; // ����������붼����Ч�ģ����� validInput Ϊ true������ѭ��
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numeric values only.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage()); // �������Ĵ�����Ϣ
            }
        }
        return order;
    }
    

    
    // �������ڹر� Scanner��ֻ�е������������ʱ�ŵ���
    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    
    //EXPAND����
    public void expand(Game game) {
        int round = currentCommandOrder.indexOf(1) + 1;
        int usage = game.getCommandUsage(round, 1); // ��ȡ����"Expand"�����ʹ�ô���
        int shipsToAdd = Math.max(4 - usage, 1); // ���������ӵĴ�ֻ����
        System.out.println(name + " can add " + shipsToAdd + " ships using Expand command in round " + round);

        // ��ʾ��ҵ�ǰ���Ƶ�ϵͳ�����ǿ����������´�ֻ��λ��
        List<Hex> controlledHexes = new ArrayList<>();
        hexBoard.getOccupationMap().forEach((hex, occupationInfo) -> {
            if (occupationInfo.getPlayerId() == this.id) {
                controlledHexes.add(hex);
                System.out.println("Controlled hex: " + hex + ", Number of ships: " + occupationInfo.getOccupyingShips().size());
            }
        });

        // ��ʾ���ѡ������Щϵͳ�з����µĽ���
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
                        hexBoard.updateOccupation(targetHex, newShip); // ����ռ����Ϣ
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
    }


    //EXPLORE����
    public void explore(Game game) {
    	int round = currentCommandOrder.indexOf(2) + 1;
        int usage = game.getCommandUsage(round,2); // ��Game��ȡ����explore�����ʹ�ô���

        int movesPerShip = Math.max(4 - usage, 1); // ÿ�Ҵ��Ŀ��ƶ�����
        System.out.println(name + " can move each ship " + movesPerShip + " times using Explore command in round " + round);

        // ��ʾ���зɴ�����λ��
        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        // ���ѡ��ɴ����ƶ�����
        for (Ship ship : ships) {
            System.out.println("Move Ship ID: " + ship.getIdShip() + " at Hex coordinates " + ship.getShipLocation());

            for (int i = 0; i < movesPerShip; i++) {
                System.out.println("Enter new coordinates for move " + (i+1) + " (or skip):");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("skip")) {
                    break; // �����˴����ƶ�
                }

                String[] parts = input.split(" ");
                try {
                    int q = Integer.parseInt(parts[0]);
                    int r = Integer.parseInt(parts[1]);
                    int s = Integer.parseInt(parts[2]);
                    Hex targetHex = new Hex(q, r, s);

                    // ���Ŀ��Hex�Ƿ�Ϊ��Ч�ƶ�ѡ��
                    if (isValidMove(ship.getShipLocation(), targetHex)) {
                        ship.move(targetHex, hexBoard.getBoard().get(targetHex)); // �ƶ���ֻ
                        hexBoard.updateOccupation(targetHex, ship); // ����ռ����Ϣ
                        hexBoard.clearOccupation(ship.getShipLocation(), ship); // ���ԭλ�õ�ռ����Ϣ
                    } else {
                        System.out.println("Invalid move. Hex occupied by other player or out of reach.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter valid hex coordinates.");
                }
            }
        }
    }

    private boolean isValidMove(Hex from, Hex to) {
        // ����Ƿ������Լ�Ŀ��hex�Ƿ��������ռ��
        if (from.isNeighbor(to) && hexBoard.getOccupationInfo(to) == null) {
            return true;
        } else {
            return false;
        }
    }


    //EXTERMINATE����
    public void exterminate(Game game) {
    	int round = currentCommandOrder.indexOf(3) + 1;
        int usage = game.getCommandUsage(round,3); // ��Game��ȡ����exterminate�����ʹ�ô���

        int attacksAllowed = Math.max(4 - usage, 1); // ��ҵ��ܽ�������
        System.out.println(name + " has " + attacksAllowed + " total attacks using Exterminate command in round " + round);

        // ��ʾ���зɴ�����λ��
        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        // ���ѡ��ɴ��ͽ�������
        while (attacksAllowed > 0) {
            System.out.println("Select a ship and an adjacent hex to attack (or type 'skip' to end attacks):");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("skip")) {
                break; // ���ѡ���������
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
                    resolveAttack(attackingShip, targetHex); // ִ�н���
                    attacksAllowed--;
                } else {
                    System.out.println("Invalid attack. Check if target is adjacent and occupied by enemy ships.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter valid ship ID and hex coordinates.");
            }
        }
        System.out.println("All attacks have been executed.");
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

    //�÷������Ʒ�
    public void calculatePlayerScore(String sectorCard, HexBoard hexBoard) {
        int score = 0;
        List<Hex> hexesInSector = hexBoard.generateCardHexes().get(sectorCard); // ��ȡѡ�����Ƶ�����Hex����

        for (Hex hex : hexesInSector) {
            if (hexBoard.getOccupationMap().containsKey(hex) && hexBoard.getOccupationMap().get(hex).getPlayerId() == this.id) {
                score += hexBoard.getBoard().get(hex).getSector();  // Add the level of controlled sectors
            }
        }

        // Check for control of Tri-Prime sector and add bonus points
        List<Hex> triPrimeHexes = Arrays.asList(new Hex(0, 0, 0), new Hex(1, -1, 0), new Hex(0, -1, 1), new Hex(1, 0, -1));
        if (triPrimeHexes.stream().allMatch(hex -> hexBoard.getOccupationMap().containsKey(hex) && hexBoard.getOccupationMap().get(hex).getPlayerId() == this.id)) {
            score += 3;  // Bonus for controlling Tri-Prime
        }

        this.score += score;  // Update the player's total score
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
        this.roundScore = score; // ���õ��ֵ÷�
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