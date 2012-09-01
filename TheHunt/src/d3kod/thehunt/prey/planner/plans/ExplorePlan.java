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
//		mRotating = true;
//		mRotStep = 0;
	}
	
	@Override
	public void update(WorldModel worldModel) {
		Dir curDir = worldModel.getCurrentDir();
		if (curDir == Dir.UNDEFINED) {
			super.addNextAction(chooseRandomWanderAction());
		}
		else {
			PointF currendDirDelta = curDir.getDelta();
			float headX = worldModel.getHeadX();
			float headY = worldModel.getHeadY();
			float headWithCurrentX = headX + currendDirDelta.x;
			float headWithCurrentY = headY + currendDirDelta.y;
			float det = D3Maths.det(worldModel.getBodyX(), worldModel.getBodyY(), 
					headX, headY, headWithCurrentX, headWithCurrentY);
			if (det < 0) {
				super.addNextAction(Action.TURN_RIGHT_LARGE);
			}
			else super.addNextAction(Action.TURN_LEFT_LARGE);
		}
	}
	
	private Action chooseRandomWanderAction() {
		int actionInd = Math.round((wanderActions.length-1) * mRandom.nextFloat());
		return wanderActions[actionInd];
	}
	
	@Override
	public boolean isFinished() {
		return true;
	}
	
}
