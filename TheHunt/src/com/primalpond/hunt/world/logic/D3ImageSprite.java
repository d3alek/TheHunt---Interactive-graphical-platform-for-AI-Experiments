package com.primalpond.hunt.world.logic;

import android.graphics.PointF;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Circle;

public class D3ImageSprite extends D3Sprite {

	public D3ImageSprite(PointF position, String resourceName, SpriteManager d3gles20) {
		super(position, d3gles20);
	}

	@Override
	public void initGraphic() {
		mGraphic = new D3Circle(0.08f, new float[]{0, 0, 0}, 30);
		super.initGraphic(mGraphic);
	}

}
