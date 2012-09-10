package d3kod.thehunt.events;

import d3kod.d3gles20.D3Maths;

public class EventFood extends Event {
	private static final float EQUALS_TOLERANCE = 0.02f; // FoodAlgae velocity
	private float foodX;
	private float foodY;
	private int mNutri;

	public EventFood(float x, float y, int nutrition) {
		super(EventType.FOOD);
		foodX = x; foodY = y;
		mNutri = nutrition;
	}
	
	public float getFoodX() {
		return foodX;
	}
	
	public float getFoodY() {
		return foodY;
	}
	
	public int getNutri() {
		return mNutri;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventFood) ) return false;
		
		EventFood that = (EventFood)o;
		
		return D3Maths.compareFloatsTolerance(that.getFoodX(), foodX, EQUALS_TOLERANCE) == 0 
				&& D3Maths.compareFloatsTolerance(that.getFoodY(), foodY, EQUALS_TOLERANCE) == 0 
				&& that.getNutri() == mNutri;
	}
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	@Override
	public String toString() {
		return "EventFood " + foodX + " " + foodY + " " + mNutri;
	}
}
