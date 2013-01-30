package d3kod.thehunt.world.logic;

import java.util.ArrayList;
import java.util.HashMap;

import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.thehunt.agent.Agent;
import d3kod.thehunt.world.environment.Environment;

public class SaveState {
//	SpriteManager mSpriteManager;
//	Agent mAgent;
	Environment mEnv;

//	public SaveState(SpriteManager spriteManager, Environment env, Agent agent) {
	public SaveState(Environment env) {
//		mSpriteManager = spriteManager;
//		mAgent = agent;
		mEnv = env;
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
}
