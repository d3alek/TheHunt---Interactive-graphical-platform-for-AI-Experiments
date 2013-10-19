package com.primalpond.hunt.world.logic;

import com.primalpond.hunt.world.Camera;

import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.shader.programs.Program;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.sprite.shapes.D3Quad;
import d3kod.graphics.text.GLText;

public class TheHuntMenu extends D3Sprite {

	private float mWidth = 0.3f;
	private float mHeight = 0.5f;
//	private Program mProgram;
	private D3FadingText mButtonResume;
	private GLText mGLText;
	private static final float[] bgHideColor = {0.0f, 0.0f, 0.0f, 1.0f};
	private static final float[] bgShowColor = {1.0f, 1.0f, 1.0f, 1.0f};
	private static final String TAG = "TheHuntMenu";
	private boolean shown = false;
	private boolean firstUp = false;
	private float[] mProjMatrix;
	private float[] mViewMatrix;
	private Camera mCamera;

	public TheHuntMenu(SpriteManager d3gles20, Camera camera) {
		super(new PointF(0, 0), null);
//		mProgram = d3gles20.getDefaultProgram();
		mGLText = d3gles20.getTextManager();
		mProjMatrix = camera.getUnscaledProjMatrix();
		mViewMatrix = camera.toCenteredViewMatrix();
		mCamera = camera;
	}

	public void show() {
//		mGraphic.setColor(bgShowColor);
//		mGraphic.noFade();
		shown = true;
		firstUp = false;
	}
	
	public void hide() {
//		mGraphic.setColor(bgHideColor);
//		mGraphic.setFaded();
		shown = false;
	}

	@Override
	public void initGraphic() {
//		mGraphic = new D3Quad(mWidth, mHeight);
//		mGraphic.setProgram(mProgram);
		mButtonResume = new D3FadingText("resume", 3, 0);
		mButtonResume.setCentered(true);
		mButtonResume.setPosition(0, 0, 0);
		hide();
	}
	
	@Override
	public void draw(float[] vMatrix, float[] projMatrix, float interpolation) {
//		super.draw(vMatrix, projMatrix, interpolation);
//		float[] vpMatrix = new float[16];
		mButtonResume.draw(mGLText, projMatrix, vMatrix);
	}

	public boolean handleTouch(float x, float y, int action) {
		// on first up do nothing
		// otherwise return false if not in rectange
		PointF location = mCamera.fromScreenToWorld(x, y, mViewMatrix, mProjMatrix);
		Log.v(TAG, "Menu Handling Touch " + action + " " + firstUp);
		if (!firstUp && action == MotionEvent.ACTION_UP) {
			firstUp = true;
			return true;
		}
		if (firstUp && action == MotionEvent.ACTION_UP && 
				D3Maths.rectContains(mButtonResume.getX(), 
				mButtonResume.getY(), mButtonResume.getLength(mGLText), 
				mButtonResume.getHeight(mGLText), location.x, location.y)) {
			Log.v(TAG, "Resume pressed");
			return false;
		}
		return true;
	}

	public boolean notShown() {
		// TODO Auto-generated method stub
		return !shown;
	}

	public void draw() {
		this.draw(mViewMatrix, mProjMatrix, 0);
		
	}
	
}