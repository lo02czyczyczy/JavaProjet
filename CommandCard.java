package game;

import java.util.*;

public class CommandCard {
    private CommandType commandType;
    private Player player;
    private List<Integer> order;

    // 构造函数
    public CommandCard(CommandType commandType, Player player, String order) {
        this.commandType = commandType;
        this.player = player;
        this.order = player.chooseCommandOrder();
    }

    // 方法
    public void execute() {
        // 实现命令执行的逻辑
    }

    public boolean isExpandable() {
        // 判断是否可扩张
        return commandType == CommandType.EXPAND;
    }

    public boolean isExplorable() {
        // 判断是否可探索
        return commandType == CommandType.EXPLORE;
    }

    public boolean isExterminable() {
        // 判断是否可消灭
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