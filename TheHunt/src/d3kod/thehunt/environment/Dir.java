package d3kod.thehunt.environment;

import android.graphics.PointF;

public enum Dir {
	E(0), S(2), W(4), N(6), NE(7), SE(1), SW(3), NW(5), UNDEFINED(8);
	public static final int numDirs = 9;
	private int index;
	private Dir(int setIndex) {
		index = setIndex;
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
}
