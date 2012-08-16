package d3kod.thehunt.prey;

public enum TurnAngle {
	LEFT_SMALL(PreyData.angleFlopSmall, PreyData.rotateSpeedSmall), 
	LEFT_MEDIUM(PreyData.angleFlopMedium, PreyData.rotateSpeedMedium), 
	LEFT_LARGE(PreyData.angleFlopLarge, PreyData.rotateSpeedLarge), 
	RIGHT_SMALL(-PreyData.angleFlopSmall, PreyData.rotateSpeedSmall), 
	RIGHT_MEDIUM(-PreyData.angleFlopMedium, PreyData.rotateSpeedMedium), 
	RIGHT_LARGE(-PreyData.angleFlopLarge, PreyData.rotateSpeedLarge), 
	BACK_LEFT_SMALL(PreyData.angleFlopBackSmall, PreyData.rotateSpeedBackSmall), 
	BACK_LEFT_MEDIUM(PreyData.angleFlopBackMedium, PreyData.rotateSpeedBackMedium), 
	BACK_LEFT_LARGE(PreyData.angleFlopBackLarge, PreyData.rotateSpeedBackLarge), 
	BACK_RIGHT_SMALL(-PreyData.angleFlopBackSmall, PreyData.rotateSpeedBackSmall), 
	BACK_RIGHT_MEDIUM(-PreyData.angleFlopBackMedium, PreyData.rotateSpeedBackMedium), 
	BACK_RIGHT_LARGE(-PreyData.angleFlopBackLarge, PreyData.rotateSpeedBackLarge), 
	BACK_SMALL(PreyData.angleFlopBackSmall, PreyData.rotateSpeedBackSmall), 
	BACK_MEDIUM(PreyData.angleFlopBackMedium, PreyData.rotateSpeedBackMedium), 
	BACK_LARGE(PreyData.angleFlopBackLarge, PreyData.rotateSpeedBackLarge);
	
	private int mValue;
	private float mSpeed;

	private TurnAngle(int value, float speed) {
		mValue = value;
		mSpeed = speed;
	}
	
	public int getValue() {
//		if (equals(BACK_SMALL) || equals(BACK_MEDIUM) || equals(BACK_LARGE)) mValue = -mValue;
		return mValue;
	}
	
	public float getRotateSpeed() {
		return mSpeed;
	}
}
