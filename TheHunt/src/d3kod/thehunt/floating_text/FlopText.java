package d3kod.thehunt.floating_text;

import d3kod.d3gles20.TextureManager;
import d3kod.d3gles20.TextureManager.Texture;
import d3kod.thehunt.prey.D3FadingText;

public class FlopText extends D3FadingText {

	private static final String TAG = "FlopText";
	private static final float FADE_SPEED = 0.01f;
	private static final float textSize = 0.1f;
	
	protected FlopText(float posX, float posY, float angleDeg, TextureManager tm) {
		super(textSize, FADE_SPEED, tm.getTextureHandle(Texture.FLOP_TEXT), 0.6f);
		super.setPosition(posX, posY, angleDeg);
//		super.setTextureDataHandle();
	}
}
