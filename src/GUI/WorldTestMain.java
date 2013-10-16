package GUI;

import java.util.logging.Level;

import javax.swing.JOptionPane;

import network.interfaces.WorldObserver;

import world.Entity;
import world.World;
import world.WorldType;
import world.entity.mob.Mob;
import GUI.Managers.ScreenManager;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;

/**
 * @author Scott
 * @author Garming
 * @author Craig Forret
 * @author Alex Campbell 300252131
 * @author Tony 300242775
 */
public class WorldTestMain extends SimpleApplication implements GameLoadContext, Startable{
	public static final boolean CHASE_CAMERA = true;

    public static AssetManager assets;
    private World world;
    private InputListener inputListener;
    private ChaseCamera chaseCam;
    private ScreenManager screenManager;
    private float lastX;
    private boolean gameStarted;

    public static void main(String[] args){
        WorldTestMain app = new WorldTestMain ();
        java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);  //this removes all that red warning text that jme poops out
        app.setShowSettings(true);
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        settings.setBitsPerPixel(32);
        app.setSettings(settings);
        app.start();

    }

    private Nifty nifty;

    @Override
    public void simpleInitApp() {
    	//Set up GUI Heads up display
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, viewPort);
        nifty = niftyDisplay.getNifty();
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        screenManager = new ScreenManager(nifty, inputManager,this);
        guiViewPort.addProcessor(niftyDisplay);	//add to gui port

        InputListener.addMappings(inputManager);

        //Set up world
        assets = assetManager;
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));   //just for testing purposes
        setWorld2(new World());
        fpsText.setText("");
        rootNode.setShadowMode(ShadowMode.CastAndReceive);
        setDisplayFps(false);
        setDisplayStatView(false);
        flyCam.setEnabled(false);
    }

    private void setWorld2(World w) {
    	if(world != null)
    		world.detachFromGame(stateManager);
    	world = w;
    	world.attachScreenManager(screenManager);
    }

    @Override
    public void setWorld(World w) {
    	setWorld2(w);
    	attachWorldToGame();
    }


    public void attachWorldToGame(){
    	try {
    		world.attachToGame(stateManager, assetManager, listener, cam, WorldType.SP, viewPort);
	    	rootNode.detachAllChildren();
	    	rootNode.attachChild(world.getNode());
	    	if(inputListener != null)
	    		inputListener.destroy();
	    	inputListener = new InputListener(this, world, world.getPlayer(), super.inputManager);
	    	screenManager.setPlayer(world.getPlayer());
	    	setUpCamera();
	    	world.getPlayer().setCamera(cam);

	    	world.addObserver(new WorldObserver() {
	    		@Override
	    		public void onGameWon(World w) {
	    			JOptionPane.showMessageDialog(null, "YOU WON!");
	    			System.exit(0);
	    		}
			});

	    	gameStarted = true;
    	} catch(Throwable E) {
    		E.printStackTrace();
    	}
    }

    public void setUpCamera(){
        flyCam.setEnabled(false);

        if(CHASE_CAMERA) {
	        if(chaseCam != null) {
	        	chaseCam.setSpatial(world.getPlayer().getMesh());
	        	world.getPlayer().getMesh().addControl(chaseCam);
	        	//chaseCam.setEnabled(false);
	        } else
	        	cam.setFrustumFar(10000f);
	        	chaseCam = new ChaseCamera(cam, world.getPlayer().getMesh(), inputManager);
	        chaseCam.setMinDistance(10f);
	        chaseCam.setChasingSensitivity(3f);
	        chaseCam.setRotationSensitivity(25f);
	        chaseCam.setRotationSpeed(4f);
	        chaseCam.setTrailingRotationInertia(0.1f);
	        chaseCam.setTrailingSensitivity(30f); //this is the one you really want to change to change the speed the camera trails behind at
	        chaseCam.setMaxDistance(65f);
	        chaseCam.setSmoothMotion(true);
	        chaseCam.setLookAtOffset(new Vector3f(0, 2.5f, 0));
	        chaseCam.setTrailingEnabled(true);

        } else {
	        CameraNode cn = new CameraNode("camera", cam);
	        cn.setLocalRotation(new Quaternion(new float[] {(float)Math.PI*0.25f, (float)Math.PI, 0}));
	        cn.setLocalTranslation(0, 80, 80);
	        ((Node)world.getPlayer().getMesh()).attachChild(cn);
        }

        world.setChaseCam(chaseCam);
    	world.getPlayer().setFirstPersonCam();
    }

    @Override
    public void simpleUpdate(float tpf) {
    	if (gameStarted){
	    	if (world.getPlayer().isFirstPerson() && !world.getPlayer().isInInventory()) {
	    		inputManager.setCursorVisible(false);
	    	} else {
	    		inputManager.setCursorVisible(true);
	    	}

	        world.gameTick (tpf);
	        screenManager.gameTick (tpf, cam, world);

	        if(chaseCam != null && !world.getPlayer().isFirstPerson()){
				CollisionResults results = new CollisionResults();
		        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
		        rootNode.collideWith(ray, results);
		        float dist = chaseCam.getDistanceToTarget();

		        if (results.size() > 4){
			        for(CollisionResult result : results)
			            if((((Entity)result.getGeometry().getUserData("entity")) instanceof Mob) && result.getDistance() < dist)
			                dist = result.getDistance();

			        dist = dist > 65 ? 65 : (dist < 10 ? 10 : dist);
		        } else dist = 55;

		        chaseCam.setDefaultDistance(dist);
			}

	        if (chaseCam.isEnabled() && world.getPlayer()!= null && world.getPlayer().getPhysics()!= null){
	        	if (Math.abs(world.getPlayer().getPhysics().getPhysicsLocation().x-lastX) < 0.01){
	        		chaseCam.setTrailingEnabled(false);
	        	} else{
	        		chaseCam.setTrailingEnabled(true);
	        	}
	        	lastX = world.getPlayer().getPhysics().getPhysicsLocation().x;
	        }

		}
    }
}