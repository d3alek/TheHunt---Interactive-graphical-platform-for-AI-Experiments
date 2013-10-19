package com.primalpond.hunt.world.logic;

import com.primalpond.hunt.world.environment.Environment;

public class SaveState {
//	SpriteManager mSpriteManager;
//	Agent mAgent;
	Environment mEnv;
//	private boolean sameAsLast;

//	public SaveState(SpriteManager spriteManager, Environment env, Agent agent) {
	public SaveState(Environment env) {
//		mSpriteManager = spriteManager;
//		mAgent = agent;
		mEnv = env;
//		sameAsLast = false;
	}
//	ArrayList<D3Sprite> mSprites;
//	
//	public SaveState(ArrayList<D3Sprite> sprites) {
//		mSprites = sprites;
//	}
	
//	public ArrayList<D3Sprite> getSprites() {
//		return mSprites;
//	}
//	ArrayList<StorableD3Sprite> mSprites;
//	
//	public SaveState(ArrayList<StorableD3Sprite> sprites) {
//		mSprites = sprites;
//	}
//	public SpriteManager getSpriteManager() {
//		return mSpriteManager;
//	}
//	
//	public Agent getAgent() {
//		return mAgent;
//	}
//	
	public Environment getEnv() {
		return mEnv;
	}
//	
//	public void setSameAsLast(boolean sameAsLast) {
//		this.sameAsLast = sameAsLast;
//	}
//	
//	public boolean getSameAsLast() {
//		return sameAsLast;
//	}
}
