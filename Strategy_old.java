package game;

import java.util.*;

public interface Strategy_old {
    
	// Ĭ����������Ǹ�ɵ��
    // Ĭ�Ϸ�������� 1, 2, 3 ˳��
    default List<Integer> chooseCommandOrder() {
    	List<Integer> commands = Arrays.asList(1, 2, 3);
        Collections.shuffle(commands);  // �����������
        return commands;
    }
    
    default String chooseSectorCard(Set<String> availableSectors) {
        List<String> sectorsList = new ArrayList<>(availableSectors);
        Collections.shuffle(sectorsList);
        return sectorsList.isEmpty() ? null : sectorsList.get(0);  // ���ѡ��һ�����õ���ϵ��
    }

    void makeDecision();
}