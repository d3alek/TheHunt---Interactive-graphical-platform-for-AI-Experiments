package com.primalpond.hunt.world;

import com.primalpond.hunt.MyApplication;
import com.primalpond.hunt.world.logic.TheHuntRenderer;
import com.primalpond.hunt.world.tools.CatchNet;
import com.primalpond.hunt.world.tools.D3CatchNet;
import com.primalpond.hunt.world.tools.Tool;

import android.graphics.PointF;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Circle;

public class Palette extends D3Sprite {

	private static final float TOUCH_RADIUS_PX = MyApplication.TOUCH_RADIUS_PX;
	private float[] mHudProjMatrix;
	private float[] mHudViewMatrix;
	private float mTouchRadius;
	public PaletteElement net, knife;
	private PaletteElement mActiveElement;
	private Camera mCamera;
	private boolean mHidden;
	public float mPosOffsetY = mTouchRadius/2;
	public float mPosOffsetX = 0;

	public Palette(PointF position, SpriteManager d3gles20, float[] hudProjMatrix, float[] hudViewMatrix, Camera camera) {
		super(position, d3gles20);
		mTouchRadius = TOUCH_RADIUS_PX*2/TheHuntRenderer.mScreenHeightPx;
		mHudProjMatrix = hudProjMatrix;
		mHudViewMatrix = hudViewMatrix;
		net = new PaletteElement(position, mTouchRadius*2/3f, 0, "Net", d3gles20);
		knife = new PaletteElement(position, mTouchRadius*2/3f, 1, "Knife", d3gles20);
		mActiveElement = null;
		mCamera = camera;
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
		mCamera.addScaleDependentSprite(this);
		mCamera.addScaleDependentSprite(net);
		mCamera.addScaleDependentSprite(knife);
//		net.setActive();
	}

	public void show(Class<? extends Tool> activeToolClass) {
		mHidden = false;
//		mGraphic.noFade();
		
		net.show();
		knife.show();
		
		if (activeToolClass == CatchNet.class) {
			net.setPermanentlyActive();
		}
		else {
			knife.setPermanentlyActive();
		}
		mActiveElement = null;
	}

	public void hide() {
		mGraphic.setFaded();
		mHidden = true;
		net.hide();
		knife.hide();
	}

	public boolean handleTouch(PointF touch) {
		if (mHidden) return false;
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
		if (contains(touch)) {
			net.setActive(false);
			knife.setActive(false);
			return true;
		}
//		if (net.handleTouch(touch)) return true;
//		if (knife.handleTouch(touch)) return true;
		hide();
		return false;
	}
	
	@Override
	public void update() {
		super.update();
		PointF positionToTellElements = new PointF();
		positionToTellElements.set(getPosition());
		positionToTellElements.offset(-mPosOffsetX, -mPosOffsetY);
		net.update(positionToTellElements);
		knife.update(positionToTellElements);
	}

	public String getActiveElement() {
		if (mActiveElement != null) {
			return mActiveElement.getText();
		}
		return null;
	}
	
	@Override
	public void setPosition(PointF position) {
		position.offset(mPosOffsetX, mPosOffsetY);
		super.setPosition(position);
	}
	
	public boolean isShown() {
		return !mHidden;
	}

}
