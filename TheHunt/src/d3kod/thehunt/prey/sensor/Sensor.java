package d3kod.thehunt.prey.sensor;

import java.util.ArrayList;

import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.environment.EnvironmentData;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.events.EventAt;

public class Sensor {
	
	enum Sensors {
		CURRENT_SENSOR, FOOD_SENSOR, ALGAE_SENSOR, LIGHT_SENSOR;
	}
	private ArrayList<Sensors> mSensors;
	private Environment mEnv;
	
	public Sensor(Environment env) {
		mEnv = env;
		mSensors = new ArrayList<Sensor.Sensors>();
		mSensors.add(Sensors.CURRENT_SENSOR);
		mSensors.add(Sensors.FOOD_SENSOR);
		mSensors.add(Sensors.ALGAE_SENSOR);
		mSensors.add(Sensors.LIGHT_SENSOR);
	}
	public ArrayList<Event> sense(float hX, float hY, float bX, float bY) {
		ArrayList<Event> sensedEvents = new ArrayList<Event>();
		sensedEvents.add(new EventAt(hX, hY, bX, bY));
		for (Sensors sensor: mSensors) {
			switch(sensor) {
			case CURRENT_SENSOR: sensedEvents.add(mEnv.senseCurrent(bX, bY)); break;
			case FOOD_SENSOR:  sensedEvents.addAll(mEnv.senseFood(hX, hY)); break;
			case ALGAE_SENSOR: sensedEvents.addAll(mEnv.senseAlgae()); break;
			case LIGHT_SENSOR: sensedEvents.add(mEnv.senseLight(hX, hY)); break;
			}
		}
		return sensedEvents;
	}

}
