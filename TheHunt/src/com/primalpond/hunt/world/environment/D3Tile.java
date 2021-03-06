package com.primalpond.hunt.world.environment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.primalpond.hunt.world.logic.TheHuntRenderer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.shader.programs.Program;
import d3kod.graphics.sprite.shapes.D3Quad;

public class D3Tile extends D3Quad {
	
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
	
	public D3Tile(float width, float height, Program program) {
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
		float mScreenHeight = TheHuntRenderer.SCREEN_HEIGHT;
		float mScreenWidth = TheHuntRenderer.SCREEN_WIDTH;
		Matrix.setIdentityM(mMMatrix , 0);
        Matrix.translateM(mMMatrix, 0, 
        		-mScreenWidth/2+c*EnvironmentData.tWidth+EnvironmentData.tWidth/2, 
        		mScreenHeight/2-r*EnvironmentData.tHeight-EnvironmentData.tHeight/2, 0);
		
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
