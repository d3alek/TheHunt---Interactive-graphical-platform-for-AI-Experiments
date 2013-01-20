package d3kod.thehunt.agent.prey.planner.plans;

import d3kod.thehunt.agent.prey.Action;

public class NoPlan extends Plan {
	public NoPlan(float hX, float hY) {
		super(hX, hY);
	}
	@Override
	public Action nextAction() {
		return Action.none;
	}
}
