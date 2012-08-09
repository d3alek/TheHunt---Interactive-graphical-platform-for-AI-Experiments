package d3kod.d3gles20;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

abstract public class D3Shape {
	private int VERTICES_NUM;
	protected static final int STRIDE_BYTES = D3GLES20.COORDS_PER_VERTEX * D3GLES20.BYTES_PER_FLOAT;
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
	
	protected D3Shape(int verticesNum, FloatBuffer vertBuffer, float[] colorData, int drType, boolean useDefaultShaders) {
		color = colorData;
		vertexBuffer = vertBuffer;
		drawType = drType;
		VERTICES_NUM = verticesNum;
		mMMatrix = new float[16];
		mCenterDefault = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
		mCenter = new float[4];
		Matrix.setIdentityM(mMMatrix, 0);
		if (useDefaultShaders) {
			mProgram = D3GLES20.createProgram(D3GLES20.defaultVertexShader(), D3GLES20.defaultFragmentShader());
			mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix"); 
	        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
	        mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
		}
		else {
			mProgram = mMVPMatrixHandle = mPositionHandle = mColorHandle = -1;
		}
	}
	
	public void setModelMatrix(float[] modelMatrix) {
		mMMatrix = modelMatrix;
		Matrix.multiplyMV(mCenter, 0, mMMatrix, 0, mCenterDefault, 0);
	}
	
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		//float[] mMMatrix, 
		// Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);
        
//        Log.v(TAG, "Vertex buffer size: " + vertexBuffer.capacity());
        // Pass in the position information
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, STRIDE_BYTES, vertexBuffer);
        
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        
        // Pass in color information
        GLES20.glUniform4fv(mColorHandle, 1, color , 0);
        
        GLES20.glEnableVertexAttribArray(mColorHandle);
        
        Matrix.multiplyMM(mMVPMatrix  , 0, mVMatrix, 0, mMMatrix, 0);
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
	
	protected void setProgram() {
		GLES20.glUseProgram(mProgram);
	}
	
	public void setVertexBuffer(FloatBuffer vertexBuffer) {
		this.vertexBuffer = vertexBuffer;
	}
	
	public void setDrawType(int drawType) {
		this.drawType = drawType;
	}
	
	public void setColor(float[] color) {
		this.color = color;
	}
}
