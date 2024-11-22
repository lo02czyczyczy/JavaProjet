package game;

public class Ship {
    private Player owner;  // ����������
    private Sector currentSector;  // ��ǰ������ϵ
    private Hex position;  // ��ǰ����λ��
    private String idShip;  // ������ ID
    private String color;  // ��������ɫ
    private boolean moved;

    // ���캯��
    public Ship(Player owner, Sector currentSector, Hex position, String idShip) {
        this.owner = owner;
        this.currentSector = currentSector;
        this.position = position;
        this.idShip = idShip;
        this.color = owner.getColor();  // ����Ҷ����л�ȡ��ɫ
        this.moved = false;
    }

    // �ƶ�����
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

    // ����ս���ķ���
    public void engageInBattle() {
        // ʵ�ֲ���ս�����߼�
        System.out.println("Ship " + idShip + " owned by " + owner.getName() + " is engaging in battle.");
    }

    // ��ȡ������ǰλ��
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
