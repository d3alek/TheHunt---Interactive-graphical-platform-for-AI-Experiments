package d3kod.thehunt.environment;

import java.util.Random;

import android.util.FloatMath;

import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;

public class FoodAlgae extends FloatingObject {

	private static final int FOOD_ALGAE_NUTRITION = 10;
	private static final float MAX_VELOCITY = 0.005f;
	private D3FoodAlgae mGraphic;
	
	public FoodAlgae(float x, float y, D3GLES20 d3GLES20) {
		super(x, y, Type.FOOD_ALGAE, d3GLES20);
		mGraphic = new D3FoodAlgae(d3GLES20.getShaderManager());
		setGraphic(d3GLES20.putShape(mGraphic));
		Random rand = new Random();
		float randAngl = rand.nextFloat()*D3Maths.PI*2;
		setVelocity(FloatMath.cos(randAngl )*MAX_VELOCITY, FloatMath.sin(randAngl)*MAX_VELOCITY);
	}

	@Override
	public void applyFriction() {
	}
	
	@Override
	public int nutrition() {
		return FOOD_ALGAE_NUTRITION;
	}
	
}
