package d3kod.thehunt.prey.planner.plans;

import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.prey.memory.WorldModel;

public class GotoAndStayWhileHidden extends GoToAndStayPlan {

	public GotoAndStayWhileHidden(float hX, float hY, float bX, float bY,
			Event target) {
		super(hX, hY, bX, bY, target);
	}
	
	@Override
	public void update(WorldModel mWorldModel) {
		super.update(mWorldModel);
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3Maths.distance(hX, hY, getTX(), getTY());
		if (super.arrived(headFromTarget)) {
			if (mWorldModel.getLightLevel() != 0) {
				mWorldModel.noAlgaeHere();
			}
		}
	}
}
