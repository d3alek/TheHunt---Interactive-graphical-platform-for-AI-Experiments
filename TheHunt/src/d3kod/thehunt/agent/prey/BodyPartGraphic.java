package d3kod.thehunt.agent.prey;

import java.nio.FloatBuffer;

import d3kod.graphics.extra.Utilities;

public abstract class BodyPartGraphic {
	protected float mSize;
	protected D3Prey mGraphic;
	protected float mDetailsStep;
	protected float[] mVerticesData;
	protected FloatBuffer mVertexBuffer;
	private static final String TAG = "BodyPartGraphic";

	public BodyPartGraphic(D3Prey graphic, float size) {
		mSize = size;
		mGraphic = graphic;
		mDetailsStep = graphic.getDetailsStep();
		mVerticesData = calcVerticesData();
		mVertexBuffer = Utilities.newFloatBuffer(mVerticesData);
	}

	protected abstract float[] calcVerticesData();

	public void draw(float[] modelMatrix) {
        mGraphic.drawBuffer(mVertexBuffer, modelMatrix);
	}

}
