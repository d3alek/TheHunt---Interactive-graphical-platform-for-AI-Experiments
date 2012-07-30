package d3kod.thehunt.prey;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.environment.EnvironmentData;

public class ManualControl {
	
	public enum Buttons { LEFT, FORWARD, RIGHT, NONE};
	
	private static final float BUTTON_SIZE = 0.2f;
	private static final float[] BUTTON_COLOR = {
		0.5f, 0.5f, 0.5f, 0.0f };
	private static final int ARROW_COLOR = Color.CYAN;
	private static final int BUTTON_MARGIN = 10;
	private static final String TAG = "ManualControl";
	private int bLeft;
	private int bForward;
	private int bRight;
	private float[] mMMatrixL = new float[16];
	private float[] mMMatrixF = new float[16];
	private float[] mMMatrixR = new float[16];
	private float bLeftX;
	private float bLeftY;
	private float bForwardX;
	private float bForwardY;
	private float bRightX;
	private float bRightY;
	
	public ManualControl() {
		bLeft = D3GLES20.newDefaultQuad(BUTTON_SIZE, BUTTON_SIZE, BUTTON_COLOR);
		bForward = D3GLES20.newDefaultQuad(BUTTON_SIZE, BUTTON_SIZE, BUTTON_COLOR);
		bRight = D3GLES20.newDefaultQuad(BUTTON_SIZE, BUTTON_SIZE, BUTTON_COLOR);
	}
	
	public void setSize() {
		bLeftX = -(3f/2)*BUTTON_SIZE;
		bLeftY = -EnvironmentData.mScreenHeight/2 + BUTTON_SIZE/2;
		Matrix.setIdentityM(mMMatrixL, 0);
		Matrix.translateM(mMMatrixL, 0, bLeftX, bLeftY, 0);
		
		bForwardX = 0;
		bForwardY = -EnvironmentData.mScreenHeight/2 + BUTTON_SIZE/2;
		Matrix.setIdentityM(mMMatrixF, 0);
		Matrix.translateM(mMMatrixF, 0, bForwardX, bForwardY, 0);
		
		bRightX = (3f/2)*BUTTON_SIZE;
		bRightY = -EnvironmentData.mScreenHeight/2 + BUTTON_SIZE/2;
		Matrix.setIdentityM(mMMatrixR, 0);
		Matrix.translateM(mMMatrixR, 0, bRightX, bRightY, 0);
	}
	
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		D3GLES20.draw(bLeft, mMMatrixL , mVMatrix, mProjMatrix);
		D3GLES20.draw(bForward, mMMatrixF , mVMatrix, mProjMatrix);
		D3GLES20.draw(bRight, mMMatrixR , mVMatrix, mProjMatrix);
	}
	
	public Buttons contains(float x, float y) {
		if (D3GLES20.rectContains(bLeftX, bLeftY, BUTTON_SIZE, BUTTON_SIZE, x, y)) return Buttons.LEFT;
		else if (D3GLES20.rectContains(bForwardX, bForwardY, BUTTON_SIZE, BUTTON_SIZE, x, y)) return Buttons.FORWARD;
		else if (D3GLES20.rectContains(bRightX, bRightY, BUTTON_SIZE, BUTTON_SIZE, x, y)) return Buttons.RIGHT;
		else return Buttons.NONE;
	}
	
}
