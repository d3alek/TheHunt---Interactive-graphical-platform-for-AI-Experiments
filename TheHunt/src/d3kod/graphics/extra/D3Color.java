package d3kod.graphics.extra;

public enum D3Color {
	BLACK(0.0f, 0.0f, 0.0f),
	RED(1.0f, 0.0f, 0.0f),
	GREEN(0.0f, 1.0f, 0.0f),
	DARK_GREEN(0.0f, 0.4f, 0.0f),
	YELLOW(1f, 1f, 0.0f),
	ORANGE(1.0f, 0.2f, 0.0f);
	
	float r, g, b;
	private D3Color(float r, float g, float b) {
		this.r = r; this.g = g; this.b = b;
	}
	public float getR() {
		return r;
	}
	public float getG() {
		return g;
	}
	public float getB() {
		return b;
	}
}
