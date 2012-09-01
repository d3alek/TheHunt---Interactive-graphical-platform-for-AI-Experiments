package d3kod.thehunt.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import android.graphics.Color;
import android.util.Log;

public class Currents {
	public static final String TAG = "TheHuntDebug";
	public static final float[] OPTIMAL_DIR_SPREAD = 
		{ 0.5f, 0.25f, 0, -0.0f, -0f, -0.0f, 0, 0.25f };
	public static final float[] INITIAL_DIR_PROBS = {0.125f, 0.125f, 0.125f, 0.125f, 0.125f, 0.125f, 0.125f, 0.125f, 0};
		//{0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static final int CURRENTS_COLOR = Color.CYAN;
	private EnvironmentData data;
	private final int numDirs = EnvironmentData.numDirs;
	public static int tileRows = EnvironmentData.tileRows;
	public static final int tileCols = EnvironmentData.tileCols;
	public static float tHeight = EnvironmentData.tHeight;
	public static float tWidth = EnvironmentData.tWidth;
	float[][] dirMatrix;
	
	int[][] dirTile = {
			{ 0, 1 },
			{ 1, 1 },
			{ 1, 0 },
			{ 1, -1 },
			{ 0, -1 },
			{ -1, -1 },
			{ -1, 0 },
			{ -1, 1 },
			{ 0, 0 }
	};
	
	class DirProb {
		public float[] prob;
		public float getRange() {
			float range = 0;
			for (int i = 0; i < numDirs; ++i) {
				range += prob[i];
			}
			return range;
		}
		public void addProbs(float[] probs) {
			for (int i = 0; i < numDirs; ++i) {
				prob[i] += probs[i];
			}
		}
		public DirProb() {
			prob = new float[] {0.125f, 0.125f, 0.125f, 0.125f, 0.125f, 0.125f, 0.125f, 0.125f, 0};
//			prob = new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		}
		@Override
		public String toString() {
			String ret = "(";
			for (int i = 0; i < numDirs-1; ++i) {
				ret += prob[i]+",";
			}
			ret += prob[numDirs-1]+")";
			return ret;
		}
	}
	
	public Currents(EnvironmentData envData) {
		data = envData;
//		dirMatrix = getDirSpreadMatrix(OPTIMAL_DIR_SPREAD);
	}
	
	protected float[][] getDirSpreadMatrix(float[] dirSpread) {
//		final float[][] dirSpreadMatrixPrim = {
//				{ 0.5f, 0.25f, 0, 0, 0, 0, 0, 0.25f, 0 },
//				{ 0.25f, 0.5f, 0.25f, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0.25f, 0.5f, 0.25f, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0.25f, 0.5f, 0.25f, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0.25f, 0.5f, 0.25f, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0.25f, 0.5f, 0.25f, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0.25f, 0.5f, 0.25f, 0 },
//				{ 0.25f, 0, 0, 0, 0, 0, 0.25f, 0.5f, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0 }
//		};
		float[][] dirSpreadMatrix = new float[numDirs][numDirs];
		int start = 0; // which position of dirSpread do we start from
		for (int i = 0; i < numDirs-1; ++i) {
			int k = start; // j is counter on Matrix, k is counter on dirSpread
			if (k < 0) {
				k += numDirs - 1;
			}
			for (int j = 0; j < numDirs-1; ++j) {
				dirSpreadMatrix[i][j] = dirSpread[k];
				if (++k >= numDirs-1) k = 0;
			}
			start--;
			// dir spread for UNDECIDED is 0
			dirSpreadMatrix[i][numDirs-1] = 0;
		}
		
		// dir spread for UNDECIDED
		for (int i = 0; i < numDirs; ++i) {
			dirSpreadMatrix[numDirs-1][i] = 0;
		}
//		if (dirSpreadMatrix.equals(dirSpreadMatrixPrim)) Log.v(TAG, "EQUALS!");
//		else {
			Log.v(TAG, "DirSpreadMatrix is:");
			for (float[] r:dirSpreadMatrix) {
				String line = "\t";
				for (float c:r) {
					line += c;
				}
				Log.v(TAG, line);
			}
//		}
		return dirSpreadMatrix;
	}
	
	Tile[] getNeighbors(Tile tile) {
		ArrayList<Tile> neighbors = new ArrayList<Tile>(3);
		int curR = tile.getR(), curC = tile.getC();
//		Dir curDir = tile.getDir();
		// For all neighbor tiles of curTile 
		for (Dir dir:Dir.values()) {
			int dirInd = dir.getIndex(),
					rowInd = curR + dirTile[dirInd][0],
					colInd = curC + dirTile[dirInd][1];
			// Skip tile if out of range
			if (rowInd < 0 || rowInd >= tileRows || colInd < 0 || colInd >= tileCols) {
				continue;
			}
			neighbors.add(data.mTiles[rowInd][colInd]);
		}
		Tile[] neighborsArray = new Tile[neighbors.size()];
		neighbors.toArray(neighborsArray);
		return neighborsArray;
	}
	
	protected void initialize() {
		LinkedList<Tile> queue1 = new LinkedList<Tile>(),
				queue2 = new LinkedList<Tile>();
		HashMap<Tile, DirProb> tileProb = new HashMap<Tile, DirProb>();
		
		Random random = new Random();
		
		// Add border tiles to queue1
		for (int i = 0; i < tileRows; ++i) {
			queue1.add(data.mTiles[i][0]); queue1.add(data.mTiles[i][tileCols-1]);
		}
		for (int i = 0; i < tileCols; ++i) {
			queue1.add(data.mTiles[0][i]); queue1.add(data.mTiles[tileRows-1][i]);
		}
		
		do {
			while(!queue1.isEmpty()) {
				Tile curTile = queue1.pollFirst();
//				int curR = curTile.getR(), curC = curTile.getC();
				Dir curDir = curTile.getDir();
				for (Tile neighborTile: getNeighbors(curTile)) {
					if (!neighborTile.getDir().equals(Dir.UNDEFINED)) continue;
					DirProb dirProb;
					if (!tileProb.containsKey(neighborTile)) {
						// first visit to neighborTile, add to queue2
						queue2.add(neighborTile);
						dirProb = new DirProb();
					}
					else {
						dirProb = tileProb.get(neighborTile);
					}
					dirProb.addProbs(dirMatrix[curDir.getIndex()]);
					tileProb.put(neighborTile, dirProb);
				}
			}
			
			queue1 = (LinkedList<Tile>) queue2.clone();
			
			while (!queue2.isEmpty()) {
				Tile cur = queue2.pop();
				DirProb curProb = tileProb.get(cur);
				float choice = random.nextFloat() * curProb.getRange();
				// go through tileDirs, subtract from choice until choice > 0
				// when choice <= 0 --> choose the current dir and set the tile to this dir
				Log.v(TAG, "Choosing dir for " + cur.getR() + " " + cur.getC() + " " + curProb.getRange());
				for (int i = 0; i < numDirs; ++i) {
					Log.v(TAG, "Choice: " + choice + " dir " + i + " prob " + curProb.prob[i]);
					choice -= curProb.prob[i];
					if (choice <= 0) {
						data.mTiles[cur.getR()][cur.getC()].setDir(Dir.dirByIndex(i));
						break;
					}
				}
			}
		} while(!queue1.isEmpty());
	}
	
	public void smoother() {
		HashMap<Tile, DirProb> tileProb = new HashMap<Tile, DirProb>();

		for (int i = 0; i < tileRows; ++i) {
			for (int j = 0; j < tileCols; ++j) {
				Dir tileDir = data.mTiles[i][j].getDir();
				for (Tile neighbor: getNeighbors(data.mTiles[i][j])) {
					DirProb dirProb;
					if (!tileProb.containsKey(neighbor)) {
						dirProb = new DirProb();
					}
					else {
						dirProb = tileProb.get(neighbor);
					}
					dirProb.addProbs(dirMatrix[tileDir.getIndex()]);
					tileProb.put(neighbor, dirProb);
				}
			}
		}
		
		Random random = new Random();
		
		for (int i = 0; i < tileRows; ++i) {
			for (int j = 0; j < tileCols; ++j) {
				Tile cur = data.mTiles[i][j];
				DirProb curProb = tileProb.get(cur);
				float choice = random.nextFloat() * curProb.getRange();
				for (int k = 0; k < numDirs; ++k) {
					choice -= curProb.prob[k];
					if (choice <= 0) {
						data.mTiles[i][j].setDir(Dir.dirByIndex(k));
						break;
					}
				}
			}
		}
	}
}
