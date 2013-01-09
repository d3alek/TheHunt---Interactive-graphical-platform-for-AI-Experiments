package d3kod.thehunt.prey;

import android.graphics.PointF;
import d3kod.d3gles20.D3GLES20;

public interface Prey {

	void initGraphics(D3GLES20 mD3GLES20);

	PointF getPosition();

	void update(float f, float g);

	boolean getCaught();

	void release();

	PointF getPredictedPosition();

	void clearGraphic();

}