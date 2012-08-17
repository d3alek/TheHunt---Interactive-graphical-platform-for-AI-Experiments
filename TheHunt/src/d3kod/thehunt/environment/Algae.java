package d3kod.thehunt.environment;

import d3kod.d3gles20.D3GLES20;

class Algae extends FloatingObject {

	private D3Algae mGraphic;

	public Algae(float x, float y, int textureDataHandle) {
		super(x, y, Type.ALGAE);
		mGraphic = new D3Algae(textureDataHandle);
		setGraphic(D3GLES20.putShape(mGraphic));
	}
	
	public void update() {
//		mGraphic.wiggle();
	}
	
}