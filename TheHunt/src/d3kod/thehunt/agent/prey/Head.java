package d3kod.thehunt.agent.prey;

import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;


public class Head {

	private static final String TAG = "Head";
	private PointF mPos;// = new PointF(0, y);
	private float[] mHeadPosMatrix = new float[16];
	
	public static final float bodyToHeadLength = 0.07f;

	public Head() {
		
	}
	
	public HeadGraphic getGraphic(D3Prey graphic, float size) {
		return new HeadGraphic(graphic, size);
	}

	public PointF getPos() {
		if (mPos == null) {
			Log.e(TAG, "getPos is null!");
			return new PointF(0, 0);
		}
		return mPos;
	}

	public float getY() {
		if (mPos == null) {
			Log.e(TAG, "getPos is null!");
			return 0;
		}
		return mPos.y;
	}

	public float getX() {
		if (mPos == null) {
			Log.e(TAG, "getPos is null!");
			return 0;
		}
		return mPos.x;
	}

	public void updatePos(float posX, float posY, float bodyStartAngle) {
		// TODO Auto-generated method stub
		float[] posTemp = { 0.0f, bodyToHeadLength, 0.0f, 1.0f };
		
		Matrix.setIdentityM(mHeadPosMatrix, 0);
		Matrix.translateM(mHeadPosMatrix, 0, posX, posY, 0);
		Matrix.rotateM(mHeadPosMatrix, 0, bodyStartAngle, 0, 0, 1);
		Matrix.multiplyMV(posTemp, 0, mHeadPosMatrix, 0, posTemp, 0);

		mPos = new PointF(posTemp[0], posTemp[1]);
		
	}
	
}
