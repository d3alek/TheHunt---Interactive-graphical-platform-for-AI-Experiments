package d3kod.thehunt.prey;

import android.graphics.PointF;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Sprite;

public abstract class Prey extends D3Sprite {

	public abstract void initGraphic();
//
//	PointF getPosition();

//	void update(float f, float g);

	public Prey(D3GLES20 d3gles20) {
		super(new PointF(0, 0), d3gles20);
		// TODO Auto-generated constructor stub
	}

	abstract boolean getCaught();

	abstract void release();

	public abstract PointF getPredictedPosition();

//	void clearGraphic();

}