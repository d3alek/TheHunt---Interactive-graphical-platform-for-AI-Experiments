package d3kod.thehunt.environment;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.shapes.D3Shape;

public abstract class FloatingObject {

	public enum Type {
		FOOD_ALGAE, FOOD_GM, ALGAE;
	}

	private static final String TAG = "FloatingObject";

	private static final float FRICTION = 0f;
	
	private int mKey;
	Type mType;
	private float mX;
	private float mY;
	private boolean mGraphicSet = false;

	private D3GLES20 mD3GLES20;

	private float vX;
	private float vY;

	private boolean mRemove;

	private D3Shape mGraphic;
	
	
	public FloatingObject(float x, float y, Type type) {
//		mD3GLES20 =  d3GLES20;
		mType = type;
		mX = x; mY = y;
		mRemove = false;
	}
	
	protected void setGraphic(D3Shape graphic, D3GLES20 d3GLES20) {
		mGraphicSet = true;
		mGraphic = graphic;
		mD3GLES20 = d3GLES20;
		mKey = mD3GLES20.putShape(mGraphic);
		mD3GLES20.setShapePosition(mKey, mX, mY);
		mD3GLES20.setShapeVelocity(mKey, vX, vY);
		Log.v(TAG, "Set graphic for floating object type " + getType() + " with key " + mKey + " at " + mX + " " + mY);
	}
	
	abstract public void setGraphic(D3GLES20 d3gles20);

	public void update() {
		//Log.v(TAG, "UPDATING FLOATING OBJECT!");
		applyFriction();
		mX += vX; mY += vY;
		mGraphic.setPosition(mX, mY);
//		mD3GLES20.setShapePosition(mKey, mX, mY);
	}
//	
	public void applyFriction() {
		//vX -= EnvironmentData.frictionCoeff*vX;
		//vY -= EnvironmentData.frictionCoeff*vY;
//		setVelocity(vX - FRICTION*vX, vY - FRICTION*vY);
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
	
	public void setVelocity(float vx, float vy) {
		vX = vx; vY = vy;
		if (mGraphicSet) mD3GLES20.setShapeVelocity(mKey, vx, vy);
	}

	public float getRadius() {
//		if (!mGraphicSet) return 0; //TODO: dirty fix
		return mGraphic.getRadius();
	}
	
	public float getVX() {
		return vX;
	}
	
	public float getVY() {
		return vY;
	}

	public boolean contains(float x, float y) {
		if (!mGraphicSet) {
			Log.v(TAG, "Illegal contains call " + x + " " + y + " because graphics are not set!");
			return false;
		}
//		Log.v(TAG, "Contains check with center " + mX + " " + mY + " or should it be " + mGraphic.getCenterX() + " " + mGraphic.getCenterY());
		return D3Maths.circleContains(mX, mY, getRadius(), x, y); //TODO: fix getRadius() call, keep seperate radiuses for graphic and logic
	}

	public boolean graphicIsSet() {
		return mGraphicSet;
	}

	public D3Shape getGraphic() {
		return mGraphic;
	}
	
	@Override
	public String toString() {
		return "Floating object " + mType + "(" + mKey + ")";
	}
}