/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spatial;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 *
 * @author jonatankilhamn
 */
public class LevelChunk extends Node {
    
    private Node levelRootNode;
    private ArrayList<Light> lights = new ArrayList<Light>();
    
    public LevelChunk(Node levelRootNode) {
        super();
        this.levelRootNode = levelRootNode;
    }
    
    @Override
    /**
     * @Inheritdoc
     * Physics-safe movement of chunk. Any other transformation might disrupt
     * the physics of platforms etc.
     */
    public void setLocalTranslation(Vector3f v) {
        disablePhysics();
        super.setLocalTranslation(v);
        enablePhysics();
        for (Light light : lights) {
            if (light instanceof PointLight) {
                PointLight pointLight = ((PointLight)light);
                pointLight.setPosition(v.add(pointLight.getPosition()));
            } else if (light instanceof SpotLight) {
                SpotLight spotLight = ((SpotLight)light);
                spotLight.setPosition(v.add(spotLight.getPosition()));
            }
        }
    }
    
    @Override
    /**
     * 
     */
    public void addLight(Light light) {
        lights.add(light);
    }
    
    /**
     * Adds this chunk to the level. Use this instead of directly attaching to
     * parent node in order to get lights right.
     */
    public void addToLevel() {
        for (Light light : lights) {
            levelRootNode.addLight(light);
        }
        levelRootNode.attachChild(this);
    }
    
    /**
     * Removes this chunk from the level. Use this instead of directly
     * detaching from parent node in order to get lights right.
     */
    public void remove() {
        for (Light light : lights) {
            levelRootNode.removeLight(light);
        }
        this.getParent().detachChild(this);
    } 

    /**
     * Disables the physics of all objects in this chunk.
     */
    private void disablePhysics() {
        // traverse the scenegraph starting from the chunk node
        this.depthFirstTraversal(new SceneGraphVisitor() {
            public void visit(Spatial spatial) {
                // get the PhysicsControl if there is any
                PhysicsControl physicsControl = spatial.getControl(PhysicsControl.class);
                if (physicsControl != null) {
                    physicsControl.setEnabled(false);
                }
            }
        });
    }

    /**
     * Enables the physics of all objects in this chunk.
     */
    private void enablePhysics() {
        // traverse the scenegraph starting from the chunk node
        this.depthFirstTraversal(new SceneGraphVisitor() {
            public void visit(Spatial spatial) {
                // get the PhysicsControl if there is any
                PhysicsControl physicsControl = spatial.getControl(PhysicsControl.class);
                if (physicsControl != null) {
                    physicsControl.setEnabled(true);
                }
            }
        });
    }

}
