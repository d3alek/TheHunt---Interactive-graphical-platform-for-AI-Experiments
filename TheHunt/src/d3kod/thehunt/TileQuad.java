package d3kod.thehunt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Quad;
import d3kod.thehunt.environment.EnvironmentData;
import d3kod.thehunt.environment.EnvironmentData.Dir;

public class TileQuad extends D3Quad {
	
	private static final int NUM_CURRENT_DATA = 3;
	private static final float currentSize = 0.3f;
	private static short drawOrder[] = { 0, 1, 2, 3 };
	private static final float[] currentNDefault = {
		0.0f, currentSize, 0.0f,
		-currentSize, -currentSize, 0.0f,
		currentSize, -currentSize, 0.0f };
	private static float[] currentN = new float[9];
	private FloatBuffer currentBuffer;
	private float[] mMMatrix = new float[16];
	
	private static final float[] tileVerticesDefault = {
		//X, Y, 0
		-0.5f, -0.5f, 0.0f,
		0.5f, -0.5f, 0.0f,
		0.5f, 0.5f, 0.0f,
		-0.5f, 0.5f, 0.0f,
	};
	private static final String TAG = "TileQuad";
	
//	private static FloatBuffer makeVerticesBuffer(float width, float height) {
//		float[] quadVertices = new float[verticesNum * D3GLES20.COORDS_PER_VERTEX];
//		FloatBuffer buffer = ByteBuffer.allocateDirect(quadVertices.length * D3GLES20.BYTES_PER_FLOAT)
//				.order(ByteOrder.nativeOrder()).asFloatBuffer();
//		for (int i = 0; i < verticesNum; ++i) {
//			quadVertices[i*3] = tileVerticesDefault[i * 3] * width;
//			quadVertices[i*3+1] = tileVerticesDefault[i * 3 + 1] * height;
////			Log.v(TAG, "D3Quad vertex " + i + " " + quadVertices[i*3] + " " + quadVertices[i*3 + 1] + " " + quadVertices[i*3 + 2]);
//		}
//		buffer.put(quadVertices).position(0);
//		return buffer;
//	}
//	
	public TileQuad(float width, float height) {
		super(width, height, tileVerticesDefault, colorDefault, GLES20.GL_LINE_LOOP, true);
		
		for (int i = 0; i < NUM_CURRENT_DATA; ++i) {
			currentN[i*3] = currentNDefault[i*3] * width;
			currentN[i*3+1] = currentNDefault[i*3+1] * height;
		}
		currentBuffer = ByteBuffer.allocateDirect(currentN.length * D3GLES20.BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		currentBuffer.put(currentN).position(0);
	}
	
	public void draw(int r, int c, float[] mVMatrix, float[] mProjMatrix, boolean showCurrents, Dir dir) {
		Matrix.setIdentityM(mMMatrix , 0);
        Matrix.translateM(mMMatrix, 0, 
        		-EnvironmentData.mScreenWidth/2+c*EnvironmentData.tWidth+EnvironmentData.tWidth/2, 
        		EnvironmentData.mScreenHeight/2-r*EnvironmentData.tHeight-EnvironmentData.tHeight/2, 0);
		
        super.setModelMatrix(mMMatrix);
        super.draw(mVMatrix, mProjMatrix);
        
		if (showCurrents) {
        	currentBuffer.position(0);
        	GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
            		GLES20.GL_FLOAT, false, STRIDE_BYTES, currentBuffer);
            
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            
        	switch(dir) {
        	case N: break;
        	case NE: Matrix.rotateM(mMMatrix, 0, -45, 0, 0, 1); break;
        	case E: Matrix.rotateM(mMMatrix, 0, -90, 0, 0, 1); break;
        	case SE: Matrix.rotateM(mMMatrix, 0, -135, 0, 0, 1); break;
        	case S: Matrix.rotateM(mMMatrix, 0, 180, 0, 0, 1); break;
        	case SW: Matrix.rotateM(mMMatrix, 0, 135, 0, 0, 1); break;
        	case W: Matrix.rotateM(mMMatrix, 0, 90, 0, 0, 1); break;
        	case NW: Matrix.rotateM(mMMatrix, 0, 45, 0, 0, 1); break;
        	default: return;
        	}
        	if (!dir.equals(Dir.N)){
        		Matrix.multiplyMM(mMVPMatrix , 0, mVMatrix, 0, mMMatrix, 0);
        		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        	}
        	GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 3);
        }
	}

}
