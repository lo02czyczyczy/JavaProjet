### **1. 引言**

#### **1.1 项目背景**

本项目是一款基于Java编程语言开发的策略类太空战争游戏。游戏的主题围绕“帝国争霸”展开，三位玩家通过舰船的扩张（Expand）、探索（Explore）、歼灭（Exterminate）在银河系中争夺控制权。游戏目标是通过部署舰船、争夺星区、实现资源利用来积累分数，最终赢得最高分的玩家取得胜利。

在开发此项目的过程中，我们旨在模拟一个完整的游戏环境，通过Java的图形用户界面（GUI）实现棋盘绘制、玩家交互和实时得分显示等功能。游戏采用2到3名玩家模式，允许玩家与虚拟玩家（AI）互动，虚拟玩家具有基本的策略决策能力。

#### **1.2 项目意义**

1. **技术能力提升**  
   项目设计和实现过程中，我们深刻理解并实践了面向对象编程的核心理念，包括类的封装、继承和多态。通过棋盘的六边形结构设计，我们进一步巩固了对复杂数据结构的掌握。  
   此外，项目涉及Java GUI的实现，让我们初步掌握了Swing库的使用，能够设计出直观友好的用户界面。

2. **团队协作锻炼**  
   游戏的开发以小组形式完成，在明确分工的基础上，通过高效沟通和版本管理，我们完成了从设计到实现的完整过程。这种合作模式锻炼了我们的团队协作能力，为将来在实际开发项目中的合作奠定了基础。

3. **面向未来的学习能力**  
   通过开发此项目，我们初步了解了如何将软件开发的理论知识应用于实际问题，并在过程中解决实际问题，例如调试错误、优化算法等。这为未来更复杂的项目开发提供了宝贵的经验。

---

### **2. 需求分析**

#### **2.1 功能需求**

游戏的核心功能主要包括以下几点：

1. **玩家角色与交互**  
   - 支持2到3名玩家，其中可以包含虚拟玩家（AI）。  
   - 玩家能够选择舰船颜色并部署舰船，所有玩家轮流进行操作。  
   - 玩家每轮可选择指挥卡牌的执行顺序，并通过界面进行交互。

2. **游戏棋盘与星区管理**  
   - 使用六边形网格棋盘模拟银河星区，其中包含不同等级（Level 0至Level 3）的星区。  
   - 中央的“Tri-Prime”星区固定为核心区域，为争夺的关键目标。  

3. **舰船的行为与控制**  
   - 玩家可通过“扩张”指挥卡向其控制的星区中添加舰船。  
   - “探索”指挥卡允许玩家移动舰船至邻近的六边形，探索新的星区。  
   - “歼灭”指挥卡用于发动战斗，玩家可攻击敌方舰船并尝试占领新的星区。

4. **得分与游戏目标**  
   - 游戏目标是累计得分，得分依据星区等级及其控制情况计算。  
   - 玩家每轮结束时需选择一个星区进行计分，控制Tri-Prime星区的玩家可额外选择一个星区。  
   - 游戏结束时，所有星区得分再次计算并翻倍，得分最高者获胜。

#### **2.2 非功能需求**

1. **用户体验**  
   - 游戏界面需直观友好，包括清晰的六边形棋盘显示、玩家占领情况以及实时得分表。  
   - 系统需对用户输入提供及时的反馈和错误提示，避免操作失误。

2. **系统性能**  
   - 游戏需在普通计算机配置下流畅运行，界面响应迅速。  
   - 数据结构和算法设计需高效，保证游戏逻辑的计算在合理时间内完成。

3. **扩展性与维护性**  
   - 系统设计应保持模块化，便于未来功能扩展，如增加新指挥卡或更智能的虚拟玩家。  
   - 代码结构需清晰明了，便于维护和调试。

---

### **3. 系统设计**

#### **3.1 总体架构**

游戏的系统架构分为以下主要模块：

1. **用户界面模块**：负责游戏的图形展示和玩家交互。
   - **`HexBoardDisplay`**：绘制六边形棋盘，显示星区等级及其状态。
   - **`OccupationDisplay`**：显示每个六边形的占领信息，包括占领玩家及舰船数量。
   - **`ScoreBoard`**：显示各玩家得分情况，并实时更新。

2. **核心游戏逻辑模块**：控制游戏流程、规则实现和玩家行为。
   - **`Game`**：核心控制器，负责管理回合、指挥执行、得分计算及游戏结束逻辑。
   - **`Player`**和**`VirtualPlayer`**：表示玩家的行为，虚拟玩家具有简单的AI逻辑。
   - **`Ship`**：表示舰船及其位置、状态和移动行为。

3. **数据管理模块**：负责管理棋盘数据和得分信息。
   - **`HexBoard`**和**`Hex`**：管理六边形棋盘及其星区等级、邻居计算和占领情况。
   - **`ScoreManager`**和**`RoundScore`**：记录每个玩家的得分，并支持按回合查询和更新。

4. **指挥与命令模块**：实现玩家操作的指挥逻辑。
   - **`CommandCard`**：表示指挥卡牌（扩张、探索、歼灭）。
   - **`CommandType`**：定义指挥卡牌的类型。

---

#### **3.2 类图和关系**

通过UML类图可以清晰展示各模块及其相互关系，例如：
- `Game`依赖`HexBoard`和`Player`，控制游戏流程。
- `Player`持有多个`Ship`，通过`CommandCard`执行指令。
- `HexBoard`管理多个`Hex`及其占领情况。
- `ScoreManager`管理所有玩家的分数，并与`ScoreBoard`交互。

![Uploading 5826fb5359ba7a154ff9614dd009f35.png…]()


---

#### **3.3 核心类设计**

**1. `Game` 类**
- **功能**：作为游戏的核心控制器，管理游戏的回合、指令执行和得分计算。
- **关键方法**：
  - **`startGame()`**：初始化棋盘、玩家和分数板，并开始第一回合。
    ```java
    public void startGame() {
        EventQueue.invokeLater(() -> hexBoardDisplay.setVisible(true));
        System.out.println("Starting ship placement...");
        for (Player player : players) {
            player.placeShips(new HashSet<>());
        }
        System.out.println("Game started. It's " + startPlayer.getName() + "'s turn.");
        nextTurn();
    }
    ```
  - **`nextTurn()`**：控制每一回合的流程，包括执行指令、计算得分和更新状态。
    ```java
    public void nextTurn() {
        for (Player player : players) {
            player.setCommandOrder();
        }
        executeRound();
        scoreManager.calculateRoundScores(this);
        scoreBoard.updateScores();
        endRound();
    }
    ```
  - **`executeRound()`**：按顺序执行每位玩家的指挥卡。
    ```java
    public void executeRound() {
        for (int commandPos = 0; commandPos < 3; commandPos++) {
            for (Player player : players) {
                executeCommands(player, player.getCurrentCommandOrder().get(commandPos));
            }
        }
    }
    ```

**2. `HexBoard` 类**
- **功能**：管理六边形棋盘的星区信息及其占领状态。
- **关键方法**：
  - **`initializeBoard()`**：初始化棋盘，包括固定的核心区域和随机的边界星区。
    ```java
    private void initializeBoard() {
        Hex[] fixedHexes = { new Hex(0, 0, 0), new Hex(1, -1, 0) };
        for (Hex hex : fixedHexes) {
            board.put(hex, new Sector(3));
        }
    }
    ```
  - **`updateOccupation(Hex, Ship)`**：更新某个六边形的占领信息。
    ```java
    public void updateOccupation(Hex hex, Ship ship) {
        occupationMap.computeIfAbsent(hex, k -> new OccupationInfo(ship.getOwner().getId())).addShip(ship);
    }
    ```

**3. `Player` 类**
- **功能**：表示玩家的行为，包括放置舰船、执行指挥和得分。
- **关键方法**：
  - **`placeShips(Set<String> occupiedSectors)`**：玩家选择星区并放置舰船。
    ```java
    public void placeShips(Set<String> occupiedSectors) {
        String selectedSector = selectSector(occupiedSectors);
        Hex targetHex = findLevelIHex(selectedSector);
        for (int i = 0; i < 2; i++) {
            ships.add(new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, generateShipId(ships.size() + 1)));
        }
    }
    ```
  - **`chooseCommandOrder()`**：玩家选择指挥卡的执行顺序。
    ```java
    public List<Integer> chooseCommandOrder() {
        System.out.println("Enter the order of commands (1=Expand, 2=Explore, 3=Exterminate):");
        return Arrays.asList(1, 2, 3); // 示例为固定顺序
    }
    ```

---

### **4. 实现过程**

#### **4.1 用户界面模块的实现**

**1. `HexBoardDisplay` 类**
- **功能**：绘制六边形棋盘，显示星区等级和坐标。
- **关键方法**：
  - **`paint(Graphics g)`**：实现六边形的绘制逻辑。
    ```java
    private void drawHex(Graphics g, int x, int y, int size, int level, Hex hex) {
        int[] cx = new int[6];
        int[] cy = new int[6];
        for (int i = 0; i < 6; i++) {
            cx[i] = (int) (x + size * Math.cos(i * Math.PI / 3));
            cy[i] = (int) (y + size * Math.sin(i * Math.PI / 3));
        }
        g.drawPolygon(cx, cy, 6);
    }
    ```

**2. `OccupationDisplay` 类**
- **功能**：文本显示每个六边形的占领状态。
- **关键方法**：
  - **`displayOccupation()`**：动态更新占领情况的显示。
    ```java
    public void displayOccupation() {
        Map<Hex, OccupationInfo> occupationMap = hexBoard.getOccupationMap();
        occupationMap.forEach((hex, info) -> System.out.println("Hex: " + hex + " occupied by player: " + info.getPlayerId()));
    }
    ```

**3. `ScoreBoard` 类**
- **功能**：实时更新各玩家的得分信息。
- **关键方法**：
  - **`updateScores()`**：更新每轮得分和总得分。
    ```java
    public void updateScores() {
        int round = scoreManager.getGame().getTurnCounter();
        scoreManager.getPlayers().forEach(player -> {
            int score = scoreManager.getRoundScores(player.getId()).getOrDefault(round, 0);
            System.out.println("Player " + player.getName() + ": " + score);
        });
    }
    ```

---

#### **4.2 游戏核心逻辑的实现**

**1. 指挥逻辑**
- **`Player` 执行指令**：
  - `expand`方法：允许玩家在控制的星区中添加舰船。
  - `explore`方法：实现舰船的移动。
  - `exterminate`方法：允许玩家发动战斗。

**2. 数据更新**
- 每次操作后，`HexBoard`通过`updateOccupation`方法更新占领信息，确保数据同步。

---


