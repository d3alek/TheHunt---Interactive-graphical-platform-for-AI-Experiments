package d3kod.thehunt.prey;

public enum TurnAngle {
	BACK_LEFT_SMALL(PreyData.angleFlopBackSmall, PreyData.rotateSpeedBackSmall), 
	BACK_LEFT_MEDIUM(PreyData.angleFlopBackMedium, PreyData.rotateSpeedBackMedium), 
	BACK_LEFT_LARGE(PreyData.angleFlopBackLarge, PreyData.rotateSpeedBackLarge), 
	BACK_RIGHT_SMALL(-PreyData.angleFlopBackSmall, PreyData.rotateSpeedBackSmall), 
	BACK_RIGHT_MEDIUM(-PreyData.angleFlopBackMedium, PreyData.rotateSpeedBackMedium), 
	BACK_RIGHT_LARGE(-PreyData.angleFlopBackLarge, PreyData.rotateSpeedBackLarge), 
	BACK_SMALL(PreyData.angleFlopBackSmall, PreyData.rotateSpeedBackSmall), 
	BACK_MEDIUM(PreyData.angleFlopBackMedium, PreyData.rotateSpeedBackMedium), 
	BACK_LARGE(PreyData.angleFlopBackLarge, PreyData.rotateSpeedBackLarge),
	LEFT_SMALL(PreyData.angleFlopSmall, PreyData.rotateSpeedSmall, BACK_RIGHT_SMALL), 
	LEFT_MEDIUM(PreyData.angleFlopMedium, PreyData.rotateSpeedMedium, BACK_RIGHT_MEDIUM), 
	LEFT_LARGE(PreyData.angleFlopLarge, PreyData.rotateSpeedLarge, BACK_RIGHT_LARGE), 
	RIGHT_SMALL(-PreyData.angleFlopSmall, PreyData.rotateSpeedSmall, BACK_LEFT_SMALL), 
	RIGHT_MEDIUM(-PreyData.angleFlopMedium, PreyData.rotateSpeedMedium, BACK_LEFT_MEDIUM), 
	RIGHT_LARGE(-PreyData.angleFlopLarge, PreyData.rotateSpeedLarge, BACK_LEFT_LARGE);
	
	private int mValue;
	private float mSpeed;
	private TurnAngle mBackAngle;

	private TurnAngle(int value, float speed) {
		this(value, speed, null);
	}
	private TurnAngle(int value, float speed, TurnAngle backAngle) {
		mValue = value;
		mSpeed = speed;
		mBackAngle = backAngle;
	}
	
	public int getValue() {
//		if (equals(BACK_SMALL) || equals(BACK_MEDIUM) || equals(BACK_LARGE)) mValue = -mValue;
		return mValue;
	}
	
	public float getRotateSpeed() {
		return mSpeed;
	}
	
	public TurnAngle getBackAngle() {
		return mBackAngle;	
	}
	
}
