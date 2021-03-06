/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package variables;

import com.jme3.math.Vector3f;

/**
 * This is where you put variables. This makes for easier finding and changing
 * values for a given variable without checking the entire code.
 *
 * @author Mathias
 */
public class P {

    //level variables
    public static final float platformLength = 12.0f;
    public static final float platformWidth = 4f;
    public static final float platformHeight = 1f;
    public static final float platformDistance = 3.0f;
    public static final float run_speed = 10f;
    public static final float jump_speed = 25f;
    
    public static final float chunkLength = 30.0f;
    
    //lighting variables
    public static final Vector3f windowLightDirection = new Vector3f(0f,-10f,20f);
    
    
    public static int screenWidth;
    public static int screenHeight;
    
}
