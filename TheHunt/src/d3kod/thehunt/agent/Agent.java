package d3kod.thehunt.agent;

import android.graphics.PointF;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.texture.TextureManager;
import d3kod.thehunt.world.environment.Environment;

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
	}

	public int getEnergy() {
		return -1;
	} 
}