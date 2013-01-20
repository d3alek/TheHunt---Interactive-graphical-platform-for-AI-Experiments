package d3kod.thehunt.agent.prey.planner.plans;

import d3kod.thehunt.agent.prey.Action;
import d3kod.thehunt.agent.prey.memory.WorldModel;


public class RapidExplorePlan extends ExplorePlan {

	Action[] wanderActions = {
			Action.TURN_LEFT_LARGE, Action.TURN_RIGHT_LARGE, Action.FORWARD_LARGE
	};
	
	public RapidExplorePlan(WorldModel worldModel) {
		super(worldModel);
	}
	
	@Override
	protected Action chooseRandomWanderAction() {
		int actionInd = Math.round((wanderActions.length-1) * getRandom().nextFloat());
		return wanderActions[actionInd];
	}
	
	@Override
	public boolean isRapid() {
		return true;
	}
}
