package d3kod.thehunt.floating_text;

import java.util.Random;

import android.util.FloatMath;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.ShaderManager;
import d3kod.d3gles20.TextureManager;
import d3kod.d3gles20.TextureManager.Texture;
import d3kod.d3gles20.shapes.D3FadingText;

public class CatchText extends D3FadingText {
	private static final float textSize=0.3f;
	private static final float fadingSpeed=0.02f;
	private static final float SHIFT_LENGTH = 0.2f;
	private static final float RANDOM_ROT_MAX = 50;
	
	public CatchText(float x, float y, TextureManager tm, ShaderManager sm) {
		super(textSize, fadingSpeed, tm.getTextureHandle(Texture.CATCH_TEXT), sm, 0.1f);
		Random rand = new Random();
		float randAngle = rand.nextFloat() * 2 * D3Maths.PI;
		float rotAngle = RANDOM_ROT_MAX - 2*RANDOM_ROT_MAX*rand.nextFloat();
		super.setPosition(x + FloatMath.sin(randAngle)*SHIFT_LENGTH, 
				y - FloatMath.cos(randAngle)*SHIFT_LENGTH, rotAngle);
	}

}
