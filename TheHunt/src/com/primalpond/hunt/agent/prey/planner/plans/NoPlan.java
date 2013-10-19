package com.primalpond.hunt.agent.prey.planner.plans;

import com.primalpond.hunt.agent.prey.Action;

public class NoPlan extends Plan {
	public NoPlan(float hX, float hY) {
		super(hX, hY);
	}
	@Override
	public Action nextAction() {
		return Action.none;
	}
}
