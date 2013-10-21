package com.primalpond.hunt.world;

import com.primalpond.hunt.world.logic.TheHuntRenderer;

import android.graphics.PointF;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.sprite.shapes.D3Image;
import d3kod.graphics.texture.TextureInfo;
import d3kod.graphics.texture.TextureManager.Texture;

public class PaletteElement extends D3Sprite {
	class PaletteText extends D3FadingText {

		public PaletteText(String text) {
			super(text, 2.0f, 0);
			setCentered(true);
		}

	}

//	private static final float[] bgActiveColor = {0.0f, 0.0f, 0.0f, 1.0f};
	private static final float[] defaultOpacity = {0.0f, 0.0f, 0.0f, 0.3f};

	private static final String TAG = "PaletteElement";

	private static final float[] bgInactiveColor = TheHuntRenderer.bgColor;

//	private static final float[] textActiveColor = {1.0f, 1.0f, 1.0f, 1.0f};

//	private static float mWidth = 0.1f;
//	private static float mHeight = 0.06f;
	private static float mWidth = 0.15f;
	private static float mHeight = 0.15f;
	private static double mAngleStart = Math.toRadians(0);
	private static double numberAngleTurn = Math.toRadians(45);
	private String mText;
	private PointF mRelativePos;
//	private D3Image mTextGraphic;
	private PointF mPos = new PointF();

	private float mAngle;

	private PointF mPalettePosition;

	private float mDistFromPaletteCenter;
	D3Image mGraphic;

	private boolean mPermanentlyActive;


	public PaletteElement(PointF palettePosition, float paletteRadius, int numberInPalette, String text, SpriteManager d3gles20) {
		super(calcPosition(palettePosition, paletteRadius, numberInPalette), d3gles20);
//		mPos = calcPosition(palettePosition, paletteRadius, numberInPalette);
		mText = text;
		mPalettePosition = palettePosition;
		mDistFromPaletteCenter = paletteRadius*2f;
		switch(numberInPalette) {
		case 0: mAngle = 135; break;
		case 1: mAngle = -135; break;
		}
	}

	// not used!!!
	private static PointF calcPosition(PointF palettePosition, float paletteRadius,
			int numberInPalette) {
		PointF pos = new PointF();
		//TODO: fix once you have labels
		pos.x = palettePosition.x + paletteRadius * (float)Math.cos(-mAngleStart + numberAngleTurn*numberInPalette) + mWidth/4;
		pos.y = palettePosition.y - paletteRadius * (float)Math.sin(-mAngleStart + numberAngleTurn*numberInPalette) + mHeight/4;
		return pos;
	}

	@Override
	public void initGraphic() {
//		mGraphic = new D3TriangleFill(mWidth, mHeight);
//		mGraphic.setColor(TheHuntRenderer.bgColor);
		TextureInfo textureInfo;
		if (mText == "Net") {
			textureInfo = getSpriteManager().getTextureManager().getTextureInfo(Texture.ICON_NET);
		}
		else if (mText == "Knife") {
			textureInfo = getSpriteManager().getTextureManager().getTextureInfo(Texture.ICON_KNIFE);
		}
		else {
			textureInfo = null; //TODO: load ??? graphic instead
			Log.e(TAG, "Unknown icon for name " + mText);
		}
		mGraphic = new D3Image(textureInfo, mWidth, getSpriteManager().getShaderManager());
		initGraphic(mGraphic);
		mGraphic.setColor(defaultOpacity);
//		mTextGraphic = new PaletteText(mText);
		
//		mTextGraphic = new D3Image(textureHandle, getSpriteManager().getShaderManager());
//		getSpriteManager().
//		mTextGraphic.setScale()
//		getSpriteManager().putText(mTextGraphic);
	}

	public void update(PointF palettePosition) {
		mRelativePos = new PointF((float)Math.cos(Math.toRadians(mAngle-90))
				*mDistFromPaletteCenter*mGraphic.getScale(), 
				(float)Math.sin(Math.toRadians(mAngle-90))
				*mDistFromPaletteCenter*mGraphic.getScale());
		setPosition(new PointF(palettePosition.x + mRelativePos.x, palettePosition.y + mRelativePos.y));
//		mTextGraphic.setPosition(getPosition().x, getPosition().y, 0);
//		mGraphic.setPosition(getX(), getY(), 0);
//		mTextGraphic.setScale(mGraphic.getScale());;
		super.update();
	}

	public void hide() {
//		mTextGraphic.setColor(bgActiveColor[0], bgActiveColor[1], bgActiveColor[2]);
//		mTextGraphic.setAlpha(0);
//		mTextGraphic.setFaded();
//		mGraphic.setColor(bgActiveColor);
		setActive(false);
		mGraphic.setFaded();
	}
	
	public void show() {
//		mTextGraphic.noFade();
		mGraphic.noFade();
		mPermanentlyActive = false;
		setActive(false);
//		mGraphic.setColor(defaultOpacity);
	}

	public void setActive(boolean active) {
//		Log.v(TAG, "Set to active " + active + " " + mText);
		if (mPermanentlyActive) return;
		if (active) {
//			mGraphic.setColor(bgActiveColor);
			mGraphic.noFade();
//			mGraphic.setInverted(true);
//			mTextGraphic.setColor(1, 1, 1);
//			mTextGraphic.noFade();
		}
		else {
//			mGraphic.setColor(bgInactiveColor);
			mGraphic.setColor(defaultOpacity);
			mGraphic.setInverted(false);
//			mTextGraphic.setColor(0, 0, 0);
		}
	}
	
	@Override
	public boolean contains(PointF point) {
		return D3Maths.rectContains(getX(), getY(), mWidth*mGraphic.getScale(), mHeight*mGraphic.getScale(), point.x, point.y);
	}

	public String getText() {
		return mText;
	}
	
	@Override
	public void draw(float[] vMatrix, float[] projMatrix, float interpolation) {
		super.draw(vMatrix, projMatrix, interpolation);
//		mTextGraphic.draw(vMatrix, projMatrix);
	}

	public void setPermanentlyActive() {
		setActive(true);
		mPermanentlyActive = true;
	}
}
