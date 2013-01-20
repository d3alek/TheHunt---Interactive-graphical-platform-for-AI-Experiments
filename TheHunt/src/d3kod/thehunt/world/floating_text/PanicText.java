package d3kod.thehunt.world.floating_text;

import android.util.FloatMath;
import android.util.Log;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.texture.TextureManager;
import d3kod.graphics.texture.TextureManager.Texture;

public class PanicText extends D3FadingText {

	private static final float textSize = 0.03f;
	private static final float fadingSpeed = 0.02f;
	private static final float SHIFT_LENGTH_X = -0.05f;
	private static final float SHIFT_LENGTH_Y = 0.07f;
//	private static final float RANDOM_ROT_MAX = 50;
	private static final String TAG = "PanicText";
	
	public PanicText(float x, float y, float headAngle, TextureManager tm, ShaderProgramManager sm) {
		super(textSize, fadingSpeed, tm.getTextureHandle(Texture.PANIC_TEXT), sm, 0.1f);
//		Random rand = new Random();
//		float randAngle = rand.nextFloat() * 2 * D3Maths.PI;
//		float rotAngle = RANDOM_ROT_MAX - 2*RANDOM_ROT_MAX*rand.nextFloat();
		setPosition(x, y, headAngle);
	}

	@Override
	public void setPosition(float x, float y, float angleDeg) {
//		Log.v(TAG, "angleDeg is " + angleDeg + " " + FloatMath.sin(angleDeg) + " " + FloatMath.cos(angleDeg));
		float angleRad = (float)Math.toRadians(angleDeg);
//		angleRad = -angleRad;
//		super.setPosition(x + FloatMath.cos(angleRad)*SHIFT_LENGTH - FloatMath.sin(angleRad)*SHIFT_LENGTH, 
//				y + FloatMath.cos(angleRad)*SHIFT_LENGTH + FloatMath.sin(angleRad)*SHIFT_LENGTH, 0);
//		super.setPosition(x + SHIFT_LENGTH, y - SHIFT_LENGTH, angleDeg);
		super.setPosition(x + FloatMath.cos(angleRad)*SHIFT_LENGTH_X - FloatMath.sin(angleRad)*SHIFT_LENGTH_Y, 
		y + FloatMath.cos(angleRad)*SHIFT_LENGTH_Y + FloatMath.sin(angleRad)*SHIFT_LENGTH_X, angleDeg + 90);
	}
	
}
