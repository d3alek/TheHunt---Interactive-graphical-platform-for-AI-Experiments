package d3kod.d3gles20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import d3kod.d3gles20.shapes.D3Circle;
import d3kod.d3gles20.shapes.D3FadingShape;
import d3kod.d3gles20.shapes.D3Quad;
import d3kod.d3gles20.shapes.D3Shape;
import d3kod.d3gles20.shapes.D3TempCircle;
import d3kod.thehunt.floating_text.FlopText;

import android.opengl.GLES20;
import android.util.Log;

public class D3GLES20 {
	public static final int COORDS_PER_VERTEX = 3;
	
	static final String TAG = "D3GLES20";
	private static final int TEMP_CIRCLE_TICKS = 50;
	
	private HashMap<Integer, D3Shape> shapes;
	private HashMap<Integer, D3FadingShape> expiringShapes;
	private int shapesNum = 0;

	private ShaderManager sm;
	
	public D3GLES20(ShaderManager shaderManager) {
		shapes = new HashMap<Integer, D3Shape>();
		expiringShapes = new HashMap<Integer, D3FadingShape>();
		shapesNum = 0;
		sm = shaderManager;
	}
	
//	public static void init() {
//		shapes = new HashMap<Integer, D3Shape>();
//		expiringShapes = new HashMap<Integer, D3FadingShape>();
//		shapesNum = 0;
//	}

	public void draw(int key, float[] mMMatrix, float[] mVMatrix, float[] mProjMatrix) {
		shapes.get(key).setModelMatrix(mMMatrix);
		shapes.get(key).draw(mVMatrix, mProjMatrix);
	}
	
	public void drawAll(float[] mVMatrix, float[] mProjMatrix,
			float interpolation) {
		if (shapes == null) {
			Log.w(TAG, "Shapes are null in drawAll!");
			return;
		}
		for (D3Shape shape: shapes.values()) {
			shape.draw(mVMatrix, mProjMatrix, interpolation);
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
	
	public int putShape(D3Shape shape) {
		if (shapes == null) {
			Log.w(TAG, "Shapes are null in putShape!");
			return 0;
		}
		while (shapes.containsKey(shapesNum)) {
			shapesNum++;
		}
		shapes.put(shapesNum, shape);
		return shapesNum++;
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
		if (shapes == null) {
			Log.w(TAG, "Shapes are null in removeShape!");
			return;
		}
		shapes.remove(key);
	}

	public void setShapePosition(int key, float x,
			float y) {
		if (shapes == null) {
			Log.w(TAG, "Shapes are null in setShapePosition!");
			return;
		}
		shapes.get(key).setPosition(x, y);
	}

	public void clearGraphics() {
		shapes.clear();
		expiringShapes.clear();
		shapes = null;
		expiringShapes = null;
	}
//	public void clean() {
//		GLES20.glDeleteShader(defVertexShaderHandle);
//		GLES20.glDeleteShader(defFragmentShaderHandle);
//		defVertexShaderHandle = -1;
//		defFragmentShaderHandle = -1;
//	}

	public HashMap<Integer, D3Shape> getShapes() {
		return shapes;
	}

	public void setShapes(HashMap<Integer, D3Shape> savedShapes) {
		shapes.clear(); // maybe unnecessary
		shapes.putAll(savedShapes);
	}
	
	public boolean contains(int key, float hX, float hY) {
		if (shapes == null) {
			Log.w(TAG, "Shapes are null in contains!");
			return false;
		}
		return D3Maths.circleContains(shapes.get(key).getCenterX(), shapes.get(key).getCenterY(), 
				shapes.get(key).getRadius(), hX, hY);
	}

	public D3TempCircle newContainsCheckCircle(int key, float hX, float hY) {
		return new D3TempCircle(shapes.get(key).getCenterX(), shapes.get(key).getCenterY(), 
				shapes.get(key).getRadius(), TEMP_CIRCLE_TICKS, sm.getDefaultProgram());
	}
	
	public int newDefaultQuad(float width, float height, float[] color) {
		return putShape(new D3Quad(width, height, color, sm.getDefaultProgram()));
	}

	public int newDefaultCircle(float r, float[] color, int vertices) {
		return putShape(new D3Circle(r, color, vertices, sm.getDefaultProgram()));
	}

	public ShaderManager getShaderManager() {
		return sm;
	}
}
