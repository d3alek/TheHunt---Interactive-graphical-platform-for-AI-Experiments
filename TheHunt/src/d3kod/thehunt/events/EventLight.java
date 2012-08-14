package d3kod.thehunt.events;


public class EventLight extends Event {
	int lightLevel;
	public EventLight(int level) {
		super(EventType.LIGHT);
		lightLevel = level;
	}
	public int getLightLevel() {
		return lightLevel;
	}
}
