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

<img width="1016" alt="5826fb5359ba7a154ff9614dd009f35" src="https://github.com/user-attachments/assets/a93aaff1-8f49-4df1-863e-b0ebf59df5d2">



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

### **5. 测试与调试**

#### **5.1 测试策略**

为了确保游戏功能的完整性和稳定性，我们采取了以下测试策略：

1. **单元测试**  
   - 测试各个类的方法是否按照预期运行，例如`HexBoard`的`updateOccupation`方法能否正确更新占领信息，`Player`的`placeShips`方法是否能正确放置舰船。
   - 使用JUnit框架对关键方法编写测试用例，验证核心逻辑。

2. **集成测试**  
   - 检查不同模块之间的交互是否正确，例如`Game`与`HexBoard`之间的协调是否一致，玩家的行为是否影响分数显示。

3. **系统测试**  
   - 从用户的角度模拟完整的游戏流程，验证所有功能是否正常运行，包括GUI的交互体验。

4. **边界测试**  
   - 测试游戏在极端情况下的表现，例如所有星区都被占满时的舰船放置、所有玩家同时选择同一指挥卡时的处理逻辑。

---

#### **5.2 测试案例**

以下列举了部分测试案例：

1. **测试棋盘初始化**
   - 目标：确保棋盘正确生成，包括固定的`Tri-Prime`区域和随机的星区。
   - 测试方法：调用`HexBoard`的`initializeBoard`方法，检查每个六边形的星区等级是否符合规则。

2. **测试玩家指挥逻辑**
   - 目标：验证玩家的`expand`、`explore`和`exterminate`方法。
   - 测试方法：模拟玩家控制一个星区，测试在不同场景下执行指挥卡的结果。

3. **测试GUI界面**
   - 目标：确保`HexBoardDisplay`和`ScoreBoard`显示正确。
   - 测试方法：启动游戏，检查六边形绘制是否清晰、得分显示是否实时更新。

---

#### **5.3 调试过程**

在开发过程中，我们遇到了一些关键问题并成功解决：

1. **问题**：玩家无法正确选择未占用的星区放置舰船。  
   **解决方法**：在`Player`类的`placeShips`方法中，增加对已占用星区的检查逻辑，并通过`occupiedSectors`集合跟踪状态。

2. **问题**：GUI界面在更新时出现延迟或绘制不完整。  
   **解决方法**：调整`HexBoardDisplay`的`paint`方法，使用`EventQueue.invokeLater`确保线程安全。

---

### **6. 项目特点**

#### **6.1 亮点功能**

1. **六边形棋盘设计**
   - 使用三轴坐标系统表示六边形，结合算法实现邻居计算和移动逻辑。
   - 中心的`Tri-Prime`区域固定为3级星区，是争夺的重点区域。

2. **虚拟玩家（AI）逻辑**
   - AI玩家能够随机选择星区放置舰船，并基于简单策略决定指挥卡的执行顺序。
   - 在进攻（`exterminate`）时，AI会随机选择目标星区，模拟人类玩家行为。

3. **动态得分显示**
   - 实现`ScoreBoard`类，通过表格显示各玩家的回合得分和总分，实时更新。

---

#### **6.2 技术创新**

1. **模块化设计**  
   - 每个功能模块单独设计，增强了代码的可维护性和可扩展性。

2. **面向对象编程（OOP）**  
   - 使用继承和多态实现`Player`与`VirtualPlayer`的行为差异，减少代码冗余。

3. **用户界面与逻辑分离**  
   - 界面层（如`HexBoardDisplay`）与逻辑层（如`HexBoard`）分离，保证清晰的代码结构。

---

### **7. 项目难点与挑战**

#### **7.1 难点分析**

1. **六边形网格实现**  
   - 难点：六边形坐标的计算和邻居逻辑实现。
   - 解决方案：采用立方坐标系统，结合`Hex`类的`getNeighbors`方法精确计算邻居。

2. **虚拟玩家AI设计**  
   - 难点：如何让AI玩家在不增加复杂度的情况下作出合理决策。
   - 解决方案：使用简单随机策略，结合现有的游戏规则设计，使AI行为看起来较为自然。

3. **多模块协作**  
   - 难点：各模块之间的交互可能导致耦合度增加，影响后续扩展。
   - 解决方案：通过`Game`类统一管理流程，减少模块之间的直接依赖。

---

#### **7.2 挑战处理**

- **挑战**：如何在游戏流程中实时更新GUI。
  **处理方法**：采用Swing框架的线程机制，确保GUI绘制和游戏逻辑独立运行。

- **挑战**：当玩家输入非法数据时的处理。
  **处理方法**：在所有用户交互方法中增加输入验证，并提供友好的错误提示。

---

### **8. 总结与展望**

#### **8.1 总结**

通过本项目的开发，我们实现了一个完整的策略类桌面游戏。项目特点包括：
- 模拟真实的六边形棋盘和星区规则。
- 玩家与虚拟玩家的互动机制。
- 动态得分计算和实时界面显示。

项目不仅加深了我们对Java面向对象编程的理解，还让我们初步掌握了GUI开发的基础知识。同时，在团队协作中，我们学会了任务分工、代码整合和版本管理。

---

#### **8.2 展望**

在未来的改进方向上，我们提出以下几点：

1. **功能扩展**
   - 增加更多指挥卡类型，例如“防御”或“资源采集”。
   - 引入不同种族，赋予每个玩家独特能力。

2. **AI优化**
   - 为虚拟玩家设计更加智能的决策算法，模拟更真实的对抗体验。

3. **网络支持**
   - 实现多人联网对战，提升游戏的互动性和可玩性。

4. **界面优化**
   - 改用更现代的图形库（如JavaFX）提升界面美观性。

通过这些改进，我们希望将游戏打造为一个完整且可持续扩展的项目，同时为后续学习和研究奠定坚实基础。

---


