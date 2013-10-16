package network;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import network.interfaces.GUIObserver;
import network.packets.InventoryTransferMessage;
import network.packets.Packets;
import network.packets.PlayerSetupMessage;
import world.Inventory;
import world.World;
import world.WorldType;
import world.entity.item.Item;
import GUI.InputListener;
import GUI.Startable;
import GUI.Managers.ScreenManager;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ErrorListener;
import com.jme3.network.Network;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;

/**
 * Main client application with the game loop running. Tells the network client
 * to broadcast the player position every 0.1 seconds.
 *
 * Observes the GUI and acts as the in-between for the GUI and network client.
 *
 * @author Garming Sam 300198721
 */
public class ClientMain extends SimpleApplication implements GUIObserver, ClientStateListener, ErrorListener<Client>, Startable{
	private Client client;
	private ChaseCamera chaseCam;
	private World world;
	private NetworkClient networkClient;
	private Nifty nifty;
	private ScreenManager sm;
	private float lastX;

	private long last = System.currentTimeMillis();
	private boolean initializedInput;
	private String name; // not your actual name, but the one you asked for in
							// the beginning

	/**
	 * Construct a client main application with default values.
	 *
	 * @throws IOException
	 */
	public ClientMain() throws IOException {
		this("localhost", 6143, "");
	}

	/**
	 * Construct a client main application with the given arguments.
	 *
	 * @param address
	 *            address to use
	 * @param port
	 *            port
	 * @throws IOException
	 */
	public ClientMain(String address, int port, String name) throws IOException {
		Packets.register();
		client = Network.connectToServer(address, port);
		this.name = name;
		setDisplayFps(false);
        setDisplayStatView(false);
	}

	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
		ClientMain test;
		try {
			if (args != null && args.length == 3) {
				try {
					int port = Integer.parseInt(args[1]);
					test = new ClientMain(args[0], port, args[2]);
				} catch (NumberFormatException e) {
					test = new ClientMain();
				}
			} else {
				test = new ClientMain();
			}
			test.start();
		} catch (IOException e) {
			System.out.println("Server unavailable.");
		}
	}

	@Override
	public void simpleInitApp() {
		client.addClientStateListener(this);
		client.addErrorListener(this);
		assetManager.registerLocator("assets", FileLocator.class);
		// Set up GUI Heads up display
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		/** Create a new NiftyGUI object */
		nifty = niftyDisplay.getNifty();
		nifty.loadStyleFile("nifty-default-styles.xml");
		nifty.loadControlFile("nifty-default-controls.xml");

		guiViewPort.addProcessor(niftyDisplay); // add to gui port
		sm = new ScreenManager(nifty, inputManager,this);
		sm.getHudScreenController().addObserver(this);
		world = new World();
		world.attachScreenManager(sm);

		flyCam.setEnabled(false);
		setPauseOnLostFocus(false);
	}

	@Override
	public void attachWorldToGame() {
		world.attachToGame(stateManager, assetManager, listener, cam, WorldType.CLIENT, viewPort);
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

		rootNode.attachChild(world.getNode());
		fpsText.setText("");
		networkClient = new NetworkClient(client, world, this);
		client.addMessageListener(networkClient);
		client.start();

		InputListener.addMappings(inputManager);

	}

	/**
	 * Setup the camera.
	 */
	public void setUpCamera() {
		cam.setFrustumFar(10000f);
		flyCam.setEnabled(false);
		// for the camera node way

		/*
		 * CameraNode camNode = new CameraNode("CamNode", cam);
		 * camNode.setControlDir(ControlDirection.SpatialToCamera);
		 * camNode.setLocalTranslation(new Vector3f(0, 3, -20)); Quaternion quat
		 * = new Quaternion(); // These coordinates are local, the camNode is
		 * attached to the character node! quat.lookAt(Vector3f.UNIT_Z,
		 * Vector3f.UNIT_Y); camNode.setLocalRotation(quat);
		 * world.getPlayer().getPlayerNode().attachChild(camNode);
		 * camNode.setEnabled(true);
		 */

		chaseCam = new ChaseCamera(cam, world.getPlayer().getMesh(), inputManager);
		world.getPlayer().setChaseCam(chaseCam);

		chaseCam.setDefaultHorizontalRotation(FastMath.DEG_TO_RAD * -90);
		flyCam.setEnabled(false);

		chaseCam.setDefaultHorizontalRotation(FastMath.DEG_TO_RAD * -90);
		chaseCam.setMinDistance(5f);
		// chaseCam.setLookAtOffset(Vector3f.UNIT_Y.mult(3f));
		// chaseCam.setInvertVerticalAxis(true);
		// chaseCam.setRotationSpeed(5f);
		chaseCam.setChasingSensitivity(5f);
		chaseCam.setRotationSensitivity(15f);
		chaseCam.setTrailingRotationInertia(0.5f);
		chaseCam.setTrailingSensitivity(25f); // this is the one you really want
												// to change to change the speed
												// the camera trails behind at
		chaseCam.setMaxDistance(45f);
		chaseCam.setSmoothMotion(true);
		chaseCam.setLookAtOffset(new Vector3f(0, 2.5f, 0));
		chaseCam.setTrailingEnabled(true);
		chaseCam.setMinVerticalRotation(0.05f);
		chaseCam.setMaxVerticalRotation(0.6f);
		// chaseCam.setZoomSensitivity(200);

		world.getPlayer().setFirstPersonCam();
	}

	@Override
	public void simpleUpdate(float tpf) {

		if (world.getPlayer() != null) {
			if (world.getPlayer().isFirstPerson() && !world.getPlayer().isInInventory()) {
				inputManager.setCursorVisible(false);
			} else {
				inputManager.setCursorVisible(true);
			}
		}

		// initialize the input if necessary
		if (!initializedInput && world.getPlayer() != null) {
			new InputListener(null, world, world.getPlayer(), super.inputManager, networkClient);
			setUpCamera();
			initializedInput = true;
		}

		// tick the game
		if (world.getPlayer() != null) {
			world.gameTick(tpf);
			long newTime = System.currentTimeMillis();
			if (newTime - last > 100) {
				networkClient.sendCurrentLocation();
				last = newTime;
			}
		}

		// update the camera
		if (chaseCam != null) {
			CollisionResults results = new CollisionResults();

			Ray ray = new Ray(cam.getLocation(), cam.getDirection());
			rootNode.collideWith(ray, results);
			float dist = chaseCam.getDistanceToTarget();
			// float minDist = 1000;

			if (world.getPlayer() != null && world.getPlayer().getPhysics() != null) {
				if (Math.abs(world.getPlayer().getPhysics().getPhysicsLocation().x - lastX) < 0.1) {
					chaseCam.setTrailingEnabled(false);
				} else {
					chaseCam.setTrailingEnabled(true);
				}
				lastX = world.getPlayer().getPhysics().getPhysicsLocation().x;

			}
			if (results.size() > 4) {
				for (CollisionResult result : results) {
					if (result.getDistance() < dist && result.getGeometry().getTriangleCount() > 500) {
						dist = result.getDistance();
					}
				}
				if (dist > 25) {
					dist = 15;
				}
				if (dist < 5) {
					dist = 5;
				}

			} else {
				dist = 15;
			}
			chaseCam.setDefaultDistance(dist);
		}

	}

	@Override
	public void destroy() {
		super.destroy();
		client.close();
	}

	/** adding players and avatars **/
	// Element chatPanel =
	// nifty.getCurrentScreen().findElementByName("chatbox");
	// Chat chat = chatPanel.findNiftyControl("chatbox", Chat.class);
	// chat.addPlayer(playerName, avatarImage);

	/** Removing players **/
	// chatPanel = nifty.getCurrentScreen().findElementByName("chatbox");
	// Chat chatController = chatPanel.findNiftyControl("chatbox", Chat.class);
	// chatController.removePlayer(playerName);

	/** Sending message to room **/
	// chatPanel = nifty.getCurrentScreen().findElementByName("chatbox");
	// Chat chatController = chatPanel.findNiftyControl("chatbox", Chat.class);
	// chatController.receivedChatLine(playerName +">" + chatLine, avatarImage);

	// GUI observer methods

	@Override
	public void onDropItem(Item i) {
		networkClient.dropItem(i);

	}

	@Override
	public void onChatMessage(String text) {
		networkClient.sendChatMessage(text);
	}

	@Override
	public void onItemTransfer(Inventory inventory, Inventory chest, Item toMove) {
		client.send(new InventoryTransferMessage(inventory.getOwner().getEntityID(), chest.getOwner().getEntityID(), toMove.getEntityID()));
	}

	@Override
	public void onChestAccess(Inventory chest, boolean open) {
		networkClient.sendChestAcccess(chest, open);
	}

	@Override
	public void onRightClicked(Item i) {
		networkClient.sendRightClick(i);

	}

	@Override
	public void onStartGame() {
		// display name on joining
		enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if (networkClient.getName() != null && !networkClient.getName().equals("")) {
					displayOnStartMessage("Current name: " + networkClient.getName());
				} else {
					displayOnStartMessage("Please set your name using /name");
				}
				return null;
			}
		});
	}

	// GUI interactions

	/**
	 * Displays a chat message on the GUI.
	 *
	 * @param source
	 *            name of person from
	 * @param text
	 *            text sent
	 */
	public void showMessage(String source, String text) {
		world.getScreenManager().getHudScreenController().addChatLine(source,text);
	//	chatController.receivedChatLine(source + ">" + text, null);
	}

	/**
	 * Show or hide a chest in the GUI.
	 *
	 * @param show
	 *            to show
	 */
	public void showChest(boolean show) {
		if (show) {
			sm.getHudScreenController().actualShowWorldChest();
		} else {
			sm.getHudScreenController().hideWorldChest();
		}
	}

	/**
	 * Display a HUD message on the GUI.
	 *
	 * @param message
	 *            text
	 */
	public void displayHUDMessage(String message) {
		sm.getHudScreenController().displayMessage(message);
	}

	/**
	 * Display a HUD message on the GUI.
	 *
	 * @param message
	 *            text
	 */
	public void displayOnStartMessage(String message) {
		sm.getHudScreenController().displayStartMessage(message);
	}

	// Client state listener methods

	@Override
	public void clientConnected(Client c) {
		// send a message for the players name
		c.send(new PlayerSetupMessage(c.getId(), name));
	}

	@Override
	public void clientDisconnected(Client arg0, DisconnectInfo arg1) {
		networkClient.setReady(false);
	}

	// Error state listener

	@Override
	public void handleError(Client arg0, Throwable arg1) {
		networkClient.setReady(false);
		System.out.println("Server appears to have been disconnected.");
		displayHUDMessage("SERVER DISCONNECTED");
	}



}
