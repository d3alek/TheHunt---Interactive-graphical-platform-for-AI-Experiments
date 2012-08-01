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
}
