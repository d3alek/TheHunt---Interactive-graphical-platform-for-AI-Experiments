package d3kod.thehunt.prey.planner.plans;

import d3kod.thehunt.prey.Action;

public class NoPlan extends Plan {
	public NoPlan(float hX, float hY) {
		super(hX, hY);
	}
	@Override
	public Action nextAction() {
		return Action.none;
	}
}
