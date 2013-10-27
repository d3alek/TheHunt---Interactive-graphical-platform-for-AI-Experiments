package com.primalpond.hunt.agent.prey;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.PointF;
import android.opengl.Matrix;

public class Tail extends BodyPart {

	private float[] mTailPosMatrix = new float[16];

	public Tail(JSONObject savedTail) {
	}

	@Override
	public BodyPartGraphic getGraphic(D3Prey graphic, float size) {
		return new TailGraphic(graphic, this, size);
	}

	public void updatePos(float mPosX, float mPosY) {
		float[] posTail = { D3Prey.tailPosition[0], D3Prey.tailPosition[1], 0.0f, 1.0f };

		Matrix.setIdentityM(mTailPosMatrix, 0);
		Matrix.translateM(mTailPosMatrix, 0, mPosX, mPosY, 0);
		Matrix.multiplyMV(posTail, 0, mTailPosMatrix, 0, posTail, 0);
		
		setPos(new PointF(posTail[0], posTail[1]));
		 
	}

	public JSONObject toJSON() throws JSONException {
		return new JSONObject();
	}

}
