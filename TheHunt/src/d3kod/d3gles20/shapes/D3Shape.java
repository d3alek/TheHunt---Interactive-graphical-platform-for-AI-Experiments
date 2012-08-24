package d3kod.d3gles20.shapes;

import java.nio.FloatBuffer;

import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.Utilities;
import d3kod.thehunt.TheHuntRenderer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

abstract public class D3Shape {
	private int VERTICES_NUM;
	protected static final int STRIDE_BYTES = D3GLES20.COORDS_PER_VERTEX * Utilities.BYTES_PER_FLOAT;
	private static final String TAG = "D3Shape";
	private float[] color = new float[4];
	private int mProgram;
	protected int mMVPMatrixHandle;
	protected int mPositionHandle;
	private int mColorHandle;
	protected float[] mMVPMatrix = new float[16];
	private float[] mProjMatrix = new float[16];
	private float[] mVMatrix = new float[16];
	
	private FloatBuffer vertexBuffer;
	private int drawType;
	private float[] mMMatrix;
	private float[] mCenter;
	private float[] mCenterDefault;
	protected boolean customProgram;
	
//	private static final int SEC_TфO_FADE = 1;
//	private static final int FADE_TICKS = TheHuntRenderer.TICKS_PER_SECOND*SEC_TO_FADE;
	
	private static final float FADE_SPEED = 0.01f;
	private static final float MIN_ALPHA = 0.1f;
	
	protected D3Shape(FloatBuffer vertBuffer, float[] colorData, int drType, boolean useDefaultShaders) {
		this(colorData, drType, useDefaultShaders);
		setVertexBuffer(vertBuffer); //TODO: make this the default constructor without the vertBuffer argument and ssetVertexBuffer abstract
	}
	
	protected D3Shape(float[] colorData, int drType, boolean useDefaultShaders) {
		color = colorData;
		drawType = drType;
		setMMatrix(new float[16]);
		mCenterDefault = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
		mCenter = new float[4];
		Matrix.setIdentityM(getMMatrix(), 0);
        
		if (useDefaultShaders) {
			customProgram = false;
			//TODO: create program and handles just once
			mProgram = Utilities.createProgram(defaultVertexShader(), defaultFragmentShader());
			mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix"); 
	        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
	        mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
		}
		else {
			customProgram = true;
			mProgram = mMVPMatrixHandle = mPositionHandle = mColorHandle = -1;
		}
	}
	
//	public void setShaders(int program, int mMVPMa) {
	
	public void setVertexBuffer(FloatBuffer vertBuffer) {
		vertexBuffer = vertBuffer;
		if (vertBuffer != null) VERTICES_NUM = vertBuffer.capacity()/D3GLES20.COORDS_PER_VERTEX;
		else {
			VERTICES_NUM = 0;
			Log.v(TAG, "Vertex buffer is null!");
		}
	}
	
	public void setModelMatrix(float[] modelMatrix) {
		setMMatrix(modelMatrix);
		Matrix.multiplyMV(mCenter, 0, getMMatrix(), 0, mCenterDefault, 0);
	}
	
	public void draw(float[] mVMatrix, float[] mProjMatrix, float interpolation) {
		draw(mVMatrix, mProjMatrix);
	}
	
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		//float[] mMMatrix, 
		// Add program to OpenGL ES environment
        if (!customProgram) GLES20.glUseProgram(mProgram);
        
//        Log.v(TAG, "Vertex buffer size: " + vertexBuffer.capacity());
        // Pass in the position information
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, STRIDE_BYTES, vertexBuffer);
        
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        
        // Pass in color information
        GLES20.glUniform4fv(mColorHandle, 1, color , 0);
        
        GLES20.glEnableVertexAttribArray(mColorHandle);
        
        Matrix.multiplyMM(mMVPMatrix  , 0, mVMatrix, 0, getMMatrix(), 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(drawType, 0, VERTICES_NUM);
	}

	public float getCenterX() {
		return mCenter[0];
	}

	public float getCenterY() {
		return mCenter[1];
	}
	
	abstract public float getRadius();
	
	protected void setProgram(int program) {
		mProgram = program;
//		GLES20.glUseProgram(program);
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix"); 
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
	}
	
	public void setDrawType(int drawType) {
		this.drawType = drawType;
	}
	
	public void setColor(float[] color) {
		this.color = color.clone();
	}
	public int getVerticesNum() {
		return VERTICES_NUM;
	}
	public boolean fadeDone() {
		return fadeDone(MIN_ALPHA);
	}
	public boolean fadeDone(float minAlpha) {
		return D3Maths.compareFloats(color[3], minAlpha) <= 0;
	}

	public void fade(float fadeSpeed) {
		if (color[3] > 0) {
			color[3] -= fadeSpeed;
		}
	}

	public void setPosition(float x, float y) {
		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, x, y, 0);
		Matrix.multiplyMV(mCenter, 0, mMMatrix, 0, mCenterDefault, 0);
	}
	public void setPosition(float x, float y, float angleDeg) {
		setPosition(x, y);
		Matrix.rotateM(mMMatrix, 0, angleDeg, 0, 0, 1);
	}
	public void useProgram() {
		GLES20.glUseProgram(mProgram);
	}

	public float[] getMMatrix() {
		return mMMatrix;
	}

	public void setMMatrix(float[] mMMatrix) {
		this.mMMatrix = mMMatrix;
	}

	public FloatBuffer getVertexBuffer() {
		return vertexBuffer;
	}
	public float[] getColor() {
		return color;
	}

	public void drawBuffer(FloatBuffer vertexBuffer, float[] modelMatrix) {
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		
		GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, STRIDE_BYTES, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(drawType, 0, vertexBuffer.capacity()/D3GLES20.COORDS_PER_VERTEX);
	}

	public void setDrawProjMatrix(float[] projMatrix) {
		mProjMatrix = projMatrix;
	}

	public void setDrawVMatrix(float[] vMatrix) {
		mVMatrix = vMatrix;
	}
	

	private static int defVertexShaderHandle = -1;
	private static int defFragmentShaderHandle = -1;
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
	
	
	public int defaultVertexShader() {
//		if (defVertexShaderHandle == -1) 
//		if (!GLES20.glIsShader(defVertexShaderHandle)) {
//			Log.v(TAG, "Creating new default vertex shader");
//			defVertexShaderHandle = Utilities.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
//		}
		return defVertexShaderHandle;
	}
	
	public int defaultFragmentShader() {
//		if (defFragmentShaderHandle == -1)
//		if (!GLES20.glIsShader(defFragmentShaderHandle)) {
//			Log.v(TAG, "Creating new default fragment shader");
//			defFragmentShaderHandle = Utilities.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
//		}
		return defFragmentShaderHandle;
	}
	public static void initDefaultShaders() {
		defVertexShaderHandle = Utilities.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		defFragmentShaderHandle = Utilities.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
	}
	public static void cleanDefaultShaders() {
		GLES20.glDeleteShader(defVertexShaderHandle);
		GLES20.glDeleteShader(defFragmentShaderHandle);
	}
}
