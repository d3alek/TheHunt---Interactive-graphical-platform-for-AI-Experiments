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
	
	private FloatBuffer vertexBuffer;
	private int drawType;
	private float[] mMMatrix;
	private float[] mCenter;
	private float[] mCenterDefault;
	protected boolean customProgram;
	
//	private static final int SEC_TO_FADE = 1;
//	private static final int FADE_TICKS = TheHuntRenderer.TICKS_PER_SECOND*SEC_TO_FADE;
	
	private static final float FADE_SPEED = 0.05f;
	
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
			mProgram = Utilities.createProgram(D3GLES20.defaultVertexShader(), D3GLES20.defaultFragmentShader());
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
		for (int i = 0; i < 3; ++i) {
			if (D3Maths.compareFloats(color[i], TheHuntRenderer.bgColor[i]) != 0) {
				return false;
			}
		}
		return true;
	}

	public void fade() {
		for (int i = 0; i < color.length; ++i) {
			int compare = D3Maths.compareFloats(color[i], TheHuntRenderer.bgColor[i]);
			if (compare < 0) color[i] += FADE_SPEED;
			else if (compare > 0) color[i] -= FADE_SPEED;
		}
	}

	public void setPosition(float x, float y) {
		Matrix.setIdentityM(getMMatrix(), 0);
		Matrix.translateM(getMMatrix(), 0, x, y, 0);
		Matrix.multiplyMV(mCenter, 0, getMMatrix(), 0, mCenterDefault, 0);
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
}
