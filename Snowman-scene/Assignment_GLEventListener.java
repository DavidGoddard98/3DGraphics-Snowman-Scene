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
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import java.awt.image.BufferedImage;

/*
 * This class implements GLEventListener - It implements the scene
*/
public class Assignment_GLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;
  public static final int SCENE_WIDTH = 32;
  public static final int SCENE_HEIGHT = 20;
  public static final int SCENE_DEPTH = 26;

  public Assignment_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }

  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }

  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    spotLight.dispose(gl);
    floor.dispose(gl);
    snowyScene.dispose(gl);
    snowMan.dispose(drawable, gl);
    lightPole.dispose(drawable, gl);
  }


  // ***************************************************
  /* INTERACTION
   *
   *
   */

  private boolean slideAnimation = false;
  private boolean rockAnimation = false;
  private boolean rollAnimation = false;
  private boolean resetAnimation = false;

  private double savedTime = 0;

  /*
  * Called when slide button is pressed
  * Starts the slide animation
  */
  public void slideAnimation() {
    slideAnimation = !slideAnimation;
  }

  /*
  * Called when rock button is pressed
  * Starts the rock animation
  */
  public void rockAnimation() {
    rockAnimation = !rockAnimation;
  }

  /*
  * Called when roll button is pressed
  * Starts the roll animation
  */
  public void rollAnimation() {
    rollAnimation = !rollAnimation;
  }

  /*
  * Called when Roll, Rock and Slide button is pressed
  * sets all the animations to true so they begin working.
  */
  public void rrsAnimation() {
    slideAnimation = true;
    rockAnimation = true;
    rollAnimation = true;
  }

  /*
  * Called when reset button is pressed
  * sets all the animations to false.
  */
  public void stopAnimation() {
    slideAnimation = false;
    rockAnimation = false;
    rollAnimation = false;
    resetAnimation = true;
  }


  /**
   * Toggles the sun on and off
   * @param sun_toggle  boolean indicating if sun is on or off
   */
  public void toggleSun(boolean sun_toggle) {
    Material material;
    if (!sun_toggle) {
      material = new Material(new Vec3(0.1f, 0.1f, 0.1f), new Vec3(0.1f, 0.1f, 0.1f), new Vec3(0.1f, 0.1f, 0.1f), 32.0f);
    } else {
      material = new Material(new Vec3(0.4f, 0.4f, 0.4f), new Vec3(0.4f, 0.4f, 0.4f), new Vec3(0.4f, 0.4f, 0.4f), 32.0f);
    }
    light.setMaterial(material);
  }

  /**
   * Toggles the spotLight on and off
   * @param spotLight_toggle  boolean indicating if spotLight is on or off.
   */
  public void toggleSpotLight(boolean spotLight_toggle) {
    Material material;
    if (!spotLight_toggle) {
      material = new Material(new Vec3(0, 0, 0), new Vec3(0, 0 ,0), new Vec3(0, 0, 0), 32.0f);
    } else {
      material = new Material(new Vec3(0.4f, 0.4f, 0.4f), new Vec3(0.4f, 0.4f, 0.4f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
    }
    spotLight.setMaterial(material);
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  //Instance variables
  private Snowman snowMan;
  private LightPole lightPole;
  private Camera camera;
  private Mat4 perspective, saveSpotLightPos;
  private Model floor, snowyScene, christmas_present ;
  private Light light;
  private SpotLight spotLight;
  private SGNode snowmanRoot, lightPoleRoot;
  private double elapsedRock, startRock, startRoll, elapsedRoll, startSlide, elapsedSlide;


  private float xPosition = 0, zPosition = xPosition;

  /**
   * Creates the models used within the scene graph filling the buffers and the such
   * @param gl  the gl program
   */
  private void initialise(GL3 gl) {

    //IMPORT TEXTURES////////////////////////////////////////////////////////////////////////////////////////////////////////
    int[] snow_background = TextureLibrary.loadTexture(gl, "textures/snow_background.jpg");
    int[] snowfall_1 = TextureLibrary.loadTexture(gl, "textures/snowFall.jpg");
    int[] snowfall_2 = TextureLibrary.loadTexture(gl, "textures/snowFall2.jpg");
    int[] snow_texture = TextureLibrary.loadTexture(gl, "textures/snow_test.jpg");
    int[] present_diffuse = TextureLibrary.loadTexture(gl, "textures/present_diffuse.jpg");
    int[] present_specular = TextureLibrary.loadTexture(gl, "textures/present_specular.jpg");

    light = new Light(gl, camera);
    spotLight = new SpotLight(gl, camera);

    //SNOWMAN CONSTRUCTION
    snowMan = new Snowman(gl, camera, light, spotLight);
    snowmanRoot = snowMan.initialise();

    //LIGHTPOLE CONSTRUCTION
    lightPole = new LightPole(gl, camera, light, spotLight);
    lightPoleRoot = lightPole.initialise();


    //THE FLOOR OF THE SCENE
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs/standard.txt", "fs/single_texture.txt");
    Material material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(SCENE_WIDTH,1f,SCENE_DEPTH);
    floor = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, snow_texture);

    //SNOWY SCENE SETUP (BACKGROUND) (uses floor and then translates)
    //New material to make the primary texture (the background scene) lighter due to the mixing of three textues
    shader = new Shader(gl, "vs/snowScene.txt", "fs/snowscene.txt");
    material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.9f, 0.9f, 0.9f), 128.0f);
    //Translate the floor to make it vertical
    modelMatrix = constructSnowyScene();
    snowyScene = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, snow_background, snowfall_1, snowfall_2);

    //PRESENT SETUP
    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs/standard.txt", "fs/present.txt");
    modelMatrix = Mat4.multiply(Mat4Transform.scale(3,3,3), Mat4Transform.translate(-3.2f,0.5f,0));
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1f, 1f, 1f), 2.0f);
    christmas_present = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, present_diffuse, present_specular);

  }

  /**
   * Renders the modles every frame (60 a sec)
   * Also updates the position of the spotlight
   * @param gl  the gl program
   */
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    light.render(gl);
    //LIGHTPOLE
    lightPole.moveSpotLight(getElapsedTime());
    lightPole.updateSpotLight();
    lightPoleRoot.draw(gl);

    //PRESENT
    christmas_present.render(gl);

    //FLOOR..
    floor.render(gl);

    //Deals with the falling snow in the background
    snowyScene.updateOffset(gl, getElapsedTime());
    //RENDERS BACKGROUND
    snowyScene.render(gl);


    //The animations - booleans are set in methods above
    if (slideAnimation) {
      snowMan.slideSnowman(getSeconds());
    }
    if (rockAnimation) {
      elapsedRock = snowMan.rockSnowman(getSeconds(), startRock);

    }
    if (rollAnimation) {
      elapsedRoll = snowMan.rollSnowman(getSeconds(), startRoll);

    }
    if (resetAnimation) {
      snowMan.resetAnimations();
      resetAnimation = false;
      //set roll and rock and animation to start
      elapsedRock = startTime;
      elapsedRoll = startTime;
    }

    /*Times how long the snowman has been rocking for so it can continue rocking
    * if the button is pressed again.
    */
    startRock = getSeconds() - elapsedRock;
    startRoll = getSeconds() - elapsedRoll;


    //SNOWMAN
    snowmanRoot.draw(gl);

  }

  /**
   * Finds the time in seconds since the program started
   * @param gl  the gl program
   */
  private double getElapsedTime() {
    double elapsedTime = getSeconds()-startTime;
    return elapsedTime;
  }

  /**
   * Returns a matrix used on the floor model to create the background model.
   * changes its size, pushes it back in z axis and makes it vertical.
   * @return      Mat4 matrix to translate and scale floor
   */
  private Mat4 constructSnowyScene() {
    Mat4 modelMatrix = Mat4Transform.translate(0.0f,SCENE_HEIGHT/2,-SCENE_DEPTH/2);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(SCENE_WIDTH,1f,SCENE_HEIGHT));
    return modelMatrix;
  }

  // ***************************************************
  /* TIME
   */

  private double startTime;

  /**
   * Find the current time in miliseconds
   * @return      Double current time
   */
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

}
