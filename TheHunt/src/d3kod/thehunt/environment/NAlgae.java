package d3kod.thehunt.environment;

import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;

public class NAlgae extends FloatingObject implements Eatable {

	private static final float ALGAE_START_SPEED = 0.005f;
	private static final float FRICTION = 0.001f;
	private static final float MAX_FRICTION = 0.1f;
	private static final int MAX_N = 50;
	public static final int FOOD_ALGAE_BITE_NUTRITION = 20;
	private static final String TAG = "Nalgae";
	private int mSize;
	private D3NAlgae mGraphic;
	private Environment mEnvironment;

	public NAlgae(int n, PointF pos, float dirAngle, Environment environment) {
		super(pos.x, pos.y, Type.ALGAE);
		mSize = n;
		setVelocity(FloatMath.cos(dirAngle)*getSpeed(), FloatMath.sin(dirAngle)*getSpeed());
		mEnvironment = environment;
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
	
	//TODO: Use grow here! 
	public void mergeWith(NAlgae algae) {
		int prevSize1 = getN();
		int prevSize2 = algae.getN();
		int newSize = prevSize1 + prevSize2;
		setN(newSize);
		setVelocity(
				getVX()+algae.getVX()*prevSize2/((float)prevSize1), 
				getVY()+algae.getVY()*prevSize2/((float)prevSize1));
		if (getN() < newSize) {
			// release 1 seed, decrease the mergee's n with 1
			algae.setN(prevSize2 - 1); 
			releaseSeed();
		}
		else {
			algae.setN(0);
		}
	}
	
	public int getN() {
		return mSize;
	}
	
	public void setN(int n) {
		mSize = Math.min(n, MAX_N);
		updateGraphicSize();
		//if (n > MAX_N) {
			//releaseSeeds(n - MAX_N);
			//kreleaseSeed();
		//}
	}
	
	private void releaseSeeds(int number) {
		Log.v(TAG, "Algae wants to release " + number + " seeds!");
		while (number > 0) {
			releaseSeed();
			number--;
		}
	}

	private void releaseSeed() {
		float randAngle = D3Maths.getRandAngle();
		PointF seedPos = new PointF(
				getX() + (getRadius()+0.01f)*FloatMath.cos(randAngle),
				getY() + (getRadius()+0.01f)*FloatMath.sin(randAngle));
		
		mEnvironment.addNewAlgae(1, seedPos, randAngle);
		//setN(getN()-1);
	}

	public void grow(int increment) {
		if (mSize == MAX_N) {
			releaseSeed(); //TODO: merge this case with mergeWith
		}
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
