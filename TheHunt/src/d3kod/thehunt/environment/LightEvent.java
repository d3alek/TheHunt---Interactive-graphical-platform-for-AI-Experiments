package d3kod.thehunt.environment;

import d3kod.thehunt.prey.sensor.Event;

public class LightEvent extends Event {
	int lightLevel;
	public LightEvent(int level) {
		super(EventType.LIGHT);
		lightLevel = level;
	}
	public int getLightLevel() {
		return lightLevel;
	}
}
