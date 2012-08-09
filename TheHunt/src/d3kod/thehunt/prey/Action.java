package d3kod.thehunt.prey;

public enum Action {
	TURN_LEFT_SMALL(Prey.SMALL_TICKS_PER_TURN), TURN_LEFT_MEDIUM(Prey.MEDIUM_TICKS_PER_TURN), 
	TURN_LEFT_LARGE(Prey.LARGE_TICKS_PER_TURN), 
	TURN_RIGHT_SMALL(Prey.SMALL_TICKS_PER_TURN), TURN_RIGHT_MEDIUM(Prey.MEDIUM_TICKS_PER_TURN), 
	TURN_RIGHT_LARGE(Prey.LARGE_TICKS_PER_TURN), 
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
