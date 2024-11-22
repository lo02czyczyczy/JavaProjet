package game;

import java.util.*;

public interface Strategy_old {
    
	// 默认虚拟玩家是个傻逼
    // 默认返回随机的 1, 2, 3 顺序
    default List<Integer> chooseCommandOrder() {
    	List<Integer> commands = Arrays.asList(1, 2, 3);
        Collections.shuffle(commands);  // 随机排序命令
        return commands;
    }
    
    default String chooseSectorCard(Set<String> availableSectors) {
        List<String> sectorsList = new ArrayList<>(availableSectors);
        Collections.shuffle(sectorsList);
        return sectorsList.isEmpty() ? null : sectorsList.get(0);  // 随机选择一个可用的星系卡
    }

    void makeDecision();
}