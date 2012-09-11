package d3kod.thehunt.prey.planner.plans;

import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.prey.memory.WorldModel;

public class GoToPlan extends MoveTowardsPlan {

	protected boolean arrived;
	private static final float DISTANCE_ENOUGH = 0.1f;
	
	public GoToPlan(float hX, float hY, float bX, float bY, Event target) {
		super(hX, hY, bX, bY, target);
		arrived = false;
	}

	@Override
	public void update(WorldModel mWorldModel) {
		if (arrived) return;
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3Maths.distance(hX, hY, getTX(), getTY());
		
		if (headFromTarget <= DISTANCE_ENOUGH) {
			arrived = true;
//			if (isEmpty()) addNextAction(Action.none);
			return;
		}
		super.update(mWorldModel);
	}
	
}
