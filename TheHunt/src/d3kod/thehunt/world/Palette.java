package d3kod.thehunt.world;

import android.graphics.PointF;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Circle;
import d3kod.thehunt.world.tools.CatchNet;
import d3kod.thehunt.world.tools.D3CatchNet;
import d3kod.thehunt.world.tools.Tool;

public class Palette extends D3Sprite {

	private float[] mHudProjMatrix;
	private float[] mHudViewMatrix;
	private float mTouchRadius = 0.06f;
	private PaletteElement net, knife;
	private PaletteElement mActiveElement;
	private Camera mCamera;
	private boolean mHidden;

	public Palette(PointF position, SpriteManager d3gles20, float[] hudProjMatrix, float[] hudViewMatrix, Camera camera) {
		super(position, d3gles20);
		mHudProjMatrix = hudProjMatrix;
		mHudViewMatrix = hudViewMatrix;
		net = new PaletteElement(position, mTouchRadius, 0, "Net", d3gles20);
		knife = new PaletteElement(position, mTouchRadius, 1, "Knife", d3gles20);
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
		if (contains(touch)) {
			net.setActive(false);
			knife.setActive(false);
			return true;
		}
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
