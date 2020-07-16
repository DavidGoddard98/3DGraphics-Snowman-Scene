/*
 * The code in this file is a collaboration of Steve Maddock's work from his
 * 3d graphics tutorials along with a few adaptations by David Goddard.
 * See their emails below
 * David Goddard - dgoddard3@sheffield.ac.uk
 * Steve Maddock - s.maddock@sheffield.ac.uk
*/

import com.jogamp.opengl.*;

/*
 * This class essentially does what ModelNode does but extends it to work on
 * spotlight instances instead of models.
*/

public class SpotLightNode extends SGNode {

  protected SpotLight spotLight;

  public SpotLightNode(String name, SpotLight spotLight) {
    super(name);
    this.spotLight = spotLight;
  }

  public void draw(GL3 gl) {
    spotLight.render(gl, worldTransform);
    for (int i=0; i<children.size(); i++) {
      children.get(i).draw(gl);
    }
  }

}
