package d3kod.thehunt.world.environment;

import android.graphics.PointF;

public enum Dir {
	E(0, -90), S(2, 180), W(4, 90), N(6, 0), NE(7, -45), SE(1, -135), SW(3, 135), NW(5, 45), UNDEFINED(8, 0);
	public static final int numDirs = 9;
	private int index;
	private int mAngle;
	private Dir(int setIndex, int angle) {
		index = setIndex;
		mAngle = angle;
	}
	public int getIndex() {
		return index;
	}
	public static Dir dirByIndex(int index) {
		switch(index) {
		case 0: return E;
		case 1: return SE;
		case 2: return S;
		case 3: return SW;
		case 4: return W;
		case 5: return NW;
		case 6: return N;
		case 7: return NE;
		default: return UNDEFINED;
		}
	}
	public PointF getDelta() {
		switch(index) {
		case 0: return new PointF(1, 0);
		case 1: return new PointF(1, -1);
		case 2: return new PointF(0, -1);
		case 3: return new PointF(-1, -1);
		case 4: return new PointF(-1, 0);
		case 5: return new PointF(-1, 1);
		case 6: return new PointF(0, 1);
		case 7: return new PointF(1, 1);
		default: return new PointF(0, 0);
		}
	}
	public float getAngle() {
		return mAngle;
	}
}
