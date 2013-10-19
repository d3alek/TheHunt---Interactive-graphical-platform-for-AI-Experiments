package com.primalpond.hunt.world.events;

import d3kod.graphics.extra.D3Maths;

public abstract class MovingEvent extends Event {
	private static final float EQUALS_TOLERANCE = 0.02f; // FoodAlgae velocity
	
	float mX, mY;
	
	public MovingEvent(float x, float y, EventType type) {
		super(type);
		mX = x; mY = y;
	}

	public void set(MovingEvent to) {
		mX = to.getX();
		mY = to.getY();
	}
	
	@Override
	public float getX() {
		return mX;
	}
	
	@Override
	public float getY() {
		return mY;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MovingEvent) ) return false;
		
		MovingEvent that = (MovingEvent)o;
		
		return D3Maths.compareFloatsTolerance(that.getX(), mX, EQUALS_TOLERANCE) == 0 
				&& D3Maths.compareFloatsTolerance(that.getY(), mY, EQUALS_TOLERANCE) == 0;
	}
	
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + mX + " " + mY;
	}
	
	public int compare(MovingEvent other, float x, float y) {
		float distOther = D3Maths.distanceToCircle(x, y, other.getX(), other.getY(), other.getRadius());
		float distThis = D3Maths.distanceToCircle(x, y, this.getX(), this.getY(), this.getRadius());
		return D3Maths.compareFloats(distThis, distOther);
	}
}
