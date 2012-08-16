package d3kod.thehunt.environment;

import d3kod.d3gles20.D3GLES20;

class Algae extends FloatingObject {

	public Algae(float x, float y) {
		super(D3GLES20.putShape(new D3Algae()), x, y, Type.ALGAE);
	}
	
}