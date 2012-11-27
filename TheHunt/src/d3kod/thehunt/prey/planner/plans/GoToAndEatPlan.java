package d3kod.thehunt.prey.planner.plans;

import android.util.Log;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.Prey;
import d3kod.thehunt.prey.memory.MoodLevel;
import d3kod.thehunt.prey.memory.StressLevel;
import d3kod.thehunt.prey.memory.WorldModel;

public class GoToAndEatPlan extends GoToPlan {

	private static final String TAG = "GoToAndEatPlan";
	private boolean ate;
	
	public GoToAndEatPlan(float hX, float hY, float bX, float bY, Event target) {
		super(hX, hY, bX, bY, target);
		ate = false;
	}

	@Override
	public void update(WorldModel mWorldModel) {
		if (ate) return;
		if (mWorldModel.getStressLevel() == StressLevel.PLOK_CLOSE && mWorldModel.getMoodLevel() != MoodLevel.DESPAIR) {
			Log.v(TAG, "Plok close!");
			finish();
		}
		else super.update(mWorldModel);
		if (arrived) {
			finish();
			addNextAction(Action.eat);
			ate = true;
//			logActions();
			return;
		}
	}
	
//	@Override
//	protected boolean closeEnough(float headFromTarget) {
//		return headFromTarget < Prey.EAT_FOOD_RADIUS;
//	}
}
