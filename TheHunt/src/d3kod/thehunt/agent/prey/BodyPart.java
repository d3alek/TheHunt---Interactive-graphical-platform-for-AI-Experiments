package d3kod.thehunt.agent.prey;

import android.graphics.PointF;
import android.util.Log;

public abstract class BodyPart {
	
	private static final String TAG = "BodyPart";
	private PointF mPos;// = new PointF(0, y);
	
	public PointF getPos() {
		if (mPos == null) {
			Log.e(TAG, "getPos is null!");
			return new PointF(0, 0);
		}
		return mPos;
	}
	
	public void setPos(PointF pointF) {
		mPos = pointF;
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

	public abstract BodyPartGraphic getGraphic(D3Prey graphic, float size);

	public void mutate() {
		
	}
}
