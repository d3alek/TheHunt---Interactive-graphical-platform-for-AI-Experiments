package com.primalpond.hunt.world.events;

import d3kod.graphics.extra.D3Maths;


public class EventAlgae extends EatableEvent {

	//TODO: Combine with EventFood? equals at least
	private static final float EQUALS_TOLERANCE = 0.02f; // TODO: set to Algae velocity
	
	public EventAlgae(float x, float y, float radius) {
		super(x, y, EventType.ALGAE, radius);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventAlgae) ) return false;
		
		EventAlgae that = (EventAlgae)o;
		
		return  that.getRadius() == getRadius() 
				&& D3Maths.compareFloatsTolerance(that.getX(), mX, EQUALS_TOLERANCE) == 0 
				&& D3Maths.compareFloatsTolerance(that.getY(), mY, EQUALS_TOLERANCE) == 0;
	}
	

}
