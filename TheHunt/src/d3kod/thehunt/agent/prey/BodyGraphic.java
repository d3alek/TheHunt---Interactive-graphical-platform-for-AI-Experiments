package d3kod.thehunt.agent.prey;

import java.nio.FloatBuffer;

import android.opengl.Matrix;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.sprite.SpriteManager;

public class BodyGraphic extends BodyPartGraphic {
	public static boolean angleInterpolation = true;
	
	// Body

	protected static final float[] bodyStart4 = { 0, 0.5f, 0, 0};
	protected static final float[] bodyB4 = {0, 0.2f, 0, 0};
	protected static final float[] bodyC4 = {0, -0.2f, 0, 0};
	protected static final float[] bodyEnd4 = { 0, -0.5f, 0, 0};

	// Ribs

	protected final float[] ribA = { -0.5f, -0.2f , 0 };
	protected final float[] ribB = { -0.25f, 0 , 0 };
	protected final float[] ribC = { 0.25f, 0 , 0 };
	protected final float[] ribD = { 0.5f, -0.2f , 0 };	
	
	private float[] ribVerticesData;
	private int ribVerticesNum;
	private FloatBuffer ribVertexBuffer;

	private int bodyVerticesNum;

	private int rib1PosIndex;

	private int rib2PosIndex;

	private float[] mBodyStartRMatrix = new float[16];

	private float[] mBodyBRMatrix = new float[16];

	private float[] mBodyCRMatrix = new float[16];

	private float[] mBodyEndRMatrix = new float[16];

	private Body mBodyPartCasted;

	private float bodyStartAnglePredicted;

	private float bodyCAnglePredicted;

	private float bodyBAnglePredicted;

	private float bodyEndAnglePredicted;

	protected float[] bodyStartRot = new float[4];
	protected float[] bodyBRot = new float[4];
	protected float[] bodyCRot = new float[4];
	protected float[] bodyEndRot = new float[4];
	
	private float[] rib1Pos = new float[3];
	private float[] rib2Pos = new float[3];	
	
	private float[] mRibsModelMatrix = new float[16];
	
	protected final float ribSizeAdj = 0.7f;
	
	public BodyGraphic(D3Prey graphic, BodyPart bodyPart, float size) {
		super(graphic, bodyPart, size);
		ribVerticesData = caclRibVerticesData();

		ribVerticesNum = ribVerticesData.length / SpriteManager.COORDS_PER_VERTEX;
		ribVertexBuffer = Utilities.newFloatBuffer(ribVerticesData);

		bodyVerticesNum = mVerticesData.length/SpriteManager.COORDS_PER_VERTEX;
//		bodyVertexBuffer = Utilities.newFloatBuffer(bodyVerticesData);

		rib1PosIndex = (1*bodyVerticesNum/4)*SpriteManager.COORDS_PER_VERTEX;
		rib2PosIndex = (3*bodyVerticesNum/4-2)*SpriteManager.COORDS_PER_VERTEX;
	}

	@Override
	protected float[] calcVerticesData() {
		return D3Maths.quadBezierCurveVertices(bodyStart4, bodyB4, bodyC4, bodyEnd4, mDetailsStep, mSize);
	}
	
	private float[] caclRibVerticesData() {
		return D3Maths.quadBezierCurveVertices(
				ribA, ribB, ribC, ribD, mDetailsStep, ribSizeAdj*mSize);
	}

	private void updateBodyVertexBuffer() {
        Matrix.setIdentityM(mBodyStartRMatrix, 0);
        Matrix.setIdentityM(mBodyBRMatrix, 0);
        Matrix.setIdentityM(mBodyCRMatrix, 0);
        Matrix.setIdentityM(mBodyEndRMatrix, 0);
        Matrix.rotateM(mBodyStartRMatrix, 0, bodyStartAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mBodyBRMatrix, 0, bodyBAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mBodyCRMatrix, 0, bodyCAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mBodyEndRMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
        
		Matrix.multiplyMV(bodyStartRot, 0, mBodyStartRMatrix, 0, bodyStart4, 0);
		Matrix.multiplyMV(bodyBRot, 0, mBodyBRMatrix, 0, bodyB4, 0);
		Matrix.multiplyMV(bodyCRot, 0, mBodyCRMatrix, 0, bodyC4, 0);
		Matrix.multiplyMV(bodyEndRot, 0, mBodyEndRMatrix, 0, bodyEnd4, 0);
		mVerticesData = D3Maths.quadBezierCurveVertices(bodyStartRot, bodyBRot, bodyCRot, bodyEndRot, mDetailsStep, mSize);
		mVertexBuffer.put(mVerticesData).position(0);
	}

	@Override
	public void update(float interpolation) {
		updateBodyVertexBuffer();
		mBodyPartCasted = (Body)mBodyPart;
		if (angleInterpolation) {
			bodyStartAnglePredicted = mBodyPartCasted.bodyStartAngle + mBodyPartCasted.bodyStartAngleRot * interpolation;
			bodyBAnglePredicted = mBodyPartCasted.bodyBAngle + mBodyPartCasted.bodyBAngleRot * interpolation;
			bodyCAnglePredicted = mBodyPartCasted.bodyCAngle + mBodyPartCasted.bodyCAngleRot * interpolation;
			bodyEndAnglePredicted = mBodyPartCasted.bodyEndAngle + mBodyPartCasted.bodyEndAngleRot * interpolation;
		}
		else {
			bodyStartAnglePredicted = mBodyPartCasted.bodyStartAngle;
			bodyBAnglePredicted = mBodyPartCasted.bodyBAngle;
			bodyCAnglePredicted = mBodyPartCasted.bodyCAngle;
			bodyEndAnglePredicted = mBodyPartCasted.bodyEndAngle;
		}
	}
	
	@Override
	public void draw(float[] modelMatrix, float[] mVMatrix, float[] mProjMatrix) {
        
		super.draw(modelMatrix, mVMatrix, mProjMatrix);
		rib1Pos[0] = mVerticesData[rib1PosIndex];
        rib1Pos[1] = mVerticesData[rib1PosIndex + 1];
        
        rib2Pos[0] = mVerticesData[rib2PosIndex];
        rib2Pos[1] = mVerticesData[rib2PosIndex + 1];		
        
        Matrix.translateM(mRibsModelMatrix, 0, modelMatrix, 0, rib1Pos[0], rib1Pos[1], rib1Pos[2]);
        Matrix.rotateM(mRibsModelMatrix, 0, bodyBAnglePredicted, 0, 0, 1);
        mGraphic.drawBuffer(ribVertexBuffer, mRibsModelMatrix);
        
        Matrix.translateM(mRibsModelMatrix, 0, modelMatrix, 0, rib2Pos[0], rib2Pos[1], rib2Pos[2]);
        Matrix.rotateM(mRibsModelMatrix, 0, bodyCAnglePredicted, 0, 0, 1);
        mGraphic.drawBuffer(ribVertexBuffer, mRibsModelMatrix);
        
	}	
	
	@Override
	public float getEndAnglePredicted() {
		return bodyEndAnglePredicted;
	}
	@Override
	public float getStartAnglePredicted() {
		return bodyStartAnglePredicted; 
	}

}
