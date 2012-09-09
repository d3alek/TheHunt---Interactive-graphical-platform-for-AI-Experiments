package d3kod.thehunt.prey;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.ShaderManager;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.programs.AttribVariable;
import d3kod.d3gles20.programs.Program;
import d3kod.d3gles20.shapes.D3Shape;

public class D3Prey extends D3Shape {
	private static final String TAG = "D3Prey";
	
	protected static float[] preyColor = {
			0.0f, 0.0f, 0.0f, 1.0f };
	protected final static float[] preyColorDefault = {
			0.0f, 0.0f, 0.0f, 1.0f };
	private static final float colorFadeSpeed = 0.01f;
	
	public static boolean angleInterpolation = true;
	public static boolean posInterpolation = true;
	protected final float detailsStep = 0.1f;
	protected final int STRIDE_BYTES = D3GLES20.COORDS_PER_VERTEX * Utilities.BYTES_PER_FLOAT;
	protected static final float preySize = 0.8f;
	protected final float bodyLength = 0.1f * preySize;
	protected final int eyeDetailsLevel = 10;
	
	//Head
	protected final float headSize = 0.04f * preySize;
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
	protected final float[] headPart3B = { 1.0f, 0.5f, 0.0f };
	protected final float[] headPart3C = { 0.5f, 0.75f, 0.0f };
	
	
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
	
	private final float[] leftFootPosition = {
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
	
	private int mProgramHandle;
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
	
	protected D3Prey(PreyData data, ShaderManager sm) {
		super(preyColor, GLES20.GL_LINE_STRIP, sm.getDefaultProgram());
		mD = data;
		Matrix.setIdentityM(mModelMatrix, 0);
		
		headVerticesData = calcHeadVerticesData();
		leftFinVerticesData = calcLeftFinVerticesData();
		rightFinVerticesData = calcRightFinVerticesData();
		eyeVertexData = D3Maths.circleVerticesData(eyeSize, eyeDetailsLevel);
		ribVerticesData = caclRibVerticesData();
		
		finVerticesNum = rightFinVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
		ribVerticesNum = ribVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
		
		leftFinVertexBuffer = Utilities.newFloatBuffer(leftFinVerticesData);
		rightFinVertexBuffer = Utilities.newFloatBuffer(rightFinVerticesData);
		headVertexBuffer = Utilities.newFloatBuffer(headVerticesData);
		eyeVertexBuffer = Utilities.newFloatBuffer(eyeVertexData);
		ribVertexBuffer = Utilities.newFloatBuffer(ribVerticesData);
		
		bodyVerticesData = D3Maths.quadBezierCurveVertices(bodyStart4, bodyB4, bodyC4, bodyEnd4, detailsStep, bodyLength);
		bodyVerticesNum = bodyVerticesData.length/D3GLES20.COORDS_PER_VERTEX;
		bodyVertexBuffer = Utilities.newFloatBuffer(bodyVerticesData);
		
        rib1PosIndex = (1*bodyVerticesNum/4)*D3GLES20.COORDS_PER_VERTEX;
        rib2PosIndex = (3*bodyVerticesNum/4-2)*D3GLES20.COORDS_PER_VERTEX;
		
        mProgramHandle = sm.getDefaultProgram().getHandle();
        
//        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        //TODO: fix visibility
//        mPositionHandle = AttribVariable.A_Position.getHandle();
//        mColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Color");
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
				headPart3Start, headPart3B, headPart3C, headPart1Start, detailsStep, headSize);
		float[] headVerticesData = new float[part1.length + part2.length + part3.length];
		for (int i = 0; i < part1.length; ++i) {
			headVerticesData[i] = part1[i];
		}
		for (int i = 0; i < part2.length; ++i) {
			headVerticesData[part1.length + i] = part2[i];
		}
		for (int i = 0; i < part3.length; ++i) {
			headVerticesData[part1.length + part2.length + i] = part3[i];
		}
		headVerticesNum = headVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
		return headVerticesData;
	}

	float bodyStartAnglePredicted, bodyBAnglePredicted, bodyCAnglePredicted, bodyEndAnglePredicted;
    float mPredictedPosX, mPredictedPosY;

	private float[] rib1Pos = new float[3];
	private float[] rib2Pos = new float[3];
    
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
        
//		GLES20.glUseProgram(mProgramHandle);
		super.useProgram();
		
		super.useColor();
//        GLES20.glUniform4fv(mColorHandle, 1, super.getColor(), 0);
//        GLES20.glEnableVertexAttribArray(mColorHandle);
        
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
        		leftFootPosition[0], leftFootPosition[1], 0);
        Matrix.rotateM(mFeetModelMatrix, 0, mLeftFootAngle, 0, 0, 1);
        super.drawBuffer(leftFinVertexBuffer, mFeetModelMatrix);
        
        Matrix.rotateM(mFeetModelMatrix, 0, mModelMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
        Matrix.translateM(mFeetModelMatrix, 0, 
        		leftFootPosition[0], leftFootPosition[1], 0);
        Matrix.rotateM(mFeetModelMatrix, 0, mRightFootAngle, 0, 0, 1);
        super.drawBuffer(rightFinVertexBuffer, mFeetModelMatrix);
        
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
		throw(new UnsupportedOperationException());
	}

	public void reset() {
		super.setColor(preyColor);
	}

	public float[] getHeadModelMatrix() {
		return mHeadModelMatrix;
	}

}
