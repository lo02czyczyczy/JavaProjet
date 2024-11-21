package game;

public class Ship {
    private Player owner;  // 舰船的主人
    private Sector currentSector;  // 当前所在星系
    private Hex position;  // 当前坐标位置
    private String idShip;  // 舰船的 ID
    private String color;  // 舰船的颜色
    private boolean moved;

    // 构造函数
    public Ship(Player owner, Sector currentSector, Hex position, String idShip) {
        this.owner = owner;
        this.currentSector = currentSector;
        this.position = position;
        this.idShip = idShip;
        this.color = owner.getColor();  // 从玩家对象中获取颜色
        this.moved = false;
    }

    // 移动方法
    public void move(Hex newPosition, Sector newSector) {
        this.position = newPosition;
        this.currentSector = newSector;
        System.out.println("Ship " + idShip + " owned by " + owner.getName() + " moved to Hex coordinates (" + newPosition.q + ", " + newPosition.r + ", " + newPosition.s + ").");
    }
    
    public boolean isMoved() {
        return moved;
    }
    
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    // 参与战斗的方法
    public void engageInBattle() {
        // 实现参与战斗的逻辑
        System.out.println("Ship " + idShip + " owned by " + owner.getName() + " is engaging in battle.");
    }

    // 获取舰船当前位置
    public Hex getShipLocation() {
        return position;
    }

    // Getters and Setters
    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Sector getCurrentSector() {
        return currentSector;
    }

    public void setCurrentSector(Sector currentSector) {
        this.currentSector = currentSector;
    }

    public String getIdShip() {
        return idShip;
    }

    public void setIdShip(String idShip) {
        this.idShip = idShip;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "owner=" + owner.getName() +
                ", idShip='" + idShip + '\'' +
                ", color='" + color + '\'' +
                ", position=(" + position.q + ", " + position.r + ", " + position.s + ")" +
                ", currentSectorLevel=" + currentSector.getSector() +
                '}';
    }
}
