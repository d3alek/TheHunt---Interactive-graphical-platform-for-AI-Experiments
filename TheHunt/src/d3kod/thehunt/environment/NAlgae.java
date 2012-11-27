package d3kod.thehunt.environment;

import java.security.spec.MGF1ParameterSpec;

import android.graphics.PointF;
import android.util.FloatMath;
import d3kod.d3gles20.D3GLES20;

public class NAlgae extends FloatingObject implements Eatable {

	private static final float ALGAE_START_SPEED = 0.005f;
	private static final float FRICTION = 0.001f;
	private static final float MAX_FRICTION = 0.1f;
	private static final int MAX_N = 50;
	public static final int FOOD_ALGAE_BITE_NUTRITION = 20;
	private int mSize;
	private D3NAlgae mGraphic;

	public NAlgae(int n, PointF pos, float dirAngle) {
		super(pos.x, pos.y, Type.ALGAE);
		mSize = n;
		setVelocity(FloatMath.cos(dirAngle)*getSpeed(), FloatMath.sin(dirAngle)*getSpeed());
	}

	private float getSpeed() {
		return ALGAE_START_SPEED/mSize;
	}

	public void setGraphic(D3GLES20 d3gles20) {
		mGraphic = new D3NAlgae(d3gles20.getShaderManager());
		super.setGraphic(mGraphic, d3gles20);
		updateGraphicSize();
	}

	private void updateGraphicSize() {
		mGraphic.setSizeCategory(mSize);
	}

	public void mergeWith(NAlgae algae) {
		int prevSize1 = getN();
		int prevSize2 = algae.getN();
		int newSize = prevSize1 + prevSize2;
		setN(newSize);
		setVelocity(
				getVX()+algae.getVX()*prevSize2/((float)prevSize1), 
				getVY()+algae.getVY()*prevSize2/((float)prevSize1));
		algae.setN(prevSize2 - (mSize - prevSize1)); 
	}
	
	public int getN() {
		return mSize;
	}
	
	public void setN(int n) {
		mSize = Math.min(n, MAX_N);
		updateGraphicSize();
	}

	public void grow(int increment) {
		setN(mSize + increment);
	}
	
	public float getRadius() {
		return mGraphic.getRadius();
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
		setN(mSize-1);
		if (getN() < 1) setToRemove();
	}
}
