package d3kod.thehunt.prey.planner;

import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

public class GoToPlan extends MoveTowardsPlan {

	protected boolean arrived;
	private static final float DISTANCE_ENOUGH = 0.1f;
	
	public GoToPlan(float hX, float hY, float bX, float bY, float tX, float tY) {
		super(hX, hY, bX, bY, tX, tY);
		arrived = false;
	}

	@Override
	public void update(WorldModel mWorldModel) {
		if (arrived) return;
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3GLES20.distance(hX, hY, tX, tY);
		
		if (headFromTarget <= DISTANCE_ENOUGH) {
			arrived = true;
//			if (isEmpty()) addNextAction(Action.none);
			return;
		}
		super.update(mWorldModel);
	}
	
}
