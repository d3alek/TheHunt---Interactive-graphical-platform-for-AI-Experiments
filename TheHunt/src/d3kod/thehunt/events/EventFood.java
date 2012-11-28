package d3kod.thehunt.events;

import d3kod.d3gles20.D3Maths;

public class EventFood extends EatableEvent {
	private static final float EQUALS_TOLERANCE = 0.02f; // FoodAlgae velocity
	
	private int mNutri;

	public EventFood(float x, float y, int nutrition) {
		super(x, y, EventType.FOOD, 0); // pass 0 for radius, it will be made to 0.06 or so in EatableEvent
		mNutri = nutrition;
	}
	
	public void set(EventFood to) {
		super.set(to);
		mNutri = to.getNutri();
	}
	
	public int getNutri() {
		return mNutri;
	}
	
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventFood) ) return false;
		
		EventFood that = (EventFood)o;
		
		return D3Maths.compareFloatsTolerance(that.getX(), mX, EQUALS_TOLERANCE) == 0 
				&& D3Maths.compareFloatsTolerance(that.getY(), mY, EQUALS_TOLERANCE) == 0 
				&& that.getNutri() == mNutri;
	}
	
}
