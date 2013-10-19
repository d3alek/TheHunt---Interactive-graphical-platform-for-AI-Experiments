package com.primalpond.hunt.world.environment;

import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.SpriteManager;

public class NAlgae extends FloatingObject implements Eatable {

	private static final float ALGAE_START_SPEED = 0.005f;
	private static final float FRICTION = 0.001f;
	private static final float MAX_FRICTION = 0.1f;
	private static final int MAX_N = 50;
	public static final int FOOD_ALGAE_BITE_NUTRITION = 10;
	private static final String TAG = "Nalgae";
	private int mSize;
	transient private D3NAlgae mGraphic;

	public NAlgae(int n, PointF pos, float dirAngle, Environment environment, SpriteManager d3gles20) {
		this(n, pos, environment, d3gles20);
		setVelocity(FloatMath.cos(dirAngle)*getSpeed(), FloatMath.sin(dirAngle)*getSpeed());
	}
	
	public NAlgae(int n, PointF pos, PointF dirVector, Environment environment, SpriteManager d3gles20) {
		this(n, pos, environment, d3gles20);
		setVelocity(dirVector.x*getSpeed(), dirVector.y*getSpeed());
	}
	
	public NAlgae(int n, PointF pos, Environment environment, SpriteManager d3gles20) {
		super(pos.x, pos.y, Type.ALGAE, d3gles20);
		mSize = n;
	}
	
	@Override
	public void update() {
		if (Math.random() < Environment.GLOBAL.getAlgaeGrowthChance()) {
			grow(1);
//			Log.v(TAG, "Growing algae due to growth chance! ");
		}
		super.update();
	}

	private float getSpeed() {
		return ALGAE_START_SPEED/mSize;
	}

	public void initGraphic() {
		mGraphic = new D3NAlgae();
		super.initGraphic(mGraphic);
		updateGraphicSize();
	}

	private void updateGraphicSize() {
		if (mGraphic != null)
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
		
		Log.v(TAG, "Releasing seed!");
		Environment.GLOBAL.addNewAlgae(1, seedPos, randAngle);
	}

	public void grow(int increment) {
		if (mSize == MAX_N) {
			releaseSeed(); //TODO: merge this case with mergeWith
		}
		setN(mSize + increment);
	}
//	
//	public float getRadius() {
//		return mGraphic.getRadius();
//	}
	
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

//	public void cut(float a, float b) {
//		Log.v(TAG, "Cutting algae slope " + a + " adj " +  b);
//		float rad = getRadius(), centerX = getX();
//		float x, y, xOrthPos, xOrthNeg, yOrthPos, yOrthNeg, aOrth, bOrth;
//		for (x = centerX - rad; x < centerX + rad; x += 2*rad/getN()) {
//			y = a*x + b;
//			aOrth = -(1/a);
//			bOrth = -aOrth*x+y;
//			yOrthPos = xOrthPos*CUT_ADJ + bOrth;
//			
//			
//		}
//	} 
	public void cut(PointF dir) { 
		PointF center = new PointF(getX(), getY());
		PointF putAlgaePoint = new PointF(getX(), getY()), putAlgaePos = new PointF(0, 0), putAlgaeNeg = new PointF(0, 0);
//		float angleAdj = (float)Math.toDegrees(Math.atan(-dir.y/dir.x));
//		float angleNeg = (float)Math.toDegrees(Math.atan(dir.y/-dir.x));
		float angleBase = (float)Math.toDegrees(Math.atan(dir.y/ dir.x));
		float distanceBetweenSpawns = Math.max(getRadius()/getN(), 2*D3NAlgae.algaeStartSize + 0.01f);
//		float distanceBetweenSpawns = 0.1f;
//		float SPAWN_ADJ = 0.02f;
		Log.v(TAG, "Angle is " + angleBase + " dir is " + dir.x + " " + dir.y);
		int i, j, n = getN();
		float SPAWN_ADJ = D3NAlgae.algaeStartSize*(n+1)/2; 
		PointF newAlgaeSpeed = new PointF(0.001f, 0.001f);
		int n4;
		if (n < 4) n4 = 1;
		else n4 = n/4;
		int n1 = n / 2, n2 = n / 2;
		if (n % 2 == 0) {
			if (Math.random() % 2 == 0) {
				n1 += 1;
			}
			else n2 += 1;
		}
		else {
		}
		Log.v(TAG, "Angles are " + Math.toDegrees(Math.atan(-dir.x/dir.y)) + " " + Math.toDegrees(Math.atan(dir.x/-dir.y)));
//		for (j = 0; j < 2; ++j) { //TODO: Make it split on 2
//			for (i = 0; i < n4; ++i) {
				putAlgaePos.set(putAlgaePoint);
				putAlgaePos.offset(-SPAWN_ADJ*dir.y, SPAWN_ADJ*dir.x);
//				mEnvironment.addNewAlgae(1, putAlgaePos, D3Maths.getRandAngle(), newAlgaeSpeed);
//				mEnvironment.addNewAlgae(1, putAlgaePos, 0, newAlgaeSpeed);
				Environment.GLOBAL.addNewAlgae(n1, putAlgaePos, new PointF(-dir.y, dir.x), newAlgaeSpeed);
				
//				mEnvironment.add
				putAlgaeNeg.set(putAlgaePoint);
				putAlgaeNeg.offset(SPAWN_ADJ*dir.y, -SPAWN_ADJ*dir.x);
//				mEnvironment.addNewAlgae(1, putAlgaeNeg, D3Maths.getRandAngle(), newAlgaeSpeed);
//				mEnvironment.addNewAlgae(1, putAlgaeNeg, 0, newAlgaeSpeed);
				Environment.GLOBAL.addNewAlgae(n2, putAlgaeNeg, new PointF(dir.y, -dir.x), newAlgaeSpeed);

				putAlgaePoint.offset(distanceBetweenSpawns*dir.x, distanceBetweenSpawns*dir.y);
//			}
//			putAlgaePoint.set(center);
////			dir.negate();
//			distanceBetweenSpawns = -distanceBetweenSpawns;
//			putAlgaePoint.offset(distanceBetweenSpawns*dir.x, distanceBetweenSpawns*dir.y);
//		}
		setToRemove();
	}
}
