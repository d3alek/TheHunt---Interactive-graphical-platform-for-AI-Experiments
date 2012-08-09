package d3kod.thehunt.prey.sensor;


public class EventAlgae extends Event {
	private float algaeX;
	private float algaeY;

	public EventAlgae(float x, float y) {
		super(EventType.ALGAE);
		algaeX = x; algaeY = y;
	}
	
	public float getAlgaeX() {
		return algaeX;
	}
	
	public float getAlgaeY() {
		return algaeY;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventAlgae) ) return false;
		
		EventAlgae that = (EventAlgae)o;
		
		return that.getAlgaeX() == algaeX && that.getAlgaeY() == algaeY;
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
