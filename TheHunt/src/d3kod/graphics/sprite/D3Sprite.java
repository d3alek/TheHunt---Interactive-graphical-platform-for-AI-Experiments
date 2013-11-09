package d3kod.graphics.sprite;

import android.graphics.PointF;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.shapes.D3Shape;

public abstract class D3Sprite {
	private static final String TAG = "D3Sprite";
	transient protected D3Shape mGraphic; 
	private PointF mPos;
	private PointF dirVector;
	transient private SpriteManager mD3GLES20;
	private float cachedRadius = -1;
	
	public D3Sprite(PointF position, SpriteManager d3gles20) {
		mD3GLES20 = d3gles20;
		mPos = new PointF(position.x, position.y);
		dirVector = new PointF();
		if (mD3GLES20 == null) {
			Log.v(TAG, "mD3GlES20 is null on D3Sprite construct!");
		}
		else mD3GLES20.putSprite(this);
	}
	
	public void setPosition(PointF position) {
		mPos.set(position);
	}	
	
	public void update() {
		mPos.x += dirVector.x;
		mPos.y += dirVector.y;
		if (mGraphic == null) {
			Log.v(TAG, "Not updating graphic as it is null!");
		}
		else {
			mGraphic.setPosition(mPos.x, mPos.y);
		}
	}
	
	protected void initGraphic(D3Shape graphic) {
		mGraphic = graphic;
		mGraphic.setPosition(mPos.x, mPos.y); 
		if (mGraphic.getProgram() == null) {
//			Log.v(TAG, "Setting program to default program " + mD3GLES20);
			if (mD3GLES20 != null) mGraphic.setProgram(mD3GLES20.getDefaultProgram());
			else {
				Log.e(TAG, "D3Sprite graphic program is null and Sprite Manager is null");
			}
		}
//		Log.v(TAG, "Set graphic for d3sprite at " + mPos.x + " " + mPos.y);
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
		if (mGraphic == null) {
			Log.v(TAG, "Graphic not set, can't clear!");
		}
		else {
			mD3GLES20.removeShape(mGraphic);
			mGraphic = null;
		}
	}
	
	public void setVelocity(float vx, float vy) {
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
		if (mGraphic != null) {
			cachedRadius = mGraphic.getRadius();
		}
//		else {
////			Log.v(TAG, "mGraphic is null, return cached radius!");
//		}
//		return mGraphic.getRadius();
		return cachedRadius;
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
		if (point == null) {
			Log.v(TAG, "Illegal contains call because point is null");
			return false;
		}
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
		if (mGraphic != null) mGraphic.draw(vMatrix, projMatrix, interpolation);
		
	}
	
	public void setSpriteManager(SpriteManager d3gles20) {
		mD3GLES20 = d3gles20;
		mD3GLES20.putSprite(this);
	}
	
	public boolean spriteManagerNotSet() {
		return mD3GLES20 == null;
	}

	public SpriteManager getSpriteManager() {
		return mD3GLES20;
	}

}
