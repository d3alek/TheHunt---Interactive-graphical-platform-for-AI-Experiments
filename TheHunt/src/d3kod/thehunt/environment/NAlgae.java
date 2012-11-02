package d3kod.thehunt.environment;

import d3kod.d3gles20.D3GLES20;
import android.graphics.PointF;
import android.util.FloatMath;

public class NAlgae extends FloatingObject {

	private static final float ALGAE_START_SPEED = 0.005f;
	private int mSize;

	public NAlgae(int n, PointF pos, float dirAngle) {
		super(pos.x, pos.y, Type.ALGAE);
		mSize = n;
		setVelocity(FloatMath.cos(dirAngle)*ALGAE_START_SPEED, FloatMath.sin(dirAngle)*ALGAE_START_SPEED);
	}

	public void setGraphic(D3GLES20 d3gles20) {
		super.setGraphic(new D3NAlgae(d3gles20.getShaderManager()), d3gles20);
		updateGraphicSize();
	}

	private void updateGraphicSize() {
		((D3NAlgae)super.getGraphic()).setSizeCategory(mSize);
	}
	
}
