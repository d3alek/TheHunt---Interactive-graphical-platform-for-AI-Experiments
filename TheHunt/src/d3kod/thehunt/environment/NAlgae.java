package d3kod.thehunt.environment;

import d3kod.d3gles20.D3GLES20;
import android.graphics.PointF;

public class NAlgae extends FloatingObject {

	private int mSize;

	public NAlgae(int n, PointF pos) {
		super(pos.x, pos.y, Type.ALGAE);
		mSize = n;
	}

	public void setGraphic(D3GLES20 d3gles20) {
		super.setGraphic(new D3NAlgae(d3gles20.getShaderManager()), d3gles20);
		updateGraphicSize();
	}

	private void updateGraphicSize() {
		((D3NAlgae)super.getGraphic()).setSizeCategory(mSize);
	}
	
}
