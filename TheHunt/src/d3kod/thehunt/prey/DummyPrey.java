package d3kod.thehunt.prey;

import android.graphics.PointF;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.environment.Environment;

public class DummyPrey extends Prey {

	public DummyPrey(Environment env, D3GLES20 d3gles20) {
		super(env, d3gles20);
	}

	public void initGraphic() {
		// TODO Auto-generated method stub
		
	}

	public PointF getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(float f, float g) {
		// TODO Auto-generated method stub
		
	}

	public boolean getCaught() {
		return false;
	}

	public void release() {
		// TODO Auto-generated method stub
		
	}

	public PointF getPredictedPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearGraphic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCaught(boolean b) {
		
	}
}
