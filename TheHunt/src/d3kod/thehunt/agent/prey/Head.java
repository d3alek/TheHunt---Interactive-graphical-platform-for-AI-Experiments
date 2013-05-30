package d3kod.thehunt.agent.prey;

import android.graphics.PointF;
import android.opengl.Matrix;


public class Head extends BodyPart {

	private static final String TAG = "Head";
	private float[] mHeadPosMatrix = new float[16];
	
	public static final float bodyToHeadLength = 0.07f;

	public Head() {
		
	}
	
	@Override
	public HeadGraphic getGraphic(D3Prey graphic, float size) {
		return new HeadGraphic(graphic, this, size);
	}

	

	public void updatePos(float posX, float posY, float bodyStartAngle) {
		float[] posTemp = { 0.0f, bodyToHeadLength, 0.0f, 1.0f };
		
		Matrix.setIdentityM(mHeadPosMatrix, 0);
		Matrix.translateM(mHeadPosMatrix, 0, posX, posY, 0);
		Matrix.rotateM(mHeadPosMatrix, 0, bodyStartAngle, 0, 0, 1);
		Matrix.multiplyMV(posTemp, 0, mHeadPosMatrix, 0, posTemp, 0);

		setPos(new PointF(posTemp[0], posTemp[1]));
	}
	
}
