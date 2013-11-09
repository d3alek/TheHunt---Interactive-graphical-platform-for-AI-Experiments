package d3kod.graphics.extra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import d3kod.graphics.shader.programs.AttribVariable;

public class Utilities {

	public static final int BYTES_PER_FLOAT = 4;
	public static final int BYTES_PER_SHORT = 2;
	private static final String TAG = "Utilities";

	public static int createProgram(int vertexShaderHandle, int fragmentShaderHandle, AttribVariable[] variables) {
		int  mProgram = GLES20.glCreateProgram();

		if (mProgram != 0) {
			GLES20.glAttachShader(mProgram, vertexShaderHandle);
			GLES20.glAttachShader(mProgram, fragmentShaderHandle);

			for (AttribVariable var: variables) {
				GLES20.glBindAttribLocation(mProgram, var.getHandle(), var.getName());
			}   

			GLES20.glLinkProgram(mProgram);

			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);

			if (linkStatus[0] == 0)
			{
				Log.v(TAG, GLES20.glGetProgramInfoLog(mProgram));
				GLES20.glDeleteProgram(mProgram);
				mProgram = 0;
			}
		}

		if (mProgram == 0)
		{
			throw new RuntimeException("Error creating program.");
		}
		return mProgram;
	}

	public static int loadShader(int type, String shaderCode){
		int shaderHandle = GLES20.glCreateShader(type);

		if (shaderHandle != 0)
		{
			GLES20.glShaderSource(shaderHandle, shaderCode);
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0)
			{
				Log.v(TAG, "Shader fail info: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}


		if (shaderHandle == 0)
		{
			throw new RuntimeException("Error creating shader " + type);
		}
		return shaderHandle;
	}

	public static FloatBuffer newFloatBuffer(float[] verticesData) {
		FloatBuffer floatBuffer;
		floatBuffer = ByteBuffer.allocateDirect(verticesData.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		floatBuffer.put(verticesData).position(0);
		return floatBuffer;
	}

//	public static int loadAndCompileShaderCode(int type, String name, String shaderCode)
//	{
//		int shader = GLES20.glCreateShader(type);
//		if (shader == 0) {
//			throw new RuntimeException(
//					"shader: could not get handler for " + name);
//		}
//		GLES20.glShaderSource(shader, shaderCode);
//		GLES20.glCompileShader(shader);
//
//		// Get the compilation status.
//		final int[] compileStatus = new int[1];
//		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
//
//		// If the compilation failed, delete the shader.
//		if (compileStatus[0] == 0) {
//			GLES20.glDeleteShader(shader);
//			throw new RuntimeException(
//					"shader: could not compile " + name + " : " + GLES20.glGetShaderInfoLog(shader));
//		}
//		Log.i(TAG, "Shader: " + name + " compiled"); 
//
//		return shader;
//	}

	public static String getShaderCode(String name, Context context) 
	{
		InputStream is;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			is = context.getAssets().open(name);
			BufferedReader bufferedBuilder = new BufferedReader(new InputStreamReader(is));

			String line;
			while ((line = bufferedBuilder.readLine()) != null) {
				stringBuilder.append(line + '\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
}
