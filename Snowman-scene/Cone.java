/*
 * The code in this file is a collaboration of Steve Maddock's work from his
 * 3d graphics tutorials along with a few adaptations by David Goddard.
 * See their emails below
 * David Goddard - dgoddard3@sheffield.ac.uk
 * Steve Maddock - s.maddock@sheffield.ac.uk
*/


/*
 * This class creates a cone model. Note it is not fully working as their
 * is a gap in the back...
*/
public final class Cone {

  private static final int NUM_POINTS = 960;
  private static final float HEIGHT = 2.0f, RADIUS = 1.0f;
  public static final float[] vertices = createVertices();
  public static final int[] indices = createIndices();

  private static float[] createVertices() {
    float[] vertices = new float[8 * (NUM_POINTS + 2)];
    vertices[0] = 0.0f;
    vertices[1] = 0.0f;
    vertices[2] = 0.0f;
    vertices[3] = 0.0f;
    vertices[4] = 0.0f;
    vertices[5] = 0.0f;
    vertices[6] = 0.0f;
    vertices[7] = 0.0f;

    float angle = ((float)Math.PI * 2)/(NUM_POINTS);

    for (int c = 0; c < NUM_POINTS; c++) {
        float x = RADIUS * (float)Math.sin(angle * c);
        float y = -2;
        float z = RADIUS * (float)Math.cos(angle * c);

        vertices[8*c] = x;
        vertices[8*c + 1] = y;
        vertices[8*c + 2] = z;
        vertices[8*c + 3] = 0.0f;
        vertices[8*c + 4] = -1f;
        vertices[8*c + 5] = 0.0f;
        vertices[8*c + 6] = 0.0f;
        vertices[8*c + 7] = 1.0f;
    }
      //manually input the top of the cone and the bottom of the cone.
      vertices[8*NUM_POINTS ]     = 0;
      vertices[8*NUM_POINTS  + 1] = -2;
      vertices[8*NUM_POINTS  + 2] = 0;
      vertices[8*NUM_POINTS  + 3] = 0.0f;
      vertices[8*NUM_POINTS  + 4] = -1;
      vertices[8*NUM_POINTS  + 5] = 0.0f;
      vertices[8*NUM_POINTS  + 6] = 0.0f;
      vertices[8*NUM_POINTS  + 7] = 1.0f;

      vertices[8*NUM_POINTS +   8] = 0;
      vertices[8*NUM_POINTS +   9] = 0;
      vertices[8*NUM_POINTS  + 10] = 0;
      vertices[8*NUM_POINTS  + 11] = 0.0f;
      vertices[8*NUM_POINTS  + 12] = -1;
      vertices[8*NUM_POINTS  + 13] = 0.0f;
      vertices[8*NUM_POINTS  + 14] = 0.0f;
      vertices[8*NUM_POINTS  + 15] = 1.0f;
    return vertices;
  }


  private static int[] createIndices() {
    int[] indices = new int[6*(NUM_POINTS) + 6];
    int counter = 0;
    for (int c = 0; c < NUM_POINTS; c++) {
      indices[6*c] = counter;
      indices[6*c + 1] = counter+8;
      indices[6*c + 2] = (8*NUM_POINTS);
      indices[6*c +3] = counter;
      indices[6*c + 4] = counter+8;
      indices[6*c + 5] = (8*NUM_POINTS) -7;
      counter += 8;

    }
    //manually try and connect last two vertex'
    indices[6*(NUM_POINTS)] = 0;
    indices[6*(NUM_POINTS) + 1] = (8*NUM_POINTS);
    indices[6*(NUM_POINTS) + 2] = (8*NUM_POINTS);
    indices[6*(NUM_POINTS) +3] = 0;
    indices[6*(NUM_POINTS) + 4] = (8*NUM_POINTS) - 7;
    indices[6*(NUM_POINTS)+ 5] = (8*NUM_POINTS) + 8;
    return indices;
  }



}
