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
	protected final int eyeDetailsLevel = 10;
	
	//Head
	protected static final float headSize = 0.04f * preySize;
	protected final float[] headPosition = {
			0, bodyLength/2 + headSize*0.4f, 0
	};
	
	protected final float[] eyePosition = { -0.40f * headSize, 0.25f * headSize, 0.0f };
	protected final float eyeSize = 0.25f*headSize;
		
	protected final float[] headPart1Start = { 0.0f, 1.0f, 0.0f };
	protected final float[] headPart1B = { -0.5f, 0.75f, 0.0f };
	protected final float[] headPart1C = { -1.0f, 0.5f, 0.0f };
	protected final float[] headPart2Start = { -1.0f, -0.5f, 0.0f };
	protected final float[] headPart2B = { -0.2f, -0.3f, 0.0f };
	protected final float[] headPart2C = { 0.2f, -0.3f, 0.0f };
	protected final float[] headPart3Start = { 1.0f, -0.5f, 0.0f };
	protected final float[] headPart3B = { 1.0f, 0.0f, 0.0f };
	protected final float[] headPart3C = { 1.0f, 0.5f, 0.0f };
	protected final float[] headPart4Start = { 0.7f, 0.7f, 0.0f };
	protected final float[] headPart4B = { 0.5f, 0.5f, 0.0f };
	protected final float[] headPart4C = { 0.0f, 0.0f, 0.0f };
	

	protected final float eatingMotionLengthSeconds = 0.5f;
	protected final int eatingMotionSteps = 8;
	protected int eatingStep;
	protected final int eatingMotionStepTicks = (int)(eatingMotionLengthSeconds*TheHuntRenderer.TICKS_PER_SECOND/eatingMotionSteps);
	protected int eatingMotionStepCounter;
	
	protected final float[][] headPart4StartEatingMotion = 
		{ 	
			{ 	0.7f,	0.7f, 	0.0f },
			{ 	0.6f,	0.75f, 	0.0f },
			{ 	0.5f,	0.8f, 	0.0f },
			{ 	0.4f,	0.85f, 	0.0f },
			{ 	0.3f,	0.9f, 	0.0f },
			{ 	0.2f,	0.95f, 	0.0f },
			{ 	0.1f,	0.95f, 	0.0f },
			{ 	0f, 	1f, 	0.0f }
		};
	protected final float[][] headPart4BEatingMotion = 
		{	
			{	0.5f, 	0.5f, 	0.0f },
			{	0.42f, 	0.5f, 	0.0f },
			{	0.35f, 	0.5f, 	0.0f },
			{	0.28f, 	0.5f, 	0.0f },
			{	0.2f, 	0.5f, 	0.0f },
			{	0.12f, 	0.5f, 	0.0f },
			{	0.05f, 	0.5f, 	0.0f },
			{	0f, 	0.5f, 	0.0f }
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
	
	protected final float[] rightFinStart = { 0.0f, 0.0f, 0.0f };
	protected final float[] rightFinB = { 0.55f, -0.4f, 0.0f };
	protected final float[] rightFinC = { 0.7f, -0.6f, 0.0f };
	protected final float[] rightFinEnd = { 0.8f, -1.0f, 0.0f };
	
	protected final float[] leftFinStart = { rightFinStart[0], rightFinStart[1], 0.0f };
	protected final float[] leftFinB = { -rightFinB[0], rightFinB[1], 0.0f };
	protected final float[] leftFinC = { -rightFinC[0], rightFinC[1], 0.0f };
	protected final float[] leftFinEnd = { -rightFinEnd[0], rightFinEnd[1], 0.0f };
	
	protected int finVerticesNum;
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
	
	protected D3Prey(PreyData data) {
		super();
		super.setColor(preyColor);
		super.setDrawType(GLES20.GL_LINE_STRIP);
		mD = data;
		Matrix.setIdentityM(mModelMatrix, 0);
		
		mMouthOpen = true;
		headVerticesData = calcHeadVerticesData();
		leftFinVerticesData = calcLeftFinVerticesData();
		rightFinVerticesData = calcRightFinVerticesData();
		eyeVertexData = D3Maths.circleVerticesData(eyeSize, eyeDetailsLevel);
		ribVerticesData = caclRibVerticesData();
		
		finVerticesNum = rightFinVerticesData.length / SpriteManager.COORDS_PER_VERTEX;
		ribVerticesNum = ribVerticesData.length / SpriteManager.COORDS_PER_VERTEX;
		
		leftFinVertexBuffer = Utilities.newFloatBuffer(leftFinVerticesData);
		rightFinVertexBuffer = Utilities.newFloatBuffer(rightFinVerticesData);
		headVertexBuffer = Utilities.newFloatBuffer(headVerticesData);
		eyeVertexBuffer = Utilities.newFloatBuffer(eyeVertexData);
		ribVertexBuffer = Utilities.newFloatBuffer(ribVerticesData);
		
		bodyVerticesData = D3Maths.quadBezierCurveVertices(bodyStart4, bodyB4, bodyC4, bodyEnd4, detailsStep, bodyLength);
		bodyVerticesNum = bodyVerticesData.length/SpriteManager.COORDS_PER_VERTEX;
		bodyVertexBuffer = Utilities.newFloatBuffer(bodyVerticesData);
		
        rib1PosIndex = (1*bodyVerticesNum/4)*SpriteManager.COORDS_PER_VERTEX;
        rib2PosIndex = (3*bodyVerticesNum/4-2)*SpriteManager.COORDS_PER_VERTEX;
		
        eatingStep = -1;
	}

	private float[] caclRibVerticesData() {
		return D3Maths.quadBezierCurveVertices(
				ribA, ribB, ribC, ribD, detailsStep, ribSize);
	}

	private float[] calcRightFinVerticesData() {
		return D3Maths.quadBezierCurveVertices(
				rightFinStart, rightFinB, rightFinC, rightFinEnd, detailsStep, finSize);
	}

	private float[] calcLeftFinVerticesData() {
		return D3Maths.quadBezierCurveVertices(
				leftFinStart, leftFinB, leftFinC, leftFinEnd, detailsStep, finSize);
	}

	private float[] calcHeadVerticesData() {
		float[] part1 = D3Maths.quadBezierCurveVertices(
				headPart1Start, headPart1B, headPart1C, headPart2Start, detailsStep, headSize);
		float[] part2 = D3Maths.quadBezierCurveVertices(
				headPart2Start, headPart2B, headPart2C, headPart3Start, detailsStep, headSize);
		float[] part3 = D3Maths.quadBezierCurveVertices(
				headPart3Start, headPart3B, headPart3C, headPart4Start, detailsStep, headSize);
		float[] part4 = D3Maths.quadBezierCurveVertices(
				headPart4Start, headPart4B, headPart4C, headPart1Start, detailsStep, headSize);
		float[] headVerticesData = new float[part1.length + part2.length + part3.length + part4.length];
		for (int i = 0; i < part1.length; ++i) {
			headVerticesData[i] = part1[i];
		}
		for (int i = 0; i < part2.length; ++i) {
			headVerticesData[part1.length + i] = part2[i];
		}
		for (int i = 0; i < part3.length; ++i) {
			headVerticesData[part1.length + part2.length + i] = part3[i];
		}
		for (int i = 0; i < part4.length; ++i) {
			headVerticesData[part1.length + part2.length + part3.length + i] = part4[i];
		}
		headVerticesNum = headVerticesData.length / SpriteManager.COORDS_PER_VERTEX;
		return headVerticesData;
	}

	private float[] calcMoveHeadVerticesData(int step) {
		float[] part4Modif = D3Maths.quadBezierCurveVertices(
				headPart4StartEatingMotion[step], headPart4BEatingMotion[step], headPart4C, headPart1Start, detailsStep, headSize);
		float[] headVerticesData = new float[this.headVerticesData.length];//Arrays.copyOf(this.headVerticesData, this.headVerticesData.length);
	
		for (int i = 0; i < headVerticesData.length; ++i) {
			headVerticesData[i] = this.headVerticesData[i];
		}
		
		for (int i = 0; i < part4Modif.length; ++i) {
			headVerticesData[SpriteManager.COORDS_PER_VERTEX * ((int)(3/detailsStep) + 3) + i] = part4Modif[i];
		}
		
		return headVerticesData;
	}
	
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
        Matrix.rotateM(mFeetModelMatrix, 0, mLeftFootAngle, 0, 0, 1);
        super.drawBuffer(leftFinVertexBuffer, mFeetModelMatrix);
        
        Matrix.rotateM(mFeetModelMatrix, 0, mModelMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
        Matrix.translateM(mFeetModelMatrix, 0, 
        		tailPosition[0], tailPosition[1], 0);
        Matrix.rotateM(mFeetModelMatrix, 0, mRightFootAngle, 0, 0, 1);
        super.drawBuffer(rightFinVertexBuffer, mFeetModelMatrix);
        
        if (eatingStep != -1) {
        	headVertexBuffer = Utilities.newFloatBuffer(calcMoveHeadVerticesData(eatingStep));
        	if (eatingMotionStepCounter >= eatingMotionStepTicks) {
        		eatingStep++;
        		eatingMotionStepCounter = 0;
        	}
        	else eatingMotionStepCounter++;
        	if (eatingStep >= eatingMotionSteps) {
        		eatingStep = -1;
        		headVertexBuffer = Utilities.newFloatBuffer(headVerticesData);
        	}
        }
        
        Matrix.rotateM(mHeadModelMatrix, 0, mModelMatrix, 0, bodyStartAnglePredicted, 0, 0, 1);
        Matrix.translateM(mHeadModelMatrix , 0, 
        		headPosition[0], headPosition[1], 0);
        super.drawBuffer(headVertexBuffer, mHeadModelMatrix);
        
        Matrix.translateM(mEyeModelMatrix, 0, mHeadModelMatrix , 0, eyePosition[0], eyePosition[1], 0);
        super.setDrawType(GLES20.GL_LINE_LOOP);
        super.drawBuffer(eyeVertexBuffer, mEyeModelMatrix);
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
	
	public void initEatingMotion() {
		eatingStep = 0;
	}

	public void openMouth() {
		if (!mMouthOpen) {
			mMouthOpen = true;
			headVerticesData = calcMoveHeadVerticesData(0);
			headVertexBuffer = Utilities.newFloatBuffer(headVerticesData);
		}
	}
	
	public void closeMouth() {
		if (mMouthOpen) {
			mMouthOpen = false;
			if (eatingStep == -1) initEatingMotion();
			headVerticesData = calcMoveHeadVerticesData(eatingMotionSteps-1);
		}
	}

	public float getPredictedX() {
		return mPredictedPosX;
	}
	public float getPredictedY() {
		return mPredictedPosY;
	}
}
