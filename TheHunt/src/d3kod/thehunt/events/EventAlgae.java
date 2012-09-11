package d3kod.thehunt.events;


public class EventAlgae extends Event {
	private float algaeX;
	private float algaeY;

	public EventAlgae(float x, float y) {
		super(EventType.ALGAE);
		algaeX = x; algaeY = y;
	}
	
	@Override
	public float getX() {
		return algaeX;
	}
	
	@Override
	public float getY() {
		return algaeY;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventAlgae) ) return false;
		
		EventAlgae that = (EventAlgae)o;
		
		return that.getX() == algaeX && that.getY() == algaeY;
	}
	
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		return "EventAlgae " + algaeX + " " + algaeY;
	}
}
