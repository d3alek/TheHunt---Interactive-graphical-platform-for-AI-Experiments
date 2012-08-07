package d3kod.thehunt.prey;

public enum TurnAngle {
	LEFT_SMALL(PreyData.angleFlopSmall), LEFT_MEDIUM(PreyData.angleFlopMedium), LEFT_LARGE(PreyData.angleFlopLarge), 
	RIGHT_SMALL(-PreyData.angleFlopSmall), RIGHT_MEDIUM(-PreyData.angleFlopMedium), RIGHT_LARGE(-PreyData.angleFlopLarge), 
	BACK_LEFT_SMALL(PreyData.angleFlopSmall), BACK_LEFT_MEDIUM(PreyData.angleFlopMedium), BACK_LEFT_LARGE(PreyData.angleFlopLarge), 
	BACK_RIGHT_SMALL(-PreyData.angleFlopSmall), BACK_RIGHT_MEDIUM(-PreyData.angleFlopMedium), BACK_RIGHT_LARGE(-PreyData.angleFlopLarge), 
	BACK_SMALL(PreyData.angleFlopSmall), BACK_MEDIUM(PreyData.angleFlopMedium), BACK_LARGE(PreyData.angleFlopLarge);
	
	private int mValue;

	private TurnAngle(int value) {
		mValue = value;
	}
	
	public int getValue() {
		return mValue;
	}
}
