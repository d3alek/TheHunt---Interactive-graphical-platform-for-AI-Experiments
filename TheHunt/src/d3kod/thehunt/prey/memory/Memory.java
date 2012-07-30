package d3kod.thehunt.prey.memory;

import java.util.ArrayList;
import java.util.LinkedList;


public class Memory {
	MemoryGraph mNodes;
	public Memory(float screenWidth, float screenHeight) {
		mNodes = new MemoryGraph(screenWidth, screenHeight);
	}
	public void updateNode(float posX, float posY, float currentX, float currentY) {
		mNodes.getNode(posX, posY).setCurrent(currentX, currentY);
	}
}
