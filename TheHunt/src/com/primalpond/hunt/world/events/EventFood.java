package com.primalpond.hunt.world.events;

import d3kod.graphics.extra.D3Maths;

public class EventFood extends EatableEvent {
	private static final float EQUALS_TOLERANCE = 0.02f; // FoodAlgae velocity
	

	public EventFood(float x, float y, int nutrition) {
		super(x, y, EventType.FOOD, nutrition, 0); // pass 0 for radius, it will be made to 0.06 or so in EatableEvent
	}
	
	public void set(EventFood to) {
		super.set(to);
		super.setNutri(to.getNutri());
	}
	
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventFood) ) return false;
		
		EventFood that = (EventFood)o;
		
		return D3Maths.compareFloatsTolerance(that.getX(), mX, EQUALS_TOLERANCE) == 0 
				&& D3Maths.compareFloatsTolerance(that.getY(), mY, EQUALS_TOLERANCE) == 0 
				&& that.getNutri() == getNutri();
	}
	
}
