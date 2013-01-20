package d3kod.thehunt.agent.prey.planner.plans;

import d3kod.thehunt.agent.prey.Action;

public class ParallelAction {
	private int mTicks;
	private Action mAction;
	private boolean mStarted;
	
	public ParallelAction(Action action) {
		mAction = action;
		mTicks = mAction.getTicks();
		mStarted = false;
	}
	
	public int getTicks() {
		return mTicks;
	}
	public Action getAction() {
		return mAction;
	}
	public void tickOnce() {
		mTicks--;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mAction == null) ? 0 : mAction.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParallelAction other = (ParallelAction) obj;
		if (mAction != other.mAction)
			return false;
		return true;
	}

	public boolean started() {
		return mStarted;
	}
	
	public void start() {
		mStarted = true;
	}
	
}
