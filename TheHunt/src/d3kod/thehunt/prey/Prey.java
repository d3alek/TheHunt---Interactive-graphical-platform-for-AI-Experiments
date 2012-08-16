package d3kod.thehunt.prey;

import java.util.Random;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.FloatMath;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.TheHuntRenderer;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.environment.EnvironmentData;
import d3kod.thehunt.prey.memory.WorldModel;
import d3kod.thehunt.prey.planner.Planner;
import d3kod.thehunt.prey.sensor.Sensor;

public class Prey {
	
	private static final String TAG = "Prey";

	private static final float colorFadeSpeed = 0.01f;
	
	private Planner mPlanner;
	private WorldModel mWorldModel;
	private Sensor mSensor;
	private Environment mEnv;
	private PreyData mD;
	
	
	private int bodyBendCounter;
	private float bodyStartAnglePredicted;
	private float bodyBAnglePredicted;
	private float bodyCAnglePredicted;
	private float bodyEndAnglePredicted;
	private float bodyStartAngleRot;
	private float bodyBAngleRot;
	private float bodyCAngleRot;
	private float bodyEndAngleRot;
	private int backFinAngle;
	private boolean flopBack;
	private int flopBackTargetFirst;
	private int flopBackTargetSecond;
	private boolean floppedFirst;
	private boolean floppedSecond;
	private boolean turningBackFinMotion;
	private float flopBackAngle;
	private TurnAngle turningBackFinAngle;
	public static boolean angleInterpolation = true;
	public static boolean posInterpolation = true;

	public static final float bodyToHeadLength = 0.07f;
	
	
//	private static final float FORCE_TO_DISTANCE = 0.00004f;
	private static final float FORCE_TO_DISTANCE = 0.00003f;
	
	public static int flopBacksPerSecond = 2;
	public static int flopBackTicks = TheHuntRenderer.TICKS_PER_SECOND/flopBacksPerSecond;
	private float flopBackSpeed;
	private boolean floppedThird;
	private boolean mIsCaught;

	private int sMovementAdj = 5;

//	private float bodyEndAngleRotated;
	
	public void update(float dx, float dy) {
		if (mIsCaught) return;
		
		calcPosHead();
		
		mWorldModel.update(mSensor.sense(mD.mPosHeadX, mD.mPosHeadY, mD.mPosX, mD.mPosY));

		if (PreyData.AI) {
			doAction(mPlanner.nextAction(mWorldModel));
		}
		move(dx, dy);
	}
	
	private void calcPosHead() {
		float[] posTemp = { 0.0f, bodyToHeadLength, 0.0f, 1.0f };
		
		Matrix.setIdentityM(mD.mHeadModelMatrix, 0);
		Matrix.translateM(mD.mHeadModelMatrix, 0, mD.mPosX, mD.mPosY, 0);
		Matrix.rotateM(mD.mHeadModelMatrix, 0, mD.bodyStartAngle, 0, 0, 1);
		Matrix.multiplyMV(posTemp, 0, mD.mHeadModelMatrix, 0, posTemp, 0);
		
		mD.mPosHeadX = posTemp[0]; 
		mD.mPosHeadY = posTemp[1];
	}

	public void move(float x, float y) {
		if (mIsCaught) return;
//		Log.v(TAG, "Debug " + mD.bodyStartAngleTarget + " " + mD.bodyStartAngle + " " + mD.bodyBAngleTarget + " " + mD.bodyBAngle + " " + mD.bodyCAngleTarget + " " + mD.bodyCAngle + " " + mD.bodyEndAngleTarget + mD.bodyEndAngle);
		if (bodyBendCounter == 0) {
//			if (!flopBack) moveForward(Math.abs(mD.bodyEndAngleTarget-mD.bodyCAngleTarget)*mD.rotateSpeedBody*sMovementAdj );
			mD.bodyEndAngleTarget = mD.bodyCAngleTarget;
			mD.bodyEndSpeed = mD.bodyCSpeed;
			mD.bodyCAngleTarget = mD.bodyBAngleTarget;
			mD.bodyCSpeed = mD.bodyBSpeed;
			mD.bodyBAngleTarget = mD.bodyStartAngleTarget;
			mD.bodyBSpeed = mD.rotateSpeedHead;
			bodyBendCounter = PreyData.BODY_BEND_DELAY-1;
			
//			moveForward(Math.abs(bodyEndAngleRotated*mD.rotateSpeedBody)); // F = ma
//			Log.v(TAG, "Passing spin " + mD.bodyStartAngleTarget + " " + mD.bodyStartAngle + " " + mD.bodyBAngleTarget + " " + mD.bodyBAngle + " " + mD.bodyCAngleTarget + " " + mD.bodyCAngle + " " + mD.bodyEndAngleTarget + " " + mD.bodyEndAngle);
		}
		else {
			--bodyBendCounter;
		}
		
//		if (turningBackFinMotion && !flopBack) {
//			if (stoppedTurning()) {
//				turningBackFinMotion = false;
//			}
//			backFinMotion(turningBackFinAngle);
//		}
		
		if (mD.bodyStartAngleTarget > mD.bodyStartAngle + mD.rotateSpeedHead) bodyStartAngleRot = mD.rotateSpeedHead;
		else if (mD.bodyStartAngleTarget < mD.bodyStartAngle - mD.rotateSpeedHead) bodyStartAngleRot = -mD.rotateSpeedHead;
		else {
			bodyStartAngleRot = 0;
			mD.bodyStartAngle = mD.bodyStartAngleTarget;
		}
		
		if (mD.bodyBAngleTarget > mD.bodyBAngle + mD.bodyBSpeed) bodyBAngleRot = +mD.bodyBSpeed;
		else if (mD.bodyBAngleTarget < mD.bodyBAngle - mD.bodyBSpeed) bodyBAngleRot = -mD.bodyBSpeed;
		else {
			bodyBAngleRot = 0;
			mD.bodyBAngle = mD.bodyBAngleTarget;
		}
		
		if (mD.bodyCAngleTarget > mD.bodyCAngle + mD.bodyCSpeed) bodyCAngleRot = +mD.bodyCSpeed;
		else if (mD.bodyCAngleTarget < mD.bodyCAngle - mD.bodyCSpeed) bodyCAngleRot = -mD.bodyCSpeed;
		else {
			bodyCAngleRot = 0;
			mD.bodyCAngle = mD.bodyCAngleTarget;
		}
		
		if (!flopBack) {
//			Log.v(TAG, "Not flopping back, normal bend of the back flop "
//					+ mD.bodyEndAngleTarget + " " + mD.bodyEndAngle + " " + mD.rotateSpeedBody);
//			flopBackSpeed = Math.max(Math.abs(flopBackSpeed), mD.rotateSpeedBody);
			if (mD.bodyEndAngleTarget > mD.bodyEndAngle + mD.bodyEndSpeed) bodyEndAngleRot = mD.bodyEndSpeed;
			else if (mD.bodyEndAngleTarget < mD.bodyEndAngle - mD.bodyEndSpeed) bodyEndAngleRot = -mD.bodyEndSpeed;
			else {
				bodyEndAngleRot = 0;
				mD.bodyEndAngle = mD.bodyEndAngleTarget;
//				Log.v(TAG, "bodyEndAngleRotated " + bodyEndAngleRotated);
				
//				bodyEndAngleRotated = 0;
			}
		}

		if (flopBack) doFlopBack();
		
		mD.bodyStartAngle += bodyStartAngleRot;
		mD.bodyBAngle += bodyBAngleRot;
		mD.bodyCAngle += bodyCAngleRot;
		if (!flopBack) {
			mD.bodyEndAngle += (int)bodyEndAngleRot;
//			bodyEndAngleRotated += bodyEndAngleRot;
		}
		
		updateSpeed(x, y);
		applyFriction();
		mD.mPosX += mD.vx ; mD.mPosY += mD.vy;
	}
	private boolean stoppedTurning() {
		return (mD.bodyStartAngleTarget == mD.bodyBAngleTarget && mD.bodyBAngleTarget == mD.bodyCAngleTarget);
//		return (mD.bodyStartAngleTarget == mD.bodyBAngleTarget 
//				&& mD.bodyBAngleTarget == mD.bodyCAngleTarget
//				&& mD.bodyCAngle == mD.bodyCAngleTarget);
	}

	private void doFlopBack() {
		if (!floppedFirst) {
//			Log.v(TAG, "Flopping first " + flopBackTargetFirst + " " + flopBackAngle);
			if (flopBackTargetFirst > flopBackAngle + flopBackSpeed) flopBackAngle += flopBackSpeed;
			else if (flopBackTargetFirst < flopBackAngle - flopBackSpeed) flopBackAngle -= flopBackSpeed;
			else {
				flopBackAngle = flopBackTargetFirst;
				floppedFirst = true;
				moveForward(Math.abs(backFinAngle*flopBackSpeed)); // F = ma
//				Log.v(TAG, "Flopped First done " + flopBackAngle);
			}
			bodyEndAngleRot = mD.bodyCAngle + flopBackAngle-mD.bodyEndAngle;
			mD.bodyEndAngle = mD.bodyCAngle + flopBackAngle;
		}
		else if (!floppedSecond) {
//			Log.v(TAG, "Flopping second " + flopBackTargetSecond + " " + flopBackAngle);
			if (flopBackTargetSecond > flopBackAngle + flopBackSpeed) flopBackAngle += flopBackSpeed;
			else if (flopBackTargetSecond < flopBackAngle - flopBackSpeed) flopBackAngle -= flopBackSpeed;
			else {
				flopBackAngle = flopBackTargetSecond;
				floppedSecond = true;
				moveForward(Math.abs(2*backFinAngle*flopBackSpeed)); // F = ma
//				Log.v(TAG, "Flopped Second done " + flopBackAngle);
			}
			bodyEndAngleRot = mD.bodyCAngle + flopBackAngle-mD.bodyEndAngle;
			mD.bodyEndAngle = mD.bodyCAngle + flopBackAngle;
		}
		else {
			//flopping third
//			Log.v(TAG, "Flopping third " + flopBackAngle);
			if (0 > flopBackAngle + flopBackSpeed) flopBackAngle += flopBackSpeed;
			else if (0 < flopBackAngle - flopBackSpeed) flopBackAngle -= flopBackSpeed;
			else {
				flopBackAngle = 0;
//				floppedSecond = true;
				floppedThird = true;
				moveForward(Math.abs(backFinAngle*flopBackSpeed)); // F = ma
//				Log.v(TAG, "Flopped Third done " + flopBackAngle);
			}
			bodyEndAngleRot = mD.bodyCAngle + flopBackAngle-mD.bodyEndAngle;
			mD.bodyEndAngle = mD.bodyCAngle + flopBackAngle;
		}
//		else {
//			if (f)
//		}
		if (floppedFirst && floppedSecond && floppedThird) {
			if (turningBackFinMotion) {
				if (stoppedTurning()) {
//					Log.v(TAG, "Stopped turning flop");
					turningBackFinMotion = false;
					flopBack = false;
				}
				else {
//					Log.v(TAG, "Continue turning flop");
					backFinMotion(turningBackFinAngle);
				}
			}
			else {
//				Log.v(TAG, "Stopping flop");
				flopBack = false;
			}
		}
	}
	public void turn(TurnAngle angle) {
		
		int value = angle.getValue();
		
		if (mD.bodyStartAngleTarget + value - mD.bodyCAngle > mD.MAX_BODY_BEND_ANGLE 
				|| mD.bodyStartAngleTarget + value - mD.bodyCAngle < -mD.MAX_BODY_BEND_ANGLE) {
			Log.v(TAG, "Can't bend that much!");
			return;
		}
			
//		switch(angle) {
//		case LEFT_SMALL: 
//		case RIGHT_SMALL: mD.rotateSpeedHead = mD.rotateSpeedSmall; break;
//		case LEFT_MEDIUM: 
//		case RIGHT_MEDIUM: mD.rotateSpeedHead = mD.rotateSpeedMedium; break;
//		case LEFT_LARGE: 
//		case RIGHT_LARGE: mD.rotateSpeedHead = mD.rotateSpeedLarge; break;
//		default: Log.v(TAG, "Turning to unknown angle");
//		}
		mD.rotateSpeedHead = angle.getRotateSpeed();
//		mD.rotateSpeedBody = mD.rotateSpeedHead/2;
//		if (mD.rotateSpeedBody < 1) mD.rotateSpeedBody = 1;
 		mD.bodyStartAngleTarget += value;
	
		if (!turningBackFinMotion) {
			turningBackFinMotion = true;
	 		switch(angle) {
	 		case RIGHT_SMALL: turningBackFinAngle = TurnAngle.BACK_LEFT_SMALL; break;
	 		case RIGHT_MEDIUM: turningBackFinAngle = TurnAngle.BACK_LEFT_MEDIUM; break;
	 		case RIGHT_LARGE: turningBackFinAngle = TurnAngle.BACK_LEFT_LARGE; break;
	 		case LEFT_SMALL: turningBackFinAngle = TurnAngle.BACK_RIGHT_SMALL; break;
	 		case LEFT_MEDIUM: turningBackFinAngle = TurnAngle.BACK_RIGHT_MEDIUM; break;
	 		case LEFT_LARGE: turningBackFinAngle = TurnAngle.BACK_RIGHT_LARGE; break;
	 		default: Log.v(TAG, "Unexpected turningBackFinAngle " + turningBackFinAngle);
	 		}
			backFinMotion(turningBackFinAngle);
		}
		
	}
	
	public void backFinMotion(TurnAngle angle) {
		flopBack = true;
		backFinAngle = angle.getValue();
//		flopBackTicks = angle.getTicks(), ;
		mD.bodyEndAngle = mD.bodyCAngle;
		flopBackTargetFirst = +backFinAngle;
		//flopBackAngle = mD.bodyEndAngle-mD.bodyCAngle;
		flopBackAngle = 0;
		flopBackTargetSecond = -backFinAngle;
		floppedFirst = false;
		floppedSecond = false;
		floppedThird = false;
//		flopBackSpeed = Math.abs(4*backFinAngle)/(float)flopBackTicks; // S=3*backFinAngle
		flopBackSpeed = angle.getRotateSpeed();
//		Log.v(TAG, "flopBackSpeed is " + flopBackSpeed);
//		moveForward(Math.abs(2*backFinAngle*flopBackSpeed)); // F = ma
//		Log.v(TAG, "New back fin motion " + flopBackSpeed + " " 
//				+ backFinAngle + " " + flopBackAngle + " " + flopBackTargetFirst 
//				+ " " + flopBackTargetSecond + " " + mD.bodyCAngle + " " + " " + mD.bodyCAngleTarget);
	}
	
	public void updateSpeed(float dx, float dy) {
		mD.vx += dx; mD.vy += dy;
	}
	
	public void moveForward(float force) {
		float distance = force * FORCE_TO_DISTANCE;
//		Log.v(TAG, "Moving the prey forward to a distance of " + distance + " thrust is " + mD.thrust);
		float radAngle = (float)Math.toRadians(mD.bodyCAngle);
		mD.vx += -FloatMath.sin(radAngle)*distance;
		mD.vy += FloatMath.cos(radAngle)*distance;  
	}
	
	public void applyFriction() {
		mD.vx -= EnvironmentData.frictionCoeff*mD.vx;
		mD.vy -= EnvironmentData.frictionCoeff*mD.vy;
	}
	
	public Prey(float screenWidth, float screenHeight, Environment env) {
		mD = new PreyData();
		
//		mD.delayV = new float[mD.delayVLength][2];
		
		mWorldModel = new WorldModel(screenWidth, screenHeight);
		mPlanner = new Planner();
		mEnv = env;
		mSensor = new Sensor(mEnv);
		
//		backAngleToggle = 1;
		
		Matrix.setIdentityM(mD.mModelMatrix, 0);
		mD.mPosX = mD.mPosY = 0;

		for (int i = 0; i < 3; ++i) {
			mD.bodyStart4[i] = mD.bodyStart[i];
		}
		for (int i = 0; i < 3; ++i) {
			mD.bodyB4[i] = mD.bodyB[i];
		}
		for (int i = 0; i < 3; ++i) {
			mD.bodyC4[i] = mD.bodyC[i];
		}
		for (int i = 0; i < 3; ++i) {
			mD.bodyEnd4[i] = mD.bodyEnd[i];
		}
		
		mD.headVerticesData = calcHeadVerticesData();
		mD.leftFinVerticesData = calcLeftFinVerticesData();
		mD.rightFinVerticesData = calcRightFinVerticesData();
		mD.eyeVertexData = D3GLES20.circleVerticesData(mD.eyePosition, mD.eyeSize, mD.eyeDetailsLevel);
		mD.ribVerticesData = caclRibVerticesData();
		
		mD.finVerticesNum = mD.rightFinVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
		mD.ribVerticesNum = mD.ribVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
		
		mD.rotateSpeedHead = mD.rotateSpeedSmall;//Math.abs(TurnAngle.LEFT_SMALL.getValue())/SMALL_TICKS_PER_TURN;
//		mD.rotateSpeedBody = mD.rotateSpeedHead/2;
//		if (mD.rotateSpeedBody < 1) mD.rotateSpeedBody = 1;
		
		mIsCaught = false;
//		bodyEndAngleRotated = 0;
	}
	
	private float[] caclRibVerticesData() {
		return D3GLES20.quadBezierCurveVertices(
				mD.ribA, mD.ribB, mD.ribC, mD.ribD, mD.detailsStep, mD.ribSize);
	}

	private float[] calcRightFinVerticesData() {
		return D3GLES20.quadBezierCurveVertices(
				mD.rightFinStart, mD.rightFinB, mD.rightFinC, mD.rightFinEnd, mD.detailsStep, mD.finSize);
	}

	private float[] calcLeftFinVerticesData() {
		return D3GLES20.quadBezierCurveVertices(
				mD.leftFinStart, mD.leftFinB, mD.leftFinC, mD.leftFinEnd, mD.detailsStep, mD.finSize);
	}

	private float[] calcHeadVerticesData() {
		float[] part1 = D3GLES20.quadBezierCurveVertices(
				mD.headPart1Start, mD.headPart1B, mD.headPart1C, mD.headPart2Start, mD.detailsStep, mD.headSize);
		float[] part2 = D3GLES20.quadBezierCurveVertices(
				mD.headPart2Start, mD.headPart2B, mD.headPart2C, mD.headPart3Start, mD.detailsStep, mD.headSize);
		float[] part3 = D3GLES20.quadBezierCurveVertices(
				mD.headPart3Start, mD.headPart3B, mD.headPart3C, mD.headPart1Start, mD.detailsStep, mD.headSize);
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
		mD.headVerticesNum = headVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
		return headVerticesData;
	}

	public void initGraphics() {
		mD.leftFinVertexBuffer = D3GLES20.newFloatBuffer(mD.leftFinVerticesData);
		mD.rightFinVertexBuffer = D3GLES20.newFloatBuffer(mD.rightFinVerticesData);
		mD.headVertexBuffer = D3GLES20.newFloatBuffer(mD.headVerticesData);
		mD.eyeVertexBuffer = D3GLES20.newFloatBuffer(mD.eyeVertexData);
		mD.ribVertexBuffer = D3GLES20.newFloatBuffer(mD.ribVerticesData);
		
		int vertexShaderHandle = D3GLES20.loadShader(GLES20.GL_VERTEX_SHADER, mD.vertexShaderCode);
        int fragmentShaderHandle = D3GLES20.loadShader(GLES20.GL_FRAGMENT_SHADER, mD.fragmentShaderCode);
        
        mD.mProgram = D3GLES20.createProgram(vertexShaderHandle, fragmentShaderHandle);
        
        mD.mMVPMatrixHandle = GLES20.glGetUniformLocation(mD.mProgram, "u_MVPMatrix"); 
        mD.mPositionHandle = GLES20.glGetAttribLocation(mD.mProgram, "a_Position");
        mD.mColorHandle = GLES20.glGetUniformLocation(mD.mProgram, "u_Color");
        
		if (Planner.SHOW_TARGET) {
			mPlanner.makeTarget();
		}
		
		randomizePos();
	}
	
	public void draw(float[] mVMatrix, float[] mProjMatrix, float interpolation) {
//		GLES20.glLineWidth(2f);
		
		if (mIsCaught) {
			if (colorIsBgColor()) return;
			fadeColor();
		}
		
        // Interpolate
		if (angleInterpolation) {
			bodyStartAnglePredicted = mD.bodyStartAngle + bodyStartAngleRot * interpolation;
			bodyBAnglePredicted = mD.bodyBAngle + bodyBAngleRot * interpolation;
			bodyCAnglePredicted = mD.bodyCAngle + bodyCAngleRot * interpolation;
			bodyEndAnglePredicted = mD.bodyEndAngle + bodyEndAngleRot * interpolation;
		}
		else {
			bodyStartAnglePredicted = mD.bodyStartAngle;
			bodyBAnglePredicted = mD.bodyBAngle;
			bodyCAnglePredicted = mD.bodyCAngle;
			bodyEndAnglePredicted = mD.bodyEndAngle;
		}
        Matrix.setIdentityM(mD.mBodyStartRMatrix, 0);
        Matrix.setIdentityM(mD.mBodyBRMatrix, 0);
        Matrix.setIdentityM(mD.mBodyCRMatrix, 0);
        Matrix.setIdentityM(mD.mBodyEndRMatrix, 0);
        Matrix.rotateM(mD.mBodyStartRMatrix, 0, bodyStartAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mD.mBodyBRMatrix, 0, bodyBAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mD.mBodyCRMatrix, 0, bodyCAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mD.mBodyEndRMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
		
        if (posInterpolation) {
        	mD.mPredictedPosX = mD.mPosX + mD.vx*interpolation; 
        	mD.mPredictedPosY = mD.mPosY + mD.vy*interpolation;
        }
        else {
        	mD.mPredictedPosX = mD.mPosX; mD.mPredictedPosY = mD.mPosY;
        }
        
        // Rotate the body vertices
        
        updateBodyVertexBuffer();
        
        // Start Drawing
        
		GLES20.glUseProgram(mD.mProgram);
		
        GLES20.glUniform4fv(mD.mColorHandle, 1, mD.preyColor , 0);
        GLES20.glEnableVertexAttribArray(mD.mColorHandle);
        
		// Body
        
        Matrix.setIdentityM(mD.mModelMatrix, 0);
        Matrix.translateM(mD.mModelMatrix , 0, mD.mPredictedPosX, mD.mPredictedPosY, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.bodyVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.bodyVerticesNum);
        
        // Ribs
  
//        mD.mRibsModelMatrix = mD.mModelMatrix.clone();
        int rib1PosIndex = (1*mD.bodyVerticesNum/4)*D3GLES20.COORDS_PER_VERTEX;
        int rib2PosIndex = (3*mD.bodyVerticesNum/4-2)*D3GLES20.COORDS_PER_VERTEX;
        float[] rib1Pos = {mD.bodyVerticesData[rib1PosIndex], 
        		mD.bodyVerticesData[rib1PosIndex + 1],
        		mD.bodyVerticesData[rib1PosIndex + 2]
        };
        float[] rib2Pos = {mD.bodyVerticesData[rib2PosIndex], 
        		mD.bodyVerticesData[rib2PosIndex + 1],
        		mD.bodyVerticesData[rib2PosIndex + 2]
        };
        mD.mRibsModelMatrix = mD.mModelMatrix.clone();
        Matrix.translateM(mD.mRibsModelMatrix, 0, rib1Pos[0], rib1Pos[1], rib1Pos[2]);
//        Matrix.rotateM(mD.mRibsModelMatrix, 0, mD.mRibsModelMatrix, 0, bodyBAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mD.mRibsModelMatrix, 0, bodyBAnglePredicted, 0, 0, 1);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mRibsModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.ribVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.ribVerticesNum);
        
        
        mD.mRibsModelMatrix = mD.mModelMatrix.clone();
        Matrix.translateM(mD.mRibsModelMatrix, 0, rib2Pos[0], rib2Pos[1], rib2Pos[2]);
//        Matrix.rotateM(mD.mRibsModelMatrix, 0, mD.mRibsModelMatrix, 0, bodyCAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mD.mRibsModelMatrix, 0, bodyCAnglePredicted, 0, 0, 1);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mRibsModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.ribVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.ribVerticesNum);
        
        // Feet
        
        Matrix.rotateM(mD.mFeetModelMatrix, 0, mD.mModelMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
        Matrix.translateM(mD.mFeetModelMatrix, 0, 
        		mD.leftFootPosition[0], mD.leftFootPosition[1], 0);
        Matrix.rotateM(mD.mFeetModelMatrix, 0, mD.mLeftFootAngle, 0, 0, 1);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mFeetModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.leftFinVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.finVerticesNum);
        
        Matrix.rotateM(mD.mFeetModelMatrix, 0, mD.mModelMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
        Matrix.translateM(mD.mFeetModelMatrix, 0, 
        		-mD.leftFootPosition[0], mD.leftFootPosition[1], 0);
        Matrix.rotateM(mD.mFeetModelMatrix, 0, -mD.mRightFootAngle, 0, 0, 1);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mFeetModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.rightFinVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.finVerticesNum);
        
        // Head
        Matrix.rotateM(mD.mHeadModelMatrix, 0, mD.mModelMatrix, 0, bodyStartAnglePredicted, 0, 0, 1);
        Matrix.translateM(mD.mHeadModelMatrix , 0, 
        		mD.headPosition[0], mD.headPosition[1], 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mHeadModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.headVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
		GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.headVerticesNum );
		
		//EYE
//        mHeadModelMatrix = mModelMatrix.clone();
       
        Matrix.translateM(mD.mHeadModelMatrix , 0, 
        		mD.eyePosition[0], mD.eyePosition[1], 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mHeadModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
		GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.eyeVertexBuffer);
		GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mD.eyeDetailsLevel);
	}
	
	private boolean colorIsBgColor() {
		for (int i = 0; i < 3; ++i) {
			if (D3GLES20.compareFloats(mD.preyColor[i], TheHuntRenderer.bgColor[i]) != 0) {
				return false;
			}
		}
		return true;
	}

	private void fadeColor() {
		for (int i = 0; i < mD.preyColor.length; ++i) {
			mD.preyColor[i] += colorFadeSpeed ;
		}
	}

	private void updateBodyVertexBuffer() {
		Matrix.multiplyMV(mD.bodyStartRot, 0, mD.mBodyStartRMatrix, 0, mD.bodyStart4, 0);
		Matrix.multiplyMV(mD.bodyBRot, 0, mD.mBodyBRMatrix, 0, mD.bodyB4, 0);
		Matrix.multiplyMV(mD.bodyCRot, 0, mD.mBodyCRMatrix, 0, mD.bodyC4, 0);
		Matrix.multiplyMV(mD.bodyEndRot, 0, mD.mBodyEndRMatrix, 0, mD.bodyEnd4, 0);
		mD.bodyVerticesData = D3GLES20.quadBezierCurveVertices(mD.bodyStartRot, mD.bodyBRot, mD.bodyCRot, mD.bodyEndRot, mD.detailsStep, mD.bodyLength);
		mD.bodyVerticesNum = mD.bodyVerticesData.length/D3GLES20.COORDS_PER_VERTEX;
		mD.bodyVertexBuffer = D3GLES20.newFloatBuffer(mD.bodyVerticesData);
	}

	public PointF getWorldPosition() {
		return new PointF(D3GLES20.toWorldWidth(mD.mPosX), D3GLES20.toWorldHeight(mD.mPosY));
	}

	public PointF getPosition() {
		return new PointF(mD.mPosX, mD.mPosY);
	}

	private void doAction(Action nextAction) {
		if (nextAction == null) return;
		switch(nextAction) {
		case TURN_LEFT_SMALL: turn(TurnAngle.LEFT_SMALL);break;//flopLeft(); break;
		case TURN_LEFT_MEDIUM: turn(TurnAngle.LEFT_MEDIUM);break;
		case TURN_LEFT_LARGE: turn(TurnAngle.LEFT_LARGE);break;
		case TURN_RIGHT_SMALL: turn(TurnAngle.RIGHT_SMALL);break;//flopRight(); break;
		case TURN_RIGHT_MEDIUM: turn(TurnAngle.RIGHT_MEDIUM);break;
		case TURN_RIGHT_LARGE: turn(TurnAngle.RIGHT_LARGE);break;
		case FORWARD_SMALL: backFinMotion(TurnAngle.BACK_SMALL); break;
		case FORWARD_MEDIUM: backFinMotion(TurnAngle.BACK_MEDIUM); break;
		case FORWARD_LARGE: backFinMotion(TurnAngle.BACK_LARGE); break;
		case eat: eat(); break; 
		case none: break;
		default: Log.v(TAG, "Could not process action!");
		}
	}

	private void eat() {
//		Log.v(TAG, "Eating food at " + mD.mPosHeadX + " " + mD.mPosHeadY);
		mEnv.eatFood(mD.mPosHeadX, mD.mPosHeadY);
		mWorldModel.eatFood(mD.mPosHeadX, mD.mPosHeadY);
	}

	public void setCaught(boolean caught) {
		mIsCaught = caught;
		if (caught) {
			mD.vx = mD.vy = bodyEndAngleRot =
				bodyStartAngleRot = bodyBAngleRot 
				= bodyCAngleRot = 0;
		}
	}

	public boolean getCaught() {
		return mIsCaught;
	}

	public void release() {
		mD.preyColor = mD.preyColorDefault.clone();
		mIsCaught = false;
//		mD.mPosX = mD.mPosY = 0;
		randomizePos();
//		mD.bodyStartAngle = mD.bodyBAngle = mD.bodyCAngle = mD.bodyEndAngle = 0;
		mPlanner.clear();
		calcPosHead();
		mWorldModel.update(mSensor.sense(mD.mPosHeadX, mD.mPosHeadY, mD.mPosX, mD.mPosY));
		mWorldModel.recalcNearestFood();
	}

	private void randomizePos() {
		Random rand = new Random();
		mD.mPosX = -EnvironmentData.mScreenWidth/2+rand.nextFloat()*EnvironmentData.mScreenWidth;
		mD.mPosY = -EnvironmentData.mScreenHeight/2+rand.nextFloat()*EnvironmentData.mScreenHeight;
	}
}
