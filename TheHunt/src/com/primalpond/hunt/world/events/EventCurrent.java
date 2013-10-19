package com.primalpond.hunt.world.events;

import com.primalpond.hunt.world.environment.Dir;


public class EventCurrent extends Event {
	private Dir mDir;

	public EventCurrent(Dir currentDir) {
		super(EventType.CURRENT);
		mDir = currentDir;
	}
	
	public Dir getDir() {
		return mDir;
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
