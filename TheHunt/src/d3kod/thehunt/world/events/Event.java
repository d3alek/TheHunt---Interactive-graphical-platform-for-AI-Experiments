package d3kod.thehunt.world.events;

public abstract class Event {
	public enum EventType {
		AT, FOOD, CURRENT, ALGAE, LIGHT, NOISE, NONE;
	}
	EventType mType;
	
	public Event(EventType type) {
		mType = type;
	}
	
	public EventType type() {
		return mType;
	}
	
	public abstract float getX();
	
	public abstract float getY();
	
	public boolean isOfType(EventType[] types) {
		for (EventType type: types) {
			if (mType == type) return true;
		}
		return false;
	}
	
	public float getRadius() {
		throw(new UnsupportedOperationException());
	}
	
	public String toString() {
		return "Event " + mType;
	}
}
