package com.primalpond.hunt.world.environment;

import org.json.JSONException;
import org.json.JSONObject;

import d3kod.graphics.sprite.SpriteManager;

public class FoodGM extends FloatingObject implements Eatable {

	private static final int FOOD_GM_NUTRITION = 100;
//	private D3FoodGM mGraphic;
	
	public FoodGM(float x, float y, SpriteManager d3gles20) {
		super(x, y, Type.FOOD_GM, d3gles20);
	}
	
	public FoodGM(JSONObject jsonObject, SpriteManager d3gles20) throws JSONException {
		this((float)jsonObject.getDouble(KEY_POS_X),
				(float)jsonObject.getDouble(KEY_POS_Y), d3gles20); 
		setVelocity((float)jsonObject.getDouble(KEY_VX), (float)jsonObject.getDouble(KEY_VY));
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

	public JSONObject toJSON() throws JSONException {
		return super.toJSON();
	}

}
