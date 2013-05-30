package d3kod.thehunt.agent.prey;

import java.nio.FloatBuffer;

import d3kod.graphics.extra.Utilities;

public abstract class BodyPartGraphic {
	protected float mSize;
	protected D3Prey mGraphic;
	protected float mDetailsStep;
	protected float[] mVerticesData;
	protected FloatBuffer mVertexBuffer;
	protected BodyPart mBodyPart;
	private static final String TAG = "BodyPartGraphic";

	public BodyPartGraphic(D3Prey graphic, BodyPart bodyPart, float size) {
		mSize = size;
		mGraphic = graphic;
		mBodyPart = bodyPart;
		mDetailsStep = graphic.getDetailsStep();
		mVerticesData = calcVerticesData();
		mVertexBuffer = Utilities.newFloatBuffer(mVerticesData);
	}

	protected abstract float[] calcVerticesData();

	public void draw(float[] modelMatrix) {
        mGraphic.drawBuffer(mVertexBuffer, modelMatrix);
	}

	public abstract void update(float interpolation);

	public float getEndAnglePredicted() {
		return 0;
	}

	public float getStartAnglePredicted() {
		// TODO Auto-generated method stub
		return 0;
	}

}
