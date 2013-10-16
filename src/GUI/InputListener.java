package GUI;

import java.io.File;

import network.NetworkClient;
import savefile.SaveUtils;
import world.Player;
import world.World;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 * @author Tony
 * @author Alex Campbell 300252131
 * @author Craig Forret
 * @author Garming Sam 300198721
 */
public class InputListener implements ActionListener, AnalogListener{

	private InputManager inputManager;
	private Player player;
	private World world;
	private GameLoadContext loadContext;
	private NetworkClient network;

	private boolean pickPressed;
	private boolean rmbPressed;
	private boolean tabPressed;
	private boolean lmbPressed;

	/**Sets mapping for given keys
	 *
	 * @param inputManager - input manager to set key bindings for
	 */
	public static void addMappings(InputManager inputManager) {
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping("WarpJump", new KeyTrigger(KeyInput.KEY_LCONTROL));
		inputManager.addMapping("Strafe Right", new KeyTrigger(KeyInput.KEY_E));
		inputManager.addMapping("Strafe Left", new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addMapping("Pick", new KeyTrigger(KeyInput.KEY_F));
		inputManager.addMapping("Target", new KeyTrigger(KeyInput.KEY_TAB));
		inputManager.addMapping("Save", new KeyTrigger(KeyInput.KEY_4));
		inputManager.addMapping("Load", new KeyTrigger(KeyInput.KEY_5));
		inputManager.addMapping("FirstPerson", new KeyTrigger(KeyInput.KEY_P));
		inputManager.addMapping("Chat", new KeyTrigger(KeyInput.KEY_K));

		inputManager.addMapping("LMB", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping("RMB", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping("MMB", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
		inputManager.addMapping("Scroll", new MouseButtonTrigger(MouseInput.AXIS_WHEEL));

		inputManager.addMapping("Inventory", new KeyTrigger(KeyInput.KEY_I));
		inputManager.addMapping("Equip", new KeyTrigger(KeyInput.KEY_U));

		inputManager.addMapping("MouselookDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping("MouselookUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addMapping("TurnLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping("TurnRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));


	}

	public InputListener(GameLoadContext loadContext, World world, Player p, InputManager im){
		this.inputManager = im;
		this.player = p;
		this.world = world;
		this.loadContext = loadContext;

		inputManager.setCursorVisible(false);

		inputManager.addListener(this, "Left");
		inputManager.addListener(this, "Right");
		inputManager.addListener(this, "Up");
		inputManager.addListener(this, "Down");
		inputManager.addListener(this, "Jump");
		inputManager.addListener(this, "WarpJump");
		inputManager.addListener(this, "Strafe Right");
		inputManager.addListener(this, "Strafe Left");
		inputManager.addListener(this, "Pick");
		inputManager.addListener(this, "Target");
		inputManager.addListener(this, "Save");
		inputManager.addListener(this, "Load");
		inputManager.addListener(this, "FirstPerson");
		inputManager.addListener(this, "Inventory");
		inputManager.addListener(this, "Equip");
		inputManager.addListener(this, "Chat");
		inputManager.addListener(this, "LMB");
		inputManager.addListener(this, "RMB");
		inputManager.addListener(this, "MMB");
		inputManager.addListener(this, "Scroll");

		inputManager.addListener(this, "MouselookDown");
		inputManager.addListener(this, "MouselookUp");
		inputManager.addListener(this, "TurnLeft");
		inputManager.addListener(this, "TurnRight");
	}

	public void destroy() {
		inputManager.removeListener(this);
	}

	public InputListener(GameLoadContext loadContext, World world, Player p, InputManager im, NetworkClient c){
		this(loadContext, world, p, im);
		network = c;
	}

	static boolean loadPressed = false;

	@Override
	public void onAction(String binding, boolean isPressed, float tpf){
		if (player.isRemovedFromWorld())
			return; // on player dead

		if(binding.equals("Left")){
			player.setMovingLeft(isPressed);
		} else if(binding.equals("Right")){
			player.setMovingRight(isPressed);
		} else if(binding.equals("Up")){
			player.setMovingUp(isPressed);
		} else if(binding.equals("Down")){
			player.setMovingDown(isPressed);
		} else if(binding.equals("Jump") && isPressed){
			player.getPhysics().jump();
		} else if(binding.equals("WarpJump") && isPressed){
			player.changeLocation(0, 500, 0);
		} else if(binding.equals("Strafe Right")){
			player.setStrafeRight(isPressed);
		} else if(binding.equals("Strafe Left")){
			player.setStrafeLeft(isPressed);
		} else if(binding.equals("FirstPerson") && !isPressed){
			player.setFirstPersonCam();
		} else if(binding.equals("Inventory") && !isPressed){
			player.toggleInventory();
		} else if(binding.equals("Equip") && !isPressed){
			player.toggleEquip();
		} else if(binding.equals("Chat") && !isPressed){
			player.showChat();
		} else if (binding.equals("LMB")) {
			if(!lmbPressed){
				if(network != null) network.sendAttack();
				player.primaryAction ();
			}
			lmbPressed = !lmbPressed;
		} else if (binding.equals("RMB")) {
			if(!rmbPressed){
				player.secondaryAction ();
			}
			rmbPressed = !rmbPressed;
		} else if (binding.equals("Pick")) {
			if (!pickPressed) {
				player.pick ();
			}
			pickPressed = !pickPressed;
		} else if (binding.equals("Target")) {
			if (!tabPressed) {
				player.target();
			}
			tabPressed = !tabPressed;
		} else if (binding.equals("Save") && isPressed) {
			if (network != null)
				network.sendSave();
			else
				SaveUtils.save(world, new File("test.sav"));
		} else if (binding.equals("Load") && network == null) {
			if(!loadPressed && isPressed) {
				loadContext.setWorld(SaveUtils.load(world.getAssetManager(), new File("test.sav"), World.class));
			}
			loadPressed = isPressed;
		}
	}

	@Override
	public void onAnalog(String binding, float value, float tpf) {
		if (binding.equals("TurnLeft")) {
			player.addHorizontalLook(value);
		} else if (binding.equals("TurnRight")) {
			player.addHorizontalLook(-value);
		} else if (binding.equals("MouselookDown")) {
			player.addVerticalLook(-value);
		} else if (binding.equals("MouselookUp")) {
			player.addVerticalLook(value);
		}
	}
}
