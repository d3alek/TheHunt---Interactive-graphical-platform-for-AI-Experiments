package d3kod.d3gles20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import android.opengl.GLES20;
import android.util.FloatMath;
import android.util.Log;
import d3kod.thehunt.TheHuntRenderer;
import d3kod.thehunt.environment.EnvironmentData;

public class D3GLES20 {
	public static final int BYTES_PER_FLOAT = 4;
	public static final int BYTES_PER_SHORT = 2;
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
	private static final String TAG = "D3GLES20";
	private static int defVertexShaderHandle = -1;
	private static int defFragmentShaderHandle = -1;
	
//	ArrayList<D3Shape> shapes;
	//private static ArrayList<D3Shape> shapes = new ArrayList<D3Shape>();
	private static HashMap<Integer, D3Shape> shapes = new HashMap<Integer, D3Shape>();
	private static int shapesNum = 0;
	
	public static int createProgram(int vertexShaderHandle, int fragmentShaderHandle) {
		// Create a program object and store the handle to it.
		int  mProgram = GLES20.glCreateProgram();
		
		if (mProgram != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(mProgram, vertexShaderHandle);
         
            // Bind the fragment shader to the program.
            GLES20.glAttachShader(mProgram, fragmentShaderHandle);
         
//            // Bind attributes
//            GLES20.glBindAttribLocation(mProgram, 0, "a_Position");
         
            // Link the two shaders together into a program.
            GLES20.glLinkProgram(mProgram);
         
            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
         
            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
            	Log.v(TAG, GLES20.glGetProgramInfoLog(mProgram));
                GLES20.glDeleteProgram(mProgram);
                mProgram = 0;
            }
        }
         
        if (mProgram == 0)
        {
            throw new RuntimeException("Error creating program.");
        }
		return mProgram;
	}
	
	public static int defaultVertexShader() {
		if (defVertexShaderHandle == -1) 
			defVertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		
		return defVertexShaderHandle;
	}
	
	public static int defaultFragmentShader() {
		if (defFragmentShaderHandle == -1) 
			defFragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
		return defFragmentShaderHandle;
	}
	
	public static int newDefaultQuad(float width, float height, float[] color) {
		return putShape(new D3Quad(width, height, color, true));
	}

	public static int newDefaultCircle(float r, float[] color, int vertices) {
		return putShape(new D3Circle(r, color, vertices, true));
	}
	
	private static int putShape(D3Shape shape) {
		while (shapes.containsKey(shapesNum)) {
			shapesNum++;
		}
		shapes.put(shapesNum, shape);
		return shapesNum++;
	}
	
	public static void draw(int key, float[] mMMatrix, float[] mVMatrix, float[] mProjMatrix) {
		shapes.get(key).setModelMatrix(mMMatrix);
		shapes.get(key).draw(mVMatrix, mProjMatrix);
	}

	public static void removeShape(int key) {
		shapes.remove(key);
	}
	
	public static void clean() {
		defVertexShaderHandle = -1;
		defFragmentShaderHandle = -1;
	}

	public static boolean rectContains(float rX, float rY,
			float rWidth, float rHeight, float x, float y) {
		if (x > rX - rWidth/2 && x < rX + rWidth/2 
				&& y > rY - rHeight/2 && y < rY + rHeight/2) {
//			Log.v(TAG, "Contains! " + rX + " " + rY + " " + x + " " + y);
			return true;
		}
		
		return false;
	}

	public static float fromWorldWidth(float x) {
		return EnvironmentData.mScreenWidth*(x/EnvironmentData.realWidth) - EnvironmentData.mScreenWidth/2;
	}

	public static float fromWorldHeight(float y) {
//		Log.v(TAG, EnvironmentData.mScreenHeight + "");
		return -EnvironmentData.mScreenHeight*(y/EnvironmentData.realHeight) + EnvironmentData.mScreenHeight/2;
	}
	
	public static float toWorldWidth(float x) {
		return (x + EnvironmentData.mScreenWidth/2)*EnvironmentData.realWidth/EnvironmentData.mScreenWidth;
	}

	public static float toWorldHeight(float y) {
		return -(y - EnvironmentData.mScreenHeight/2)*EnvironmentData.realHeight/EnvironmentData.mScreenHeight;
	}

	public static FloatBuffer newFloatBuffer(float[] verticesData) {
		FloatBuffer floatBuffer;
		floatBuffer = ByteBuffer.allocateDirect(verticesData.length * D3GLES20.BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		floatBuffer.put(verticesData).position(0);
		return floatBuffer;
	}

	public static float[] quadBezierCurveVertices(float[] a,
			float[] b, float[] c, float[] d, float dt, float scale) {
		int verticesNum = (int)(1/dt);
		float[] vertices = new float[COORDS_PER_VERTEX * (verticesNum + 1)];
		float t = 0;
		for (int i = 0; i < verticesNum; i++, t += dt) {
			vertices[i * COORDS_PER_VERTEX] = scale * (
					(1 - t) * (1 - t) * (1 - t) * a[0] 
					+ 3 * (1 - t) * (1 - t) * t * b[0]
					+ 3 * (1 - t) * t * t * c[0]
					+ t * t * t * d[0]);
			
			vertices[i * COORDS_PER_VERTEX + 1] = scale * (
					(1 - t) * (1 - t) * (1 - t) * a[1] 
					+ 3 * (1 - t) * (1 - t) * t * b[1]
					+ 3 * (1 - t) * t * t * c[1]
					+ t * t * t * d[1]);

			vertices[i * COORDS_PER_VERTEX + 2] = 0;
		}
		vertices[verticesNum * COORDS_PER_VERTEX] = d[0] * scale;
		vertices[verticesNum * COORDS_PER_VERTEX + 1] = d[1] * scale;
		vertices[verticesNum * COORDS_PER_VERTEX + 2] = d[2] * scale;
		return vertices;
	}

	public static float[] circleVerticesData(float[] center, float r, int verticesNum) {
		float[] vertices = new float[verticesNum*COORDS_PER_VERTEX];
		float step = 2*3.14f/verticesNum;
		float angleRad = 0;
		for (int i = 0; i < verticesNum; ++i) {
			vertices[i*COORDS_PER_VERTEX] = r * FloatMath.sin(angleRad);
			vertices[i*COORDS_PER_VERTEX + 1] = r * FloatMath.cos(angleRad);
			vertices[i*COORDS_PER_VERTEX + 2] = 0;
			angleRad += step;
//			Log.v(TAG, "Making new circle" + vertices[i*COORDS_PER_VERTEX] + " " + vertices[i*COORDS_PER_VERTEX + 1]);
		}
		return vertices;
	}

	public static float distance(float mX, float mY, float fX, float fY) {
		return FloatMath.sqrt((mX-fX)*(mX-fX)+(mY-fY)*(mY-fY));
	}
	
	public static float det(float x1, float y1, float x2, float y2, float x3, float y3) {
		return x1 * y2 + y1 * x3 + x2 * y3 - x3 * y2 - y3 * x1 - x2 * y1;
	}

	public static boolean contains(int key, float hX, float hY) {
//		shapes.get(key).contains(hX, hY);
		return circleContains(shapes.get(key).getCenterX(), shapes.get(key).getCenterY(), shapes.get(key).getRadius(), hX, hY);
	}

	private static boolean circleContains(float centerX, float centerY,
			float radius, float x, float y) {
		return D3GLES20.distance(centerX, centerY, x, y) <= radius;
	}
	public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    	
        int shaderHandle = GLES20.glCreateShader(type);
         
        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderCode);
         
            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);
         
            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
         
            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }
        
         
        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }
        return shaderHandle;
    }
}
