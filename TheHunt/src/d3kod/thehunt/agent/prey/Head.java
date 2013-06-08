package d3kod.thehunt.agent.prey;

import java.util.ArrayList;

import d3kod.thehunt.agent.prey.sensor.Sensor.Sensors;

import android.graphics.PointF;
import android.opengl.Matrix;


public class Head extends BodyPart {

	private static final String TAG = "Head";
	private float[] mHeadPosMatrix = new float[16];
	
	public static final float bodyToHeadLength = 0.07f;
	ArrayList<Sensors> mSensors;
	private int independentMutationsNum = 2;
	private HeadGraphic mGraphic;

	public Head() {
		mSensors = new ArrayList<Sensors>();
		mSensors.add(Sensors.CURRENT_SENSOR);
	}
	
	@Override
	public HeadGraphic getGraphic(D3Prey graphic, float size) {
		if (mGraphic == null) mGraphic = new HeadGraphic(graphic, this, size);
		return mGraphic;
	}

	

	public void updatePos(float posX, float posY, float bodyStartAngle) {
		float[] posTemp = { 0.0f, bodyToHeadLength, 0.0f, 1.0f };
		
		Matrix.setIdentityM(mHeadPosMatrix, 0);
		Matrix.translateM(mHeadPosMatrix, 0, posX, posY, 0);
		Matrix.rotateM(mHeadPosMatrix, 0, bodyStartAngle, 0, 0, 1);
		Matrix.multiplyMV(posTemp, 0, mHeadPosMatrix, 0, posTemp, 0);

		setPos(new PointF(posTemp[0], posTemp[1]));
	}
	
	@Override
	public void mutate() {
		int rand = (int)(Math.random()*1000);
		if (rand % independentMutationsNum == 0) {
			mSensors.add(Sensors.HEARING_SENSOR);
			mGraphic.putEars();
			
		}
		else if (rand % independentMutationsNum == 1) {
			mSensors.add(Sensors.SIGHT_SENSOR);
			mGraphic.putEyes();
		}
	}

	public ArrayList<Sensors> getSensors() {
		return mSensors;
	}
	
}

