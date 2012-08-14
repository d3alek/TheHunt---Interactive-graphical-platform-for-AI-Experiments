package d3kod.thehunt.prey.planner.plans;

import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.planner.Plan;

public class NoPlan extends Plan {
	public NoPlan(float hX, float hY) {
		super(hX, hY);
	}
	@Override
	public Action nextAction() {
		return Action.none;
	}
}
