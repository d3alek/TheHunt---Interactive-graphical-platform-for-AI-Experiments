package com.primalpond.hunt.world;

import android.graphics.PointF;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Shape;

public class DummySprite extends D3Sprite {

	private D3Shape mShape;

	public DummySprite(SpriteManager spriteManager, D3Shape shape) {
		super(new PointF(0, 0), spriteManager);
		mShape = shape;
	}

	@Override
	public void initGraphic() {
		initGraphic(mShape);
	}

}
