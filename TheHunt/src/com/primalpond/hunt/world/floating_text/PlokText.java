package com.primalpond.hunt.world.floating_text;

import java.util.Random;

import android.util.FloatMath;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.shapes.D3FadingText;

public class PlokText extends D3FadingText {
	
	private static final String text = "Plok!";
	private static final float textSize= 2.0f;
	private static final float fadingSpeed=0.03f;
	private static final float SHIFT_LENGTH = 0.1f;
	private static final float RANDOM_ROT_MAX = 50;
	
	public PlokText(float x, float y)  {
		super(text, textSize, fadingSpeed);
		Random rand = new Random();
		float randAngle = rand.nextFloat() * 2 * D3Maths.PI;
		float rotAngle = RANDOM_ROT_MAX - 2*RANDOM_ROT_MAX*rand.nextFloat();
		super.setPosition(x + FloatMath.sin(randAngle)*SHIFT_LENGTH, 
				y - FloatMath.cos(randAngle)*SHIFT_LENGTH, rotAngle);
	}

}
