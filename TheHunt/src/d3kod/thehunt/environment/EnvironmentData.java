package d3kod.thehunt.environment;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.environment.FloatingObject.Type;
import d3kod.thehunt.events.EventNoise;

public class EnvironmentData {
	public static final String TAG = "EnvironmentData";
	
	public static final int numDirs = Dir.numDirs;
	
	public static final float frictionCoeff = 0.1f;//0.015f;
	
	public static final int tileRows = 12;
	public static final int tileCols = 20; 

	public static final float currentStep = 0.002f;

	private static final int ALGAE_NUM = 5;
	
	private static final float[] AlGAE_HARDCODED_POS = {
		-0.5f, 0.5f,
		1.0f, -0.3f,
		0.3f, 0.11f,
		-0.8f, -0.5f,
		1.2f, 0.7f
	};
	
//	private static final float[] AlGAE_HARDCODED_POS = {
//		0f, 0f
//	};
	
	public static float tHeight;
	public static float tWidth;
	public static float mScreenWidth;
	public static float mScreenHeight;
	
	
	private ArrayList<FloatingObject> mFloatingObjects;

//	private ArrayList<EventNoise> mNoiseEvents;
	public static Tile[][] mTiles;
//	public Currents currents;

	public static int realWidth;

	public static int realHeight;

	private static float mFoodX;

	private static float mFoodY;

//	public EnvironmentData() {
//		this(1, 1);
//	}
	
	public EnvironmentData(int width, int height) {
		setSize(width, height);
		createTiles();
//		currents = new Currents(this);
//		currents.initialize();
		mFoodX = -1; mFoodY = -1;
		mFloatingObjects = new ArrayList<FloatingObject>();
//		mNoiseEvents = new ArrayList<EventNoise>();
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
//		Tile.initBuffers();
	}
//
	public void addFloatingObject(FloatingObject floatingObject, int graphicKey) {
		floatingObject.setGraphic(graphicKey);
		mFloatingObjects.add(floatingObject);
		
	}

	public ArrayList<FloatingObject> getFloatingObjects() {
		return mFloatingObjects;
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
	
	public void removeFood(float x, float y) {
		for (FloatingObject fo: mFloatingObjects) {
			if (fo.getType() != Type.FOOD) continue;
			float foX = fo.getX(), foY = fo.getY();
			if (D3Maths.rectContains(x, y, 0.2f, 0.2f, foX, foY)) {
				fo.clearGraphic();
				mFloatingObjects.remove(fo);
				return;
			}
		}
	}

	public void makeAlgae(int textureDataHandle, D3GLES20 d3GLES20) {
		float algaeX, algaeY;
		for (int i = 0; i < ALGAE_NUM; ++i) {
			algaeX = AlGAE_HARDCODED_POS[i*2]; algaeY = AlGAE_HARDCODED_POS[i*2+1];
			mFloatingObjects.add(new Algae(algaeX, algaeY, textureDataHandle, d3GLES20));
//			addFloatingObject(
//					new FloatingObject(D3GLES20.newDefaultCircle(ALGAE_SIZE, algaeColor, ALGAE_DETAILS), algaeX, algaeY, Type.ALGAE));
		}
	}
	
	public void logFloatingObjects() {
		String log = "Floating objects log: ";
		for (FloatingObject fo: mFloatingObjects) {
			log += fo.getType() + " " + fo.getKey() + " ";
		}
		Log.v(TAG, log);
	}

	public void updateFloatingObjects() {
		for (FloatingObject fo: mFloatingObjects) {
			fo.update();
		}
	}

//	public ArrayList<EventNoise> getNoiseEvents() {
//		return mNoiseEvents;
//	}
//
//	public void addNoise(float x, float y) {
////		Tile noiseTile = getTileFromPos(new PointF(x, y));
//		mNoiseEvents.add(new EventNoise(x, y));
//	}
}