package com.primalpond.hunt.world.environment;

import d3kod.graphics.sprite.shapes.D3FilledCircle;

public class D3FoodGM extends D3FilledCircle {

	private static float[] foodColor = {0.0f, 0.8f, 0.0f, 1.0f};
	private static final float radius = 0.01f;
	
	protected D3FoodGM() {
		super(radius, foodColor, 20);
	}

	@Override
	public float getRadius() {
		return radius;
	}

}
