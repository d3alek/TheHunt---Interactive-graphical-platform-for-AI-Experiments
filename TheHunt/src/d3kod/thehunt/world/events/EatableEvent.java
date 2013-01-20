package d3kod.thehunt.world.events;

public class EatableEvent extends MovingEvent {

	public static final float MIN_RADIUS = 0.06f; // TODO: fix inaccurate fish eating...
	private float mRadius;
	
	public EatableEvent(float x, float y, EventType type, float radius) {
		super(x, y, type);
		mRadius = Math.max(radius, MIN_RADIUS);
	}
	
	public float getRadius() {
		return mRadius;
	}

}
