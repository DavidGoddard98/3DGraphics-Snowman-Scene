import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import java.lang.*;


public class Model {

  private Mesh mesh;
  private int[] textureId1;
  private int[] textureId2;
  private int[] textureId3;
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private Light light;
  private SpotLight spotLight;

  /*The following models constructors have been adapted to take a spotlight instance */
  public Model(GL3 gl, Camera camera, Light light, SpotLight spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, int[] textureId3) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.light = light;
    this.spotLight = spotLight;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
    this.textureId3 = textureId3;
  }

  public Model(GL3 gl, Camera camera, Light light, SpotLight spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.light = light;
    this.spotLight = spotLight;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }

  public Model(GL3 gl, Camera camera, Light light, SpotLight spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, textureId1, null);
  }

  public Model(GL3 gl, Camera camera, Light light, SpotLight spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, null, null);
  }

  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }

  /**
   * Creates the snow effect on the background by incrementing the x and y coordinates respectively
   * @param gl  the gl program
   * @param elapsedTime the time since the program start
   */
  public void updateOffset(GL3 gl, double elapsedTime) {
    double t = elapsedTime*0.1;
    float offsetY = (float)(t - Math.floor(t));
    float offsetX = 0.0f;
    this.shader.use(gl);
    this.shader.setFloat(gl, "downOffset", offsetX, offsetY);
    this.shader.setFloat(gl, "diagonalOffset", offsetY, offsetY);
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public void setLight(Light light) {
    this.light = light;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light.direction", light.getDirection());
    shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
    shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
    shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());

    /*Sets the spotLights structure uniforms */
    shader.setVec3(gl, "spotLight.position", spotLight.getPosition());
    shader.setVec3(gl, "spotLight.direction", spotLight.getDirection());
    shader.setFloat(gl, "spotLight.cutOff",  spotLight.getCutOff());
    shader.setVec3(gl, "spotLight.ambient", spotLight.getMaterial().getAmbient());
    shader.setVec3(gl, "spotLight.diffuse", spotLight.getMaterial().getDiffuse());
    shader.setVec3(gl, "spotLight.specular", spotLight.getMaterial().getSpecular());

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    if (textureId3!=null) {
      shader.setInt(gl, "third_texture", 2);
      gl.glActiveTexture(GL.GL_TEXTURE2);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId3[0]);
    }
    mesh.render(gl);
  }

  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }

  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
    if (textureId3!=null) gl.glDeleteBuffers(1, textureId3, 0);

  }

}
