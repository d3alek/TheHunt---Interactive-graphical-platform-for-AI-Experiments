package d3kod.d3gles20;

import android.graphics.PointF;
import android.util.Log;
import d3kod.d3gles20.shapes.D3Shape;

public abstract class D3Sprite {
	private static final String TAG = "D3Sprite";
	protected D3Shape mGraphic;
	private PointF mPos;
	private PointF dirVector;
	private D3GLES20 mD3GLES20;
//	private boolean mGraphicSet;
	
	public D3Sprite(PointF position, D3GLES20 d3gles20) {
		mD3GLES20 = d3gles20;
		mPos = new PointF(position.x, position.y);
		dirVector = new PointF();
//		mGraphicSet = false; 
//		mRemove = false;
	}
	
	public void setPosition(PointF position) {
		mPos.set(position);
	}	
	
	public void update() {
//		mX += vX; mY += vY;
//		mPos.x += vX; mPos.y += vY;
		mPos.x += dirVector.x;
		mPos.y += dirVector.y;
		mGraphic.setPosition(mPos.x, mPos.y);
	}
	
	protected void initGraphic(D3Shape graphic) {
//		mGraphicSet = true;
		mGraphic = graphic;
//		mD3GLES20 = d3GLES20;
//		mKey = mD3GLES20.putShape(mGraphic);
//		mD3GLES20.setShapePosition(mKey, mX, mY);
//		mD3GLES20.setShapeVelocity(mKey, vX, vY);
		mGraphic.setPosition(mPos.x, mPos.y); 
		mD3GLES20.putSprite(this);
		Log.v(TAG, "Set graphic for d3sprite at " + mPos.x + " " + mPos.y);
	}
	
	public abstract void initGraphic();

	public float getX() {
		return mPos.x;
	}
	public float getY() {
		return mPos.y;
	}
	public PointF getPosition() {
		return mPos;
	}
	
	public void clearGraphic() {
//		if (!mGraphicSet) Log.v(TAG, "Graphic not set, can't clear!");
		if (mGraphic == null) Log.v(TAG, "Graphic not set, can't clear!");
//		else mD3GLES20.removeShape(mKey);
		mGraphic = null;
//		mRemove = true;
	}
	
	public void setVelocity(float vx, float vy) {
//		vX = vx; vY = vy;
		dirVector.x = vx; dirVector.y = vy;
		if (mGraphic != null) {
			mGraphic.setVelocity(vx, vy);
		}
	}
	
	public void updateVelocity(float dVx, float dVy) {
		dirVector.x += dVx; dirVector.y += dVy;
		if (mGraphic != null) {
			mGraphic.setVelocity(dirVector.x, dirVector.y);
		}
	}
	
	public float getRadius() {
//		if (!mGraphicSet) return 0; //TODO: dirty fix
		return mGraphic.getRadius();
	}
	
	public float getVX() {
		return dirVector.x;
	}
	
	public float getVY() {
		return dirVector.y;
	}

	public boolean contains(PointF point) {
		if (mGraphic == null) {
			Log.v(TAG, "Illegal contains call " + point.x +  " " + point.y + " because graphics are not set!");
			return false;
		}
//		Log.v(TAG, "Contains check with center " + mX + " " + mY + " or should it be " + mGraphic.getCenterX() + " " + mGraphic.getCenterY());
		return D3Maths.circleContains(mPos.x, mPos.y, getRadius(), point.x, point.y); //TODO: fix getRadius() call, keep seperate radiuses for graphic and logic
	}
	public D3Shape getGraphic() {
		return mGraphic;
	}
	@Override
	public String toString() {
		return "D3Sprite " +  mPos.toString();
	}

	public void draw(float[] vMatrix, float[] projMatrix, float interpolation) {
		// TODO Auto-generated method stub
		if (mGraphic != null) mGraphic.draw(vMatrix, projMatrix, interpolation);
		
	}


}
