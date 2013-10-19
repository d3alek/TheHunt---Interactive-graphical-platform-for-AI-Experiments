package com.primalpond.hunt.world.events;

import com.primalpond.hunt.world.environment.NAlgae;

import android.util.Log;

public class EatableEvent extends MovingEvent {

	public static final float MIN_RADIUS = 0.06f; // TODO: fix inaccurate fish eating...
	private static final String TAG = "EatableEvent";
	private float mRadius;
	private int mNutri;
	
	public EatableEvent(float x, float y, EventType type, float radius) {
		this(x, y, type, NAlgae.FOOD_ALGAE_BITE_NUTRITION, radius);
	}
	
	public EatableEvent(float x, float y, EventType type, int nutrition, float radius) {
		super(x, y, type);
		mRadius = Math.max(radius, MIN_RADIUS);
		mNutri = nutrition;
	}
	
	public float getRadius() {
		return mRadius;
	}
	
	public int getNutri() {
		return mNutri;
	}
	
	public void setNutri(int nutri) {
		this.mNutri = nutri;
	}
	
	@Override
	public int compare(MovingEvent other, float x, float y) {
		try {
			EatableEvent eatableOther = (EatableEvent) other;
			if (this.getNutri() > eatableOther.getNutri()) {
				return -1;
			}
			if (this.getNutri() < eatableOther.getNutri()) {
				return 1;
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "Impossible comparison: EatableEvent with " + other.getClass());
		}
		return super.compare(other, x, y);
	}


}
