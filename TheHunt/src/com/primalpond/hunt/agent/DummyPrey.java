package com.primalpond.hunt.agent;

import com.primalpond.hunt.world.environment.Environment;

import android.graphics.PointF;
import d3kod.graphics.sprite.SpriteManager;

public class DummyPrey extends Agent {

	public DummyPrey(Environment env, SpriteManager d3gles20) {
		super(env, d3gles20);
	}

	public void initGraphic() {
	}

	public PointF getPosition() {
		return null;
	}

	public void update(float f, float g) {
	}

	public boolean getCaught() {
		return false;
	}

	public void release() {
	}

	public PointF getPredictedPosition() {
		return null;
	}

	public void clearGraphic() {
	}

	@Override
	public void setCaught(boolean b) {
		
	}
}
