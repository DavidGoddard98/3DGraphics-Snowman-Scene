/*
 * The code in this file is a collaboration of Steve Maddock's work from his
 * 3d graphics tutorials along with a few adaptations by David Goddard.
 * See their emails below
 * David Goddard - dgoddard3@sheffield.ac.uk
 * Steve Maddock - s.maddock@sheffield.ac.uk
*/

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

/*
 * This class extends the light class to work as a Spotlight
 * The additional methods achieve this functionality.
*/

public class SpotLight extends Light {

  private Material material;
  private Vec3 position, direction, front;
  private Mat4 worldTransform;
  private Mat4 model;
  private Shader shader;
  private Camera camera;
  float cutOff;

  public SpotLight(GL3 gl, Camera camera) {
    super(gl, camera);
    material = new Material();
    material.setAmbient(0.4f, 0.4f, 0.4f);
    material.setDiffuse(0.4f, 0.4f, 0.4f);
    material.setSpecular(0.8f, 0.8f, 0.8f);

    //The radius of the spotlight
    cutOff = (float)Math.cos( 12.5 * 360.0 / 180.0 );
    //for spotlight (lightPole)
    position = new Vec3(-7, 9.6f, 7);
    direction = new Vec3(0f,-0.5f,0f);
    worldTransform = new Mat4(1);
    this.camera = camera;
    model = new Mat4(1);
    shader = new Shader(gl, "vs/light.txt", "fs/light.txt");
    fillBuffers(gl);
  }

  public void setWorldTransform(Mat4 v){
    worldTransform = v;
  }

  public Mat4 getWorldTransform() {
    return worldTransform;
  }

  /**
   * Sets the radius of the spotlight in radians
   * @param      cutoff - the radius of the spotlight in radians
   */
  public void setCutOff(float cutoff) {
    this.cutOff = cutoff;
  }

  /**
   * Gets the radius of the spotlight in radians
   * @return      float - the radius of the spotlight in radians
   */
  public float getCutOff(){
    return this.cutOff;
  }

  /**
   * Render method from light but with an additional paramater - worldTransform
   * @param      gl - the gl program
   * @param      worldTransform - the model matrix for the spotlights position
   */
  public void render(GL3 gl, Mat4 worldTransform) {

    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), worldTransform));

    shader.use(gl);
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  public void dispose(GL3 gl) {
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

    // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering

    private float[] vertices = new float[] {  // x,y,z
      -0.5f, -0.5f, -0.5f,  // 0
      -0.5f, -0.5f,  0.5f,  // 1
      -0.5f,  0.5f, -0.5f,  // 2
      -0.5f,  0.5f,  0.5f,  // 3
       0.5f, -0.5f, -0.5f,  // 4
       0.5f, -0.5f,  0.5f,  // 5
       0.5f,  0.5f, -0.5f,  // 6
       0.5f,  0.5f,  0.5f   // 7
     };

    private int[] indices =  new int[] {
      0,1,3, // x -ve
      3,2,0, // x -ve
      4,6,7, // x +ve
      7,5,4, // x +ve
      1,5,7, // z +ve
      7,3,1, // z +ve
      6,4,0, // z -ve
      0,2,6, // z -ve
      0,4,5, // y -ve
      5,1,0, // y -ve
      2,3,7, // y +ve
      7,6,2  // y +ve
    };

  private int vertexStride = 3;
  private int vertexXYZFloats = 3;

  // ***************************************************
  /* THE LIGHT BUFFERS
   */

  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];

  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);

    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);

    int stride = vertexStride;
    int numXYZFloats = vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);

    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    gl.glBindVertexArray(0);
  }

}
