package d3kod.thehunt.prey;

import android.graphics.PointF;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Sprite;
import d3kod.thehunt.environment.Environment;

public abstract class Prey extends D3Sprite {

	public abstract void initGraphic();
//
//	PointF getPosition();

//	void update(float f, float g);

	public Prey(Environment env, D3GLES20 d3gles20) {
		super(new PointF(0, 0), d3gles20);
		env.addPrey(this);
	}

	public abstract boolean getCaught();

	public abstract void release();

	public abstract PointF getPredictedPosition();

	public abstract void setCaught(boolean b); 

//	void clearGraphic();

}