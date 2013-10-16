package GUI.Managers;


import java.util.HashMap;
import java.util.Map;

import world.Entity;
import world.Player;
import world.World;
import world.entity.item.Item;
import GUI.HeadsUpScreen;
import GUI.LoadingScreen;
import GUI.StartScreen;
import GUI.Startable;
import GUI.VictoryScreen;
import GUI.ScreenControllers.HudScreenController;
import GUI.ScreenControllers.LoadingScreenController;
import GUI.ScreenControllers.StartScreenController;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

import de.lessvoid.nifty.Nifty;

/**
 * Sets up the Loading, Start and HUD screens and controllers
 * 
 * @author Craig Forret
 */
public class ScreenManager {

	private HudScreenController hudController;
	private LoadingScreenController loadController;
	private InventoryManager im;
	private PlayerStatManager psm;
	private InputManager inputManager;
	private VictoryScreen victoryScreen;
	
	@SuppressWarnings("unused")
	private Nifty nifty;
	
	public ScreenManager(Nifty nifty, InputManager inputManager, Startable mainApp){
		this.inputManager = inputManager;
		this.nifty = nifty;
		
		//create start controller 
		StartScreenController startController = new StartScreenController("start");
		StartScreen startScreen = new StartScreen(nifty,startController);
		startScreen.generateScreen();
		
		//create hud controller and screen
		hudController = new HudScreenController("hud",nifty,this);
		HeadsUpScreen hudScreen = new HeadsUpScreen(nifty,hudController);
		hudScreen.generateScreen();
		
		//create loading controller and screen
		loadController = new LoadingScreenController("loading", mainApp);
		LoadingScreen loadScreen = new LoadingScreen(nifty, loadController);
		loadScreen.generateScreen();

		victoryScreen = new VictoryScreen(nifty);
		victoryScreen.generateScreen();
		
		//switch to start screen
		nifty.gotoScreen("start"); 
		
		im = new InventoryManager(hudController, nifty, this);
		psm = new PlayerStatManager(nifty, this);
	}
	
	/**Sets the players for the InventoryManager and PlayerStatManager
	 * 
	 * @param player the player to be set
	 */
	public void setPlayer(Player player) {
		im.setPlayer(player);
		psm.setPlayer(player);
	}
	
	/**Returns the PlayerStatManager associated with this ScreenManager
	 * 
	 * @return the associated PlayerStatManager
	 */
	public PlayerStatManager getPlayerStatManager(){
		return this.psm;
	}
	
	/**Returns the InventoryManager associated with this ScreenManager
	 * 
	 * @return the associated InventoryManager
	 */
	public InventoryManager getInventoryManager(){
		return this.im;
	}
	
	/**Returns the HudScreenController**/
	public HudScreenController getHudScreenController(){
		return hudController;
	}
	
	private Map<Spatial, Light> highlightLights = new HashMap<Spatial, Light>();

	/**Highlights items on screen according to the mouse position
	 * 
	 * @param tpf - world tick
	 * @param cam - world camera
	 * @param world 
	 */
	public void gameTick(float tpf, Camera cam, World world) {
		CollisionResults results = new CollisionResults();
		Vector2f cursor = inputManager.getCursorPosition();
		Vector3f cursor3d = cam.getWorldCoordinates(cursor, 0);
		Vector3f dir = cam.getWorldCoordinates(cursor, 1).subtractLocal(cursor3d).normalizeLocal();
		world.getNode().collideWith(new Ray(cursor3d, dir), results);
		
		for(Map.Entry<Spatial, Light> e : highlightLights.entrySet()) {
			e.getKey().removeLight(e.getValue());
		}
		highlightLights.clear();
		
		for(CollisionResult cr : results) {
			Entity e = (Entity)cr.getGeometry().getUserData("entity");
			if(e instanceof Item && !highlightLights.containsKey(e.getMesh())) {
				AmbientLight light = new AmbientLight();
				light.setColor(ColorRGBA.White);
				e.getMesh().addLight(light);
				highlightLights.put(e.getMesh(), light);
			}
		}
	}
	
	public void goToVictoryScreen(){
		nifty.gotoScreen("victory");
	}
}


