package d3kod.thehunt.prey.sensor;

import java.util.ArrayList;

import d3kod.thehunt.environment.Eatable;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.environment.FloatingObject;
import d3kod.thehunt.environment.NAlgae;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.events.EventAlgae;
import d3kod.thehunt.events.EventAt;
import d3kod.thehunt.events.EventFood;
import d3kod.thehunt.prey.memory.WorldModel;

public class Sensor {
	
	enum Sensors {
		CURRENT_SENSOR, SIGHT_SENSOR, HEARING_SENSOR;
	}
	private ArrayList<Sensors> mSensors;
	private Environment mEnv;
	private float mSightRad = 0.5f;
	private float mHearRad = 1f;
	private ArrayList<Event> sensedEvents;
	
	public Sensor(Environment env) {
		mEnv = env;
		mSensors = new ArrayList<Sensor.Sensors>();
		mSensors.add(Sensors.CURRENT_SENSOR);
		mSensors.add(Sensors.SIGHT_SENSOR);
		mSensors.add(Sensors.HEARING_SENSOR);
		sensedEvents = new ArrayList<Event>();
	}
	public ArrayList<Event> sense(float hX, float hY, float bX, float bY, float headAngle) {
		sensedEvents.clear();
		sensedEvents.add(new EventAt(hX, hY, bX, bY, headAngle));
		for (Sensors sensor: mSensors) {
			switch(sensor) {
			case CURRENT_SENSOR: sensedEvents.add(mEnv.senseCurrent(bX, bY)); break;
			case SIGHT_SENSOR: 
				sensedEvents.add(mEnv.senseLight(hX, hY));
				ArrayList<FloatingObject> sensedObjects = mEnv.seeObjects(hX, hY, mSightRad);
				for (FloatingObject fo: sensedObjects) {
					switch(fo.getType()) {
					case ALGAE:
						sensedEvents.add(new EventAlgae(fo.getX(), fo.getY(), ((NAlgae)fo).getSize()));
						if (((NAlgae)fo).getSize() >= WorldModel.MINIMUM_HIDING_ALGAE_SIZE) {
							break;
						}
						else {
							break;
							// no break - register it as food as well
						}
					//case FOOD_ALGAE: sensedEvents.add(new EventFood(fo.getX(), fo.getY(), fo.nutrition())); break;
					case FOOD_GM: sensedEvents.add(new EventFood(fo.getX(), fo.getY(), ((Eatable)fo).getNutrition())); break;
					}
				}
				break;
			case HEARING_SENSOR:
				sensedEvents.addAll(mEnv.hearEvents(hX, hY, mHearRad));
				break;
			}
		}
		return sensedEvents;
	}

}
