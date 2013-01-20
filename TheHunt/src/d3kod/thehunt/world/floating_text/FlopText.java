package d3kod.thehunt.world.floating_text;

import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.texture.TextureManager;
import d3kod.graphics.texture.TextureManager.Texture;

public class FlopText extends D3FadingText {

	private static final String TAG = "FlopText";
	private static final float FADE_SPEED = 0.01f;
	private static final float textSize = 0.07f;
	
	public FlopText(float posX, float posY, float angleDeg, TextureManager tm, ShaderProgramManager sm) {
		super(textSize, FADE_SPEED, tm.getTextureHandle(Texture.FLOP_TEXT), sm, 0.6f);
		super.setPosition(posX, posY, angleDeg);
	}
}
