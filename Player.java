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
        System.out.println("Player created: " + name + " with id: " + id);
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
    public void placeShips(Set<String> occupiedSectors) {
        int shipsToPlace = 2; // ��ҳ�ʼ����2�Ҵ�
        Map<String, List<Hex>> cardHexes = this.hexBoard.generateCardHexes(); // ʹ�� this.hexBoard

        System.out.println("Player " + name + " is placing ships with id: " + id);

        // ��ʾ���õ���������
        List<String> availableSectors = new ArrayList<>(cardHexes.keySet());
        availableSectors.removeAll(occupiedSectors);
        if (availableSectors.isEmpty()) {
            System.out.println("No available sectors to choose for ship placement.");
            return;
        }

        System.out.println("Available sectors for ship placement: " + availableSectors);
        System.out.println("Please choose a sector card from the available sectors:");
        String selectedSector = scanner.nextLine().trim(); // ʹ�ù���� scanner

        while (!availableSectors.contains(selectedSector)) {
            System.out.println("Invalid sector card. Please choose again from: " + availableSectors);
            selectedSector = scanner.nextLine().trim(); // ʹ�ù���� scanner
        }

        occupiedSectors.add(selectedSector); // ��Ǹ����������ѱ�ռ��

        // ��ȡ��ѡ�����е� Level I ϵͳ
        List<Hex> levelIHexes = cardHexes.get(selectedSector).stream()
                .filter(hex -> this.hexBoard.getBoard().get(hex).getSector() == 1)
                .collect(Collectors.toList());

        if (levelIHexes.isEmpty()) {
            System.out.println("No Level I systems in the selected sector.");
            return;
        }

        System.out.println("Available Level I hexes for ship placement: " + levelIHexes);
        System.out.println("Please enter the coordinates of the Level I hex to place your ships (e.g., '1 -5 4'):");
        String input = scanner.nextLine().trim(); // ʹ�ù���� scanner
        String[] parts = input.split("\\s+");

        while (parts.length != 3) {
            System.out.println("Invalid input. Please enter three coordinates (e.g., '1 -5 4'):");
            input = scanner.nextLine().trim(); // ʹ�ù���� scanner
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

            // ���ý���������ռ����Ϣ
            for (int i = 1; i <= shipsToPlace; i++) {
                String shipId = generateShipId(ships.size() + 1);
                Ship newShip = new Ship(this, this.hexBoard.getBoard().get(selectedHex), selectedHex, shipId);
                ships.add(newShip);
                this.hexBoard.updateOccupation(selectedHex, newShip); // ����ռ����Ϣ
                System.out.println("Placed ship with ID " + shipId + " at Hex coordinates " + selectedHex);
            }

            // ��ӵ�����Ϣ
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
        // ʹ�����й���� scanner
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

                // ֱ�ӱȽ� q, r, s ֵ
                boolean controlsHex = false;
                for (Hex hex : controlledHexes) {
                    if (hex.q == targetHex.q && hex.r == targetHex.r && hex.s == targetHex.s) {
                        controlsHex = true;
                        break;
                    }
                }

                if (controlledHexes.contains(targetHex) && count <= shipsToAdd) {
                    // ��ӽ�����ָ���� hex
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

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter valid integers for coordinates and ship count.");
            }
        }

        System.out.println("All ships placed or skipped for this round.");
        System.out.println("**************************************************************************************************************************");
    }



    
    
    
    
    
    
    
    

    //EXPLORE����
    public void explore(Game game) {
        int round = currentCommandOrder.indexOf(2) + 1;
        int usage = game.getCommandUsage(round, 2); // ��Game��ȡ����explore�����ʹ�ô���

        int movesPerShip = Math.max(4 - usage, 1); // ÿ�Ҵ��Ŀ��ƶ�����
        System.out.println(name + " can move each ship " + movesPerShip + " times using Explore command in round " + round);

        // ��ʾ���зɴ�����λ��
        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        // ���ѡ��ɴ����ƶ�����
        for (Ship ship : ships) {
            int remainingMoves = movesPerShip;
            Hex currentHex = ship.getShipLocation();

            System.out.println("Move Ship ID: " + ship.getIdShip() + " starting at Hex coordinates " + currentHex);

            while (remainingMoves > 0) {
                System.out.println("Enter new coordinates for move (or type 'skip'):");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("skip")) {
                    break; // �����˴����ƶ�
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
                        // �ƶ���ֻ
                        ship.move(targetHex, hexBoard.getBoard().get(targetHex)); // �ƶ���ֻ
                        hexBoard.updateOccupation(targetHex, ship); // ����ռ����Ϣ
                        hexBoard.clearOccupation(currentHex, ship); // ���ԭλ�õ�ռ����Ϣ

                        currentHex = targetHex; // ���µ�ǰ����
                        remainingMoves--;

                        System.out.println("Ship moved to " + targetHex);

//                        // ���������Tri-Primeϵͳ�����Σ�����ֹͣ�ƶ�
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
        // ����Ƿ�����
        if (!from.isNeighbor(to)) {
            System.out.println("Target hex is not adjacent.");
            return false;
        }

        // ���Ŀ��hex�Ƿ��������ռ��
        HexBoard.OccupationInfo occupationInfo = hexBoard.getOccupationInfo(to);
        if (occupationInfo != null && occupationInfo.getPlayerId() != this.id) {
            System.out.println("Cannot move to a hex occupied by another player.");
            return false;
        }

        // ����Ƿ��Ǳ�Ե�ġ�����������θ�sector�ȼ�Ϊ0��
        if (hexBoard.getBoard().get(to).getSector() == 0) {
            System.out.println("Cannot move to an invalid hex.");
            return false;
        }

        // ����Ƿ񾭹���Tri-Primeϵͳ������
        boolean fromIsTriPrime = isTriPrimeHex(from);
        boolean toIsTriPrime = isTriPrimeHex(to);

        if (toIsTriPrime) {
            // ���Ŀ����Tri-Primeϵͳ�����Σ�����ֹͣ�ƶ�
            if (remainingMoves > 1) {
                System.out.println("Must stop moving after entering Tri-Prime system hex.");
                return false;
            }
        } else if (fromIsTriPrime) {
            // ��Tri-Primeϵͳ�������ƶ���������������
            // ����Ҫ���⴦��
        } else if (isMovingThroughTriPrime(from, to)) {
            // �������ͬһ�ƶ�����ͼ����Tri-Primeϵͳ�����Σ�������
            System.out.println("Cannot move through Tri-Prime system hex.");
            return false;
        }

        return true;
    }

    // ����Ƿ�ΪTri-Primeϵͳ������
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

    // ����Ƿ���ͬһ�ƶ�����ͼ����Tri-Primeϵͳ������
    private boolean isMovingThroughTriPrime(Hex from, Hex to) {
        // ��ȡfrom��to֮������п���·�����������ڣ�ֻ����from��to�Ƿ��ڽ�Tri-Prime��
        return isNeighborToTriPrime(from) && isNeighborToTriPrime(to);
    }

    // ���һ��hex�Ƿ��ڽ�Tri-Primeϵͳ������
    private boolean isNeighborToTriPrime(Hex hex) {
        for (Hex neighbor : hex.getNeighbors(hexBoard.getBoard())) {
            if (isTriPrimeHex(neighbor)) {
                return true;
            }
        }
        return false;
    }

    
    
    
    
    
    
    


    //EXTERMINATE����
    public void exterminate(Game game) {
        int round = currentCommandOrder.indexOf(3) + 1;
        int usage = game.getCommandUsage(round, 3);
        int attacksAllowed = Math.max(4 - usage, 1);
        System.out.println(name + " has " + attacksAllowed + " total attacks using Exterminate command in round " + round);

        // ��ʾ���зɴ�����λ��
        ships.forEach(ship -> {
            Hex shipPosition = ship.getShipLocation();
            System.out.println("Ship ID: " + ship.getIdShip() + " at Hex coordinates " + shipPosition);
        });

        // ���ѡ���������
        while (attacksAllowed > 0) {
            System.out.println("Enter ship ID and target hex coordinates to attack (e.g., '1 0 0 0') or type 'skip' to end attacks:");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("skip")) {
                break; // ���ѡ���������
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
                    resolveAttack(attackingShip, targetHex); // ִ�н���
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
            // ���� ship.getIdShip() �ĸ�ʽΪ "playerId-shipNumber"������ "1-1"
            String[] idParts = ship.getIdShip().split("-");
            if (idParts.length == 2) {
                try {
                    int id = Integer.parseInt(idParts[1]);
                    if (id == shipId) {
                        return ship;
                    }
                } catch (NumberFormatException e) {
                    // ���Ը�ʽ����ȷ�� shipId
                }
            }
        }
        return null; // δ�ҵ�ƥ��Ĵ�ֻ
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

        // �Ƴ����ط���һ�ҽ���
        if (!defendingShips.isEmpty()) {
            Ship shipToRemove = defendingShips.get(0);
            defendingShips.remove(shipToRemove);
            hexBoard.clearOccupation(targetHex, shipToRemove);
            shipToRemove.getOwner().getShips().remove(shipToRemove); // �ӷ��ط��Ľ����б����Ƴ�
            System.out.println("Defending ship " + shipToRemove.getIdShip() + " has been destroyed.");
        }

        // �Ƴ��������Ľ���
        ships.remove(attackingShip);
        hexBoard.clearOccupation(attackingShip.getShipLocation(), attackingShip);
        System.out.println("Attacking ship " + attackingShip.getIdShip() + " has been destroyed.");

        // ������ط�û��ʣ�ར����������ռ���������
        if (defendingShips.isEmpty()) {
            hexBoard.clearOccupation(targetHex, null); // ���ԭ���ط���ռ����Ϣ

            // ���һ���µĹ�����������Ŀ��������
            Ship newShip = new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, generateShipId(ships.size() + 1));
            ships.add(newShip);
            hexBoard.updateOccupation(targetHex, newShip);

            System.out.println(name + " has taken control of hex " + targetHex);
        } else {
            // ���·��ط���ռ����Ϣ
            hexBoard.updateOccupation(targetHex, defendingShips.get(0));
        }
    }


    
    
    
    
    
    
    
    
    //ѡ����
    public String chooseSectorCard(List<String> availableCards) {
        System.out.println(getName() + ", choose a sector card from: " + availableCards);
        String chosenCard = scanner.nextLine().trim().toLowerCase();

        while (!availableCards.contains(chosenCard)) {
            System.out.println("Invalid card. Please choose again from: " + availableCards);
            chosenCard = scanner.nextLine().trim().toLowerCase();
        }

        return chosenCard;
    }
    
    //�÷������Ʒ�
    public void calculatePlayerScore(String sectorCard, HexBoard hexBoard) {
        int score = 0;
        List<Hex> hexesInSector = hexBoard.generateCardHexes().get(sectorCard); // ��ȡѡ�����Ƶ�����Hex����

        for (Hex hex : hexesInSector) {
            OccupationInfo occupationInfo = hexBoard.getOccupationInfo(hex);
            if (occupationInfo != null && occupationInfo.getPlayerId() == this.id) {
                int systemLevel = hexBoard.getBoard().get(hex).getSector();  // ��ȡϵͳ�ȼ�
                score += systemLevel;  // �ۼ�ϵͳ�ȼ���Ϊ�÷�
            }
        }

        // �������Ƿ����������һ��Tri-Prime�������Σ������Ӷ���÷�
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
                break;  // һ��������ҿ�����һ��Tri-Prime�������Σ���������ѭ��
            }
        }

        if (controlsTriPrime) {
            score += 3;  // ��ҿ���������һ��Tri-Prime�������Σ����3�ּӳ�
        }

        this.addScore(score);  // ������ҵ��ܵ÷�
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