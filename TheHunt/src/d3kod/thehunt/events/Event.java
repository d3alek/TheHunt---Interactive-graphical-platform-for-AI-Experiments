package d3kod.thehunt.events;

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
	
	public String toString() {
		return "Event " + mType;
	};
}
