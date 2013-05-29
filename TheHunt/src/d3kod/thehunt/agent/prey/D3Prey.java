package d3kod.thehunt.agent.prey;

import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.GLES20;
import android.opengl.Matrix;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.shader.programs.AttribVariable;
import d3kod.graphics.shader.programs.Program;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Shape;
import d3kod.thehunt.world.logic.TheHuntRenderer;

public class D3Prey extends D3Shape {
	private static final String TAG = "D3Prey";
	
	protected static float[] preyColor = {
			0.0f, 0.0f, 0.0f, 1.0f };
	protected static float[] preyColorHidden = {
		0.0f, 0.0f, 0.0f, 0.5f };
	private static final float colorFadeSpeed = 0.01f;
	
	public static boolean angleInterpolation = true;
	public static boolean posInterpolation = true;
	protected final float detailsStep = 0.1f;
	protected final int STRIDE_BYTES = SpriteManager.COORDS_PER_VERTEX * Utilities.BYTES_PER_FLOAT;
	public static final float preySize = 0.8f;
	protected static final float bodyLength = 0.1f * preySize;
	
	// Head
	protected static final float headSize = 0.04f * preySize;
	protected final float[] headPosition = {
			0, bodyLength/2 + headSize*0.4f, 0
	};

	// Body

	protected final float[] bodyStart4 = { 0, 0.5f, 0, 0};
	protected final float[] bodyB4 = {0, 0.2f, 0, 0};
	protected final float[] bodyC4 = {0, -0.2f, 0, 0};
	protected final float[] bodyEnd4 = { 0, -0.5f, 0, 0};
	
	// Ribs
	
	protected final float[] ribA = { -0.5f, -0.2f , 0 };
	protected final float[] ribB = { -0.25f, 0 , 0 };
	protected final float[] ribC = { 0.25f, 0 , 0 };
	protected final float[] ribD = { 0.5f, -0.2f , 0 };
	
	protected final float ribSize = 0.07f * preySize;
	
	// Fins
	protected final static float finSize = 0.05f * preySize;
	
//	protected final float[] rightFinStart = { 0.0f, 0.0f, 0.0f };
//	protected final float[] rightFinB = { 0.55f, -0.4f, 0.0f };
//	protected final float[] rightFinC = { 0.7f, -0.6f, 0.0f };
//	protected final float[] rightFinEnd = { 0.8f, -1.0f, 0.0f };
//	
//	protected final float[] leftFinStart = { rightFinStart[0], rightFinStart[1], 0.0f };
//	protected final float[] leftFinB = { -rightFinB[0], rightFinB[1], 0.0f };
//	protected final float[] leftFinC = { -rightFinC[0], rightFinC[1], 0.0f };
//	protected final float[] leftFinEnd = { -rightFinEnd[0], rightFinEnd[1], 0.0f };
	
//	protected int finVerticesNum;
	protected int headVerticesNum;
	private int ribVerticesNum;
	private int bodyVerticesNum;
	
	protected float[] headVerticesData;
	protected float[] leftFinVerticesData;
	protected float[] rightFinVerticesData;
	private float[] ribVerticesData;
	protected float[] eyeVertexData;
	private float[] bodyVerticesData;
	
	protected FloatBuffer eyeVertexBuffer;
	protected FloatBuffer headVertexBuffer;
	protected FloatBuffer bodyVertexBuffer;
	protected FloatBuffer rightFinVertexBuffer;
	protected FloatBuffer leftFinVertexBuffer;
	public FloatBuffer ribVertexBuffer;
	
	public static float preyRadius = (headSize + bodyLength + finSize)/2f;
	
	public static final float[] tailPosition = {
			0, -bodyLength/2, 0
	};
	
	// Shaders
	
	protected final String vertexShaderCode =
			"uniform mat4 u_MVPMatrix;      \n"
		 
		  + "attribute vec4 a_Position;     \n"
		 
		  + "void main()                    \n"
		  + "{                              \n"
		  + "   gl_Position = u_MVPMatrix   \n"
		  + "               * a_Position;   \n"
		  + "}                              \n";


	protected final String fragmentShaderCode =
			  "precision mediump float;       \n"
			+ "uniform vec4 u_Color;          \n"
			+ "void main()                    \n"
			+ "{                              \n"
			+ "   gl_FragColor = u_Color;     \n"
			+ "}                              \n";
	
	private int mColorHandle;
	
	float[] mBodyStartRMatrix = new float[16];
	float[] mBodyBRMatrix = new float[16];
	float[] mBodyCRMatrix = new float[16];
	float[] mBodyEndRMatrix = new float[16];
	
	protected float[] bodyStartRot = new float[4];
	protected float[] bodyBRot = new float[4];
	protected float[] bodyCRot = new float[4];
	protected float[] bodyEndRot = new float[4];
	
	private float[] mRibsModelMatrix = new float[16];
	private float[] mFeetModelMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float[] mHeadModelMatrix = new float[16];
	private float[] mEyeModelMatrix = new float[16];
	
	// depreciated 
	protected int mLeftFootAngle = 0;
	protected int mRightFootAngle = 0;
	
    int rib1PosIndex;
    int rib2PosIndex;
	
	private PreyData mD;

	private HeadGraphic mHeadGraphic;

	private BodyPartGraphic mTailGraphic;
	
	protected D3Prey(PreyData data, Head head, Tail tail) {
		super();
		super.setColor(preyColor);
		super.setDrawType(GLES20.GL_LINE_STRIP);
		mD = data;
		Matrix.setIdentityM(mModelMatrix, 0);
		
		mHeadGraphic = head.getGraphic(this, headSize);
		mTailGraphic = tail.getGraphic(this, finSize);
		
//		leftFinVerticesData = calcLeftFinVerticesData();
//		rightFinVerticesData = calcRightFinVerticesData();
		
		ribVerticesData = caclRibVerticesData();
		
//		finVerticesNum = rightFinVerticesData.length / SpriteManager.COORDS_PER_VERTEX;
		ribVerticesNum = ribVerticesData.length / SpriteManager.COORDS_PER_VERTEX;
		
//		leftFinVertexBuffer = Utilities.newFloatBuffer(leftFinVerticesData);
//		rightFinVertexBuffer = Utilities.newFloatBuffer(rightFinVerticesData);
		ribVertexBuffer = Utilities.newFloatBuffer(ribVerticesData);
		
		bodyVerticesData = D3Maths.quadBezierCurveVertices(bodyStart4, bodyB4, bodyC4, bodyEnd4, detailsStep, bodyLength);
		bodyVerticesNum = bodyVerticesData.length/SpriteManager.COORDS_PER_VERTEX;
		bodyVertexBuffer = Utilities.newFloatBuffer(bodyVerticesData);
		
        rib1PosIndex = (1*bodyVerticesNum/4)*SpriteManager.COORDS_PER_VERTEX;
        rib2PosIndex = (3*bodyVerticesNum/4-2)*SpriteManager.COORDS_PER_VERTEX;
		
	}

	private float[] caclRibVerticesData() {
		return D3Maths.quadBezierCurveVertices(
				ribA, ribB, ribC, ribD, detailsStep, ribSize);
	}

//	private float[] calcRightFinVerticesData() {
//		return D3Maths.quadBezierCurveVertices(
//				rightFinStart, rightFinB, rightFinC, rightFinEnd, detailsStep, finSize);
//	}
//
//	private float[] calcLeftFinVerticesData() {
//		return D3Maths.quadBezierCurveVertices(
//				leftFinStart, leftFinB, leftFinC, leftFinEnd, detailsStep, finSize);
//	}
	
	float bodyStartAnglePredicted, bodyBAnglePredicted, bodyCAnglePredicted, bodyEndAnglePredicted;
    float mPredictedPosX, mPredictedPosY;

	private float[] rib1Pos = new float[3];
	private float[] rib2Pos = new float[3];

	private boolean mMouthOpen;
    
	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix, float interpolation) {
		if (mD.mIsCaught) {
			if (fadeDone()) return;
			fade(colorFadeSpeed);
		}
		
		calcPredicted(interpolation);
        
        updateBodyVertexBuffer();
        
        if (mD.emotionText != null) {
			mD.emotionText.setPosition(mPredictedPosX, mPredictedPosY, bodyStartAnglePredicted);
        }
        
        // Start Drawing
        super.setDrawType(GLES20.GL_LINE_STRIP);
        super.setDrawVMatrix(mVMatrix);
        super.setDrawProjMatrix(mProjMatrix);
        
		super.useProgram();
		
		super.useColor();
        
        // Calculate Model Matrix
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix , 0, mPredictedPosX, mPredictedPosY, 0);
        
		// Body
        super.drawBuffer(bodyVertexBuffer, mModelMatrix);
        
        rib1Pos[0] = bodyVerticesData[rib1PosIndex];
        rib1Pos[1] = bodyVerticesData[rib1PosIndex + 1];
        
        rib2Pos[0] = bodyVerticesData[rib2PosIndex];
        rib2Pos[1] = bodyVerticesData[rib2PosIndex + 1];
        
        Matrix.translateM(mRibsModelMatrix, 0, mModelMatrix, 0, rib1Pos[0], rib1Pos[1], rib1Pos[2]);
        Matrix.rotateM(mRibsModelMatrix, 0, bodyBAnglePredicted, 0, 0, 1);
        super.drawBuffer(ribVertexBuffer, mRibsModelMatrix);
        
        Matrix.translateM(mRibsModelMatrix, 0, mModelMatrix, 0, rib2Pos[0], rib2Pos[1], rib2Pos[2]);
        Matrix.rotateM(mRibsModelMatrix, 0, bodyCAnglePredicted, 0, 0, 1);
        super.drawBuffer(ribVertexBuffer, mRibsModelMatrix);
        
        Matrix.rotateM(mFeetModelMatrix, 0, mModelMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
        Matrix.translateM(mFeetModelMatrix, 0, 
        		tailPosition[0], tailPosition[1], 0);
//        Matrix.rotateM(mFeetModelMatrix, 0, mLeftFootAngle, 0, 0, 1);
//        super.drawBuffer(leftFinVertexBuffer, mFeetModelMatrix);
        mTailGraphic.draw(mFeetModelMatrix);
//        
//        Matrix.rotateM(mFeetModelMatrix, 0, mModelMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
//        Matrix.translateM(mFeetModelMatrix, 0, 
//        		tailPosition[0], tailPosition[1], 0);
//        Matrix.rotateM(mFeetModelMatrix, 0, mRightFootAngle, 0, 0, 1);
//        super.drawBuffer(rightFinVertexBuffer, mFeetModelMatrix);
        
        Matrix.rotateM(mHeadModelMatrix, 0, mModelMatrix, 0, bodyStartAnglePredicted, 0, 0, 1);
        Matrix.translateM(mHeadModelMatrix , 0, 
        		headPosition[0], headPosition[1], 0);
        mHeadGraphic.draw(mHeadModelMatrix);
	}
	
	private void calcPredicted(float interpolation) {
		if (angleInterpolation) {
			bodyStartAnglePredicted = mD.bodyStartAngle + mD.bodyStartAngleRot * interpolation;
			bodyBAnglePredicted = mD.bodyBAngle + mD.bodyBAngleRot * interpolation;
			bodyCAnglePredicted = mD.bodyCAngle + mD.bodyCAngleRot * interpolation;
			bodyEndAnglePredicted = mD.bodyEndAngle + mD.bodyEndAngleRot * interpolation;
		}
		else {
			bodyStartAnglePredicted = mD.bodyStartAngle;
			bodyBAnglePredicted = mD.bodyBAngle;
			bodyCAnglePredicted = mD.bodyCAngle;
			bodyEndAnglePredicted = mD.bodyEndAngle;
		}
		
        if (posInterpolation) {
        	mPredictedPosX = mD.mPosX + mD.vx*interpolation; 
        	mPredictedPosY = mD.mPosY + mD.vy*interpolation;
        }
        else {
        	mPredictedPosX = mD.mPosX; mPredictedPosY = mD.mPosY;
        }
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
		bodyVerticesData = D3Maths.quadBezierCurveVertices(bodyStartRot, bodyBRot, bodyCRot, bodyEndRot, detailsStep, bodyLength);
		bodyVertexBuffer.put(bodyVerticesData).position(0);
	}
	
	@Override
	public float getRadius() {
		return preyRadius;
//		throw new UnsupportedOperationException();
	}

	public void resetColor() {
		super.setColor(preyColor);
	}

	public float[] getHeadModelMatrix() {
		return mHeadModelMatrix;
	}

	public void setHiddenColor() {
		super.setColor(preyColorHidden);
	}
	
	public float getPredictedX() {
		return mPredictedPosX;
	}
	public float getPredictedY() {
		return mPredictedPosY;
	}
	
	public float getDetailsStep() {
		return detailsStep;
	}

	public void openMouth() {
		mHeadGraphic.openMouth();
	}

	public void closeMouth() {
		mHeadGraphic.closeMouth();
	}

	public void initEatingMotion() {
		mHeadGraphic.initEatingMotion();
	}
}
