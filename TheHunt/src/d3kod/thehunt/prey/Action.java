package d3kod.thehunt.prey;

public enum Action {
	TURN_LEFT_SMALL(PreyData.SMALL_TICKS_PER_TURN), TURN_LEFT_MEDIUM(PreyData.MEDIUM_TICKS_PER_TURN), 
	TURN_LEFT_LARGE(PreyData.LARGE_TICKS_PER_TURN), 
	TURN_RIGHT_SMALL(PreyData.SMALL_TICKS_PER_TURN), TURN_RIGHT_MEDIUM(PreyData.MEDIUM_TICKS_PER_TURN), 
	TURN_RIGHT_LARGE(PreyData.LARGE_TICKS_PER_TURN), 
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
