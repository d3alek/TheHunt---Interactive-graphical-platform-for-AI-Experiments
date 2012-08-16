package d3kod.thehunt.prey.planner.plans;

import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

public class GoToAndStayPlan extends MoveTowardsPlan {
	//private static final float SLOW_DOWN_DISTANCE = 0.5f;
	private static final float DISTANCE_ENOUGH = 0.05f;
	private static final float DISTANCE_MEDIUM = 0.5f;
	private static final float DISTANCE_SMALL = 0.1f;
	private static final int KEEP_DISTANCE_FOR = 1;
	
	private int slowdown;
	private int keepDistanceCounter;
	private int slowdownCounter;
	
	public GoToAndStayPlan(float hX, float hY, float bX, float bY, float tX,
			float tY) {
		super(hX, hY, bX, bY, tX, tY);
		keepDistanceCounter = 0;
		slowdownCounter = 0;
	}
	
	@Override
	public void update(WorldModel mWorldModel) {
		
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3GLES20.distance(hX, hY, tX, tY);
		
		if (headFromTarget <= DISTANCE_ENOUGH) keepDistanceCounter++;
		else keepDistanceCounter = 0;
		
		if (keepDistanceCounter >= KEEP_DISTANCE_FOR) {
			finish();
			return;
		}
		
		super.update(mWorldModel);
		
		if (getNextAction() == Action.FORWARD_LARGE) {
			if (headFromTarget < DISTANCE_SMALL) {
				changeNextAction(Action.FORWARD_SMALL);
			}
			else if (headFromTarget < DISTANCE_MEDIUM) {
				changeNextAction(Action.FORWARD_MEDIUM);
			}
		}
	}
}
