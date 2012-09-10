package d3kod.thehunt.environment;

import java.util.Random;

import d3kod.d3gles20.D3GLES20;

class Algae extends FloatingObject {

	private D3Algae mGraphic;
	
	public Algae(float x, float y, D3GLES20 d3GLES20) {
		super(x, y, Type.ALGAE, d3GLES20);
		mGraphic = new D3Algae(d3GLES20.getShaderManager());
//		mGraphic = new D3AlgaeHatching(textureDataHandle);
		setGraphic(d3GLES20.putShape(mGraphic));
	}
	
	public void update() {
//		mGraphic.wiggle();
		
	}
	
}