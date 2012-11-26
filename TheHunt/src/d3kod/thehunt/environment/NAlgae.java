package d3kod.thehunt.environment;

import android.graphics.PointF;
import android.util.FloatMath;
import d3kod.d3gles20.D3GLES20;

public class NAlgae extends FloatingObject implements Eatable {

	private static final float ALGAE_START_SPEED = 0.005f;
	private static final float FRICTION = 0.001f;
	private static final float MAX_FRICTION = 0.1f;
	private static final int MAX_SIZE = 50;
	public static final int FOOD_ALGAE_BITE_NUTRITION = 20;
	private int mSize;

	public NAlgae(int n, PointF pos, float dirAngle) {
		super(pos.x, pos.y, Type.ALGAE);
		mSize = n;
		setVelocity(FloatMath.cos(dirAngle)*getSpeed(), FloatMath.sin(dirAngle)*getSpeed());
	}

	private float getSpeed() {
		return ALGAE_START_SPEED/mSize;
	}

	public void setGraphic(D3GLES20 d3gles20) {
		super.setGraphic(new D3NAlgae(d3gles20.getShaderManager()), d3gles20);
		updateGraphicSize();
	}

	private void updateGraphicSize() {
		((D3NAlgae)super.getGraphic()).setSizeCategory(mSize);
	}

	public void mergeWith(NAlgae algae) {
		int prevSize1 = getSize();
		int prevSize2 = algae.getSize();
		int newSize = prevSize1 + prevSize2;
		setSize(newSize);
		setVelocity(
				getVX()+algae.getVX()*prevSize2/((float)prevSize1), 
				getVY()+algae.getVY()*prevSize2/((float)prevSize1));
		algae.setSize(prevSize2 - (mSize - prevSize1)); 
	}
	
	public int getSize() {
		return mSize;
	}
	
	public void setSize(int size) {
		mSize = Math.min(size, MAX_SIZE);
		updateGraphicSize();
	}

	public void grow(int increment) {
		setSize(mSize + increment);
	}
	
	@Override
	public void applyFriction() {
		float curVX = getVX(), curVY = getVY();
		float friction = calcFriction();
		setVelocity(curVX - friction*curVX, curVY - friction*curVY);
	}

	private float calcFriction() {
		return Math.min(FRICTION*(mSize*mSize+mSize+3), MAX_FRICTION);
	}

	public int getNutrition() {
		return FOOD_ALGAE_BITE_NUTRITION;
	}

	public void processBite() {
		setSize(mSize-1);
		if (getSize() < 1) setToRemove();
	}
}
