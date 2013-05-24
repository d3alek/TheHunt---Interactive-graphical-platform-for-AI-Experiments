package d3kod.thehunt.world;

import android.graphics.PointF;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Circle;
import d3kod.thehunt.world.tools.D3CatchNet;

public class Palette extends D3Sprite {

	private float[] mHudProjMatrix;
	private float[] mHudViewMatrix;
	private float mTouchRadius = 0.06f;
	private PaletteElement net, knife;
	private PaletteElement mActiveElement;

	public Palette(PointF position, SpriteManager d3gles20, float[] hudProjMatrix, float[] hudViewMatrix) {
		super(position, d3gles20);
		mHudProjMatrix = hudProjMatrix;
		mHudViewMatrix = hudViewMatrix;
		net = new PaletteElement(position, mTouchRadius, 0, "Net", d3gles20);
		knife = new PaletteElement(position, mTouchRadius, 1, "Knife", d3gles20);
		mActiveElement = null;
	}

	@Override
	public void initGraphic() {
//		mGraphic = new HUDCircle(mTouchRadius , new PointF(0, 0), mHudProjMatmrix, mHudViewMatrix);
		mGraphic = new HUDCircle(mTouchRadius , new PointF(0, 0), mHudProjMatrix, mHudViewMatrix);
//		mGraphic = new D3Circle(mTouchRadius, new float[]{0f,0f,0f,1.0f}, 100);
		initGraphic(mGraphic);
		mGraphic.setPosition(0, 0);
		net.initGraphic();
		knife.initGraphic();
//		net.setActive();
	}

	public void show() {
		mGraphic.noFade();
		net.show();
		knife.show();
		mActiveElement = null;
	}

	public void hide() {
		mGraphic.setFaded();
		net.hide();
		knife.hide();
	}

	public boolean handleTouch(PointF touch) {
		if (mGraphic.fadeDone()) return false;
		if (net.contains(touch)) {
			net.setActive(true);
			knife.setActive(false);
			mActiveElement = net;
			return true;
		}
		if (knife.contains(touch)) {
			knife.setActive(true);
			net.setActive(false);
			mActiveElement = knife;
			return true;
		}
		if (contains(touch)) return true;
//		if (net.handleTouch(touch)) return true;
//		if (knife.handleTouch(touch)) return true;
		hide();
		return false;
	}
	
	@Override
	public void update() {
		super.update();
		net.update(getPosition());
		knife.update(getPosition());
	}

	public String getActiveElement() {
		if (mActiveElement != null) {
			return mActiveElement.getText();
		}
		return null;
	}
	

}
