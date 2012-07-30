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
}
