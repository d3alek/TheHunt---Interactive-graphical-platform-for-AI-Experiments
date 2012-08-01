package d3kod.thehunt.prey.sensor;

public class Event {
	public enum EventType {
		AT, FOOD, CURRENT, ALGAE, NONE;
	}
	EventType mType;
	
	public Event(EventType type) {
		mType = type;
	}
	
	public EventType type() {
		return mType;
	}
}
