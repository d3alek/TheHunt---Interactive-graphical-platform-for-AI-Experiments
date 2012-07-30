package d3kod.thehunt.prey.memory;

public class Node {
	float mCurrentX, mCurrentY;
	private int mRow;
	private int mCol;
	private float mX;
	private float mY;
	public Node(int row, int col, float x, float y) {
		mRow = row; mCol = col;
		mX = x; mY = y;
	}
	public void setCurrent(float currentX, float currentY) {
		mCurrentX = currentX; mCurrentY = currentY;
	}
	public int getCol() {
		return mCol;
	}
	public int getRow() {
		return mRow;
	}
	public float getX() {
		return mX;
	}
	public float getY() {
		return mY;
	}
}
