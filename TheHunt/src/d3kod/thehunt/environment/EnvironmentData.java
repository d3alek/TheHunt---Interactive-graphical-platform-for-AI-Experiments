package d3kod.thehunt.environment;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.environment.FloatingObject.Type;
import d3kod.thehunt.prey.sensor.Event;
import d3kod.thehunt.prey.sensor.EventFood;
import d3kod.thehunt.prey.sensor.EventNone;

public class EnvironmentData {
	public static final String TAG = "TheHuntDebug";
	
	public static final int numDirs = Dir.numDirs;
	
	public static final float frictionCoeff = 0.05f;//0.015f;
	
	public static final int tileRows = 12;
	public static final int tileCols = 20; 

	public static final float currentStep = 0.002f;
	public static float tHeight;
	public static float tWidth;
	public static float mScreenWidth;
	public static float mScreenHeight;
	
//	public Algae mFloatingObjects;
//	public Rect mFrame;
	private ArrayList<FloatingObject> mFloatingObjects = new ArrayList<FloatingObject>();
	public static Tile[][] mTiles;
	public Currents currents;

	public static int realWidth;

	public static int realHeight;

	private static float mFoodX;

	private static float mFoodY;

	public EnvironmentData() {
		this(1, 1);
	}
	
	public EnvironmentData(int width, int height) {
		setSize(width, height);
//		tHeight = tileRows/height;
//		tWidth = tileCols/width;
		currents = new Currents(this);
		mFoodX = -1; mFoodY = -1;
	}
	
	public void setSize(int width, int height) {
		//TODO: Change tHeight instead if width < height
		realWidth = width;
		realHeight = height;
		mScreenHeight = 2f;
		mScreenWidth = (2f*width)/height;
		tHeight = mScreenHeight/tileRows; tWidth = mScreenWidth/tileCols;
		Log.v(TAG, "Setting size to " + width + " " + height + " Screen size: " + mScreenWidth + " " + mScreenHeight);
	}
	
	public static enum Dir {
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
	
	public void createTiles() {

//		tileRows = 24;
//		tileCols = 40;
		mTiles = new Tile[tileRows][tileCols];
		Log.v(TAG, "Tiles width:" + tWidth + " height:" + tHeight);
		// Make the corner and border tiles with currents pointing inwards
		
		// Corner tiles
		mTiles[0][0] = new Tile(0, 0, Dir.SE);
		mTiles[0][tileCols-1] = new Tile(0, tileCols-1, Dir.SW);
		mTiles[tileRows-1][tileCols-1] = new Tile(tileRows-1, tileCols-1, Dir.NW);
		mTiles[tileRows-1][0] = new Tile(tileRows-1, 0, Dir.NE);
		
		// Border tiles
		for (int i = 1; i < tileRows-1; i++) {
			mTiles[i][0] = new Tile(i, 0, Dir.E);
			mTiles[i][tileCols-1] = new Tile(i, tileCols-1, Dir.W);
		}
		for (int i = 1; i < tileCols-1; i++) {
			mTiles[0][i] = new Tile(0, i, Dir.S);
			mTiles[tileRows-1][i] = new Tile(tileRows-1, i, Dir.N);
		}
		
		// Make the inner tiles with undefined currents
		for (int i = 1; i < tileRows-1; i++) {
			for (int j = 1; j < tileCols-1; j++) {
				mTiles[i][j] = new Tile(i, j);
			}
		}
		
		// Fill the buffers for drawing the tiles
		Tile.initBuffers();
	}

	public void addFloatingObject(FloatingObject floatingObject) {
		mFloatingObjects.add(floatingObject);
		
	}

	public ArrayList<FloatingObject> getFloatingObjects() {
		return mFloatingObjects;
	}

	public Event senseCurrent(float x, float y) {
		Tile tile = getTileFromPos(new PointF(x, y));
		return new EventNone();
	}

	public Event senseFood(float x, float y) {
//		Tile tile = getTileFromPos(new PointF(x, y));
//		if (mFoodX == -1 || mFoodY == -1) return new EventNone();
		for (FloatingObject fo: mFloatingObjects) {
			if (fo.getType() == Type.FOOD) return new EventFood(fo.getX(), fo.getY());
		}
		return new EventNone();
	}
	public static Tile getTileFromPos(PointF pos) {
		int col = (int) Math.floor(tileCols/2+pos.x/tWidth),
				row = (int) Math.floor(tileRows/2-pos.y/tHeight);
		if (col < 0) col = 0;
		if (col >= tileCols) col = tileCols - 1;
		if (row < 0) row = 0;
		if (row >= tileRows) row = tileRows - 1;
		return mTiles[row][col]; 
	}

//	public void newFood(float x, float y) {
//		mFoodX = x; mFoodY = y;
//	}
	public void removeFood(float x, float y) {
		for (FloatingObject fo: mFloatingObjects) {
			float foX = fo.getX(), foY = fo.getY();
			if (D3GLES20.rectContains(x, y, 0.2f, 0.2f, foX, foY)) {
				Log.v(TAG, "Removing floating object " + fo.getIndex());
				mFloatingObjects.remove(fo);
				return;
			}
		}
	}
}