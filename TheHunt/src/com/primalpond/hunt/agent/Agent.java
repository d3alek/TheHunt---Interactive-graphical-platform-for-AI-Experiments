package com.primalpond.hunt.agent;

import com.primalpond.hunt.world.environment.Environment;

import android.graphics.PointF;
import d3kod.graphics.extra.D3Color;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.texture.TextureManager;

public abstract class Agent extends D3Sprite {

	transient protected TextureManager tm;
	
	transient protected Environment mEnv;

	public abstract void initGraphic();

	public Agent(Environment env, SpriteManager d3gles20) {
		super(new PointF(0, 0), d3gles20);
		env.addPrey(this);
	}

	public abstract boolean getCaught();

	public abstract void release();

	public abstract PointF getPredictedPosition();

	public abstract void setCaught(boolean b);

	public void setTextureManager(TextureManager tm) {
		this.tm = tm;
	}

	public Environment getEnvironment() {
		return mEnv;
	}

	public void setEnvironment(Environment environment) {
		mEnv = environment;
		environment.addPrey(this);
	}

	public int getEnergy() {
		return -1;
	}

	public D3Color getMoodColor() {
		return D3Color.BLACK;
	}

	public String getStateString() {
		return "None";
	} 
}