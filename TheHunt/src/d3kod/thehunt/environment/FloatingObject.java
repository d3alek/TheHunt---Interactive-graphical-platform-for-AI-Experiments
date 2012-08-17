package d3kod.thehunt.environment;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;

class FloatingObject {

	public enum Type {
		FOOD, ALGAE;
	}

	private static final String TAG = "FloatingObject";
	
	private int mKey;
	Type mType;
	private float mX;
	private float mY;
	private boolean mGraphicSet = false;
	
	
	public FloatingObject(float x, float y, Type type) {
		mType = type;
		mX = x; mY = y;
	}
	
	public void setGraphic(int key) {
		mGraphicSet = true;
		mKey = key;
		D3GLES20.setShapePosition(key, mX, mY);
	}

	public void update() {
		
	}
	
	public int getKey() {
		return mKey;
	}
	
	public Type getType() {
		return mType;
	}
	
	public float getX() {
		return mX;
	}
	public float getY() {
		return mY;
	}

	public void clearGraphic() {
		if (!mGraphicSet) Log.v(TAG, "Graphic not set, can't clear!");
		D3GLES20.removeShape(mKey);		
	}
	
}