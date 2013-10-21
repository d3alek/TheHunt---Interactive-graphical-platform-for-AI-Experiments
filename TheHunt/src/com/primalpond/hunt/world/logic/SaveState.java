package com.primalpond.hunt.world.logic;

import com.primalpond.hunt.world.environment.Environment;

public class SaveState {
//	SpriteManager mSpriteManager;
//	Agent mAgent;
	Environment mEnv;
//	private boolean sameAsLast;
	private int mScore;

//	public SaveState(SpriteManager spriteManager, Environment env, Agent agent) {
	public SaveState(Environment env, int score) {
//		mSpriteManager = spriteManager;
//		mAgent = agent;
		mEnv = env;
		mScore = score;
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

	public int getScore() {
		return mScore;
	}
}
