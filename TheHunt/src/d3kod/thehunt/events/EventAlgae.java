package d3kod.thehunt.events;

import d3kod.d3gles20.D3Maths;


public class EventAlgae extends MovingEvent {

	private static final float EQUALS_TOLERANCE = 0.02f; // TODO: set to Algae velocity
	private int mSize;
	
	public EventAlgae(float x, float y, int size) {
		super(x, y, EventType.ALGAE);
		mSize = size;
	}

	public int getSize() {
		return mSize;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventAlgae) ) return false;
		
		EventAlgae that = (EventAlgae)o;
		
		return  that.getSize() == mSize
				&& D3Maths.compareFloatsTolerance(that.getX(), mX, EQUALS_TOLERANCE) == 0 
				&& D3Maths.compareFloatsTolerance(that.getY(), mY, EQUALS_TOLERANCE) == 0;
	}
	

}
