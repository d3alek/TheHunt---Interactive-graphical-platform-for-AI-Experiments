package d3kod.thehunt.agent.prey;

import java.nio.FloatBuffer;

import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.extra.Utilities;

public class TailGraphic extends BodyPartGraphic {
	
	protected final static float[] rightFinStart = { 0.0f, 0.0f, 0.0f };
	protected final static float[] rightFinB = { 0.55f, -0.4f, 0.0f };
	protected final static float[] rightFinC = { 0.7f, -0.6f, 0.0f };
	protected final static float[] rightFinEnd = { 0.8f, -1.0f, 0.0f };
	
	protected final static float[] leftFinStart = { rightFinStart[0], rightFinStart[1], 0.0f };
	protected final static float[] leftFinB = { -rightFinB[0], rightFinB[1], 0.0f };
	protected final static float[] leftFinC = { -rightFinC[0], rightFinC[1], 0.0f };
	protected final static float[] leftFinEnd = { -rightFinEnd[0], rightFinEnd[1], 0.0f };

	private float[] leftFinVerticesData;
	private float[] rightFinVerticesData;
	private FloatBuffer rightFinVertexBuffer;

	public TailGraphic(D3Prey graphic, BodyPart bodyPart, float size) {
		super(graphic, bodyPart, size);
		rightFinVerticesData = calcRightFinVerticesData();
		rightFinVertexBuffer = Utilities.newFloatBuffer(rightFinVerticesData);

	}

	@Override
	protected float[] calcVerticesData() {
		leftFinVerticesData = calcLeftFinVerticesData();
		return leftFinVerticesData;
	}

	private float[] calcRightFinVerticesData() {
		return D3Maths.quadBezierCurveVertices(
				rightFinStart, rightFinB, rightFinC, rightFinEnd, mDetailsStep, mSize);
	}

	private float[] calcLeftFinVerticesData() {
		return D3Maths.quadBezierCurveVertices(
				leftFinStart, leftFinB, leftFinC, leftFinEnd, mDetailsStep, mSize);
	}
	
	@Override
	public void draw(float[] modelMatrix, float[] mVMatrix, float[] mProjMatrix) {
		super.draw(modelMatrix, mVMatrix, mProjMatrix);
        
        mGraphic.drawBuffer(rightFinVertexBuffer, modelMatrix);
	}

	@Override
	public void update(float interpolation) {
		// TODO Auto-generated method stub
		
	}
}
