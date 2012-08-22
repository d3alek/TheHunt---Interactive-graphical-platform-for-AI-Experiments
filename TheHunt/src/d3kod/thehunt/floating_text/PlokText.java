package d3kod.thehunt.floating_text;

import java.util.Random;

import android.util.FloatMath;

import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.TextureManager;
import d3kod.d3gles20.TextureManager.Texture;
import d3kod.thehunt.prey.D3FadingText;

public class PlokText extends D3FadingText {

	private static final float textSize=0.15f;
	private static final float fadingSpeed=0.03f;
	private static final float SHIFT_LENGTH = 0.1f;
	private static final float RANDOM_ROT_MAX = 50;
	
	public PlokText(float x, float y, TextureManager tm) {
		super(textSize, fadingSpeed, tm.getTextureHandle(Texture.PLOK_TEXT), 0.1f);
		Random rand = new Random();
		float randAngle = rand.nextFloat() * 2 * D3Maths.PI;
		float rotAngle = RANDOM_ROT_MAX - 2*RANDOM_ROT_MAX*rand.nextFloat();
		super.setPosition(x + FloatMath.sin(randAngle)*SHIFT_LENGTH, 
				y - FloatMath.cos(randAngle)*SHIFT_LENGTH, rotAngle);
	}

}
