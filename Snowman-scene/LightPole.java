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
 * This file helps clear up the code in Assignment_GLEventListener.initialise
 * by offshooting the setup of the lightpole hierarchical model.
*/

public class LightPole  {

  private GL3 gl;
  private Camera camera;
  private Light light;
  private SpotLight spotLight;

  public LightPole(GL3 gl, Camera camera, Light light, SpotLight spotLight) {
    this.gl = gl;
    this.camera = camera;
    this.light = light;
    this.spotLight = spotLight;
  }

  /* Initialise CONSTANTS for the sizes of the light pole. */
  private static final float LIGHT_BASE_LENGTH = 2.2f;
  private static final float LIGHT_BASE_SCALE = 1f;

  private static final float LIGHT_MIDDLE_LENGTH = 8f;
  private static final float LIGHT_MIDDLE_SCALE = 0.5f;
  private static final float LIGHT_MIDDLE_HEIGHT = LIGHT_MIDDLE_LENGTH + LIGHT_BASE_LENGTH;

  private static final float LIGHT_TOP_LENGTH = 2.2f;
  private static final float LIGHT_TOP_SCALE = 0.7f;

  private static final float SPOT_LIGHT_LENGTH = 1.1f;
  private static final float SPOT_LIGHT_SCALE = 0.3f;

  private SGNode lightPoleRoot;
  private TransformNode rotateSpotLight, lightTransform, lightPolePosition, rotateAroundXY;
  private Model rectangle;

  /**
   * Returs the lightpole as a SGNode ready to be rendered in Assignment_GLEventListener
   * @return      SGNode - the lightpole heirarchy
   */
  public SGNode initialise() {

    //import texture
    int[] shiny_metal = TextureLibrary.loadTexture(gl, "textures/shiny_metal.jpg");

    //Create rectangle
    Shader shader = new Shader(gl, "vs/standard.txt", "fs/single_texture.txt");
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.translate(0,0.5f,0);
    rectangle = new Model(gl, camera, light, spotLight, shader, material, modelMatrix, mesh, shiny_metal);

    // LIGHTPOLE HEIRARCHY CONSTRUCTION/////////////////////////////////////////////////////////
    Mat4 m = new Mat4(1);

    lightPoleRoot = new NameNode("light root");
    lightPolePosition = new TransformNode("lightPole translate", Mat4Transform.translate(-7f, 0, 7f));

    //Base of light pole
    NameNode lightBase = new NameNode("light base");
      m = Mat4Transform.scale(LIGHT_BASE_SCALE, LIGHT_BASE_LENGTH ,LIGHT_BASE_SCALE);
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

      TransformNode lightBaseTransform = new TransformNode("base transform", m);
        ModelNode lightBaseShape = new ModelNode("base shape", rectangle);

    //move up to height of base
    TransformNode lightBaseHeight = new TransformNode("base height", Mat4Transform.translate(0f,LIGHT_BASE_LENGTH,0f));

      //Light pole middle
      NameNode lightMiddle = new NameNode("light middle");
        m = Mat4Transform.scale(LIGHT_MIDDLE_SCALE, LIGHT_MIDDLE_LENGTH, LIGHT_MIDDLE_SCALE);
        m =  Mat4.multiply(m, Mat4Transform.translate(0f,0.5f,0f));
        TransformNode lightMiddleTransform = new TransformNode("middle transform", m);
          ModelNode lightMiddleShape = new ModelNode("middle shape", rectangle);

      //move up to height of middle part
      TransformNode lightMiddleHeight = new TransformNode("base height", Mat4Transform.translate(0f,LIGHT_MIDDLE_HEIGHT,0f));

        //Top of light pole
        NameNode lightTop = new NameNode("light top");
          m = Mat4.multiply( Mat4Transform.rotateAroundX(60), Mat4Transform.rotateAroundZ(20) );
          rotateAroundXY = new TransformNode("spotlight rotate", m);
            rotateSpotLight = new TransformNode("spotlight rotate",  new Mat4(1));

                m = Mat4Transform.scale(LIGHT_TOP_SCALE, LIGHT_TOP_LENGTH, LIGHT_TOP_SCALE);
                TransformNode lightTopTransform = new TransformNode("head transform", m);
                  ModelNode lightTopShape = new ModelNode("Sphere(light TOP)", rectangle);

                //Light source
                m = Mat4Transform.translate(0f, SPOT_LIGHT_LENGTH, 0f);
                m = Mat4.multiply(m, Mat4Transform.scale(SPOT_LIGHT_SCALE, SPOT_LIGHT_SCALE, SPOT_LIGHT_SCALE));
                lightTransform = new TransformNode("lightTransform", m );
                  SpotLightNode lightShape = new SpotLightNode("Spotlight", spotLight);

    lightPoleRoot.addChild(lightPolePosition);

      //Base
      lightPolePosition.addChild(lightBase);
        lightBase.addChild(lightBaseTransform);
          lightBaseTransform.addChild(lightBaseShape);

      //Middle
      lightPolePosition.addChild(lightBaseHeight);
        lightBaseHeight.addChild(lightMiddle);
          lightMiddle.addChild(lightMiddleTransform);
            lightMiddleTransform.addChild(lightMiddleShape);

      //Top
      lightPolePosition.addChild(lightMiddleHeight);
        lightMiddleHeight.addChild(lightTop);
          lightTop.addChild(rotateAroundXY);
            rotateAroundXY.addChild(rotateSpotLight);
              rotateSpotLight.addChild(lightTopTransform);
                lightTopTransform.addChild(lightTopShape);
              //actual spotLight
              rotateSpotLight.addChild(lightTransform);
                lightTransform.addChild(lightShape);

    lightPoleRoot.update();

    return lightPoleRoot;
  }

  /* Free up memory if necessary */
  public void dispose(GLAutoDrawable drawable, GL3 gl) {
    rectangle.dispose(gl);
  }

  /**
   * Rotates the spotlight over time based on the elapsedTime of the program
   * @param     elapsedTime - the time since program start.
   */
  public void moveSpotLight(double elapsedTime) {
    float rotateAngle = 180f+50f*(float)Math.sin(elapsedTime);
    rotateSpotLight.setTransform(Mat4Transform.rotateAroundZ(rotateAngle));
    rotateSpotLight.update();
  }

  /**
   * Finds the current positon and direction of the spotlight
   * Essentially goes from the root of tree (lightPoleRoot) and takes the path
   * down to the lightShape node, multiplying the matrix'. It sets the light position
   * as the third column of this matrix.
   * Similar process with the direction.
   */
  public void updateSpotLight() {
    Mat4 lightHeight = Mat4Transform.translate(0f, LIGHT_MIDDLE_HEIGHT, 0f);
    Mat4 lightWorldTransform = Mat4.multiply(lightPolePosition.getTransform(), lightHeight);
    lightWorldTransform = Mat4.multiply(lightWorldTransform, rotateAroundXY.getTransform());
    lightWorldTransform = Mat4.multiply(lightWorldTransform, rotateSpotLight.getTransform());
    lightWorldTransform = Mat4.multiply( lightWorldTransform, lightTransform.getTransform());
    //get the last column from matrix (position)
    spotLight.setPosition(lightWorldTransform.getMatrixPosition());

    //update direction of light
    lightWorldTransform = Mat4.multiply(Mat4Transform.translate(0f, -0.5f, 0f), rotateAroundXY.getTransform());
    lightWorldTransform = Mat4.multiply( lightWorldTransform, rotateSpotLight.getTransform());
    lightWorldTransform = Mat4.multiply( lightWorldTransform, lightTransform.getTransform());
    //get the last column from matrix (position)
    spotLight.setDirection(lightWorldTransform.getMatrixPosition());


  }

}
