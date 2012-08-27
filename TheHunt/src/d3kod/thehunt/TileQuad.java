package d3kod.thehunt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.programs.Program;
import d3kod.d3gles20.shapes.D3Quad;
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
	
	public TileQuad(float width, float height, Program program) {
		super(width, height, tileVerticesDefault, colorDefault, GLES20.GL_LINE_LOOP, program);
		
		for (int i = 0; i < NUM_CURRENT_DATA; ++i) {
			currentN[i*3] = currentNDefault[i*3] * width;
			currentN[i*3+1] = currentNDefault[i*3+1] * height;
		}
		currentBuffer = ByteBuffer.allocateDirect(currentN.length * Utilities.BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		currentBuffer.put(currentN).position(0);
	}
	
	public void draw(int r, int c, float[] mVMatrix, float[] mProjMatrix, boolean showTiles, boolean showCurrents, Dir dir) {
		Matrix.setIdentityM(mMMatrix , 0);
        Matrix.translateM(mMMatrix, 0, 
        		-EnvironmentData.mScreenWidth/2+c*EnvironmentData.tWidth+EnvironmentData.tWidth/2, 
        		EnvironmentData.mScreenHeight/2-r*EnvironmentData.tHeight-EnvironmentData.tHeight/2, 0);
		
        super.setModelMatrix(mMMatrix);
        if (showTiles) super.draw(mVMatrix, mProjMatrix);
        else super.useProgram();
        
		if (showCurrents) {
            
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
        	
        	super.drawBuffer(currentBuffer, mMMatrix);
        }
	}

}
