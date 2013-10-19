package com.primalpond.hunt.world.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.primalpond.hunt.agent.Agent;
import com.primalpond.hunt.world.environment.FloatingObject.Type;
import com.primalpond.hunt.world.events.EatableEvent;
import com.primalpond.hunt.world.events.Event;
import com.primalpond.hunt.world.events.EventCurrent;
import com.primalpond.hunt.world.events.EventLight;
import com.primalpond.hunt.world.events.EventNoise;
import com.primalpond.hunt.world.logic.TheHuntRenderer;

import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import d3kod.graphics.extra.D3Color;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.SpriteManager;
public class Environment {
	private static final String TAG = "Environment";
	public static final float LOUDNESS_PLOK = 1f;
	private static final int NOISE_EVENTS_CLEAR_FREQUENCY = 2;
	public EnvironmentData data;
//	public int mTextureDataHandle;
	transient private SpriteManager mD3GLES20;
	transient public static Environment GLOBAL;
	private ArrayList<EventNoise> mNoiseEvents;
	private Random mRandom;
	private Agent mPrey;
	
//	private int mBiomass;
	private double algaeGrowthChance;
//	private int mAlgaeNum;
	private int mPlayerPenalty;
	
	
	public static final int ALGAE_NUM = 300;
	private static final float NET_INTERSECT_RAD_ADJ = 0.2f;
	private static final double GROWTH_THRESH = 250;
	private static final double GROWTH_CONST = 0.1;
	private static final int ALGAE_NEGATIVE_SCORE = 2;
	private static final int FOOD_NEGATIVE_SCORE = 1;
	
	public Environment(SpriteManager d3gles20) {
		GLOBAL = this;
		mPlayerPenalty = 0;
		mD3GLES20 = d3gles20;
		data = new EnvironmentData();
		data.setGraphics(mD3GLES20);
		mNoiseEvents = new ArrayList<EventNoise>();
		mRandom = new Random();
		seedAlgae();
	}
	
	public void addNewAlgae(int n, PointF pos, float dirAngle) {
		data.addFloatingObject(new NAlgae(n, pos, dirAngle, this, mD3GLES20));
	}
	
	public void addNewAlgae(int n, PointF pos, PointF dirVector,
			PointF velocity) {
		NAlgae algae = new NAlgae(n, pos, dirVector, this, mD3GLES20);
		data.addFloatingObject(algae);
		algae.setVelocity(dirVector.x*velocity.x, dirVector.y*velocity.y);
//		algae.setVelocity(vx, vy)
		
	}
	public void addNewAlgae(int n, PointF pos, float dirAngle, PointF velocity) {
		NAlgae algae = new NAlgae(n, pos, dirAngle, this, mD3GLES20);
		data.addFloatingObject(algae);
		algae.setVelocity(FloatMath.cos(dirAngle)*velocity.x, FloatMath.sin(dirAngle)*velocity.y);
	}
	
	private void seedAlgae() {
		for (int i = 0; i < ALGAE_NUM; ++i) {
			addNewAlgae(1, randomPosInEnv(), D3Maths.getRandAngle());
		}
	}

	public PointF randomPosInEnv() {
		float mScreenHeight = TheHuntRenderer.SCREEN_HEIGHT;
		float mScreenWidth = TheHuntRenderer.SCREEN_WIDTH;
		return new PointF(
				-mScreenWidth/2 + mRandom.nextFloat()*mScreenWidth, 
				-mScreenHeight/2 + mRandom.nextFloat()*mScreenHeight);
	}
	
	public void update() {
		if (mD3GLES20 == null) {
			Log.e(TAG, "D3gles20 is null on env update!");
		}
		calculateAlgaeGrowthChance();
		data.updateFloatingObjects();

	}
	public void recalculateCurrents() {
	}
	
	public Tile[][] getTiles() {
		return data.mTiles;
	}
	public void putFoodGM(float x, float y) {
		data.addFloatingObject(new FoodGM(x, y, mD3GLES20));
	}
	
	public Event senseCurrent(float x, float y) {
		Dir tileDir = data.getTileFromPos(new PointF(x, y)).getDir();
		return new EventCurrent(tileDir);
	}
	
	public int eatFood(float x, float y) {
		for (FloatingObject fo: data.getFloatingObjects()) {
//			Log.v(TAG, "Eat food check fo type is " + fo.getType());
			if (fo.getType().compareTo(Type.ALGAE) != 0 
					&& fo.getType().compareTo(Type.FOOD_GM) != 0) {
//				Log.v(TAG, "Eat food check fo type is " + fo.getType());
//				Log.v(TAG, "Eat food check fo type is ")
//				Log.v(TAG, "" + fo.getType().compareTo(Type.ALGAE));
				continue;
			}
			float foX = fo.getX(), foY = fo.getY();
//			Log.v(TAG, "Eat food circle contains check!");
			if (D3Maths.circleContains(x, y, Math.max(fo.getRadius(), EatableEvent.MIN_RADIUS), foX, foY)) {
//				Log.v(TAG, "Eat food check passed!");
				Eatable eatable = (Eatable) fo;
				eatable.processBite();
				return eatable.getNutrition();
			}
		}
		return 0;
	}
	
	public ArrayList<FloatingObject> seeObjects(float x, float y, float sightRadius) {
		ArrayList<FloatingObject> sensedObjects = new ArrayList<FloatingObject>();
		
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (D3Maths.circleContains(x, y, sightRadius, fo.getX(), fo.getY())) {
				sensedObjects.add(fo);
			}
		}
		
		return sensedObjects;
	}
	
	public Event senseLight(float hX, float hY) {
		PointF at = new PointF(hX, hY);
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType().compareTo(Type.ALGAE) == 0 && fo.contains(at)) {
				return new EventLight(0);
			}
		}
		return new EventLight(1);
	}
	public boolean netObstacle(float x, float y) {
		PointF at = new PointF(x, y);
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType().compareTo(Type.ALGAE) == 0 && fo.contains(at)) 
				return true;
		}
		return false;
	}

	public ArrayList<Event> hearEvents(float hX, float hY, float hearRadius) {
		ArrayList<Event> events = new ArrayList<Event>();
		for (EventNoise noise: mNoiseEvents) {
			if (D3Maths.circlesIntersect(hX, hY, hearRadius, 
					noise.getX(), noise.getY(), noise.getLoudness())) {
				events.add(noise);
			}
		}
		mNoiseEvents.clear();
		return events;
	}

	public void putNoise(float x, float y, float loudness) {
		mNoiseEvents.add(new EventNoise(x, y, loudness));
	}
//
//	public boolean netIntersectsWithAlgae(float centerX, float centerY,
//			float radius) {
////		Log.v(TAG, "Net intersects test started with " + data.getFloatingObjects().size() + " fo");
//		for (FloatingObject fo: data.getFloatingObjects()) {
////			Log.v(TAG, "Net intersects test fo radius is " + fo.getRadius());
//			if (fo.getType().compareTo(Type.ALGAE) == 0 &&
//					D3Maths.circlesIntersect(centerX, centerY, radius, fo.getX(), fo.getY(), fo.getRadius())) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	public NAlgae knifeIntersectsWithAlgae(float knifeX, float knifeY) {
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType().compareTo(Type.ALGAE) == 0 &&
					D3Maths.circleContains(fo.getX(), fo.getY(), fo.getRadius(), knifeX, knifeY)) {
				return (NAlgae)fo;
			}
		}
		return null;
	}

	public void addPrey(Agent prey) {
		mPrey = prey;
	}
	public Agent getPrey() {
		if (mPrey.getEnvironment() != this) {
			Log.v(TAG, "Adjusting prey environment");
			mPrey.setEnvironment(this);
		}
		return mPrey;
	}

	public void initGraphics(SpriteManager mD3GLES202) {
		GLOBAL = this;
		mD3GLES20 = mD3GLES202;
		data.setGraphics(mD3GLES20);
		Log.v(TAG, "Environment graphics initialized! " + mD3GLES20);
	}

	public SpriteManager getSpriteManager() {
		return mD3GLES20;
	}
	
	private void calculateAlgaeGrowthChance() {
		int algaeNum = 0;
		int biomass = 0;
		double algaeGrowthRate = 0;
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.ALGAE) {
				algaeNum++;
				biomass += ((NAlgae) fo).getN();
			}
		}
		double m = 0; // specific growth rate
		if (GROWTH_THRESH - biomass > 0) {
			m = GROWTH_CONST*Math.log(GROWTH_THRESH - biomass);
		}
		algaeGrowthRate = m * biomass / algaeNum;
		algaeGrowthChance = algaeGrowthRate / algaeNum;
	}
	
	public String getStateString() {
		return String.format("Good %.2f", algaeGrowthChance);
	}
	
	public D3Color getStateColor() {
		return D3Color.DARK_GREEN;
	}
	
	public double getAlgaeGrowthChance() {
		return algaeGrowthChance;
	}

	public void playerRemoves(FloatingObject fo) {
		fo.setToRemove();
		switch(fo.getType()) {
		case ALGAE:
			mPlayerPenalty += ((NAlgae)fo).getN()*ALGAE_NEGATIVE_SCORE; break;
		case FOOD_GM:
			mPlayerPenalty += FOOD_NEGATIVE_SCORE; break;
		}
	}

	public int getPlayerPenalty() {
		return mPlayerPenalty;
	}
	
	public void clearPlayerPenalty() {
		mPlayerPenalty = 0;
	}
	public void reducePlayerPenalty() {
		if (mPlayerPenalty > 0) mPlayerPenalty--;
	}

	public ArrayList<FloatingObject> senseTouch(float x, float y,
			float radius) {
		ArrayList<FloatingObject> sensedObjects = new ArrayList<FloatingObject>();
		
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (D3Maths.circleContains(x, y, radius, fo.getX(), fo.getY())) {
				sensedObjects.add(fo);
			}
		}
		return sensedObjects;
	}

	public void regenerate() {
		for (FloatingObject fo: data.getFloatingObjects()) {
			fo.setToRemove();
		}
		seedAlgae();
	}

}
