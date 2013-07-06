package d3kod.thehunt.world.environment;

import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Shape;

public class FoodGM extends FloatingObject implements Eatable {

	private static final int FOOD_GM_NUTRITION = 100;
	private D3FoodGM mGraphic;
	
	public FoodGM(float x, float y, SpriteManager d3gles20) {
		super(x, y, Type.FOOD_GM, d3gles20);
	}
	
	public void initGraphic() {
		super.initGraphic(new D3FoodGM());
	}

	public int getNutrition() {
		return FOOD_GM_NUTRITION;
	}

	public void processBite() {
		setToRemove();
	}
	
}
