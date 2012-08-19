package d3kod.thehunt.environment;

import java.nio.FloatBuffer;
import java.util.Random;

import android.opengl.GLES20;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.shapes.D3Shape;

public class D3Algae extends D3Shape {
	
	private static final String TAG = "D3Algae";

	private static int drawType = GLES20.GL_LINE_LOOP;
	private static final float[] algaeColor = 
		{ 0.4f, 0.4f, 0.4f, 0.0f};

	private static final int ALGAE_DETAILS_PER_PART = 10;
	private static final float ALGAE_DETAILS_STEP = 1f/ALGAE_DETAILS_PER_PART;
	private static final float ALGAE_SIZE = 0.3f;
	
	private static final int curvePartsNum = 8;
	private static final int controlPointsPerPart = 4;
	private static final int controlPointsNum = curvePartsNum * (controlPointsPerPart-1);

	private static final float WIGGLE_SPEED = 0.001f;

	private static final int MAX_WIGGLES = 50;

	private static final float GENERATOR_MAX_DISPLACEMENT = ALGAE_SIZE/5;

	private static final float GENERATOR_MAX_DELTA = GENERATOR_MAX_DISPLACEMENT/5;

	private static final int GENERATOR_NO_FLIP_NEXT = 3;
	private float[][] controPointsData;
	
	// wiggle data for each controlPoint; 
	// first component is wiggle direction(1 or -1), second is wiggles done so far in this direction
	private float[][] wiggleData = new float[controlPointsNum][2];

	private int noFlip; 

	protected D3Algae() {
		super(algaeColor, drawType, true);
		controPointsData = null;
		setVertexBuffer(makeVerticesBuffer());
	}

	private FloatBuffer makeVerticesBuffer() {
		if (controPointsData == null) {
			controPointsData = algaeControlPointsGenerator();
			
		}
		
		int coordsPerPart = ALGAE_DETAILS_PER_PART*D3GLES20.COORDS_PER_VERTEX;
		float[] verticesData = new float[coordsPerPart*curvePartsNum];
		float[] curPart;
		int controlPointStart = 0;
		for (int i = 0; i < curvePartsNum; ++i) {
			curPart = D3Maths.quadBezierCurveVertices(
					controPointsData[controlPointStart], 
					controPointsData[controlPointStart+1], 
					controPointsData[controlPointStart+2], 
					controPointsData[(controlPointStart+3)%controlPointsNum], ALGAE_DETAILS_STEP, 1f);
			controlPointStart = controlPointStart+3;
			for (int j = 0; j < coordsPerPart; ++j) {
				verticesData[i*coordsPerPart + j] = curPart[j];
			}
		}
//		Log.v(TAG, "verticesData is: " + Arrays.toString(verticesData));
		return Utilities.newFloatBuffer(verticesData);
	}

	private float[][] algaeControlPointsGenerator() {
		Random rand = new Random();
		float[][] controlPoints = new float[controlPointsNum][D3GLES20.COORDS_PER_VERTEX];
		controlPoints = toTwoDimCoordinateArray(D3Maths.circleVerticesData(ALGAE_SIZE, controlPointsNum));
		float displacementX = (1-2*rand.nextFloat())*GENERATOR_MAX_DISPLACEMENT;
		float displacementY = (1-2*rand.nextFloat())*GENERATOR_MAX_DISPLACEMENT;
//		float displacementXDelta;
//		if (displacementX == 0) displacementXDelta = GENERATOR_MAX_DELTA;
		float displacementXDelta = GENERATOR_MAX_DELTA*((GENERATOR_MAX_DISPLACEMENT-displacementX)/GENERATOR_MAX_DISPLACEMENT);
//		float displacementYDelta;
//		if (displacementY == 0) displacementYDelta = GENERATOR_MAX_DELTA;
		float displacementYDelta = GENERATOR_MAX_DELTA*((GENERATOR_MAX_DISPLACEMENT-displacementY)/GENERATOR_MAX_DISPLACEMENT);
		float flipProb;
		float signX, signY;
		int countFlips = 0;
		noFlip = 0;
		for (int i = 1; i < controlPointsNum-1; ++i) {
			controlPoints[i][0] += displacementX;
			controlPoints[i][1] += displacementY;
			displacementX += displacementXDelta;
			displacementY += displacementYDelta;
			flipProb = Math.abs(D3Maths.distance(0, 0, displacementX, displacementY)-GENERATOR_MAX_DISPLACEMENT)/GENERATOR_MAX_DISPLACEMENT;
//			Log.v(TAG, "FlipProb is "  + flipProb + " distance is " + D3GLES20.distance(0, 0, displacementX, displacementY) + " GENERATOR_MAX_DISPLACEMENT is " + GENERATOR_MAX_DISPLACEMENT);
			if (noFlip <= 0 && rand.nextFloat() < flipProb) {
//				Log.v(TAG, "Do flip!");
				countFlips ++;
				signX = Math.signum(displacementXDelta);
				signY = Math.signum(displacementYDelta);
				displacementXDelta = -signX*GENERATOR_MAX_DELTA*rand.nextFloat();
				displacementYDelta = -signY*GENERATOR_MAX_DELTA*rand.nextFloat();
				noFlip = GENERATOR_NO_FLIP_NEXT;
			}
			else noFlip--;
		}
//		Log.v(TAG, "Control points are " + controlPointsNum + " Flips are " + countFlips);
		return controlPoints;
	}

	private float[][] toTwoDimCoordinateArray(float[] oneDimVerticesData) {
		int coordNum = oneDimVerticesData.length/D3GLES20.COORDS_PER_VERTEX;
		float[][] twoDimVerticesData = new float[coordNum][D3GLES20.COORDS_PER_VERTEX];
		for (int i = 0; i < coordNum; ++i) {
			twoDimVerticesData[i][0] = oneDimVerticesData[i*D3GLES20.COORDS_PER_VERTEX];
			twoDimVerticesData[i][1] = oneDimVerticesData[i*D3GLES20.COORDS_PER_VERTEX+1];
			twoDimVerticesData[i][2] = oneDimVerticesData[i*D3GLES20.COORDS_PER_VERTEX+2];
		}
		return twoDimVerticesData;
	}

	@Override
	public float getRadius() {
		return ALGAE_SIZE;
	}

	public void wiggle() {
		Random rand = new Random();
		float randSpeedX, randSpeedY;
		
		for (int i = 0; i < controlPointsNum; ++i) {
//			randSpeedX = WIGGLE_SPEED * (1-2*rand.nextFloat());
//			randSpeedY = WIGGLE_SPEED * (1-2*rand.nextFloat());
			if (wiggleData[i][0] == 0) {
				// decide for wiggle direction
				wiggleData[i][0] = rand.nextFloat() > 0.5 ? 1 : -1;
			}
			if (wiggleData[i][1] >= MAX_WIGGLES) {
				wiggleData[i][0] = -wiggleData[i][0];
				wiggleData[i][1] = 0;
			}
			controPointsData[i][0] += WIGGLE_SPEED*wiggleData[i][0];
			controPointsData[i][1] += WIGGLE_SPEED*wiggleData[i][0];
			wiggleData[i][1]++;
		}
		setVertexBuffer(makeVerticesBuffer());
	}

}
