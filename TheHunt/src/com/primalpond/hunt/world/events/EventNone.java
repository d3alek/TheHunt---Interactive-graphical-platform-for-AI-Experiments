package com.primalpond.hunt.world.events;

public class EventNone extends Event {
	public EventNone() {
		super(EventType.NONE);
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
