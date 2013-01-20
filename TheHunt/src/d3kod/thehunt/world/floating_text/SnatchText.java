package d3kod.thehunt.world.floating_text;

import java.util.Random;

import android.util.FloatMath;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.texture.TextureManager;
import d3kod.graphics.texture.TextureManager.Texture;

public class SnatchText extends D3FadingText {
	private static final float textSize=0.2f;
	private static final float fadingSpeed=0.02f;
	private static final float SHIFT_LENGTH = 0.2f;
	private static final float RANDOM_ROT_MAX = 50;
	
	public SnatchText(float x, float y, TextureManager tm, ShaderProgramManager sm) {
		super(textSize, fadingSpeed, tm.getTextureHandle(Texture.SNATCH_TEXT), sm, 0.1f);
		Random rand = new Random();
		float randAngle = rand.nextFloat() * 2 * D3Maths.PI;
		float rotAngle = RANDOM_ROT_MAX - 2*RANDOM_ROT_MAX*rand.nextFloat();
		super.setPosition(x + FloatMath.sin(randAngle)*SHIFT_LENGTH, 
				y - FloatMath.cos(randAngle)*SHIFT_LENGTH, rotAngle);
	}
}
