package game;

import java.util.*;

public class CommandCard {
    private CommandType commandType;
    private Player player;
    private List<Integer> order;

    // ���캯��
    public CommandCard(CommandType commandType, Player player, String order) {
        this.commandType = commandType;
        this.player = player;
        this.order = player.chooseCommandOrder();
    }

    // ����
    public void execute() {
        // ʵ������ִ�е��߼�
    }

    public boolean isExpandable() {
        // �ж��Ƿ������
        return commandType == CommandType.EXPAND;
    }

    public boolean isExplorable() {
        // �ж��Ƿ��̽��
        return commandType == CommandType.EXPLORE;
    }

    public boolean isExterminable() {
        // �ж��Ƿ������
        return commandType == CommandType.EXTERMINATE;
    }

    // Getters and Setters
    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Integer> getOrder() {
        return order;
    }

    public void setOrder(List<Integer> order) {
        this.order = order;
    }
}