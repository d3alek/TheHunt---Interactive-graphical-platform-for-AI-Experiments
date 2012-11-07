package d3kod.thehunt.environment;

import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.shapes.D3Shape;

public class FoodGM extends FloatingObject implements Eatable {

	private static final int FOOD_GM_NUTRITION = 50;
	private D3FoodGM mGraphic;
	
	public FoodGM(float x, float y) {
		super(x, y, Type.FOOD_GM);
	}
	
	public void setGraphic(D3GLES20 d3gles20) {
		super.setGraphic(new D3FoodGM(d3gles20.getShaderManager()), d3gles20);
	}

	public int getNutrition() {
		return FOOD_GM_NUTRITION;
	}

	public void processBite() {
		setToRemove();
	}
	
}
