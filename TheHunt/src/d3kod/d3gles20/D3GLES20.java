package d3kod.d3gles20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.graphics.PointF;
import android.util.Log;
import d3kod.d3gles20.shapes.D3Circle;
import d3kod.d3gles20.shapes.D3FadingShape;
import d3kod.d3gles20.shapes.D3Quad;
import d3kod.d3gles20.shapes.D3Shape;
import d3kod.d3gles20.shapes.D3TempCircle;

public class D3GLES20 {
	public static final int COORDS_PER_VERTEX = 3;
	
	static final String TAG = "D3GLES20";
	private static final int TEMP_CIRCLE_TICKS = 50;
	
	private HashMap<Integer, D3Sprite> sprites;
	private HashMap<Integer, D3FadingShape> expiringShapes;
	private int spritesNum = 0;

	private ShaderManager sm;
	
	ArrayList<Integer> toRemove = new ArrayList<Integer>();

	private boolean removeSpriteLater;
	
	public D3GLES20(ShaderManager shaderManager) {
		sprites = new HashMap<Integer, D3Sprite>();
		expiringShapes = new HashMap<Integer, D3FadingShape>();
		spritesNum = 0;
		sm = shaderManager;
		removeSpriteLater = false;
	}
	
//	public static void init() {
//		shapes = new HashMap<Integer, D3Shape>();
//		expiringShapes = new HashMap<Integer, D3FadingShape>();
//		shapesNum = 0;
//	}

	public void draw(int key, float[] mMMatrix, float[] mVMatrix, float[] mProjMatrix) {
		sprites.get(key).getGraphic().setModelMatrix(mMMatrix);
		sprites.get(key).getGraphic().draw(mVMatrix, mProjMatrix);
	}
	
	public void drawAll(float[] mVMatrix, float[] mProjMatrix,
			float interpolation) {
		if (sprites == null) {
			Log.w(TAG, "Sprites are null in drawAll!");
			return;
		}
		toRemove.clear();
		removeSpriteLater = true;
		for (D3Sprite sprite: sprites.values()) {
			if (sprite.getGraphic() == null) {
				Log.v(TAG, sprite.toString() + " graphic is not ready yet!");
				continue;
			}
			sprite.getGraphic().draw(mVMatrix, mProjMatrix, interpolation);
		}
		removeSpriteLater = false;
		for (Integer key: toRemove) {
			sprites.remove(key);
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
	}
	
	public int putSprite(D3Sprite sprite) {
		if (sprites == null) {
			Log.w(TAG, "Shapes are null in putShape!");
			return 0;
		}
		while (sprites.containsKey(spritesNum)) {
			spritesNum++;
		}
		sprites.put(spritesNum, sprite);
		if (sprite.getGraphic().getProgram() == null) {
			Log.v(TAG, "Sprite program is not set, setting to default");
			sprite.getGraphic().setProgram(sm.getDefaultProgram());
		}
		return spritesNum++;
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
		if (removeSpriteLater) {
			toRemove.add(key);
		}
		else sprites.remove(key);
	}

//	public void setShapePosition(int key, float x,
//			float y) {
//		if (shapes == null) {
//			Log.w(TAG, "Shapes are null in setShapePosition!");
//			return;
//		}
//		shapes.get(key).setPosition(x, y);
//	}

//	public void clearGraphics() {
//		shapes.clear();
//		expiringShapes.clear();
//		shapes = null;
//		expiringShapes = null;
//	}
//	public void clean() {
//		GLES20.glDeleteShader(defVertexShaderHandle);
//		GLES20.glDeleteShader(defFragmentShaderHandle);
//		defVertexShaderHandle = -1;
//		defFragmentShaderHandle = -1;
//	}

//	public HashMap<Integer, D3Shape> getShapes() {
//		return shapes;
//	}

//	public void setShapes(HashMap<Integer, D3Shape> savedShapes) {
//		shapes.clear(); // maybe unnecessary
//		shapes.putAll(savedShapes);
//	}
	
	public boolean contains(int key, PointF point) {
		if (sprites == null) {
			Log.w(TAG, "Shapes are null in contains!");
			return false;
		}
		return sprites.get(key).contains(point);//D3Maths.circleContains(shapes.get(key).getCenterX(), shapes.get(key).getCenterY(), 
				//shapes.get(key).getRadius(), hX, hY);
	}

//	public D3TempCircle newContainsCheckCircle(int key, float hX, float hY) {
//		return new D3TempCircle(shapes.get(key).getCenterX(), shapes.get(key).getCenterY(), 
//				shapes.get(key).getRadius(), TEMP_CIRCLE_TICKS, sm.getDefaultProgram());
//	}
//	
//	public int newDefaultQuad(float width, float height, float[] color) {
//		return putShape(new D3Quad(width, height, color, sm.getDefaultProgram()));
//	}
//
//	public int newDefaultCircle(float r, float[] color, int vertices) {
//		return putShape(new D3Circle(r, color, vertices, sm.getDefaultProgram()));
//	}

	public ShaderManager getShaderManager() {
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
//				sprite1.getCenterX(), sprite1.getCenterY(), sprite1.getRadius(), 
//				sprite2.getCenterX(), sprite2.getCenterY(), sprite2.getRadius());
				sprite1.getX(), sprite1.getY(), sprite1.getRadius(), 
				sprite2.getX(), sprite2.getY(), sprite2.getRadius());
	}
}
