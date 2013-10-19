package com.primalpond.hunt.world.floating_text;

import d3kod.graphics.sprite.shapes.D3FadingText;

public class FlopText extends D3FadingText {

	private static final String TAG = "FlopText";	
	private static final String text = "flop";
	private static final float textSize = 1.0f;
	private static final float FADE_SPEED = 0.01f;
	
	public FlopText(float posX, float posY, float angleDeg) {
		super(text, textSize, FADE_SPEED);
		super.setPosition(posX, posY, angleDeg);
	}
}
