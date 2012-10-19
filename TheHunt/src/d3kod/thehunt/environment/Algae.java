package d3kod.thehunt.environment;

import java.util.Random;

import android.util.FloatMath;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;

class Algae extends FloatingObject {

	private static final float MIN_RADIUS = 0.01f;
	private static final float START_SCALE = 0.01f;
	private static final float GROW_SPEED = 0.01f;
	private static final float SHRINK_SPEED = 0.005f;
	private static final String TAG = "Algae";
	private static final int PUT_FOOD_TICKS = 300;
	private static final float MAX_VELOCITY = 0.005f;
	
	private D3Algae mGraphic;
	private boolean fullyGrown;
	private Environment mEnv;
	private int mPutFoodCounter;
	private float mStartVX;
	private float mStartVY;
	
	public Algae(float x, float y, D3GLES20 d3GLES20, Environment env) {
		super(x, y, Type.ALGAE, d3GLES20);
		mGraphic = new D3Algae(d3GLES20.getShaderManager());
		mGraphic.setScale(START_SCALE/mGraphic.getRadius());
//		mGraphic = new D3AlgaeHatching(textureDataHandle);
		setGraphic(d3GLES20.putShape(mGraphic));
		fullyGrown = false;
		mEnv = env;
		Random rand = new Random();
		float randAngl = rand.nextFloat()*D3Maths.PI*2;
		mStartVX = FloatMath.cos(randAngl )*MAX_VELOCITY;
		mStartVY = FloatMath.sin(randAngl)*MAX_VELOCITY;
		setVelocity(mStartVX, mStartVY);
	}
	
	@Override
	public void update() {
		if (!fullyGrown) {
			mGraphic.grow(GROW_SPEED);
			float newVX = mStartVX * (1 - mGraphic.getScale());
			float newVY = mStartVY * (1 - mGraphic.getScale());
			setVelocity(newVX, newVY);
//			Log.v(TAG, "Growing radius is " + getRadius());
			if (mGraphic.getScale() >= 1) {
				fullyGrown = true;
			}
		}
		else {
			if (mPutFoodCounter < PUT_FOOD_TICKS) {
				mPutFoodCounter++;
			}
			else {
				mEnv.putNewAlgae(getX(), getY());
				mPutFoodCounter = 0;
			}
			
			mGraphic.shrink(SHRINK_SPEED);
			
			if (getRadius() < MIN_RADIUS) {
				Log.v(TAG, "To remove algae");
				setToRemove();
			}
		}
		super.update();
	}
}