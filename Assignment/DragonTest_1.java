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
 * @Data 2017-9-20
 */
public class DragonTest extends SimpleApplication implements ActionListener {
    public static void main(String[] args){
    DragonTest app = new DragonTest();
    app.start();
    }
    //set the variable we need to use in these class
    protected Spatial grog;
    Boolean isRunning = true;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    //Temporary vectors used on each frame.
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
    grog.setLocalTranslation(0f,10.0f,0f);
    grog.scale(0.25f,0.25f,0.25f);
    Spatial Scene = assetManager.loadModel("Scenes/HeightMap.j3o");
    rootNode.attachChild(Scene);
    //add sunlight to the scene to make the map visiable
    DirectionalLight sun = new DirectionalLight();
    sun.setDirection(new Vector3f(-0.0f,-0.0f,-0.0f));
    sun.setColor(ColorRGBA.White);
    rootNode.addLight(sun);
    //load my custom key input 
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
    helloText.setText("The First assignment by Zehua Wang");
    helloText.setLocalTranslation(180, helloText.getLineHeight(), 0);
    guiNode.attachChild(helloText);
    //set the detect procedure
    CollisionShape sceneShape =
            CollisionShapeFactory.createMeshShape(Scene);
    landscape = new RigidBodyControl(sceneShape, 0);
    Scene.addControl(landscape);
    //add the capsule collision for grog
    CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(2.5f, 6f, 1);
    player = new CharacterControl(capsuleShape, 0.05f);
    player.setFallSpeed(30);
    player.setGravity(200);
    player.setPhysicsLocation(new Vector3f(0, 10, 0));
    //add the phisical feature for the scene(high field) and grog
    bulletAppState.getPhysicsSpace().add(landscape);
    bulletAppState.getPhysicsSpace().add(player);
    }
    //do the mapping of key
    private void setUpKeys() {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Front", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Front");
    inputManager.addListener(this, "Back");
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
  //override the update of frame in the old class 
  @Override
  public void simpleUpdate(float tpf){
        //use variable camDir and variable camLeft to get the move direction
        camDir.set(cam.getDirection()).multLocal(0.4f);
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
        //bundle the capsule collision detect body to the grog model
        //give the camera a relative location with the grog to make the
        //camera move smoothly
        player.setWalkDirection(walkDirection);
        Vector3f grogCap = player.getPhysicsLocation();
        Vector3f grogSelf = grog.getLocalTranslation();
        grog.setLocalTranslation(grogCap);
        Vector3f cv = cam.getLocation();
        cv = setCamera(cv,grogCap,0,90,90);
        cam.setLocation(cv);
  }
        //write a static function setCamera to set the relative distance
        //between the camera and the grog
  public static Vector3f setCamera(Vector3f cameraVector,Vector3f rigidBodyLocation,int x_offset, int y_offset, int z_offset)
  {
      cameraVector.x = rigidBodyLocation.x + x_offset;
      cameraVector.y = rigidBodyLocation.y + y_offset;
      cameraVector.z = rigidBodyLocation.z + z_offset;
      return cameraVector;
  }
}
