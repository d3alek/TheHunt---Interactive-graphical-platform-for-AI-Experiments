package d3kod.thehunt.environment;

import d3kod.d3gles20.D3GLES20;

public class FoodGM extends FloatingObject {

	private static final int FOOD_GM_NUTRITION = 50;
	private D3FoodGM mGraphic;
	
	public FoodGM(float x, float y, D3GLES20 d3GLES20) {
		super(x, y, Type.FOOD_GM, d3GLES20);
		mGraphic = new D3FoodGM(d3GLES20.getShaderManager());
		setGraphic(d3GLES20.putShape(mGraphic));
	}
	
	@Override
	public int nutrition() {
		return FOOD_GM_NUTRITION;
	}
	
}
