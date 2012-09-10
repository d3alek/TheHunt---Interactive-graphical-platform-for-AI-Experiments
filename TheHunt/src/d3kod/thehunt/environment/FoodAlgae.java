package d3kod.thehunt.environment;

import d3kod.d3gles20.D3GLES20;

public class FoodAlgae extends FloatingObject {

	private static final int FOOD_ALGAE_NUTRITION = 10;
	private D3FoodAlgae mGraphic;
	
	public FoodAlgae(float x, float y, D3GLES20 d3GLES20) {
		super(x, y, Type.FOOD_ALGAE, d3GLES20);
		mGraphic = new D3FoodAlgae(d3GLES20.getShaderManager());
		setGraphic(d3GLES20.putShape(mGraphic));
	}
	
	@Override
	public int nutrition() {
		return FOOD_ALGAE_NUTRITION;
	}
	
}
