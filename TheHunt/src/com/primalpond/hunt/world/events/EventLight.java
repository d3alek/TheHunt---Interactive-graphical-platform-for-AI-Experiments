package com.primalpond.hunt.world.events;


public class EventLight extends Event {
	int lightLevel;
	public EventLight(int level) {
		super(EventType.LIGHT);
		lightLevel = level;
	}
	public int getLightLevel() {
		return lightLevel;
	}
	@Override
	public float getX() {
		throw new UnsupportedOperationException();
	}
	@Override
	public float getY() {
		throw new UnsupportedOperationException();
	}
}
