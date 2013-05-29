package d3kod.thehunt.agent.prey;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.thehunt.world.logic.TheHuntRenderer;

public class Head {
	
	//Head
//		protected static final float headSize = 0.04f;
//		protected final float[] headPosition = {
//				0, bodyLength/2 + headSize*0.4f, 0
//		};
		
		protected final int eyeDetailsLevel = 10;
		protected final float[] eyePosition = { -0.40f, 0.25f, 0.0f };
		protected final float eyeSize = 0.25f;
			
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

	private boolean mMouthOpen;
	private float[] headVerticesData;
	private float[] eyeVertexData;

	private float mDetailsStep;

	private int headVerticesNum;

	private FloatBuffer headVertexBuffer;
//	private float[] mHeadModelMatrix = new float[16];
	private float[] mEyeModelMatrix = new float[16];

	private D3Prey mGraphic;

	private FloatBuffer eyeVertexBuffer;
	private float mSize;	

	public Head(D3Prey graphic, float size) {
		mSize = size;
		mDetailsStep = graphic.getDetailsStep();
		
		mMouthOpen = true;
		headVerticesData = calcHeadVerticesData();
		eyeVertexData = D3Maths.circleVerticesData(eyeSize*mSize, eyeDetailsLevel);
		mGraphic = graphic;
		
		
		
		headVertexBuffer = Utilities.newFloatBuffer(headVerticesData);
		eyeVertexBuffer = Utilities.newFloatBuffer(eyeVertexData);
		
		
        eatingStep = -1;
	}

	private float[] calcHeadVerticesData() {
		float[] part1 = D3Maths.quadBezierCurveVertices(
				headPart1Start, headPart1B, headPart1C, headPart2Start, mDetailsStep, mSize);
		float[] part2 = D3Maths.quadBezierCurveVertices(
				headPart2Start, headPart2B, headPart2C, headPart3Start, mDetailsStep, mSize);
		float[] part3 = D3Maths.quadBezierCurveVertices(
				headPart3Start, headPart3B, headPart3C, headPart4Start, mDetailsStep, mSize);
		float[] part4 = D3Maths.quadBezierCurveVertices(
				headPart4Start, headPart4B, headPart4C, headPart1Start, mDetailsStep, mSize);
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
				headPart4StartEatingMotion[step], headPart4BEatingMotion[step], headPart4C, headPart1Start, mDetailsStep, mSize);
		float[] headVerticesData = new float[this.headVerticesData.length];//Arrays.copyOf(this.headVerticesData, this.headVerticesData.length);
	
		for (int i = 0; i < headVerticesData.length; ++i) {
			headVerticesData[i] = this.headVerticesData[i];
		}
		
		for (int i = 0; i < part4Modif.length; ++i) {
			headVerticesData[SpriteManager.COORDS_PER_VERTEX * ((int)(3/mDetailsStep) + 3) + i] = part4Modif[i];
		}
		
		return headVerticesData;
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

	public void draw(float[] modelMatrix) {
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
        
        mGraphic.drawBuffer(headVertexBuffer, modelMatrix);
        
        
        Matrix.translateM(mEyeModelMatrix, 0, modelMatrix , 0, eyePosition[0]*mSize, eyePosition[1]*mSize, 0);
        mGraphic.setDrawType(GLES20.GL_LINE_LOOP);
        mGraphic.drawBuffer(eyeVertexBuffer, mEyeModelMatrix);
	}
	
	public void initEatingMotion() {
		eatingStep = 0;
	}
	
}