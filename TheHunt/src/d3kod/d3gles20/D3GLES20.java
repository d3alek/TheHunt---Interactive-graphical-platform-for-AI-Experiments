package d3kod.d3gles20;

import java.util.HashMap;

import d3kod.d3gles20.shapes.D3Circle;
import d3kod.d3gles20.shapes.D3Quad;
import d3kod.d3gles20.shapes.D3Shape;
import d3kod.d3gles20.shapes.D3TempCircle;

import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;

public class D3GLES20 {
	public static final int COORDS_PER_VERTEX = 3;
	
	private static final String vertexShaderCode =
			"uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
		 
		  + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
		 
		  + "void main()                    \n"     // The entry point for our vertex shader.
		  + "{                              \n"
		  + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
		  + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
		  + "}                              \n";    // normalized screen coordinates.


	private static final String fragmentShaderCode =
				"precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
	        // precision in the fragment shader.
			+ "uniform vec4 u_Color;          \n"
	        // triangle per fragment.
			+ "void main()                    \n"     // The entry point for our fragment shader.
			+ "{                              \n"
			+ "   gl_FragColor = u_Color;     \n"     // Pass the color directly through the pipeline.
			+ "}                              \n";
	
	static final String TAG = "D3GLES20";
	private static final int TEMP_CIRCLE_TICKS = 50;
	private static int defVertexShaderHandle = -1;
	private static int defFragmentShaderHandle = -1;
	
//	private static HashMap<Integer, D3Shape> shapes;
	private static SparseArray<D3Shape> shapes;
	private static int shapesNum = 0;
	
	public static void init() {
		shapes = new SparseArray<D3Shape>();
		shapesNum = 0;
	}

	public static void draw(int key, float[] mMMatrix, float[] mVMatrix, float[] mProjMatrix) {
		shapes.get(key).setModelMatrix(mMMatrix);
		shapes.get(key).draw(mVMatrix, mProjMatrix);
	}
	
	public static void drawAll(float[] mVMatrix, float[] mProjMatrix,
			float interpolation) {
		if (shapes == null) {
			Log.v(TAG, "Shapes are null!");
			return;
		}
//		for (D3Shape shape: shapes.) {
//			shape.draw(mVMatrix, mProjMatrix);
//		}
		int key = 0;
		for(int i = 0; i < shapes.size(); i++) {
		   key = shapes.keyAt(i);
		   D3Shape shape = shapes.valueAt(i);
		   shape.draw(mVMatrix, mProjMatrix);
		}
	}
	
	public static int putShape(D3Shape shape) {
		while (shapes.indexOfKey(shapesNum)>0) {
			shapesNum++;
		}
		shapes.put(shapesNum, shape);
		return shapesNum++;
	}
	
	public static void removeShape(int key) {
		shapes.remove(key);
	}

	public static void setShapePosition(int key, float x,
			float y) {
		shapes.get(key).setPosition(x, y);
	}

	public static void clearGraphics() {
		shapes.clear();
		shapes = null;
	}
	public static void clean() {
		GLES20.glDeleteShader(defVertexShaderHandle);
		GLES20.glDeleteShader(defFragmentShaderHandle);
		defVertexShaderHandle = -1;
		defFragmentShaderHandle = -1;
	}

	public static SparseArray<D3Shape> getShapes() {
		return shapes.clone();
	}

	public static void setShapes(SparseArray<D3Shape> savedShapes) {
		shapes.clear(); // maybe unnecessary
//		shapes.putAll(savedShapes);
		int key = 0;
		for(int i = 0; i < savedShapes.size(); i++) {
		   key = savedShapes.keyAt(i);
		   D3Shape shape = savedShapes.valueAt(i);
		   shapes.put(key, shape);
		}
		
	}
	
	public static boolean contains(int key, float hX, float hY) {
		return D3Maths.circleContains(shapes.get(key).getCenterX(), shapes.get(key).getCenterY(), 
				shapes.get(key).getRadius(), hX, hY);
	}

	public static D3TempCircle newContainsCheckCircle(int key, float hX, float hY) {
		return new D3TempCircle(shapes.get(key).getCenterX(), shapes.get(key).getCenterY(), 
				shapes.get(key).getRadius(), TEMP_CIRCLE_TICKS);
	}
	
	public static int defaultVertexShader() {
		if (defVertexShaderHandle == -1) 
			defVertexShaderHandle = Utilities.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		
		return defVertexShaderHandle;
	}
	
	public static int defaultFragmentShader() {
		if (defFragmentShaderHandle == -1) 
			defFragmentShaderHandle = Utilities.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
		return defFragmentShaderHandle;
	}
	
	public static int newDefaultQuad(float width, float height, float[] color) {
		return putShape(new D3Quad(width, height, color, true));
	}

	public static int newDefaultCircle(float r, float[] color, int vertices) {
		return putShape(new D3Circle(r, color, vertices, true));
	}
}
