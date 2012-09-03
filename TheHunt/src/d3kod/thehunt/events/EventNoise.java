package d3kod.thehunt.events;


public class EventNoise extends Event {

	private float mX;
	private float mY;
	private float mLoudness;

	public EventNoise(float x, float y, float loudness) {
		super(EventType.NOISE);
		mX = x; mY = y;
		mLoudness = loudness;
	}
	
	public float getX() {
		return mX;
	}
	
	public float getY() {
		return mY;
	}
	
	public float getLoudness() {
		return mLoudness;
	}
	
}
