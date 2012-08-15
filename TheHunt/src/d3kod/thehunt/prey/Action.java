package d3kod.thehunt.prey;

public enum Action {
	TURN_LEFT_SMALL(Prey.flopBackTicks), TURN_LEFT_MEDIUM(Prey.flopBackTicks), 
	TURN_LEFT_LARGE(Prey.flopBackTicks), 
	TURN_RIGHT_SMALL(Prey.flopBackTicks), TURN_RIGHT_MEDIUM(Prey.flopBackTicks), 
	TURN_RIGHT_LARGE(Prey.flopBackTicks), 
	FORWARD_SMALL(Prey.flopBackTicks), FORWARD_MEDIUM(Prey.flopBackTicks), 
	FORWARD_LARGE(Prey.flopBackTicks), eat(1), none(1);

	private int mTicks;

	private Action(int ticks) {
		mTicks = ticks;
	}
	
	public int getTicks() {
		return mTicks;
	}
}
