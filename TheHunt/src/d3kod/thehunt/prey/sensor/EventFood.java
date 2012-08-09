package d3kod.thehunt.prey.sensor;

public class EventFood extends Event {
	private float foodX;
	private float foodY;

	public EventFood(float x, float y) {
		super(EventType.FOOD);
		foodX = x; foodY = y;
	}
	
	public float getFoodX() {
		return foodX;
	}
	
	public float getFoodY() {
		return foodY;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventFood) ) return false;
		
		EventFood that = (EventFood)o;
		
		return that.getFoodX() == foodX && that.getFoodY() == foodY;
	}
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	@Override
	public String toString() {
		return "EventFood " + foodX + " " + foodY;
	}
}
