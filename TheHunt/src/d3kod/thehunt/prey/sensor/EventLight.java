package d3kod.thehunt.prey.sensor;


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
