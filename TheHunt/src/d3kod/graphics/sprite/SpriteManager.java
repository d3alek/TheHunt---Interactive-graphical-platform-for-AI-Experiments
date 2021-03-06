package d3kod.graphics.sprite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.primalpond.hunt.world.environment.FloatingObject;
import com.primalpond.hunt.world.tools.D3CatchNetPath;

import android.content.Context;
import android.content.SyncResult;
import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.shader.programs.Program;
import d3kod.graphics.sprite.shapes.D3FadingShape;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.sprite.shapes.D3Shape;
import d3kod.graphics.text.GLText;
import d3kod.graphics.texture.TextureManager;

public class SpriteManager {
	public static final int COORDS_PER_VERTEX = 3;

	static final String TAG = "D3GLES20";
	private static final int TEMP_CIRCLE_TICKS = 50;

	private HashMap<Integer, D3Sprite> sprites;
	private HashMap<Integer, D3FadingShape> expiringShapes;
	private ArrayList<D3FadingText> mTexts;
	private int spritesNum = 0;

	transient private ShaderProgramManager sm;

	ArrayList<Integer> toRemove = new ArrayList<Integer>();

	//	private boolean removeSpriteLater;

	private GLText mGLText;

	private TextureManager tm;

	public SpriteManager(ShaderProgramManager shaderManager, TextureManager tm, Context context) {
		sprites = new HashMap<Integer, D3Sprite>();
		expiringShapes = new HashMap<Integer, D3FadingShape>();
		mTexts = new ArrayList<D3FadingText>();
		spritesNum = 0;
		sm = shaderManager;
		//		removeSpriteLater = false;
		this.tm = tm;
	}

	public SpriteManager(HashMap<Integer, D3Sprite> loadSprites, ShaderProgramManager shaderManager, TextureManager tm, Context context) {
		this(shaderManager, tm, context);
		sprites = loadSprites;
	}

	//TODO: use this instead of individual update calls
	//	public void updateAll() {
	//		for (D3Sprite sprite: sprites.values()) {
	//			sprite.update();
	//		}
	//	}

	public void draw(int key, float[] mMMatrix, float[] mVMatrix, float[] mProjMatrix) {
		sprites.get(key).getGraphic().setModelMatrix(mMMatrix);
		sprites.get(key).getGraphic().draw(mVMatrix, mProjMatrix);
	}

	public void updateAll() {
		Iterator<D3FadingText> it = mTexts.iterator();
		while (it.hasNext()) {
			D3FadingText text = it.next();
			if (!text.fades()) {
				continue;
			}
			text.fade();
			if (text.faded()) {
				it.remove();
			}
		}	
	}

	public void drawAll(float[] mVMatrix, float[] mProjMatrix, float interpolation) {
		if (sprites == null) {
			Log.w(TAG, "Sprites are null in drawAll!");
			return;
		}
		toRemove.clear();
		//		removeSpriteLater = true;
		synchronized (sprites) {
			for (D3Sprite sprite: sprites.values()) {
				sprite.draw(mVMatrix, mProjMatrix, interpolation);
			}
			//		removeSpriteLater = false;
			for (Integer key: toRemove) {
				sprites.remove(key);
			}
		}

		D3FadingShape shape;
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		for (Entry<Integer, D3FadingShape> shapeEntry: expiringShapes.entrySet()) {
			shape = shapeEntry.getValue();
			shape.draw(mVMatrix, mProjMatrix);
			if (shape.isExpired()) toRemove.add(shapeEntry.getKey());
		}
		for (Integer key: toRemove) {
			expiringShapes.remove(key);
		}
		toRemove.clear();

		float[] vpMatrix = new float[16];
		Matrix.multiplyMM(vpMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

		for (D3FadingText text: mTexts) {
			text.draw(mGLText, mProjMatrix, mVMatrix);
		}
	}

	public int putSprite(D3Sprite sprite) {
		if (sprites == null) {
			Log.w(TAG, "Shapes are null in putShape!");
			return 0;
		}
		synchronized (sprites) {

			while (sprites.containsKey(spritesNum)) {
				spritesNum++;
			}
			sprites.put(spritesNum, sprite);
			if (sprite.getGraphic() != null && sprite.getGraphic().getProgram() == null) {
				Log.v(TAG, "Sprite program is not set, setting to default");
				sprite.getGraphic().setProgram(sm.getDefaultProgram());
			}
			Log.i(TAG, sprite + " my num is " + spritesNum);
			return spritesNum++;
		}
	}

	public void putExpiringShape(D3FadingShape shape) {
		if (expiringShapes == null) {
			Log.w(TAG, "expiringShapes are null in putExpiringShape!");
			return;
		}

		int key = 0;
		while (expiringShapes.containsKey(key)) {
			key++;
		}
		expiringShapes.put(key, shape);
	}

	public void removeShape(int key) {
		if (sprites == null) {
			Log.w(TAG, "Shapes are null in removeShape!");
			return;
		}
		synchronized (sprites) {
			sprites.remove(key);
		}
	}

	public boolean contains(int key, PointF point) {
		if (sprites == null) {
			Log.w(TAG, "Shapes are null in contains!");
			return false;
		}
		return sprites.get(key).contains(point);//D3Maths.circleContains(shapes.get(key).getCenterX(), shapes.get(key).getCenterY(), 
		//shapes.get(key).getRadius(), hX, hY);
	}

	public ShaderProgramManager getShaderManager() {
		return sm;
	}


	public void setSpritePosition(int key, PointF position) {
		if (sprites == null) {
			Log.w(TAG, "Sprites are null in setShapePosition!");
			return;
		}
		if (!sprites.containsKey(key)) {
			Log.w(TAG, "Sprites does not contain key " + key);
			return;
		}
		sprites.get(key).setPosition(position);
	}

	public void setSpriteVelocity(int key, float vx, float vy) {
		if (sprites == null) {
			Log.w(TAG, "Sprites are null in setShapeVelocity!");
			return;
		}
		sprites.get(key).setVelocity(vx, vy);
	}

	public D3Sprite getSprite(int key) {
		if (sprites == null) {
			Log.w(TAG, "Shapes are null in getShape!");
			return null;
		}
		return sprites.get(key);
	}

	public boolean spritesCollide(D3Sprite sprite1, D3Sprite sprite2) {
		return D3Maths.circlesIntersect(
				sprite1.getX(), sprite1.getY(), sprite1.getRadius(), 
				sprite2.getX(), sprite2.getY(), sprite2.getRadius());
	}

	public Program getDefaultProgram() {
		return sm.getDefaultProgram();
	}

	public void setShaderManager(ShaderProgramManager sm) {
		this.sm = sm;
	}

	public void init(Context context) {
		mGLText = new GLText(sm.getBatchTextProgram(), context.getAssets());
		mGLText.load( "Roboto-Regular.ttf", 30, 2, 2 );  

	}

	public void putText(D3FadingText text) {
		mTexts.add(text);
	}

	public GLText getTextManager() {
		return mGLText;
	}

	// TODO used only once in catchnet, remove sprite instead
	public void removeShape(D3Shape searchShape) {
		D3Sprite sprite;
		boolean found = false;
		int foundIndex = 0;

		for (Entry<Integer, D3Sprite> spriteEntry: sprites.entrySet()) {
			sprite = spriteEntry.getValue();
			if (sprite.getGraphic() == searchShape) {
				Log.v(TAG, "Shape found! Deleting it");
				found = true;
				foundIndex = spriteEntry.getKey();
				break;
				//				removeShape(key)
			}
		}

		if (found) {
			removeShape(foundIndex);
		}
		else {
			Log.v(TAG, "Shape not found!");
		}

	}

	public TextureManager getTextureManager() {
		return tm;
	}

	//	public void clearAll() {
	////		if (removeSpriteLater) {
	////			for (Entry<Integer, D3Sprite> entry: sprites.entrySet()) {
	////				if (entry.getValue() instanceof FloatingObject) {
	////					if (removeSpriteLater) {
	////						toRemove.add(entry.getKey());
	////					}
	////				}
	////			}
	////		}
	////		else {
	////			sprites.clear();
	////		}
	////			if (!removeSpriteLater) {
	////				Log.i(TAG, "Removing now!");
	////				for (int i: toRemove) {
	////					sprites.remove(i);
	////				}
	////			}
	////		for (D3FadingText text: mTexts) {
	////			Log.i(TAG, "Set faded " + mTexts.indexOf(text) );
	////			text.setFaded();
	////		}
	//	}
}
