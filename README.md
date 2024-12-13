
### **1. Introduction**

#### **1.1 Contexte du projet**

Ce projet est un jeu de guerre spatiale stratégique développé en langage de programmation Java. Le thème du jeu tourne autour de "l'empire suprême", où trois joueurs rivalisent pour le contrôle de la galaxie à travers l'expansion (Expand), l'exploration (Explore) et l'extermination (Exterminate) de vaisseaux spatiaux. L'objectif du jeu est d'accumuler des points en déployant des vaisseaux, en luttant pour des secteurs et en exploitant des ressources, le joueur avec le plus de points remportant la victoire.

Dans le développement de ce projet, nous avons visé à simuler un environnement de jeu complet, en implémentant un plateau de jeu, des interactions joueurs et un affichage des scores en temps réel via l'interface graphique utilisateur (GUI) de Java. Le jeu permet à 2 à 3 joueurs de participer, y compris contre des joueurs virtuels (IA) dotés de capacités de décision stratégique basiques.

#### **1.2 Signification du projet**

1. **Amélioration des compétences techniques**  
   Au cours de la conception et de la mise en œuvre du projet, nous avons approfondi et pratiqué les principes fondamentaux de la programmation orientée objet, y compris l'encapsulation, l'héritage et le polymorphisme. Grâce à la conception de la structure hexagonale du plateau, nous avons renforcé notre maîtrise des structures de données complexes.  
   De plus, le projet implique la mise en œuvre de la GUI Java, nous permettant de maîtriser l'utilisation de la bibliothèque Swing pour concevoir une interface utilisateur intuitive et conviviale.

2. **Exercice de travail d'équipe**  
   Le développement du jeu a été réalisé en groupe, avec une division claire des tâches et une collaboration efficace à travers la communication et la gestion des versions, nous permettant de mener à bien le projet du design à la mise en œuvre. Ce mode de coopération a renforcé notre capacité à travailler en équipe, préparant le terrain pour de futurs projets de développement.

3. **Capacité d'apprentissage orientée vers l'avenir**  
   En développant ce projet, nous avons commencé à comprendre comment appliquer les connaissances théoriques du développement logiciel à des problèmes réels, et à résoudre des problèmes pratiques tels que le débogage et l'optimisation des algorithmes. Cela nous a fourni une expérience précieuse pour le développement de projets plus complexes à l'avenir.

---

### **2. Analyse des besoins**

#### **2.1 Besoins fonctionnels**

Les fonctionnalités clés du jeu comprennent les points suivants :

1. **Rôles et interactions des joueurs**  
   - Support pour 2 à 3 joueurs, y compris des joueurs virtuels (IA).  
   - Les joueurs peuvent choisir la couleur de leurs vaisseaux et les déployer, chacun prenant son tour pour agir.  
   - Les joueurs choisissent l'ordre d'exécution des cartes de commandement à chaque tour et interagissent via l'interface.

2. **Plateau de jeu et gestion des secteurs**  
   - Utilisation d'un plateau hexagonal pour simuler les secteurs galactiques, comprenant des secteurs de différents niveaux (Niveau 0 à Niveau 3).  
   - Le secteur central "Tri-Prime" est fixé comme zone clé de conflit.

3. **Comportements et contrôle des vaisseaux**  
   - La carte de commande "Expansion" permet aux joueurs d'ajouter des vaisseaux à leurs secteurs contrôlés.  
   - La carte "Exploration" permet aux joueurs de déplacer leurs vaisseaux vers des hexagones adjacents pour explorer de nouveaux secteurs.  
   - La carte "Extermination" est utilisée pour engager le combat, permettant aux joueurs d'attaquer les vaisseaux ennemis et de tenter de prendre de nouveaux secteurs.

4. **Objectifs et scoring du jeu**  
   - L'objectif est d'accumuler des points, calculés en fonction du niveau des secteurs et de leur contrôle.  
   - À la fin de chaque tour, les joueurs doivent choisir un secteur pour le scoring, avec un secteur supplémentaire possible pour le joueur contrôlant le secteur Tri-Prime.  
   - À la fin du jeu, tous les scores des secteurs sont recalculés et doublés, et le joueur avec le plus haut score gagne.

#### **2.2 Besoins non fonctionnels**

1. **Expérience utilisateur**  
   - L'interface du jeu doit être intuitive et conviviale, incluant un affichage clair du plateau hexagonal, des informations de contrôle des joueurs et un tableau de scores en temps réel.  
   - Le système doit fournir des retours et des indications d'erreur immédiats aux entrées des utilisateurs pour éviter les erreurs de manipulation.

2. **Performance du système**  
   - Le jeu doit fonctionner fluidement sur une configuration informatique standard, avec des réponses rapides de l'interface.  
   - La conception des structures de données et des algorithmes doit être efficace, assurant que la logique du jeu soit traitée dans un délai raisonnable.

3. **Extensibilité et maintenabilité**  
   - La conception du système doit être modulaire, facilitant les futures extensions, telles que l'ajout de nouvelles cartes de commandement ou de joueurs virtuels plus intelligents.  
   - La structure du code doit être claire et compréhensible, facilitant la maintenance et le débogage.

---

### **3. Conception du système**

#### **3.1 Architecture globale**

L'architecture du système de jeu comprend les modules principaux suivants :

1. **Module d'interface utilisateur** : Responsable de l'affichage graphique du jeu et de l'interaction avec les joueurs.
   - **`HexBoardDisplay`** : Dessine le plateau hexagonal, affichant le niveau des secteurs et leur état.
   - **`OccupationDisplay`** : Montre l'information d'occupation de chaque hexagone, y compris le joueur occupant et le nombre de vaisseaux.
   - **`ScoreBoard`** : Affiche les scores des joueurs et les met à jour en temps réel.

2. **Module de logique de jeu principale** : Contrôle le déroulement du jeu, l'implémentation des règles et les actions des joueurs.
   - **`Game`** : Contrôleur principal, gérant les tours, l'exécution des commandes, le calcul des scores et la logique de fin de jeu.
   - **`Player`** et **`VirtualPlayer`** : Représentent les actions des joueurs, le joueur virtuel ayant une logique d'IA simple.
   - **`Ship`** : Représente les vaisseaux et leurs positions, états et comportements de mouvement.

3. **Module de gestion des données** : Gère les données du plateau et les informations de scoring.
   - **`HexBoard`** et **`Hex`** : Gèrent le plateau hexagonal et ses niveaux de secteurs, le calcul des voisins et les informations d'occupation.
   - **`ScoreManager`** et **`RoundScore`** : Enregistrent les scores de chaque joueur et permettent des requêtes et mises à jour par tour.

4. **Module de commande et de direction** : Implémente la logique d'opération des actions des joueurs.
   - **`CommandCard`** : Représente les cartes de commande (Expansion, Exploration, Extermination).
   - **`CommandType`** : Définit les types de cartes de commande.

---

#### **3.2 Diagramme de classes et relations**

Le diagramme UML des classes présente clairement les différents modules et leurs relations, par exemple :
- **`Game`** dépend de **`HexBoard`** et **`Player`** pour contrôler le déroulement du jeu.
- **`Player`** possède plusieurs **`Ship`**, exécutant des commandes à travers **`CommandCard`**.
- **`HexBoard`** gère plusieurs **`Hex`** et leurs informations d'occupation.
- **`ScoreManager`** gère les scores de tous les joueurs et interagit avec **`ScoreBoard`**.

<img width="1016" alt="5826fb5359ba7a154ff9614dd009f35" src="https://github.com/user-attachments/assets/a93aaff1-8f49-4df1-863e-b0ebf59df5d2">

---

#### **3.3 Conception des classes principales**



**1. Classe `Game`**
- **Fonction** : Agit comme le contrôleur central du jeu, gérant les tours de jeu, l'exécution des commandes et le calcul des scores.
- **Méthodes clés** :
  - **`startGame()`** : Initialise le plateau, les joueurs et le tableau des scores, et commence le premier tour.
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
  - **`nextTurn()`** : Contrôle le déroulement de chaque tour, incluant l'exécution des commandes, le calcul des scores et la mise à jour des états.
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
  - **`executeRound()`** : Exécute les commandes de chaque joueur dans l'ordre déterminé.
    ```java
    public void executeRound() {
        for (int commandPos = 0; commandPos < 3; commandPos++) {
            for (Player player : players) {
                executeCommands(player, player.getCurrentCommandOrder().get(commandPos));
            }
        }
    }
    ```

**2. Classe `HexBoard`**
- **Fonction** : Gère les informations des secteurs hexagonaux et leur état d'occupation.
- **Méthodes clés** :
  - **`initializeBoard()`** : Initialise le plateau, y compris la zone centrale fixe et les secteurs périphériques aléatoires.
    ```java
    private void initializeBoard() {
        Hex[] fixedHexes = { new Hex(0, 0, 0), new Hex(1, -1, 0) };
        for (Hex hex : fixedHexes) {
            board.put(hex, new Sector(3));
        }
    }
    ```
  - **`updateOccupation(Hex, Ship)`** : Met à jour les informations d'occupation d'un hexagone.
    ```java
    public void updateOccupation(Hex hex, Ship ship) {
        occupationMap.computeIfAbsent(hex, k -> new OccupationInfo(ship.getOwner().getId())).addShip(ship);
    }
    ```

**3. Classe `Player`**
- **Fonction** : Représente les actions du joueur, y compris le placement des vaisseaux, l'exécution des commandes et le scoring.
- **Méthodes clés** :
  - **`placeShips(Set<String> occupiedSectors)`** : Le joueur choisit un secteur et place ses vaisseaux.
    ```java
    public void placeShips(Set<String> occupiedSectors) {
        String selectedSector = selectSector(occupiedSectors);
        Hex targetHex = findLevelIHex(selectedSector);
        for (int i = 0; i < 2; i++) {
            ships.add(new Ship(this, hexBoard.getBoard().get(targetHex), targetHex, generateShipId(ships.size() + 1)));
        }
    }
    ```
  - **`chooseCommandOrder()`** : Le joueur choisit l'ordre d'exécution des cartes de commande.
    ```java
    public List<Integer> chooseCommandOrder() {
        System.out.println("Enter the order of commands (1=Expand, 2=Explore, 3=Exterminate):");
        return Arrays.asList(1, 2, 3); // Exemple d'ordre fixe
    }
    ```

---

### **4. Processus de mise en œuvre**

#### **4.1 Implémentation du module d'interface utilisateur**

**1. Classe `HexBoardDisplay`**
- **Fonction** : Dessine le plateau hexagonal, affichant les niveaux des secteurs et leurs coordonnées.
- **Méthodes clés** :
  - **`paint(Graphics g)`** : Implémente la logique de dessin des hexagones.
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

**2. Classe `OccupationDisplay`**
- **Fonction** : Affiche textuellement l'état d'occupation de chaque hexagone.
- **Méthodes clés** :
  - **`displayOccupation()`** : Met à jour dynamiquement l'affichage des informations d'occupation.
    ```java
    public void displayOccupation() {
        Map<Hex, OccupationInfo> occupationMap = hexBoard.getOccupationMap();
        occupationMap.forEach((hex, info) -> System.out.println("Hex: " + hex + " occupied by player: " + info.getPlayerId()));
    }
    ```

**3. Classe `ScoreBoard`**
- **Fonction** : Met à jour en temps réel les informations de scoring des joueurs.
- **Méthodes clés** :
  - **`updateScores()`** : Met à jour les scores de chaque tour et les scores totaux.
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

#### **4.2 Implémentation de la logique centrale du jeu**

**1. Logique de commande**
- **Exécution des commandes par les joueurs** :
  - Méthode `expand` : Permet aux joueurs d'ajouter des vaisseaux dans les secteurs qu'ils contrôlent.
  - Méthode `explore` : Permet la mobilité des vaisseaux.
  - Méthode `exterminate` : Permet aux joueurs de lancer des attaques.

**2. Mise à jour des données**
- Après chaque action, le `HexBoard` met à jour les informations d'occupation via la méthode `updateOccupation`, assurant la synchronisation des données.

---

### **5. Tests et débogage**

#### **5.1 Stratégies de test**

Pour garantir l'intégrité et la stabilité des fonctionnalités du jeu, nous avons adopté les stratégies de test suivantes :

1. **Tests unitaires**
   - Vérifier que les méthodes de chaque classe fonctionnent comme prévu, par exemple que la méthode `updateOccupation` de `HexBoard` met à jour correctement les informations d'occupation, et que la méthode `placeShips` de `Player` place correctement les vaisseaux.
   - Utilisation du cadre JUnit pour écrire des cas de test pour les méthodes clés, validant la logique centrale.

2. **Tests d'intégration**
   - Examiner si les interactions entre différents modules sont correctes, par exemple si la coordination entre `Game` et `HexBoard` est cohérente, et si les actions des joueurs affectent correctement l'affichage des scores.

3. **Tests système**
   - Simuler le processus complet du jeu du point de vue de l'utilisateur pour vérifier que toutes les fonctionnalités fonctionnent correctement, y compris l'expérience d'interaction de l'interface utilisateur.

4. **Tests de limites**
   - Tester le comportement du jeu dans des situations extrêmes, comme lorsque tous les secteurs sont occupés pour le placement des vaisseaux, ou lorsque tous les joueurs choisissent la même carte de commande en même temps.

---

#### **5.2 Exemples de cas de test**

Voici quelques exemples de cas de test :

1. **Test de l'initialisation du plateau**
   - Objectif : Assurer que le plateau est correctement généré, incluant la zone fixe `Tri-Prime` et les secteurs aléatoires.
   - Méthode de test : Appeler la méthode `initializeBoard` de `HexBoard` et vérifier que chaque hexagone a le niveau de secteur approprié.

2. **Test de la logique de commande des joueurs**
   - Objectif : Valider les méthodes `expand`, `explore` et `exterminate` des joueurs.
   - Méthode de test : Simuler un joueur contrôlant un secteur et tester les résultats des commandes dans différents scénarios.

3. **Test de l'interface utilisateur**
   - Objectif : Assurer que `HexBoardDisplay` et `ScoreBoard` affichent correctement.
   - Méthode de test : Lancer le jeu et vérifier si les hexagones sont clairement dessinés et si les scores sont mis à jour en temps réel.

---

#### **5.3 Processus de débogage**

Au cours du développement, nous avons rencontré et résolu plusieurs problèmes clés :

1. **Problème** : Les joueurs ne pouvaient pas correctement choisir des secteurs non occupés pour placer leurs vaisseaux.  
   **Solution** : Ajout d'une logique de vérification des secteurs déjà occupés dans la méthode `placeShips` de la classe `Player`, avec suivi de l'état via l'ensemble `occupiedSectors`.

2. **Problème** : Des retards ou des rendus incomplets sur l'interface utilisateur lors des mises à jour.  
   **Solution** : Ajustement de la méthode `paint` de `HexBoardDisplay`, utilisant `EventQueue.invokeLater` pour assurer la sécurité des threads.

---

### **6. Caractéristiques du projet**

#### **6.1 Fonctionnalités phares**

1. **Conception du plateau hexagonal**
   - Utilisation d'un système de coordonnées à trois axes pour représenter les hexagones, combiné à des algorithmes pour calculer les voisins et la logique de mouvement.
   - La zone centrale `Tri-Prime` est fixée comme secteur de niveau 3, constituant l'objectif principal de la lutte.

2. **Logique des joueurs virtuels (IA)**
   - Les joueurs IA peuvent choisir aléatoirement des secteurs pour placer leurs vaisseaux et décider de l'ordre d'exécution des cartes de commandement basé sur des stratégies simples.
   - Lors des attaques (`exterminate`), l'IA choisit aléatoirement des secteurs cibles, simulant le comportement des joueurs humains.

3. **Affichage dynamique des scores**
   - Implémentation de la classe `ScoreBoard`, affichant par tableau les scores de chaque joueur par tour et le total, mis à jour en temps réel.

---

#### **6.2 Innovations techniques**

1. **Conception modulaire**
   - Chaque module de fonctionnalité est conçu séparément, améliorant la maintenabilité et l'extensibilité du code.

2. **Programmation orientée objet (OOP)**
   - Utilisation de l'héritage et du polymorphisme pour implémenter des différences de comportement entre `Player` et `VirtualPlayer`, réduisant la redondance du code.

3. **Séparation entre l'interface utilisateur et la logique**
   - Séparation des couches d'interface (comme `HexBoardDisplay`) et de logique (comme `HexBoard`), assurant une structure de code claire.

---

### **7. Défis et problèmes du projet**

#### **7.1 Analyse des défis**

1. **Implémentation de la grille hexagonale**
   - Défi : Calcul des coordonnées hexagonales et implémentation de la logique des voisins.
   - Solution : Utilisation d'un système de coordonnées cubiques, combiné à la méthode `getNeighbors` de la classe `Hex` pour calculer précisément les voisins.

2. **Conception de l'IA des joueurs virtuels**
   - Défi : Permettre à l'IA de prendre des décisions raisonnables sans ajouter de complexité.
   - Solution : Utilisation de stratégies aléatoires simples, combinées aux règles existantes du jeu, pour rendre le comportement de l'IA naturel.

3. **Collaboration entre modules**
   - Défi : L'interaction entre les modules peut augmenter le couplage, affectant les expansions futures.
   - Solution : Gestion unifiée du processus par la classe `Game`, réduisant la dépendance directe entre modules.

---

#### **7.2 Gestion des challenges**

- **Challenge** : Mise à jour en temps réel de l'interface utilisateur dans le flux de jeu.
  **Solution** : Utilisation du mécanisme de thread du framework Swing pour garantir que le rendu de l'interface et la logique du jeu fonctionnent de manière indépendante.

- **Challenge** : Gestion des entrées de données illégales par les joueurs.
  **Solution** : Ajout de la validation des entrées dans toutes les méthodes d'interaction utilisateur, avec des indications d'erreur amicales.

---

### **8. Conclusion et perspectives**

#### **8.1 Conclusion**

À travers le développement de ce projet, nous avons réalisé un jeu de stratégie de plateau complet. Les caractéristiques du projet incluent :
- Simulation d'un plateau hexagonal réel et des règles des secteurs.
- Interaction entre les joueurs et les joueurs virtuels.
- Calcul dynamique des scores et affichage en temps réel de l'interface.

Le projet a non seulement approfondi notre compréhension de la programmation orientée objet en Java, mais nous a également initiés aux bases du développement de l'interface utilisateur. De plus, dans la collaboration en équipe, nous avons appris la division des tâches, l'intégration du code et la gestion des versions.

---

#### **8.2 Perspectives**

Pour les directions d'amélioration futures, nous proposons les points suivants :

1. **Extension des fonctionnalités**
   - Ajout de nouveaux types de cartes de commande, comme "Défense" ou "Collecte de ressources".
   - Introduction de différentes races, donnant à chaque joueur des capacités uniques.

2. **Optimisation de l'IA**
   - Conception d'algorithmes de décision plus intelligents pour les joueurs virtuels, pour une expérience de confrontation plus réaliste.

3. **Support réseau**
   - Implémentation du multijoueur en ligne pour augmenter l'interactivité et la jouabilité du jeu.

4. **Optimisation de l'interface**
   - Passage à une bibliothèque graphique plus moderne (comme JavaFX) pour améliorer l'esthétique de l'interface.

Avec ces améliorations, nous espérons faire du jeu un projet complet et durablement évolutif, tout en établissant une base solide pour les études et recherches futures.
