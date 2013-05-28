package d3kod.graphics.sprite.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import d3kod.graphics.extra.Utilities;
import d3kod.graphics.sprite.SpriteManager;

import android.graphics.PointF;
import android.opengl.GLES20;

public class D3TriangleFill extends D3Shape {
	
	private static final int drawType = GLES20.GL_TRIANGLE_STRIP;
	private static final float[] color = {0f, 1f, 0f, 1f};
	private static final int verticesNum = 3;
	static float triangleCoords[] = { // in counterclockwise order:
        0.0f,  0.622008459f, 0.0f,   // top
       -0.5f, -0.311004243f, 0.0f,   // bottom left
        0.5f, -0.311004243f, 0.0f    // bottom right
   };
	private float mRadius;


	public D3TriangleFill(float base, float sides) {
		super();
		super.setVertexBuffer(makeVertexBuffer(base, sides));
		super.setColor(color);
		super.setDrawType(drawType);
	}

	private FloatBuffer makeVertexBuffer(float base, float sides) {
		float[] triagVertices = new float[verticesNum * SpriteManager.COORDS_PER_VERTEX];
		FloatBuffer buffer = ByteBuffer.allocateDirect(triagVertices.length * Utilities.BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		for (int i = 0; i < verticesNum; ++i) {
			triagVertices[i*3] = triangleCoords[i * 3] * base/2;
			triagVertices[i*3+1] = triangleCoords[i * 3 + 1] * sides/2;
			//				Log.v(TAG, "D3Quad vertex " + i + " " + quadVertices[i*3] + " " + quadVertices[i*3 + 1] + " " + quadVertices[i*3 + 2]);
		}
		buffer.put(triagVertices).position(0);
		mRadius = triagVertices[1];
		return buffer;
	}

	@Override
	public float getRadius() {
		return mRadius;
	}

}
