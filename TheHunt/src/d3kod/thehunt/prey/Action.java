package d3kod.thehunt.prey;

public enum Action {
	TURN_LEFT_SMALL(PreyData.SMALL_TICKS_BACK_PER_TURN), TURN_LEFT_MEDIUM(PreyData.MEDIUM_TICKS_BACK_PER_TURN), 
	TURN_LEFT_LARGE(PreyData.LARGE_TICKS_BACK_PER_TURN), 
	TURN_RIGHT_SMALL(PreyData.SMALL_TICKS_BACK_PER_TURN), TURN_RIGHT_MEDIUM(PreyData.MEDIUM_TICKS_BACK_PER_TURN), 
	TURN_RIGHT_LARGE(PreyData.LARGE_TICKS_BACK_PER_TURN), 
	FORWARD_SMALL(PreyData.SMALL_TICKS_BACK_PER_TURN), FORWARD_MEDIUM(PreyData.MEDIUM_TICKS_BACK_PER_TURN), 
	FORWARD_LARGE(PreyData.LARGE_TICKS_BACK_PER_TURN), eat(PreyData.EAT_TICKS), none(1), poop(1);

	private int mTicks; 

	private Action(int ticks) {
		mTicks = ticks;
	}
	
	public int getTicks() {
		return mTicks;
	}
	
	public void tickOnce() {
		mTicks--;
	}
}
