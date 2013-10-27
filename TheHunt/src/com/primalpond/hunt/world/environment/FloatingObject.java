package com.primalpond.hunt.world.environment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.primalpond.hunt.JSONable;

import android.graphics.PointF;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;

public abstract class FloatingObject extends D3Sprite implements JSONable {

	public enum Type {
		FOOD_GM, ALGAE;
	}
	
	protected static final String KEY_POS_X = "pos_x";
	protected static final String KEY_POS_Y = "pos_y";
	protected static final String KEY_VX = "vx";
	protected static final String KEY_VY = "vy";

	private static final String TAG = "FloatingObject";
	public static final String KEY_TYPE = "type";

	Type mType;

	private boolean mRemove;
	private ArrayList<Tile> mTiles;

	public FloatingObject(float x, float y, Type type, SpriteManager d3gles20) {
		super(new PointF(x, y), d3gles20);
		mType = type;
		mRemove = false;
	}
	
	public void update() {
		applyFriction();
		super.update();
	}
	
	public void applyFriction() {}
	
	public Type getType() {
		return mType;
	}
	
	public boolean toRemove() {
		return mRemove;
	}
	
	public void setToRemove() {
		mRemove = true;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		PointF pos = getPosition();
		jsonObject.put(KEY_POS_X, pos.x);
		jsonObject.put(KEY_POS_Y, pos.y);
		jsonObject.put(KEY_VX, getVX());
		jsonObject.put(KEY_VY, getVY());
		jsonObject.put(KEY_TYPE, mType);
		return jsonObject;
	}

	public void setTiles(ArrayList<Tile> tiles) {
		mTiles = tiles;
	}

//	public float getRadius() {
//		return mGraphic.getRadius();
//	}
}