package d3kod.thehunt.world;

import android.graphics.PointF;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Circle;
import d3kod.thehunt.world.tools.D3CatchNet;

public class Palette extends D3Sprite {

	private float[] mHudProjMatrix;
	private float[] mHudViewMatrix;
	private float mTouchRadius = 0.08f;

	public Palette(PointF position, SpriteManager d3gles20, float[] hudProjMatrix, float[] hudViewMatrix) {
		super(position, d3gles20);
		mHudProjMatrix = hudProjMatrix;
		mHudViewMatrix = hudViewMatrix;
	}

	@Override
	public void initGraphic() {
		// TODO Auto-generated method stub
//		mGraphic = new HUDCircle(mTouchRadius , new PointF(0, 0), mHudProjMatmrix, mHudViewMatrix);
		mGraphic = new HUDCircle(mTouchRadius , new PointF(0, 0), mHudProjMatrix, mHudViewMatrix);
//		mGraphic = new D3Circle(mTouchRadius, new float[]{0f,0f,0f,1.0f}, 100);
		initGraphic(mGraphic);
//		mGraphic.setPosition(0, 0);
	}

	public void show() {
		mGraphic.noFade();
	}

	public void hide() {
		mGraphic.setFaded();
	}
	
	

}
