package d3kod.thehunt.prey.planner.plans;

import java.util.Random;

import android.graphics.PointF;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.environment.Dir;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

public class ExplorePlan extends Plan {

//	private boolean mRotating;
//	private int mRotStep;

	private static final String TAG = "Explore plan";
	private Random mRandom;

	Action[] wanderActions = {
			Action.TURN_LEFT_SMALL, Action.TURN_RIGHT_SMALL
	};
	
	public ExplorePlan(WorldModel worldModel) {
		super(0, 0, 0, null);
		mRandom = new Random();
	}
	
	@Override
	public void update(WorldModel worldModel) {
		Dir curDir = worldModel.getCurrentDir();
		if (curDir == Dir.UNDEFINED) {
			super.addNextAction(chooseRandomWanderAction());
		}
		else {
			super.addNextAction(
					getFollowDirAction(
							curDir, worldModel.getBodyX(), worldModel.getBodyY(), 
							worldModel.getHeadX(), worldModel.getHeadY()));
		}
	}
	
	private Action getFollowDirAction(Dir curDir, float bodyX, float bodyY, float headX, float headY) {
		PointF currendDirDelta = curDir.getDelta();
		float det = D3Maths.det(bodyX, bodyY, 
				headX, headY, 
				headX + currendDirDelta.x, headY + currendDirDelta.y);
		if (det < 0) {
			return Action.TURN_RIGHT_LARGE;
		}
		else return Action.TURN_LEFT_LARGE;
	}

	protected Action chooseRandomWanderAction() {
		int actionInd = Math.round((wanderActions.length-1) * mRandom.nextFloat());
		return wanderActions[actionInd];
	}
	
	@Override
	public boolean isFinished() {
		return true;
	}

	public Random getRandom() {
		return mRandom;
	}

	public boolean isRapid() {
		return false;
	}
	
}
