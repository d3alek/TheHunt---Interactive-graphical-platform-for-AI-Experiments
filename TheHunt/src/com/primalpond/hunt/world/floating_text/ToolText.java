package com.primalpond.hunt.world.floating_text;

import java.util.Random;

import android.util.FloatMath;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.text.GLText;

public class ToolText extends D3FadingText {
	
	private static final float textSize= 2.5f;
	private static final float fadingSpeed=0.03f;
	private static final float SHIFT_LENGTH = 0.15f;
	private static final float RANDOM_ROT_MAX = 50;
	private float[] mProjMatrix;
	
	public ToolText(String text, float x, float y, float[] projMatrix)  {
		super(text, textSize, fadingSpeed);
		Random rand = new Random();
		float randAngle = rand.nextFloat() * D3Maths.PI + D3Maths.PI/2;
		float rotAngle = RANDOM_ROT_MAX - 2*RANDOM_ROT_MAX*rand.nextFloat();
		mProjMatrix = projMatrix;
		super.setPosition(x + FloatMath.sin(randAngle)*SHIFT_LENGTH, 
				y - FloatMath.cos(randAngle)*SHIFT_LENGTH, rotAngle);
	}
	
	@Override
	public void draw(GLText glText, float[] projMatrix, float[] viewMatrix) {
		// TODO Auto-generated method stub
		super.draw(glText, mProjMatrix, viewMatrix);
	}

}
