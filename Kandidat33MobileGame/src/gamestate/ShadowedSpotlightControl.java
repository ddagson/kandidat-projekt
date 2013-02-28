/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestate;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.light.SpotLight;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.shadow.PssmShadowRenderer;
import java.io.IOException;

/**
 *
 * @author jonatankilhamn
 */
public class ShadowedSpotlightControl implements Control {
    
    
    Spatial spatial;
    SpotLight spotlight;
    PssmShadowRenderer shadowRenderer;
    Vector3f defaultPositionOffset = new Vector3f(0,50,100);
    
    public ShadowedSpotlightControl(SpotLight spotlight) {
        super();
        this.spotlight = spotlight;
    }

    public Control cloneForSpatial(Spatial spatial) {
        ShadowedSpotlightControl newSSC = new ShadowedSpotlightControl((SpotLight)spotlight.clone());
        newSSC.setSpatial(spatial);
        return newSSC;
    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
        update(0f);
    }
    
    public void setShadowRenderer(PssmShadowRenderer shadowRenderer) {
        this.shadowRenderer = shadowRenderer;
    }
    
    public void setDefaultPositionOffset(Vector3f defaultPositionOffset) {
        this.defaultPositionOffset = defaultPositionOffset;
    }

    private float time;
    
    public void update(float tpf) {
        time += tpf;
        Vector3f positionOffset = defaultPositionOffset.
                add((float)(20*Math.sin(time)),
                (float)(10*(-1-2*Math.sin(2*time))),
                (float)(10*Math.sin(1.4*time)));
        Vector3f directionOffset = new Vector3f(0f,0f,0f);
        
        
        Vector3f spatialPosition = spatial.getLocalTranslation();
        Vector3f lightPos = spatialPosition.add(positionOffset);
        
        spotlight.setPosition(lightPos);
        spotlight.setDirection(spatialPosition.subtract(lightPos));

        if (shadowRenderer != null) {
            shadowRenderer.setDirection(spotlight.getDirection());
        }
    }

    public void render(RenderManager rm, ViewPort vp) {
    }

    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}