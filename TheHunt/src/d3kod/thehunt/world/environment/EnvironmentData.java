package d3kod.thehunt.world.environment;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.thehunt.world.environment.FloatingObject.Type;

public class EnvironmentData {
	public static final String TAG = "EnvironmentData";
	
	public static final int numDirs = Dir.numDirs;
	
	public static final float frictionCoeff = 0.1f;//0.015f;
	
	public static final int tileRows = 12;
	public static final int tileCols = 20; 

	public static final float currentSpeed = 0.0005f;

	public static final float[] AlGAE_HARDCODED_POS = {
		-0.5f, 0.5f,
		1.0f, -0.3f,
		0.3f, 0.11f,
		-0.8f, -0.5f,
		1.2f, 0.7f
	};

	private static final int FOOD_GM_SIZE_INCREMENT = 3;
	
//	private static final float[] AlGAE_HARDCODED_POS = {
//		0f, 0f
//	};
	
	public static float tHeight;
	public static float tWidth;
	public static float mScreenWidth;
	public static float mScreenHeight;
	
	
	private ArrayList<FloatingObject> mFloatingObjects;

	private ArrayList<FloatingObject> mFloatingObjectsToAdd;

	private ArrayList<FloatingObject> mFloatingObjectsToSetGraphics;
	
	transient private SpriteManager mD3GLES20;

	private boolean mGraphicsAreSet;

	public static Tile[][] mTiles;

	public static int realWidth;

	public static int realHeight;

	private static float mFoodX;

	private static float mFoodY;

	public EnvironmentData(int width, int height) {
		setSize(width, height);
		createTiles();
		mFoodX = -1; mFoodY = -1;
		mFloatingObjects = new ArrayList<FloatingObject>();
		mFloatingObjectsToAdd = new ArrayList<FloatingObject>();
		mFloatingObjectsToSetGraphics = new ArrayList<FloatingObject>();
		mGraphicsAreSet = false;
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
	}

	public void addFloatingObject(FloatingObject floatingObject) {
		mFloatingObjectsToAdd.add(floatingObject);
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
	
	public FloatingObject removeFood(float x, float y, float radius) {
		for (FloatingObject fo: mFloatingObjects) {
			if (fo.getType() != Type.FOOD_GM) continue;
			float foX = fo.getX(), foY = fo.getY();
			if (D3Maths.circleContains(x, y, radius, foX, foY)) {//Prey.EAT_FOOD_RADIUS
				fo.clearGraphic();
				mFloatingObjects.remove(fo);
				return fo;
			}
		}
		return null;
	}

	public void logFloatingObjects() {
		String log = "Floating objects log: ";
		for (FloatingObject fo: mFloatingObjects) {
			log += fo.getType() + " " + fo.getX() + " " + fo.getY() + " ";
		}
		Log.v(TAG, log);
	}

	public void updateFloatingObjects() {
		ArrayList<FloatingObject> toRemove = new ArrayList<FloatingObject>();
		
		for (FloatingObject fo: mFloatingObjects) {
			fo.update();
			if (fo.toRemove() || !environmentContains(fo)) {
				fo.setToRemove();
				toRemove.add(fo);
			}
		}
		
		// Elementary collision detection between algae + GM food
		FloatingObject fo1, fo2;
		NAlgae algae1, algae2;
		for (int i = 0; i < mFloatingObjects.size(); ++i) {
			fo1 = mFloatingObjects.get(i);
			if (fo1.toRemove() || fo1.getType() != Type.ALGAE) continue;
			for (int j = i; j < mFloatingObjects.size(); ++j) {
				fo2 = mFloatingObjects.get(j);
				if (fo2.toRemove() 
						|| fo2.getType() != Type.ALGAE
						|| fo2 == fo1) {
					continue;
				}
				if (mD3GLES20.spritesCollide(fo1, fo2)) {
					algae1 = ((NAlgae)fo1);
					algae2 = ((NAlgae)fo2);
					if (algae1.getN() > algae2.getN()) {
						algae1.mergeWith(algae2);
						if (algae2.getN() == 0) {
							algae2.setToRemove();
							toRemove.add(algae2);
						}
						break;
					}
					else {
						algae2.mergeWith(algae1);
						if (algae1.getN() == 0) {
							algae1.setToRemove();
							toRemove.add(algae1);
						}
						break;
					}
				}
			}
			for (int j = 0; j < mFloatingObjects.size(); ++j) {
				fo2 = mFloatingObjects.get(j);
				if (fo2.toRemove() || fo2.getType() != Type.FOOD_GM) {
					continue;
				}
				if (mD3GLES20.spritesCollide(fo1, fo2)) {
					algae1 = (NAlgae) fo1;
					algae1.grow(FOOD_GM_SIZE_INCREMENT);
					fo2.setToRemove();
					toRemove.add(fo2);
				}
			}
		}
		
		
		for (FloatingObject fo: toRemove) {
			fo.clearGraphic();
			mFloatingObjects.remove(fo);
		}

		mFloatingObjects.addAll(mFloatingObjectsToAdd);
		if (mGraphicsAreSet) {
			for (FloatingObject fo: mFloatingObjectsToAdd) {
				fo.initGraphic();
			}
			for (FloatingObject fo: mFloatingObjectsToSetGraphics) {
//				Log.v(TAG, "Init graphic for fo " + fo.getX() + fo.getY() + fo.getRadius());
				fo.setSpriteManager(mD3GLES20);
				fo.initGraphic();
			}
			mFloatingObjectsToSetGraphics.clear();
		}
		else {
			mFloatingObjectsToSetGraphics.addAll(mFloatingObjectsToAdd);
		}
		mFloatingObjectsToAdd.clear();
	}

	private boolean environmentContains(FloatingObject fo) {
		return D3Maths.rectContains(
				0, 0, mScreenWidth, mScreenHeight, fo.getX(), fo.getY());
	}
	
	public ArrayList<FloatingObject> getFloatingObjectsToAdd() {
		return mFloatingObjectsToAdd;
	}

	public void setGraphics(SpriteManager d3gles20) {
		mGraphicsAreSet = true;
		mD3GLES20 = d3gles20;
	    mFloatingObjectsToSetGraphics.addAll(mFloatingObjects); // to reinit graphics on db restore
	}
}