package d3kod.d3gles20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class D3Quad extends D3Shape {
	protected static final int verticesNum = 4;
//	private static int VERTICES_NUM = 4;
//	protected static final int STRIDE_BYTES = D3GLES20.COORDS_PER_VERTEX * D3GLES20.BYTES_PER_FLOAT;
	private static final float[] quadVerticesDefault = {
		//X, Y, 0
		-0.5f, -0.5f, 0.0f,
		0.5f, -0.5f, 0.0f,
		-0.5f, 0.5f, 0.0f,
		0.5f, 0.5f, 0.0f,
	};
	
//	private final static short drawOrderDefault[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
	protected static final float[] colorDefault = {0.0f, 1.0f, 0.0f, 0.0f};
	private static final String TAG = "D3Quad";
	
	private static FloatBuffer makeVerticesBuffer(float width, float height, float[] verticesDefault) {
		float[] quadVertices = new float[verticesNum * D3GLES20.COORDS_PER_VERTEX];
		FloatBuffer buffer = ByteBuffer.allocateDirect(quadVertices.length * D3GLES20.BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		for (int i = 0; i < verticesNum; ++i) {
			quadVertices[i*3] = verticesDefault[i * 3] * width;
			quadVertices[i*3+1] = verticesDefault[i * 3 + 1] * height;
//			Log.v(TAG, "D3Quad vertex " + i + " " + quadVertices[i*3] + " " + quadVertices[i*3 + 1] + " " + quadVertices[i*3 + 2]);
		}
		buffer.put(quadVertices).position(0);
		return buffer;
	}
	public D3Quad(float width, float height, float[] verticesDefault, float[] drawColor, int drawType, boolean useDefaultShaders) {
		this(makeVerticesBuffer(width, height, verticesDefault), drawColor, drawType, useDefaultShaders);
	}
	
	public D3Quad(float width, float height, boolean useDefaultShaders) {
		this(makeVerticesBuffer(width, height, quadVerticesDefault), colorDefault, GLES20.GL_TRIANGLE_STRIP, useDefaultShaders);
	}
	
	public D3Quad(float width, float height, float[] drawColor, boolean useDefaultShaders) {
		this(makeVerticesBuffer(width, height, quadVerticesDefault), drawColor, GLES20.GL_TRIANGLE_STRIP, useDefaultShaders);
	}
	
	public D3Quad(FloatBuffer verticesBuffer, float[] drawColor, int drawType, boolean useDefaultShaders) {	
		super(verticesNum, verticesBuffer, drawColor, drawType, useDefaultShaders);
		
//		vertexBuffer = ByteBuffer.allocateDirect(quadVertices.length * D3GLES20.BYTES_PER_FLOAT)
//				.order(ByteOrder.nativeOrder()).asFloatBuffer();
//		vertexBuffer.put(quadVertices).position(0);
		
		
	}
//	private static ShortBuffer makeDrawListBuffer(float width, float height) {
//		// TODO Auto-generated method stub
//		return ByteBuffer.allocateDirect(drawOrder.length * D3GLES20.BYTES_PER_SHORT)
//				.order(ByteOrder.nativeOrder()).asShortBuffer().put(drawOrder).position(0);
//	}

//	public void draw(float[] mMMatrix, float[] mVMatrix, float[] mProjMatrix) {
//		draw(mMMatrix,mVMatrix, mProjMatrix, );
//	}
////	public void draw(float[] mMMatrix, float[] mVMatrix, float[] mProjMatrix, int type) {
////		// Add program to OpenGL ES environment
////        GLES20.glUseProgram(mProgram);
////        
////        // Pass in the position information
////        vertexBuffer.position(0);
////        GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
////        		GLES20.GL_FLOAT, false, STRIDE_BYTES, vertexBuffer);
////        
////        GLES20.glEnableVertexAttribArray(mPositionHandle);
////        
////        // Pass in color information
////        GLES20.glUniform4fv(mColorHandle, 1, color , 0);
////        
////        GLES20.glEnableVertexAttribArray(mColorHandle);
////        
////        Matrix.multiplyMM(mMVPMatrix  , 0, mVMatrix, 0, mMMatrix, 0);
////        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
////        
////        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
////
////        GLES20.glDrawElements(type, drawListBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
//	}
	
}
