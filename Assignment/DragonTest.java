/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.AnalogListener;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.Vector;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.system.AppSettings;
/**
 * @version 1.0.0
 * @author Zehua Wang
 * @Usage: To change the view of the camera hold left mouse and turn
 */
public class DragonTest extends SimpleApplication implements ActionListener {
    public static void main(String[] args){
    DragonTest app = new DragonTest();
    app.start();
    }
    //set the variable we need to use
    protected Spatial grog;
//    private ChaseCamera chaseCam;
    Boolean isRunning = true;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    //Temporary vectors used on each frame.
    //They here to avoid instanciating new vectors on each frame
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private boolean left = false, right = false, front = false, back = false;
    @Override
    public void simpleInitApp() {
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //disable the default First Person Camera    
    //flyCam.setEnabled(false);
    flyCam.setMoveSpeed(30);
    //import a material 
    Material mat_default = new Material(assetManager,"Common/MatDefs/Misc/ShowNormals.j3md");
    //import the grog model and set the scale and location
    grog = assetManager.loadModel("Models/grog5k/grog5k.j3o");
    rootNode.attachChild(grog);
    grog.setMaterial(mat_default);
    grog.setLocalTranslation(0f,8.9f,0f);
    grog.scale(0.25f,0.25f,0.25f);
    //create a new chase camera to the grog and bundle it to grog
    //chaseCam = new ChaseCamera(cam, grog, inputManager);
    //chaseCam.setSmoothMotion(true);
    //add the HighField Scene map
    Spatial Scene = assetManager.loadModel("Scenes/HeightMap.j3o");
    rootNode.attachChild(Scene);
    DirectionalLight sun = new DirectionalLight();
    sun.setDirection(new Vector3f(-0.0f,-0.0f,-0.0f));
    sun.setColor(ColorRGBA.White);
    rootNode.addLight(sun);
    //load my custom keybiding
    setUpKeys();
    //add the triceratops on the corner and set the material
    Spatial trice_1 = assetManager.loadModel("Models/triceratops/triceratops.j3o");
    Spatial trice_2 = assetManager.loadModel("Models/triceratops/triceratops.j3o");
    Spatial trice_3 = assetManager.loadModel("Models/triceratops/triceratops.j3o");
    Spatial trice_4 = assetManager.loadModel("Models/triceratops/triceratops.j3o");
    trice_1.setMaterial(mat_default);
    trice_2.setMaterial(mat_default);
    trice_3.setMaterial(mat_default);
    trice_4.setMaterial(mat_default);
    //put the triceratops at the corner of the wall
    trice_1.setLocalScale(0.25f,0.25f,0.25f);
    trice_1.move(60.0f,14.5f,40.0f);
    trice_2.setLocalScale(0.25f,0.25f,0.25f);
    trice_2.move(65.f,14.5f,-50.0f);
    trice_3.setLocalScale(0.25f,0.25f,0.25f);
    trice_3.move(-30.0f,14.5f,40.0f);
    trice_4.setLocalScale(0.25f,0.25f,0.25f);
    trice_4.move(-30.0f,14.5f,-60.0f);
    rootNode.attachChild(trice_1);
    rootNode.attachChild(trice_2);
    rootNode.attachChild(trice_3);
    rootNode.attachChild(trice_4);
    //display a line of text with the deafult font
    guiNode.detachAllChildren();
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText helloText = new BitmapText(guiFont, false);
    helloText.setSize(guiFont.getCharSet().getRenderedSize());
    helloText.setText("To hold and change the camera view hold the left mouse");
    helloText.setLocalTranslation(180, helloText.getLineHeight(), 0);
    guiNode.attachChild(helloText);
    //set the detect procedure
    CollisionShape sceneShape =
            CollisionShapeFactory.createMeshShape(Scene);
    landscape = new RigidBodyControl(sceneShape, 0);
    Scene.addControl(landscape);
    //add the capsule collision for grog
    CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
    player = new CharacterControl(capsuleShape, 0.05f);
    player.setFallSpeed(30);
    player.setGravity(30);
    player.setPhysicsLocation(new Vector3f(0, 10, 0));
    
    bulletAppState.getPhysicsSpace().add(landscape);
    bulletAppState.getPhysicsSpace().add(player);
    
    }
    
    
    
    
    //Define the key mapping for the grog
//    private void initKeys(){
//        inputManager.addMapping("Front", new KeyTrigger(KeyInput.KEY_W));//The grog go forward
//        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_S)); //The grog go back ward
//        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A)); //The grog go left
//        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));//The grog go right
//        inputManager.addListener(analogListener,"Front","Back","Left","Right");   
//    }
//    private AnalogListener analogListener = new AnalogListener() {
//    public void onAnalog(String name, float value, float tpf) {
//      if (isRunning) {
//        if (name.equals("Right")) {
//        grog.move(5*tpf,0,0);  
//        }
//        if (name.equals("Left")) {
//        grog.move(-5 * tpf, 0, 0);
//        }
//        if (name.equals("Front")) {
//        grog.move(0, 0, -5 * tpf);
//        }
//        if (name.equals("Back")) {
//        grog.move(0, 0, 5 * tpf);
//        }
//      } 
//    }
//  };
    private void setUpKeys() {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Front", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_S));
    //inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Front");
    inputManager.addListener(this, "Back");
    //inputManager.addListener(this, "Jump");
  }
    
   public void onAction(String binding, boolean isPressed, float tpf) {
    if (binding.equals("Left")) {
      left = isPressed;
    } else if (binding.equals("Right")) {
      right= isPressed;
    } else if (binding.equals("Front")) {
      front = isPressed;
    } else if (binding.equals("Back")) {
      back = isPressed;
    } 
  } 
  @Override
  public void simpleUpdate(float tpf){
        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        walkDirection.set(0, 0, 0);
         if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (front) {
            walkDirection.addLocal(camDir);
        }
        if (back) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        Vector3f v = player.getPhysicsLocation();
        Vector3f gv = grog.getLocalTranslation();
        gv.x = v.x;
        gv.y = v.y;
        gv.z = v.z;
        grog.setLocalTranslation(gv);
        Vector3f cv = cam.getLocation();
        cv.x = v.x;
        cv.y = v.y+80;
        cv.z = v.z+80;
        cam.setLocation(cv);  
  }
}
