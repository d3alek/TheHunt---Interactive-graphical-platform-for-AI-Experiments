package d3kod.thehunt.world.environment;


public class Tile {
	private Dir dir;
	private int r, c;

	private static D3Tile mTileQuad; //TODO: init mTileQuad

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
	public void draw(float[] mVMatrix, float[] mProjMatrix, boolean showTiles, boolean showCurrents) {
        mTileQuad.draw(r, c, mVMatrix, mProjMatrix, showTiles, showCurrents, dir);
	}
}
