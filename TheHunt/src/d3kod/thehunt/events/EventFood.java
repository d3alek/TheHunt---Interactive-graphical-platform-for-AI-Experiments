package d3kod.thehunt.events;

public class EventFood extends Event {
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
		
		return that.getFoodX() == foodX && that.getFoodY() == foodY && that.getNutri() == mNutri;
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
