package d3kod.thehunt.prey.memory;

import java.util.ArrayList;

import android.util.Log;

public class MemoryGraph extends ArrayList<ArrayList<Node>> {
	private static final String TAG = "MemoryGraph";
	private static final int rowNum = 10;
	private static final int colNum = 10;
	private float mScreenHeight;
	private float mScreenWidth;
	private Node mMyNode;
	private float mAtX;
	private float mAtY;
	private float mFoodX;
	private float mFoodY;
	private Node mFoodNode;

	public MemoryGraph(float screenWidth, float screenHeight) {
		super(rowNum);
		mScreenWidth = screenWidth;
		mScreenHeight = screenHeight;
		for (int i = 0; i < rowNum; ++i) {
			ArrayList<Node> row = new ArrayList<Node>(colNum);
			for (int j = 0; j < colNum; ++j) {
				row.add(new Node(i, j, contWidth(j), contHeight(i)));
//				Log.v(TAG, i + " " + j + " " + " " + contWidth(j) + " " +  contHeight(i));
			}
			add(row);
		}
		mFoodNode = null;
	}

	public Node getNode(float posX, float posY) {
		int row = discreteHeight(posY);
		int col = discreteWidth(posX);
//		Log.v(TAG, row + " " + col + " for " + posX + " " + posY);
		return get(row).get(col);
	}

	private int discreteHeight(float height) {
		int dHeight = rowNum/2 - (int)(rowNum*height/mScreenHeight);
		if (dHeight >= rowNum) dHeight = rowNum - 1;
		if (dHeight < 0) dHeight = 0;
		return dHeight;
	}
	private int discreteWidth(float width) {
		int dWidth = colNum/2 + (int)(colNum*width/mScreenWidth);
		if (dWidth >= colNum) dWidth = colNum - 1;
		if (dWidth < 0) dWidth = 0;
		return dWidth;
	}

	private float contHeight(int height) {
		float cHeight = (rowNum/2.0f-height)*mScreenHeight/rowNum;
//		if (dHeight >= rowNum) dHeight = rowNum - 1;
//		if (dHeight < 0) dHeight = 0;
		return cHeight;
	}
	private float contWidth(int width) {
		float cWidth = (-colNum/2.0f+width)*mScreenWidth/colNum;
//		if (dWidth >= colNum) dWidth = colNum - 1;
//		if (dWidth < 0) dWidth = 0;
		return cWidth;
	}
	
	public Node getFoodNode() {
		return mFoodNode;
	}

	public Node getMyNode() {
		return mMyNode;
	}

//	public void setMyNode(float atX, float atY) {
//		int row = discreteHeight(atY);
//		int col = discreteWidth(atX);
//		mMyNode = get(row).get(col);
//	}
//
//	public void setFoodNode(float foodX, float foodY) {
//		mFoodX = foodX; mFoodY = foodY;
//		int row = discreteHeight(foodY);
//		int col = discreteWidth(foodX);
//		mFoodNode = get(row).get(col);
//	}

	public void removeFoodNode() {
		mFoodX = mFoodY = -1;
		mFoodNode = null;
	}
}
