package com.primalpond.hunt.world;

import com.google.android.gms.internal.ce;

import android.graphics.PointF;
import android.util.Log;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.shapes.D3Circle;
import d3kod.graphics.sprite.shapes.D3Shape;
import d3kod.graphics.text.GLText;

public class HUDCircle extends D3Circle {
	private static final int VERTICES_NUM = 30;
	private static final float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
//	private float[] mProjMatrix;
//	private float[] mViewMatrix;

	public HUDCircle(float radius, PointF center, float[] hudProjMatrix, float[] hudViewMatrix) {
		super(radius, color, VERTICES_NUM);
		setPosition(center.x, center.y);
//		mProjMatrix = hudProjMatrix;
//		mViewMatrix = hudViewMatrix;
	}
//	@Override
//	public void draw(float[] projMatrix, float[] viewMatrix) {
//		//			float[] idMatrix = new float[16];
//		//			Matrix.setIdentityM(idMatrix, 0);
////		Log.v("HUDCircle", "Drawing");
//		//TODO: scale!
//		super.draw(projMatrix, viewMatrix);
//	}

	@Override
	public float getRadius() {
		return super.getRadius()*getScale();
	}

}
