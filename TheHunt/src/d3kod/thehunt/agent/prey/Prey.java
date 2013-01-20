package d3kod.thehunt.agent.prey;

import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.FloatMath;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Shape;
import d3kod.graphics.texture.TextureManager;
import d3kod.thehunt.agent.Agent;
import d3kod.thehunt.agent.prey.memory.StressLevel;
import d3kod.thehunt.agent.prey.memory.WorldModel;
import d3kod.thehunt.agent.prey.planner.PlanState;
import d3kod.thehunt.agent.prey.planner.Planner;
import d3kod.thehunt.agent.prey.sensor.Sensor;
import d3kod.thehunt.world.environment.Environment;
import d3kod.thehunt.world.environment.EnvironmentData;
import d3kod.thehunt.world.environment.NAlgae;
import d3kod.thehunt.world.floating_text.CrunchText;
import d3kod.thehunt.world.floating_text.FlopText;
import d3kod.thehunt.world.floating_text.PanicText;
import d3kod.thehunt.world.floating_text.PlokText;
import d3kod.thehunt.world.logic.TheHuntRenderer;

public class Prey extends Agent {

	private static final String TAG = "Prey";
	public static final float EAT_FOOD_RADIUS = 0.1f;

	private Planner mPlanner;
	private WorldModel mWorldModel;
	private Sensor mSensor;
	private Environment mEnv;
	private PreyData mD;

	public static final float bodyToHeadLength = 0.07f;

	private static final float FORCE_TO_DISTANCE = 0.00002f;

	public static int flopBacksPerSecond = 2;
	public static int flopBackTicks = TheHuntRenderer.TICKS_PER_SECOND/flopBacksPerSecond;
	private float flopBackSpeed;
	private boolean floppedThird;
	private TextureManager tm;

	private D3Prey mGraphic;

	private SpriteManager mD3GLES20;

	public void update() {
		if (mD.mIsCaught) return;

		calcPosHeadandTail();

		if (PreyData.AI) {
			updateWorldModel();
			doAction(mPlanner.nextAction(mWorldModel));
			doAction(mPlanner.nextParallelAction());
		}
		move();

		if (mWorldModel.getLightLevel() == 0) {
			mGraphic.setHiddenColor();
		}
		else {
			mGraphic.resetColor();
		}

		if (mPlanner.getState() == PlanState.FORAGE) {
			mGraphic.openMouth();
		}
		else {
			mGraphic.closeMouth();
		}
	}

	private void updateWorldModel() {
		mWorldModel.update(mSensor.sense(mD.mPosHeadX, mD.mPosHeadY, mD.mPosX, mD.mPosY, mD.bodyStartAngle));
		expressEmotion();
		if (mD.emotionText != null) {
			if (mD.emotionText.isExpired()) {
				mD.emotionText = null;
			}
		}
	}


	private void expressEmotion() {
		if (mWorldModel.getStressLevel().compareTo(StressLevel.CAUTIOS) > 0) {
			if (mD.emotionText != null) {
				mD.emotionText.noFade();
			}

			else {
				mD.emotionText = new PanicText(mD.mPosHeadX, mD.mPosHeadY, mD.bodyStartAngle, tm, mD3GLES20.getShaderManager());
				mD3GLES20.putExpiringShape(mD.emotionText);
			}
		}
	}

	private void calcPosHeadandTail() {
		float[] posTemp = { 0.0f, bodyToHeadLength, 0.0f, 1.0f };
		float[] posTail = { D3Prey.tailPosition[0], D3Prey.tailPosition[1], 0.0f, 1.0f };

		Matrix.setIdentityM(mD.mHeadPosMatrix, 0);
		Matrix.translateM(mD.mHeadPosMatrix, 0, mD.mPosX, mD.mPosY, 0);
		Matrix.rotateM(mD.mHeadPosMatrix, 0, mD.bodyStartAngle, 0, 0, 1);
		Matrix.multiplyMV(posTemp, 0, mD.mHeadPosMatrix, 0, posTemp, 0);

		Matrix.setIdentityM(mD.mTailPosPatrix, 0);
		Matrix.translateM(mD.mTailPosPatrix, 0, mD.mPosX, mD.mPosY, 0);
		Matrix.multiplyMV(posTail, 0, mD.mTailPosPatrix, 0, posTail, 0);
		mD.mPosHeadX = posTemp[0]; 
		mD.mPosHeadY = posTemp[1];
		mD.mPosTail = new PointF(posTail[0], posTail[1]);
	}

	public void move() {
		if (mD.mIsCaught) return;
		if (mD.bodyBendCounter == 0) {
			mD.bodyEndAngleTarget = mD.bodyCAngleTarget;
			mD.bodyEndSpeed = mD.bodyCSpeed;
			mD.bodyCAngleTarget = mD.bodyBAngleTarget;
			mD.bodyCSpeed = mD.bodyBSpeed;
			mD.bodyBAngleTarget = mD.bodyStartAngleTarget;
			mD.bodyBSpeed = mD.rotateSpeedHead;
			mD.bodyBendCounter = PreyData.BODY_BEND_DELAY-1;
		}
		else {
			--mD.bodyBendCounter;
		}

		if (mD.bodyStartAngleTarget > mD.bodyStartAngle + mD.rotateSpeedHead) mD.bodyStartAngleRot = mD.rotateSpeedHead;
		else if (mD.bodyStartAngleTarget < mD.bodyStartAngle - mD.rotateSpeedHead) mD.bodyStartAngleRot = -mD.rotateSpeedHead;
		else {
			mD.bodyStartAngleRot = 0;
			mD.bodyStartAngle = mD.bodyStartAngleTarget;
		}

		if (mD.bodyBAngleTarget > mD.bodyBAngle + mD.bodyBSpeed) mD.bodyBAngleRot = +mD.bodyBSpeed;
		else if (mD.bodyBAngleTarget < mD.bodyBAngle - mD.bodyBSpeed) mD.bodyBAngleRot = -mD.bodyBSpeed;
		else {
			mD.bodyBAngleRot = 0;
			mD.bodyBAngle = mD.bodyBAngleTarget;
		}

		if (mD.bodyCAngleTarget > mD.bodyCAngle + mD.bodyCSpeed) mD.bodyCAngleRot = +mD.bodyCSpeed;
		else if (mD.bodyCAngleTarget < mD.bodyCAngle - mD.bodyCSpeed) mD.bodyCAngleRot = -mD.bodyCSpeed;
		else {
			mD.bodyCAngleRot = 0;
			mD.bodyCAngle = mD.bodyCAngleTarget;
		}

		if (!mD.flopBack) {
			if (mD.bodyEndAngleTarget > mD.bodyEndAngle + mD.bodyEndSpeed) mD.bodyEndAngleRot = mD.bodyEndSpeed;
			else if (mD.bodyEndAngleTarget < mD.bodyEndAngle - mD.bodyEndSpeed) mD.bodyEndAngleRot = -mD.bodyEndSpeed;
			else {
				mD.bodyEndAngleRot = 0;
				mD.bodyEndAngle = mD.bodyEndAngleTarget;
			}
		}

		if (mD.flopBack) doFlopBack();

		mD.bodyStartAngle += mD.bodyStartAngleRot;
		mD.bodyBAngle += mD.bodyBAngleRot;
		mD.bodyCAngle += mD.bodyCAngleRot;
		if (!mD.flopBack) {
			mD.bodyEndAngle += mD.bodyEndAngleRot;
		}

		applyFriction();
		mD.mPosX += mD.vx ; mD.mPosY += mD.vy;
	}
	private boolean stoppedTurning() {
		return (mD.bodyStartAngleTarget == mD.bodyBAngleTarget 
				&& mD.bodyBAngleTarget == mD.bodyCAngleTarget);
	}

	private void doFlopBack() {
		if (!mD.floppedFirst) {
			if (mD.flopBackTargetFirst > mD.flopBackAngle + flopBackSpeed) mD.flopBackAngle += flopBackSpeed;
			else if (mD.flopBackTargetFirst < mD.flopBackAngle - flopBackSpeed) mD.flopBackAngle -= flopBackSpeed;
			else {
				mD.flopBackAngle = mD.flopBackTargetFirst;
				mD.floppedFirst = true;
				moveForward(Math.abs(mD.backFinAngle*flopBackSpeed)); // F = ma
						putFlopText(mD.flopBackAngle + mD.bodyCAngle);
			}
			mD.bodyEndAngleRot = mD.bodyCAngle + mD.flopBackAngle-mD.bodyEndAngle;
			mD.bodyEndAngle = mD.bodyCAngle + mD.flopBackAngle;
		}
		else if (!mD.floppedSecond) {
			if (mD.flopBackTargetSecond > mD.flopBackAngle + flopBackSpeed) mD.flopBackAngle += flopBackSpeed;
			else if (mD.flopBackTargetSecond < mD.flopBackAngle - flopBackSpeed) mD.flopBackAngle -= flopBackSpeed;
			else {
				mD.flopBackAngle = mD.flopBackTargetSecond;
				mD.floppedSecond = true;
				moveForward(Math.abs(2*mD.backFinAngle*flopBackSpeed)); // F = ma
				putFlopText(mD.flopBackAngle + mD.bodyCAngle);
			}
			mD.bodyEndAngleRot = mD.bodyCAngle + mD.flopBackAngle-mD.bodyEndAngle;
			mD.bodyEndAngle = mD.bodyCAngle + mD.flopBackAngle;
		}
		else {
			//flopping third
			if (0 > mD.flopBackAngle + flopBackSpeed) mD.flopBackAngle += flopBackSpeed;
			else if (0 < mD.flopBackAngle - flopBackSpeed) mD.flopBackAngle -= flopBackSpeed;
			else {
				mD.flopBackAngle = 0;
				floppedThird = true;
				moveForward(Math.abs(mD.backFinAngle*flopBackSpeed)); // F = ma
			}
			mD.bodyEndAngleRot = mD.bodyCAngle + mD.flopBackAngle-mD.bodyEndAngle;
			mD.bodyEndAngle = mD.bodyCAngle + mD.flopBackAngle;
		}
		if (mD.floppedFirst && mD.floppedSecond && floppedThird) {
			if (mD.turningBackFinMotion) {
				mD.turningBackFinMotion = false;
				mD.flopBack = false;
			}
			else {
				mD.flopBack = false;
			}
		}
	}
	private void putFlopText(float angle) {
		float radAngle = (float)Math.toRadians(angle);
		mD3GLES20.putExpiringShape(new FlopText(mD.mPosX + FloatMath.sin(radAngle)*D3Prey.finSize*2, 
				mD.mPosY - FloatMath.cos(radAngle)*D3Prey.finSize*2, angle, tm, mD3GLES20.getShaderManager()));
	}

	public void turn(TurnAngle angle) {
		int value = angle.getValue();

		if (mD.bodyStartAngleTarget + value - mD.bodyCAngle > mD.MAX_BODY_BEND_ANGLE 
				|| mD.bodyStartAngleTarget + value - mD.bodyCAngle < -mD.MAX_BODY_BEND_ANGLE) {
			Log.v(TAG, "Can't bend that much!");
			return;
		}

		mD.rotateSpeedHead = angle.getRotateSpeed();
		mD.bodyStartAngleTarget += value;

		if (!mD.turningBackFinMotion) {
			mD.turningBackFinMotion = true;
			mD.turningBackFinAngle = angle.getBackAngle();
			backFinMotion(mD.turningBackFinAngle);
		}

	}

	public void backFinMotion(TurnAngle angle) {
		mD.flopBack = true;
		mD.backFinAngle = angle.getValue();
		mD.bodyEndAngle = mD.bodyCAngle;
		mD.flopBackTargetFirst = +mD.backFinAngle;
		mD.flopBackAngle = 0;
		mD.flopBackTargetSecond = -mD.backFinAngle;
		mD.floppedFirst = false;
		mD.floppedSecond = false;
		floppedThird = false;
		flopBackSpeed = angle.getRotateSpeed();
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

	public Prey(Environment env, TextureManager texMan, SpriteManager d3gles20) {
		super(env, d3gles20);
		mD3GLES20 = d3gles20;
		mD = new PreyData();

		tm = texMan;
		mWorldModel = new WorldModel();
		mEnv = env;
		mSensor = new Sensor(mEnv);
		mD.mPosX = mD.mPosY = 0;

		mD.rotateSpeedHead = mD.rotateSpeedSmall;//Math.abs(TurnAngle.LEFT_SMALL.getValue())/SMALL_TICKS_PER_TURN;
		//		mGraphicSet = false;
		mD.mIsCaught = false;
		mD.emotionText = null;
	}

	public PointF getPosition() {
		return new PointF(mD.mPosX, mD.mPosY);
	}
	public PointF getHeadPositon() {
		return new PointF(mD.mPosHeadX, mD.mPosHeadY);
	}
	private void doAction(Action nextAction) {
		if (nextAction == null) {
			return;
		}
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
		case eat: Log.v(TAG, "Doing eat action!"); eat(); break; 
		case poop: poop(); break;
		case none: break;
		default: Log.v(TAG, "Could not process action!");
		}
	}

	private void poop() {
		//TODO D3Prey does not use getCenter... !!! 
		mEnv.addNewAlgae(1, new PointF(mD.mPosTail.x, mD.mPosTail.y), D3Maths.getRandAngle());
		mWorldModel.reduceEnergy(NAlgae.FOOD_ALGAE_BITE_NUTRITION);
	}

	private void eat() {
		int nutrition = mEnv.eatFood(mD.mPosHeadX, mD.mPosHeadY);
		if (nutrition == 0) {
			Log.v(TAG, "I thought I ate something, but it felt like thin air :?");
			mWorldModel.eatFood(0);
		}
		else {
			mWorldModel.eatFood(nutrition);
			mGraphic.initEatingMotion();
			mD3GLES20.putExpiringShape(new CrunchText(mD.mPosHeadX, mD.mPosHeadY, tm, mD3GLES20.getShaderManager()));
		}
	}

	public void setCaught(boolean caught) {
		mD.mIsCaught = caught;
		if (caught) {
			mD.vx = mD.vy = mD.bodyEndAngleRot = 
					mD.bodyStartAngleRot = mD.bodyBAngleRot 
					= mD.bodyCAngleRot = 0;
		}
	}

	public boolean getCaught() {
		return mD.mIsCaught;
	}

	public void release() {
		mD.mIsCaught = false;
		PointF newPos = mEnv.randomPosInEnv();
		mD.mPosX = newPos.x; mD.mPosY = newPos.y;
		mPlanner.clear();
		calcPosHeadandTail();
		updateWorldModel();
		mWorldModel.refillEnergy();
		mWorldModel.recalcNearestFood();
		mD3GLES20.putExpiringShape(new PlokText(mD.mPosX, mD.mPosY, tm, mD3GLES20.getShaderManager()));
		mGraphic.resetColor();
	}

	public void initGraphic() {
		mGraphic = new D3Prey(mD);
		initGraphic(mGraphic);
		mPlanner = new Planner();
		PointF newPos = mEnv.randomPosInEnv();
		mD.mPosX = newPos.x; mD.mPosY = newPos.y;
	}

	public int getEnergy() {
		return mWorldModel.getEnergy();
	}

	public String getStateString() {
		return mPlanner.getState().toString();
	}

	public PointF getPredictedPosition() {
		return new PointF(mGraphic.getPredictedX(), mGraphic.getPredictedY());
	}

	public D3Shape getGraphic() {
		return mGraphic;
	}
}
