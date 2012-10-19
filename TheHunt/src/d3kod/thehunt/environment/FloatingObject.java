package d3kod.thehunt.environment;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;

public class FloatingObject {

	public enum Type {
		FOOD_ALGAE, FOOD_GM, ALGAE;
	}

	private static final String TAG = "FloatingObject";
	
	private int mKey;
	Type mType;
	private float mX;
	private float mY;
	private boolean mGraphicSet = false;

	private D3GLES20 mD3GLES20;

	private float vX;
	private float vY;

	private boolean mRemove;
	
	
	public FloatingObject(float x, float y, Type type, D3GLES20 d3GLES20) {
		mD3GLES20 =  d3GLES20;
		mType = type;
		mX = x; mY = y;
		mRemove = false;
	}
	
	public void setGraphic(int key) {
		Log.v(TAG, "Floating object type " + getType() + " with key " + key + " at " + mX + " " + mY);
		mGraphicSet = true;
		mKey = key;
		mD3GLES20.setShapePosition(key, mX, mY);
	}

	public void update() {
//		applyFriction();
		mX += vX; mY += vY;
//		mD3GLES20.setShapePosition(mKey, mX, mY);
	}
	
	public void applyFriction() {
		vX -= EnvironmentData.frictionCoeff*vX;
		vY -= EnvironmentData.frictionCoeff*vY;
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
		mD3GLES20.removeShape(mKey);
		mRemove = true;
	}

	public boolean toRemove() {
		return mRemove;
	}
	
	public void setToRemove() {
		mRemove = true;
	}
	
	public int nutrition() {
		return 0;
	}
	
	public void setVelocity(float vx, float vy) {
		vX = vx; vY = vy;
		mD3GLES20.setShapeVelocity(mKey, vx, vy);
	}

	public float getRadius() {
		return mD3GLES20.getShape(mKey).getRadius();
	}
	
	public float getVX() {
		return vX;
	}
	
	public float getVY() {
		return vY;
	}
}