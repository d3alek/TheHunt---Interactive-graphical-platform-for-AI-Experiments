package d3kod.thehunt.prey;

public enum TurnAngle {
	LEFT_SMALL(PreyData.angleFlopSmall), LEFT_MEDIUM(PreyData.angleFlopMedium), LEFT_LARGE(PreyData.angleFlopLarge), 
	RIGHT_SMALL(-PreyData.angleFlopSmall), RIGHT_MEDIUM(-PreyData.angleFlopMedium), RIGHT_LARGE(-PreyData.angleFlopLarge), 
	BACK_LEFT_SMALL(PreyData.angleFlopSmall), BACK_LEFT_MEDIUM(PreyData.angleFlopMedium), BACK_LEFT_LARGE(PreyData.angleFlopLarge), 
	BACK_RIGHT_SMALL(-PreyData.angleFlopSmall), BACK_RIGHT_MEDIUM(-PreyData.angleFlopMedium), BACK_RIGHT_LARGE(-PreyData.angleFlopLarge), 
	BACK_SMALL(PreyData.angleFlopBackSmall), BACK_MEDIUM(PreyData.angleFlopBackMedium), BACK_LARGE(PreyData.angleFlopBackLarge);
	
	private int mValue;

	private TurnAngle(int value) {
		mValue = value;
	}
	
	public int getValue() {
		if (equals(BACK_SMALL) || equals(BACK_MEDIUM) || equals(BACK_LARGE)) mValue = -mValue;
		return mValue;
	}
}
