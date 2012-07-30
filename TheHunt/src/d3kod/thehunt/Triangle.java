//package d3kod.thehunt;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.FloatBuffer;
//
//import android.opengl.GLES20;
//
//public class Triangle {
//
//    private FloatBuffer vertexBuffer;
//    static final int BYTES_PER_FLOAT = 4;
//    // number of coordinates per vertex in this array
//    static final int COORDS_PER_VERTEX = 3;
//    static final int COLOR_PER_VERTEX = 4;
//    /** How many elements per vertex. */
//    static final int STRIDE_BYTES = 7 * BYTES_PER_FLOAT;
//    static final int POSITION_OFFSET = 0;
//    static final int COLOR_OFFSET = 3;
//    static float[] triangleVerticesData = {
//        // X, Y, Z,
//        // R, G, B, A
//        -0.5f, -0.25f, 0.0f,
//        1.0f, 0.0f, 0.0f, 1.0f,
//
//        0.5f, -0.25f, 0.0f,
//        0.0f, 0.0f, 1.0f, 1.0f,
//
//        0.0f, 0.559016994f, 0.0f,
//        0.0f, 1.0f, 0.0f, 1.0f};
//
//    // Set color with red, green, blue and alpha (opacity) values
//    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
//
//    
//    private final String vertexShaderCode =
//    			"uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
//    		 
//    		  + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
//    		  + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
//    		 
//    		  + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.
//    		 
//    		  + "void main()                    \n"     // The entry point for our vertex shader.
//    		  + "{                              \n"
//    		  + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
//    		                                            // It will be interpolated across the triangle.
//    		  + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
//    		  + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
//    		  + "}                              \n";    // normalized screen coordinates.
//
//
//    private final String fragmentShaderCode =
//    			"precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
//            // precision in the fragment shader.
//    		+ "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
//            // triangle per fragment.
//    		+ "void main()                    \n"     // The entry point for our fragment shader.
//    		+ "{                              \n"
//    		+ "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
//    		+ "}                              \n";
//
//	private int mProgram;
//	private int mMVPMatrixHandle;
//	private int mPositionHandle;
//	private int mColorHandle;
//    
//    public Triangle() {
//        vertexBuffer = ByteBuffer.allocateDirect(triangleVerticesData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        
//        // add the coordinates to the FloatBuffer
//        vertexBuffer.put(triangleVerticesData).position(0);
//        
//        int vertexShaderHandle = TheHuntRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
//        int fragmentShaderHandle = TheHuntRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
//
//        // Create a program object and store the handle to it.
//        mProgram = GLES20.glCreateProgram();
//         
//        if (mProgram != 0)
//        {
//            // Bind the vertex shader to the program.
//            GLES20.glAttachShader(mProgram, vertexShaderHandle);
//         
//            // Bind the fragment shader to the program.
//            GLES20.glAttachShader(mProgram, fragmentShaderHandle);
//         
//            // Bind attributes
//            GLES20.glBindAttribLocation(mProgram, 0, "a_Position");
//            GLES20.glBindAttribLocation(mProgram, 1, "a_Color");
//         
//            // Link the two shaders together into a program.
//            GLES20.glLinkProgram(mProgram);
//         
//            // Get the link status.
//            final int[] linkStatus = new int[1];
//            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
//         
//            // If the link failed, delete the program.
//            if (linkStatus[0] == 0)
//            {
//                GLES20.glDeleteProgram(mProgram);
//                mProgram = 0;
//            }
//        }
//         
//        if (mProgram == 0)
//        {
//            throw new RuntimeException("Error creating program.");
//        }
//        
//        // get handle to shape's transformation matrix
//        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix"); 
//        
//        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
//        mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
//    }
//    
//    public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix
//        // Add program to OpenGL ES environment
//        GLES20.glUseProgram(mProgram);
//        
//        // Pass in the position information
//        vertexBuffer.position(POSITION_OFFSET);
//        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, STRIDE_BYTES, vertexBuffer);
//        
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//        
//        // Pass in the color information
//        vertexBuffer.position(COLOR_OFFSET);
//        GLES20.glVertexAttribPointer(mColorHandle, COLOR_PER_VERTEX, GLES20.GL_FLOAT, false, STRIDE_BYTES, vertexBuffer);
//        
//        GLES20.glEnableVertexAttribArray(mColorHandle);
//      
//        // Apply the projection and view transformation
//        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
//
//        // Draw the triangle
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
//    }
//}
