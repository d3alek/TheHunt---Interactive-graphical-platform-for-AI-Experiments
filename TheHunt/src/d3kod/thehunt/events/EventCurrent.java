package d3kod.thehunt.events;

import d3kod.thehunt.environment.Dir;


public class EventCurrent extends Event {
	private Dir mDir;

	public EventCurrent(Dir currentDir) {
		super(EventType.CURRENT);
		mDir = currentDir;
	}
	
	public Dir getDir() {
		return mDir;
	}
}
