package d3kod.thehunt.world.environment;

import android.graphics.PointF;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;

public abstract class FloatingObject extends D3Sprite {

	public enum Type {
		FOOD_ALGAE, FOOD_GM, ALGAE;
	}

	private static final String TAG = "FloatingObject";

	Type mType;

	private boolean mRemove;

	public FloatingObject(float x, float y, Type type, SpriteManager d3gles20) {
		super(new PointF(x, y), d3gles20);
		mType = type;
		mRemove = false;
	}
	
	public void update() {
		applyFriction();
		super.update();
	}
	
	public void applyFriction() {}
	
	public Type getType() {
		return mType;
	}
	
	public boolean toRemove() {
		return mRemove;
	}
	
	public void setToRemove() {
		mRemove = true;
	}
	
//	public float getRadius() {
//		return mGraphic.getRadius();
//	}
}