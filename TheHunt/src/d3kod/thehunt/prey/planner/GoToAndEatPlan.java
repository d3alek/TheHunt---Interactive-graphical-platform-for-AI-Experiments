package d3kod.thehunt.prey.planner;

import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

public class GoToAndEatPlan extends GoToPlan {

	private boolean ate;

	public GoToAndEatPlan(float hX, float hY, float bX, float bY, float tX,
			float tY) {
		super(hX, hY, bX, bY, tX, tY);
		ate = false;
	}

	@Override
	public void update(WorldModel mWorldModel) {
		if (ate) return;
		super.update(mWorldModel);
		if (arrived) {
			finish();
			addNextAction(Action.eat);
			ate = true;
			logActions();
			return;
		}
	}
	
}
