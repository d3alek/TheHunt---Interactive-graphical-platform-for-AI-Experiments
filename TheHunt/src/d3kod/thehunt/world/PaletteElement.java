package d3kod.thehunt.world;

import android.graphics.PointF;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.sprite.shapes.D3Quad;
import d3kod.thehunt.world.logic.TheHuntRenderer;

public class PaletteElement extends D3Sprite {
	class PaletteText extends D3FadingText {

		public PaletteText(String text) {
			super(text, 2.0f, 0);
			setCentered(true);
		}
	}

	private static final float[] bgActiveColor = {0.0f, 0.0f, 0.0f, 1.0f};

	private static final String TAG = "PaletteElement";

	private static final float[] bgInactiveColor = TheHuntRenderer.bgColor;

//	private static final float[] textActiveColor = {1.0f, 1.0f, 1.0f, 1.0f};

	private static float mWidth = 0.1f;
	private static float mHeight = 0.06f;
	private static double mAngleStart = Math.toRadians(45);
	private static double numberAngleTurn = Math.toRadians(90);
	private String mText;
	private PointF mRelativePos;
	private PaletteText mTextGraphic;


	public PaletteElement(PointF palettePosition, float paletteRadius, int numberInPalette, String text, SpriteManager d3gles20) {
		super(calcPosition(palettePosition, paletteRadius, numberInPalette), d3gles20);
		mRelativePos = new PointF(getPosition().x - palettePosition.x, getPosition().y - palettePosition.y);
		mText = text;
	}

	private static PointF calcPosition(PointF palettePosition, float paletteRadius,
			int numberInPalette) {
		PointF pos = new PointF();
		//TODO: fix once you have labels
		pos.x = palettePosition.x + paletteRadius * (float)Math.cos(-mAngleStart + numberAngleTurn*numberInPalette); //+ mWidth/2;
		pos.y = palettePosition.y - paletteRadius * (float)Math.sin(-mAngleStart + numberAngleTurn*numberInPalette); //+ mHeight/2;
		return pos;
	}

	@Override
	public void initGraphic() {
		mGraphic = new D3Quad(mWidth, mHeight);
		mGraphic.setColor(TheHuntRenderer.bgColor);
		initGraphic(mGraphic);
		mTextGraphic = new PaletteText(mText);
		getSpriteManager().putText(mTextGraphic);
	}

	public void update(PointF palettePosition) {
		setPosition(new PointF(palettePosition.x + mRelativePos.x, palettePosition.y + mRelativePos.y));
		mTextGraphic.setPosition(getPosition().x, getPosition().y, 0);
		super.update();
	}

	public void hide() {
//		mTextGraphic.setColor(bgActiveColor[0], bgActiveColor[1], bgActiveColor[2]);
		mTextGraphic.setAlpha(0);
		mGraphic.setColor(bgActiveColor);
		mGraphic.setFaded();
	}
	
	public void show() {
		setActive(false);
		mTextGraphic.noFade();
	}

	public void setActive(boolean active) {
		Log.v(TAG, "Set to active " + active + " " + mText);
		if (active) {
			mGraphic.setColor(bgActiveColor);
			mTextGraphic.setColor(1, 1, 1);
		}
		else {
			mGraphic.setColor(bgInactiveColor);
			mTextGraphic.setColor(0, 0, 0);
		}
	}
	
	@Override
	public boolean contains(PointF point) {
		return D3Maths.rectContains(getX(), getY(), mWidth, mHeight, point.x, point.y);
	}

	public String getText() {
		return mText;
	}
}
