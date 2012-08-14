package d3kod.thehunt.events;

public class EventAt extends Event {
	private float headX;
	private float headY;
	private float bodyX;
	private float bodyY;

	public EventAt(float hX, float hY, float bX, float bY) {
		super(EventType.AT);
		headX = hX; headY = hY;
		bodyX = bX; bodyY = bY;
	}
	
	public float getHeadX() {
		return headX;
	}
	public float getHeadY() {
		return headY;
	}
	public float getBodyX() {
		return bodyX;
	}
	public float getBodyY() {
		return bodyY;
	}
	
	@Override
	public String toString() {
		return "EventAt head: " + headX + " " + headY + " body: " + bodyX + " " + bodyY;
	}
}
