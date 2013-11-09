package com.primalpond.hunt.agent;

import org.json.JSONException;
import org.json.JSONObject;

import com.primalpond.hunt.agent.prey.memory.StressLevel;
import com.primalpond.hunt.world.environment.Environment;
import com.primalpond.hunt.world.environment.FloatingObject;

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

	@Override
	public boolean isHidden() {
		return false;
	}

	public JSONObject toJSON() throws JSONException {
		return null;
	}

	@Override
	public void setStressLevel(StressLevel stressLevel) {
		
	}

	@Override
	public void setTargetFood(FloatingObject fo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reduceEnergy(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseEnergy(int i) {
		// TODO Auto-generated method stub
		
	}
}
