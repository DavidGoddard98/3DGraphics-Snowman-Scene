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
 * This class helps clear up the code in Assignment_GLEventListener.initialise
 * by offshooting the setup of the snowmans hierarchical model.
*/

public class Snowman  {
  private GL3 gl;
  private Camera camera;
  private Light light;
  private SpotLight spotLight;

  public Snowman(GL3 gl, Camera camera, Light light, SpotLight spotLight) {
    this.gl = gl;
    this.camera = camera;
    this.light = light;
    this.spotLight = spotLight;
  }

  private SGNode snowmanRoot;
  private float xPosition = 0, zPosition = xPosition;
  private TransformNode snowmanRockTransform, rollHead, snowmanMoveXTranslate, snowmanMoveZTranslate;
  private Model nose, stoneMouth, button, sphere, santaHat, bobble, hatfluff;
  private static final float BODYSIZE = 3.5f;
  private static final float HEADSIZE = 2f;
  private static final float BUTTONSIZE = 0.2f;

  /**
   * Returs the snowman as a SGNode ready to be rendered in Assignment_GLEventListener
   * @return      SGNode - the snowman heirarchy
   */
  public SGNode initialise() {

    //Import textures
    int[] dirtySnow_texture = TextureLibrary.loadTexture(gl, "textures/dirty_snow.jpg");
    int[] snow_texture = TextureLibrary.loadTexture(gl, "textures/snow_test.jpg");
    int[] button_texture = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    int[] carrot_texture = TextureLibrary.loadTexture(gl, "textures/carrot.jpg");
    int[] stone_texture = TextureLibrary.loadTexture(gl, "textures/stone.jpg");
    int[] santa_texture = TextureLibrary.loadTexture(gl, "textures/santa_texture.jpg");
    int[] fluff_texture = TextureLibrary.loadTexture(gl, "textures/fluff_texture.jpg");

    //BODY && HEAD///////////////////////////////////////////////////////////////////////////////////////////////////////////
    Shader shader = new Shader(gl, "vs/standard.txt", "fs/snowman.txt");
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Material material = new Material(new Vec3(0.2f, 0.2f, 0.31f), new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.1f, 0.1f, 0.1f),2.0f);
    Mat4 modelMatrix = Mat4Transform.translate(0,0.5f,0);
    sphere = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, snow_texture, dirtySnow_texture);

    //Models for the head
    shader = new Shader(gl, "vs/standard.txt", "fs/single_texture.txt");

    stoneMouth = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, stone_texture);
    nose = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, carrot_texture);

    //button model used for eyes and three buttons
    modelMatrix = Mat4Transform.translate(0,0.5f,0);
    button = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, button_texture);

    //Hat
    hatfluff = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, snow_texture, fluff_texture);
    bobble = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, fluff_texture);
    material = new Material(new Vec3(0.8f, 0.1f, 0.1f), new Vec3(0.8f, 0.1f, 0.1f), new Vec3(0.6f, 0.6f, 0.6f),2.0f);
    mesh = new Mesh(gl, Cone.vertices.clone(), Cone.indices.clone());
    santaHat = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, santa_texture);

    // SNOWMAN CONSTRUCTION////////////////////////////////////////////////////////////////////////////////////////////////////

    float x, y, z;
    Mat4 buttonScaleTransform = Mat4Transform.scale(BUTTONSIZE, BUTTONSIZE, BUTTONSIZE);

    snowmanRoot = new NameNode("root");
    snowmanRockTransform  = new TransformNode("rock snowman", new Mat4(1));
    snowmanMoveXTranslate = new TransformNode("snowman x move transform", new Mat4(1));
    snowmanMoveZTranslate = new TransformNode("snowman Z move transform", new Mat4(1));
    //////////////////////////BODY///////////////////////////////////////////////
    NameNode body = new NameNode("body");
      Mat4 m = Mat4Transform.scale(BODYSIZE,BODYSIZE,BODYSIZE);
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

      TransformNode bodyTransform = new TransformNode("body transform", m);
        ModelNode bodyShape = new ModelNode("Sphere(body)", sphere);

    /////////////////////BUTTONS///////////////////////////////////////
    NameNode buttons = new NameNode("buttons");
      y = (float)(BODYSIZE*0.486);
      z = (float)(BODYSIZE*0.502);
      m = Mat4.multiply(Mat4Transform.translate(0, y, z), buttonScaleTransform);
      TransformNode buttonOneTransform = new TransformNode("button one", m);
        ModelNode buttonOneShape = new ModelNode("button one", button);

      y = (float)(BODYSIZE*0.686);
      z = (float)(BODYSIZE*0.469);
      m = Mat4.multiply(Mat4Transform.translate(0, y, z), buttonScaleTransform);
      TransformNode buttonTwoTransform = new TransformNode("button two", m);
        ModelNode buttonTwoShape = new ModelNode("button two", button);

      y = (float)(BODYSIZE*0.886);
      z = (float)(BODYSIZE*0.356);
      m = Mat4.multiply(Mat4Transform.translate(0, y, z), buttonScaleTransform);
      TransformNode buttonThreeTransform = new TransformNode("button three", m);
        ModelNode buttonThreeShape = new ModelNode("button three", button);

    //translate head up body height
    m = Mat4Transform.translate(0,(float)(BODYSIZE/2),0);
    TransformNode halfBodyHeight = new TransformNode("body height transform", m);
      rollHead = new TransformNode("body height transform", new Mat4(1));
        m = Mat4Transform.translate(0,(float)(BODYSIZE/2),0);
        TransformNode bodyHeight = new TransformNode("body height transform", m);
        ///////////////////////////////HEAAD/////////////////////////////////////////////////
        NameNode head = new NameNode("head");
          m = Mat4Transform.scale(HEADSIZE,HEADSIZE,HEADSIZE);
          m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
          TransformNode headTransform = new TransformNode("head transform", m);
            ModelNode headShape = new ModelNode("Sphere(head)", sphere);

        ///////////////////EYES//////////////////////////////////////////////////
        NameNode eyes = new NameNode("eyes");
          x = (float)(HEADSIZE*0.14);
          y = (float)(HEADSIZE*0.7);
          z = (float)(HEADSIZE*0.47);
          m = Mat4.multiply(Mat4Transform.translate(-x, y, z), buttonScaleTransform);
          TransformNode eyeLeftTransform = new TransformNode("eye left", m);
            ModelNode eyeLeftShape = new ModelNode("eye left", button);

          m = Mat4.multiply(Mat4Transform.translate(x, y, z), buttonScaleTransform);
          TransformNode eyeRightTransform = new TransformNode("eye right", m);
            ModelNode eyeRightShape = new ModelNode("eye right", button);

        ///////////////////NOSE///////////////////////////////////////////////////
        NameNode carrotNose = new NameNode("nose");
        y = (float)(HEADSIZE*0.5);
        z = (float)(HEADSIZE* 0.6);
        m = Mat4.multiply(Mat4Transform.translate(0, y, z), Mat4Transform.scale(0.15f,0.15f,0.6f));
        TransformNode noseTransform = new TransformNode("nose transform", m);
          ModelNode noseShape = new ModelNode("nose shape", nose);

        ///////////////////MOUTH////////////////////////////////////////////////
        NameNode mouth = new NameNode("mouth");
        y = (float)(HEADSIZE*0.3);
        z = (float)(HEADSIZE*0.45);
        m = Mat4.multiply(Mat4Transform.translate(0, y, z), Mat4Transform.scale(0.45f,0.15f,0.15f));
        TransformNode mouthTransform = new TransformNode("mouth transform", m);
          ModelNode mouthShape = new ModelNode("mouth shape", stoneMouth);

        //////////////////HAT////////////////////////////////////////////////////////

        NameNode hatFluff = new NameNode("hatFluff");
          m = Mat4Transform.scale(HEADSIZE-0.15f,HEADSIZE-0.5f,HEADSIZE-0.15f);
          float theHeight = (float)(HEADSIZE/2 );
          m = Mat4.multiply(m, Mat4Transform.translate(0,theHeight + 0.2f,0));
          TransformNode hatFluffTransfom = new TransformNode("hatFluff transform", m);
            ModelNode hatFluffShape = new ModelNode("hatFluff shape", hatfluff);

        NameNode hat = new NameNode("hat");
        y = (float)(HEADSIZE);
        m = Mat4.multiply(Mat4Transform.translate(0, y+ 1.62f,0 ), Mat4Transform.scale(1f,1f,1f));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(180));
        TransformNode hatTransform = new TransformNode("hat transform", m);
          ModelNode hatShape = new ModelNode("hat shape", santaHat);


          NameNode hat_bobble = new NameNode("bobble_right");
          y = (float)(HEADSIZE);
          m = Mat4.multiply(Mat4Transform.rotateAroundZ(-60), Mat4Transform.translate(0,0.1f,0 ));
          m = Mat4.multiply(m, Mat4Transform.scale(0.5f,0.5f,0.5f));
          TransformNode bobbleTransform = new TransformNode("bobble_right transform", m);
            ModelNode bobbleShape = new ModelNode("bobble_right shape", bobble);


    snowmanRoot.addChild(snowmanMoveXTranslate);
        snowmanMoveXTranslate.addChild(snowmanMoveZTranslate);
          snowmanMoveZTranslate.addChild(snowmanRockTransform);
          snowmanRockTransform.addChild(body);

            //BODY
            body.addChild(bodyTransform);
              bodyTransform.addChild(bodyShape);
            body.addChild(buttons);
              buttons.addChild(buttonOneTransform);
                buttonOneTransform.addChild(buttonOneShape);
              buttons.addChild(buttonTwoTransform);
                buttonTwoTransform.addChild(buttonTwoShape);
              buttons.addChild(buttonThreeTransform);
                buttonThreeTransform.addChild(buttonThreeShape);

            //HEAD
            body.addChild(halfBodyHeight);
              halfBodyHeight.addChild(rollHead);
              rollHead.addChild(bodyHeight);
                bodyHeight.addChild(head);
                  head.addChild(headTransform);
                    headTransform.addChild(headShape);
                  head.addChild(eyes);
                    eyes.addChild(eyeLeftTransform);
                      eyeLeftTransform.addChild(eyeLeftShape);
                    eyes.addChild(eyeRightTransform);
                      eyeRightTransform.addChild(eyeRightShape);
                  head.addChild(carrotNose);
                    carrotNose.addChild(noseTransform);
                      noseTransform.addChild(noseShape);
                   head.addChild(mouth);
                     mouth.addChild(mouthTransform);
                       mouthTransform.addChild(mouthShape);

                  //Hat
                   head.addChild(hat);
                     hat.addChild(hatTransform);
                       hatTransform.addChild(hatShape);
                         hatShape.addChild(hat_bobble);
                           hat_bobble.addChild(bobbleTransform);
                             bobbleTransform.addChild(bobbleShape);
                     hat.addChild(hatFluff);
                       hatFluff.addChild(hatFluffTransfom);
                         hatFluffTransfom.addChild(hatFluffShape);


    snowmanRoot.update();

    return snowmanRoot;
  }

   /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable, GL3 gl) {
    sphere.dispose(gl);
    nose.dispose(gl);
    stoneMouth.dispose(gl);
    button.dispose(gl);
  }

  /*ANIMATION*/

  private double elapsedRock, elapsedRoll, startSlide,elapsedSlide, startRock, savedSlide;


  //instances variables to deal with slide
   private boolean turn45 = true;
   private boolean backwards = false;
   private boolean turn180 =false;
   private boolean move = false;
   private float increment = 0.02f;
   private int i = 0;
   private int oldI;
   private float pos;
   private int difference;

   /**
    * Is called when slide button is clicked on GUI
    * Slides the snowman in a diagonal line.
    * Initially rotates him 45 degrees and then slides.
    * If he reaches a certain point he turns 180 degrees and then moves in that direction
    */
    public void slideSnowman(double getSeconds) {

      //Ensures snowman doesnt move off screen
      if (pos >= 4 || pos <=-4) {
        turn180 = true;
        move = false;
      }

      //turn 45 degree so snowman faces direction of motion
      if (turn45) {
        turn180 = false;
        move = false;
        /*
        * Deals with the reset button ie if reset button is pressed it remembers which
        * way it was currently moving and then turns back to that position accordinly.
        */
        if (backwards) {
          i += -1;
          //If backwards snowman needs to rotate 135 degrees to be facing right direction
          // this ensures that and will make the difference = 45 for later conditional
          difference = -i -90;
        } else{
          i += 1;
          difference = i;

        }
        snowmanMoveZTranslate.setTransform(Mat4Transform.rotateAroundY(i));
        snowmanMoveZTranslate.update();
        //if turned 45 degree then continue can continue sliding
        if (difference ==  45) {
          turn45 = false;
          move = true;
        }
      }

      //turn snowman around to face opposite direction
      if(turn180) {
        i += 1;
        snowmanMoveZTranslate.setTransform(Mat4Transform.rotateAroundY(i));
        snowmanMoveZTranslate.update();
        difference = i-oldI;
        if (difference == 180){
          turn180 = false;
          increment = -increment;
          move = true;
          backwards = !backwards;
        }
      }

      //move in a diagonal line
      if (move) {
        pos += increment;
        snowmanMoveXTranslate.setTransform(Mat4Transform.translate(pos,0,pos));
        snowmanMoveXTranslate.update();
        oldI = i;
      }
  }


  /**
   * Is called when rock button is clicked on GUI
   * rocks the snowman slowly back and forth around the Z axis
   * @return    elapsedRock - The time the snowman has been rocking for
   *                          Allows it to continue from this point.
   */
  public double rockSnowman(double getSeconds, double startRock) {
    elapsedRock = getSeconds - startRock;
    float rotateAngle = 35f*(float)Math.sin(elapsedRock);
    snowmanRockTransform.setTransform(Mat4Transform.rotateAroundZ(rotateAngle));
    snowmanRockTransform.update();
    return elapsedRock;
  }

  /**
   * Is called when roll button is clicked on the GUI
   * Rolls the snowmans head around the Z axis.
   * @return    elapsedRoll - The time the snowman has been rolling for
   *                          Allows it to continue from this point.
   */
  public double rollSnowman(double getSeconds, double startRoll) {
    elapsedRoll = getSeconds - startRoll;
    float rotateAngle = 50*(float)Math.sin(elapsedRoll);
    rollHead.setTransform(Mat4Transform.rotateAroundZ(rotateAngle));
    rollHead.update();
    return elapsedRoll;
  }

  /**
   * Is called when the reset button is clicked on GUI.
   * Applies the identity matrix to the relevant transform nodes to
   * set them back to normal.
   */
  public void resetAnimations() {
    Mat4 m = new Mat4(1);
    rollHead.setTransform(m);
    snowmanRockTransform.setTransform(m);
    snowmanMoveZTranslate.setTransform(m);
    //For ensuring the snowman turns 45 degrees again when slide button is pressed again
    turn45 = true;
    i = 0;
    oldI = 0;
    snowmanRoot.update();
  }

}
