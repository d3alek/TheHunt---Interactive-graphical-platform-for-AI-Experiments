package d3kod.thehunt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.environment.EnvironmentData;
import d3kod.thehunt.environment.Tile;
import d3kod.thehunt.prey.ManualControl;
import d3kod.thehunt.prey.Prey;
import android.opengl.GLSurfaceView;

public class TheHuntRenderer implements GLSurfaceView.Renderer {
	
	private static final String TAG = "TheHuntRenderer";
	private static final boolean LOGGING_TIME = false;
	private static final boolean SHOW_TILES = true;
	private static final boolean SHOW_CURRENTS = true;
	private static final boolean MANUAL_CONTROLS = true;
	private static final boolean FEED_WITH_TOUCH = true;

	private float[] mProjMatrix = new float[16]; 
	private float[] mVMatrix = new float[16];
	private long timePreviousNS;
	private Environment mEnv;
	private Prey mPrey;
	private ManualControl mManuControl;
	
    private final int TICKS_PER_SECOND = 25;
    private final int MILLISEC_PER_TICK = 1000 / TICKS_PER_SECOND;
    private final int MAX_FRAMESKIP = 5;
	private long next_game_tick;

	public void onSensorChanged(SensorEvent event) {
		
	}
	
	public TheHuntRenderer() {
		mEnv = new Environment();
		next_game_tick = System.currentTimeMillis();
	}
	
	public void release() {
		// TODO stuff to release	
	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {	
		GLES20.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
		D3GLES20.clean();
		mManuControl = new ManualControl();
		
	    Matrix.orthoM(mVMatrix, 0, -1, 1, -1, 1, 0.1f, 100f);
	}

	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 unused) {
		if (LOGGING_TIME) {
			timePreviousNS = System.nanoTime();
		}
		
		int clearMask = GLES20.GL_COLOR_BUFFER_BIT;
		
		if (MultisampleConfigChooser.usesCoverageAa()) {
			final int GL_COVERAGE_BUFFER_BIT_NV = 0x8000;
            clearMask |= GL_COVERAGE_BUFFER_BIT_NV;
		}
		
		GLES20.glClear(clearMask);

		
		if (SHOW_TILES) {
//			Log.v(TAG, "Showing the tiles");
			for (Tile[] tile_row: mEnv.getTiles()) {
				for (Tile tile: tile_row) {
					tile.draw(mVMatrix, mProjMatrix, SHOW_CURRENTS);
				}
			}
		}
		if (MANUAL_CONTROLS) {
			mManuControl.draw(mVMatrix, mProjMatrix);
		}
		
		int loops = 0;
		long now = System.currentTimeMillis();
		while (next_game_tick < now && loops < MAX_FRAMESKIP) {
			updateWorld();
			next_game_tick += MILLISEC_PER_TICK;
			loops++;
		}
		
		float interpolation = (System.currentTimeMillis() + MILLISEC_PER_TICK - next_game_tick) / (float) MILLISEC_PER_TICK;
		drawWorld(interpolation);
	    
	    if (LOGGING_TIME) {
	    	// Update base time values.
	        final long  timeCurrentNS = System.nanoTime();
	        final long  timeDeltaNS = timeCurrentNS - timePreviousNS;
	         
	        // Determine time since last frame in seconds.
	        final float timeDeltaS = timeDeltaNS * 1.0e-9f;
	         
	        // Print a notice if rendering falls behind 60Hz.
	        if( timeDeltaS > (1.0f / 60.0f) )
	        {
	            Log.d( "SpikeTest", "Spike: " + timeDeltaS );
	        }
	    }
	}		

	private void updateWorld() {
		PointF curDirDelta = mEnv.data.getTileFromPos(mPrey.getPosition()).getDir().getDelta();
		mPrey.update(curDirDelta.x * EnvironmentData.currentStep, 
				curDirDelta.y * EnvironmentData.currentStep);
	}

	private void drawWorld(float interpolation) {
		mPrey.draw(mVMatrix, mProjMatrix, interpolation);
		mEnv.draw(mVMatrix, mProjMatrix, interpolation);
	}

	/**
	 * If the surface changes, reset the view
	 */
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

	    float ratio = (float) width / height;
	    
	    if (width > height) Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
	    else Matrix.frustumM(mProjMatrix, 0, -1, 1, -1/ratio, 1/ratio, 1, 10);
	    
	    mEnv.setSize(width, height);
	    mPrey = new Prey(EnvironmentData.mScreenWidth, EnvironmentData.mScreenHeight, mEnv);
	    
	    if (MANUAL_CONTROLS) mManuControl.setSize();
	}
	
	public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    	
        int shaderHandle = GLES20.glCreateShader(type);
         
        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderCode);
         
            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);
         
            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
         
            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }
        
         
        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }
        return shaderHandle;
    }

	public void handleTouch(float x, float y) {
		x = D3GLES20.fromWorldWidth(x);
		y = D3GLES20.fromWorldHeight(y);
		if (MANUAL_CONTROLS) {
	    	switch(mManuControl.contains(x, y)) {
	    	case LEFT: mPrey.flopLeft(); return;
	    	case RIGHT: mPrey.flopRight(); return;
	    	case FORWARD: mPrey.flopLeft(); mPrey.flopRight(); return;
	    	}
		}
		if (FEED_WITH_TOUCH) {
			mEnv.putFood(x, y);
		}
    	
	}
}