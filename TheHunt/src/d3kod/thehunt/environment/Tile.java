package d3kod.thehunt.environment;

import d3kod.thehunt.TileQuad;
import d3kod.thehunt.environment.EnvironmentData.Dir;

public class Tile {
	private Dir dir;
	private int r, c;

	private static TileQuad mTileQuad;

	public static void initBuffers() {
		mTileQuad = new TileQuad(EnvironmentData.tWidth, EnvironmentData.tHeight);
	}
	
	public Tile(int row, int col) {
		this(row, col, Dir.UNDEFINED);
	}
	public Tile(int row, int col, Dir initialDir) {
		r = row; c = col; dir = initialDir;
	}
	public Dir getDir() {
		return dir;
	}
	
	public void setDir(Dir dir) {
		this.dir = dir;
	}
	public int getR() {
		return r;
	}
	public int getC() {
		return c;
	}
	@Override
	public String toString() {
		return "(" + r + ", " + c + ", " + dir + ")";
	}
	public void draw(float[] mVMatrix, float[] mProjMatrix, boolean showCurrents) {
        mTileQuad.draw(r, c, mVMatrix, mProjMatrix, showCurrents, dir);

	}
}
