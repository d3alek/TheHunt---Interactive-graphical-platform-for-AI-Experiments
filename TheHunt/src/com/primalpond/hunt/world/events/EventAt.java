package com.primalpond.hunt.world.events;

public class EventAt extends Event {
	private float headX;
	private float headY;
	private float bodyX;
	private float bodyY;
	private float mHeadAngle;

	public EventAt(float hX, float hY, float bX, float bY, float headAngle) {
		super(EventType.AT);
		headX = hX; headY = hY;
		bodyX = bX; bodyY = bY;
		mHeadAngle = headAngle;
	}
	
	public float getHeadX() {
		return headX;
	}
	public float getHeadY() {
		return headY;
	}
	public float getBodyX() {
		return bodyX;
	}
	public float getBodyY() {
		return bodyY;
	}
	
	public float getHeadAngle() {
		return mHeadAngle;
	}
	
	@Override
	public String toString() {
		return "EventAt head: " + headX + " " + headY + " body: " + bodyX + " " + bodyY;
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
