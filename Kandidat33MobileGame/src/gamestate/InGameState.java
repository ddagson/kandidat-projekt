package gamestate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import java.util.LinkedList;
import variables.P;

/**
 * This class handles all the in-game things
 *
 * @author forssenm
 */
public class InGameState extends AbstractAppState {

    private SimpleApplication app;
    private Node inGameRootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private BulletAppState physics;
    private LinkedList<Geometry> platforms;
    private Material platformMaterial;
    private Material playerMaterial;
    private Box playerModel;
    private Box playerDeathModel;
    private Node playerNode;
    private CharacterControl playerCharacter;

    /**
     * This method initializes the the InGameState
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.inGameRootNode = new Node();
        this.app.getRootNode().attachChild(this.inGameRootNode);
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.physics = new BulletAppState();
        this.stateManager.attach(physics);
        generateLevel();
        
        //Nedan kopierat från JME guide, verkar helt stänga ner grafiken på GS2
        /*FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        */
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.app.getRootNode().detachChild(this.inGameRootNode);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            //Initiate the things that are needed when the state is active
            System.out.println("InGameState is now active");
        } else {
            //Remove the things not needed when the state is inactive
            System.out.println("InGameState is now inactive");
        }
    }

    @Override
    public void update(float tpf) {
        if(playerNode.getLocalTranslation().y < -1) {
            playerDeath();
        }
    }
    
    private void playerDeath() {
        playerNode.getControl(CharacterControl.class).setEnabled(false);
        System.out.println("Game Over!");
        Spatial pG = playerNode.getChild("PlayerModel");

        playerNode.detachChild(pG);

                //detachChildNamed("PlayerModel");
        Geometry playerGeo = new Geometry("PlayerDeathModel", playerDeathModel);
        
        //playerGeo.setLocalTranslation(pG.getLocalTranslation());
        playerGeo.setMaterial(playerMaterial);
        playerNode.attachChild(playerGeo);
        

        setEnabled(false);
        //stateManager.detach(this);
    }

    private void generateLevel() {
        generateMaterials();
        generateModels();
        generatePlatforms();
        generatePlayer();
        initInputs();
        initCamera();

    }

    /**
     * Sets up the camera to follow the player.
     */
    private void initCamera() {

        /*
         * Set up a node a bit ahead of the player, to keep the the player
         * to the left of the screen.
         * Comment out this section and change camFocusNode to playerNode
         * to get player centered.
         */
        Node camFocusNode = new Node();
        camFocusNode.setLocalTranslation(playerNode.getLocalTranslation());
        playerNode.attachChild(camFocusNode);
        camFocusNode.move(15f, 0f, 0f);

        /*
         * Disable the default flyby camera. Hopefully this work even if some
         * other state has already disabled it.
         */
        this.app.getFlyByCamera().setEnabled(false);
        Camera cam = this.app.getCamera();

        ChaseCamera chaseCam;
        // Change camFocusNode to playerNode to center player
        chaseCam = new ChaseCamera(cam, camFocusNode, inputManager);

        // Set the style of camera chasing right
        chaseCam.setSmoothMotion(true);
        chaseCam.setTrailingEnabled(true);
        chaseCam.setDefaultDistance(50);
        // Set the camera to stay facing the side of the player
        chaseCam.setDefaultHorizontalRotation(-FastMath.DEG_TO_RAD * 265);
        chaseCam.setDefaultVerticalRotation(0);
    }

    private void generateMaterials() {
        platformMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        platformMaterial.setColor("Color", ColorRGBA.Blue);
        //playerMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
      //  playerMaterial = new Material (assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
      //  playerMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/brickwall/BrickWall.jpg"));
        //2013-02-14 Funkar bra att ladda BrickWall till kuben, men inte på spöket. 
        //Vad för textur kan vi lägga på spöket?
        //Kolla igen på UV-mapping och se om där finns något trevligt. 
        //Troligen är trianglarna för små.
        
       // playerMaterial.setColor("Color", ColorRGBA.Black);
      // playerMaterial.getAdditionalRenderState().setWireframe(true);
        //playerMaterial.setColor("Color", new ColorRGBA(0.1f,0.1f,0.1f,0.5f));
      //   playerMaterial.setColor("GlowColor", ColorRGBA.Green);
       // playerMaterial.setColor ("Color", ColorRGBA.White);
        //playerMaterial.
        //med ljus och lighting
        playerMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    // playerMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    }

    private void generateModels() {
        playerModel = new Box(Vector3f.ZERO, 2f, 2f, 3f);
        playerModel.scaleTextureCoordinates(new Vector2f(1f, .5f));
        playerDeathModel = new Box(Vector3f.ZERO,1f, 1f, 1f);
        playerDeathModel.scaleTextureCoordinates(new Vector2f(1f, .5f));
    }

    private void generatePlatforms() {
        platforms = new LinkedList<Geometry>();
        // Create the first platform
        Geometry firstPlatform = new Geometry("Platform", new Box(Vector3f.ZERO, P.platformLength / 2, P.platformHeight, P.platformWidth));
        firstPlatform.setMaterial(platformMaterial);
        firstPlatform.setLocalTranslation(0, 0 - 0.1f, 0);
        platforms.add(0, firstPlatform);
        inGameRootNode.attachChild(firstPlatform);
        // Make the first platform physical
        RigidBodyControl tempControl = new RigidBodyControl(0.0f);
        firstPlatform.addControl(tempControl);
        physics.getPhysicsSpace().add(tempControl);
        // Generate the rest of the platforms
        Geometry temp;
        for (int i = 0; i < P.platformsPerLevel; i++) {
            temp = new Geometry("Platform", new Box(Vector3f.ZERO, P.platformLength / 2, P.platformHeight, P.platformWidth));
            temp.setMaterial(platformMaterial);
            System.out.println(platforms.getFirst().getLocalTranslation().x);
            temp.setLocalTranslation(platforms.getFirst().getLocalTranslation().x + 1 * P.platformLength,
                    platforms.getFirst().getLocalTranslation().y,
                    platforms.getFirst().getLocalTranslation().z);
            platforms.addFirst(temp);
            inGameRootNode.attachChild(platforms.getFirst());

            // Make the platforms physical
            tempControl = new RigidBodyControl(0.0f);
            temp.addControl(tempControl);
            physics.getPhysicsSpace().add(tempControl);
        }
    }

    private void generatePlayer() {
//Här kan man köra loadmodel och asset rocket etc. och ta in en node istället troligen 2013-02-14 Emil

  //Node player_geo = (Node)assetManager.loadModel("Models/rocket/rocket.mesh.xml");
  //Node player_geo = (Node)assetManager.loadModel("Models/rocket/rocket.j3o");
  
        //laddar in fint med material och textur och allt
        //Node player_geo = (Node)assetManager.loadModel("Models/ghost3/ghost3.mesh.xml");
  
  
        Node player_geo = (Node)assetManager.loadModel("Models/ghost3/ghost3.j3o");
       // playerMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Models/ghost3/ghost3.png"));
        //playerMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Models/ghost3/ghost3.png"));
        playerMaterial = assetManager.loadMaterial("Models/ghost3/ghost3.j3m");
        player_geo.setMaterial(playerMaterial); //den blir helt svart annars
        //funkar att ladda in j3o till JME 
     player_geo.scale(3);
     player_geo.move(10, 5, 0);
     player_geo.rotate(0,3.14f,0);
        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection(new Vector3f (1,1.0f,-1.0f));
        
        DirectionalLight sun3 = new DirectionalLight();
        sun3.setDirection(new Vector3f (-3, -3, 3));
        
        Geometry playerGeo = new Geometry("PlayerModel", playerModel);
        playerGeo.setMaterial(playerMaterial);
        playerNode = new Node("PlayerNode");
        playerNode.attachChild(playerGeo);
        player_geo.setMaterial(playerMaterial); //den blir helt svart annars
       
  playerNode.attachChild(player_geo); //lagt till nu 
  playerNode.addLight(sun2);
  playerNode.addLight(sun3);
 //inGameRootNode.addLight(sun2);
        //playerNode.rotate(30f, 0.05f, 30f);
        //playerNode.
  /**
         * Create a CharacterControl object
         */
        CapsuleCollisionShape shape = new CapsuleCollisionShape(2f, 2f);
        playerCharacter = new CharacterControl(shape, 0.05f);
        playerCharacter.setJumpSpeed(P.jump_speed);

        /**
         * Position the player
         */
        Vector3f vt = new Vector3f(0, 2, 0);
        playerNode.setLocalTranslation(vt);

        inGameRootNode.attachChild(playerNode);



        playerNode.addControl(playerCharacter);
        physics.getPhysicsSpace().add(playerCharacter);
        Vector3f walkDirection = Vector3f.UNIT_X.multLocal(P.run_speed);

        playerCharacter.setWalkDirection(walkDirection);

    }

    /**
     * Sets up the input. Mouseclick jumps the character.
     */
    private void initInputs() {

        inputManager.addMapping("jump",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "jump");
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String binding, boolean value, float tpf) {
            if (binding.equals("jump")) {
                playerCharacter.jump();
            }
        }
    };
}
