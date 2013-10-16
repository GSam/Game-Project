package world;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import world.entity.item.Item;
import world.entity.item.Stat;
import world.entity.item.consumable.ConsumableFactory;
import world.entity.item.consumable.ConsumableFactory.Instance;
import world.entity.item.equippable.EquipType;
import world.entity.item.equippable.Equippable;
import world.entity.item.equippable.SimpleGun;
import world.entity.item.gizmo.Push;
import world.entity.item.gizmo.StealthField;
import world.entity.item.torch.TorchFactory;
import world.entity.mob.Mob;
import GUI.ScreenControllers.HudScreenController;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.audio.Listener;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.input.ChaseCamera;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Player represents a user's character in the game world. It controls player movement,
 * animation, world interaction (eg. picking up items), and visual feedback elements
 * like weapon recoil.
 *
 * @author Tony 300242775
 * @author scott
 */
public class Player extends Actor implements AnimEventListener, Container {
	private static final double MAX_DIST_FROM_CONTAINER = 20;
	private static final float FIRE_EFFECT_TIME = 0.13f;
	private static final ColorRGBA MUZZLE_FLASH_COLOR = new ColorRGBA(1,1,0.7f,1).mult(7f);
	private static final float MUZZLE_FLASH_RADIUS = 4f;

	private static final float MOUSE_LOOK_SPEED = FastMath.PI * 2f;
	public static final float MAX_PICKUP_DISTANCE = 200f;

	private static final float GIMBAL_LOCK_UP = 0.9f;
	private static final float GIMBAL_LOCK_DOWN = -0.84f;

	private float kickUp;
	private float kickDown;
	private static final float RECOIL_RECOVER = 0.5f;
	private static final int RECOIL_RECOVER_STEPS = 8;

	// private Camera cam;
	// private Node playerNode;
	private Vector3f viewDirection = new Vector3f(0, 0, 1);
	private Vector3f lookDirection = new Vector3f(0, 0, 1);

	private float horizontalCameraRotation = 0f;
	private float verticalCameraRotation = -0.2f;

	private AnimChannel channel;
	private AnimControl control;

	private Inventory inventory = new Inventory(this, 16);
	private PlayerEquipment equipment = new PlayerEquipment();

	private boolean isInContainer;
	private Vector3f containerLoc;

	private boolean isInFirstPerson;
	private boolean isInInventory;
	private boolean isInEquip;

	private boolean isInvisible;

	private PointLight fireEffect;
	private boolean firing;
	private float timeSinceFire;

	private ChaseCamera chaseCam;
	private Camera camera;
	private CameraNode camNode;

	private Listener audioListener;
	private boolean chatWindowOpen;

	private Mob currentTarget;
	private int hitCounter;

	public Player() {
		inventory.add(new Push ());
		inventory.add(new StealthField ());
		inventory.add(new SimpleGun ());
		inventory.add(ConsumableFactory.getConsumableInstance(Instance.HEALTH_HIGH));
		inventory.add(TorchFactory.getTorchInstance(TorchFactory.Instance.INDUSTRIAL));
	}

	private Set<PlayerInventoryObserver> invObservers = new HashSet<PlayerInventoryObserver>();
	private MobObserver mobObserver;

	public void addInventoryObserver(PlayerInventoryObserver obs) {
		invObservers.add(obs);
		inventory.addObserver(obs);
	}

	public void removeInventoryObserver(PlayerInventoryObserver obs) {
		invObservers.remove(obs);
		inventory.removeObserver(obs);
	}
	public void addMobObserver(MobObserver obs){
		mobObserver = obs;
	}

	private Mob target;

	// GEOMETRY AND PHYSICS

	@Override
	public void getPreloadAssets(Set<String> assets) {
		assets.add("sniper/sniper.mesh.xml");
	}

	public Inventory getInventory () {
		return inventory;
	}
	
	@Override
	protected void makeMesh(AssetManager assetManager) {
		Spatial meshNode = assetManager.loadModel("sniper/sniper.mesh.xml");
		meshNode.setLocalScale(2.3f);

		((Geometry) ((Node) meshNode).getChild("Material #148")).getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Front);
		((Geometry) ((Node) meshNode).getChild("Material #147")).getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Front);

		geometry = new Node();
		((Node) geometry).attachChild(meshNode);
		meshNode.setLocalTranslation(0, -4.0f, 0);

		this.control = ((Node) geometry).getChild("sniper-ogremesh").getControl(AnimControl.class);

		control.addListener(this);
		channel = control.createChannel();
		channel.setAnim("walk");
		channel.setLoopMode(LoopMode.Loop);
		channel.setSpeed(1.4f);

	}

	@Override
	protected void makePhysics(PhysicsSpace physicsSpace) {
		super.makePhysics(physicsSpace);
	}

	// LOCATIONS

	@SuppressWarnings("deprecation")
	@Override
	public Vector3f getDirection() {
		// for some reason this needs to be negated for the player
		if (isInFirstPerson) {
			return camNode.getCamera().getDirection();
		} else {
			return physics.getViewDirection().negate();
		}
	}

	/*
	 * @Override public void setDirection(Vector3f direction) {
	 * super.setDirection(direction); viewDirection = direction; }
	 */

	// STATS

	@Override
	protected ActorStats makeStats(World world) {
		Stat keys[] = new Stat[] { Stat.HEALTH, Stat.MAXHEALTH, Stat.ENERGY, Stat.MAXENERGY, Stat.SPEED };
		float values[] = new float[] { 100, 100, 100, 100, 1.5f };
		ActorStats stats = new ActorStats(keys, values);
		return stats;
	}

	// MOVEMENT AND USER INPUT

	@SuppressWarnings("deprecation")
	@Override
	public void update(float tpf) {
		if(isRemovedFromWorld())
			return;
		Vector3f diffMove = new Vector3f(0, 0, 0);
		Spatial playerNode = geometry;
		Vector3f up = playerNode.getWorldRotation().mult(Vector3f.UNIT_Z).negate();
		Vector3f left = playerNode.getWorldRotation().mult(Vector3f.UNIT_X);

		// to ensure mouse control is always in the appropriate place
		if (isInInventory) {
			HudScreenController hsc = world.getScreenManager().getHudScreenController();

			if (!hsc.isInventoryOpen() && !hsc.isEquipOpen()) {
				toggleInventory();
			}
		}
		if (isInInventory) return;

		// player movement
		left.subtractLocal(0, left.y, 0); // make sure that they don't fly off into space
		up.subtractLocal(0, up.y, 0);

		if (isMovingDown()) {
			diffMove.addLocal(up.negate());
		}
		if (isMovingUp()) {
			diffMove.addLocal(up);
		}

		if (isInFirstPerson) {
			if (isMovingLeft()) {
				diffMove.addLocal(left.negate());
			}
			if (isMovingRight()) {
				diffMove.addLocal(left);
			}
		}

		physics.setWalkDirection(diffMove.mult(stats.getStat(Stat.SPEED)));

		if (diffMove != Vector3f.ZERO) setState(ActorState.MOVING);

		float turnSpeedMult = 0.5f;

		/*float turnOnMax = 0.01f;
		int screenWidth = world.getScreenManager().getHudScreenController().getNifty().getRenderEngine().getWidth();
		int screenHeight = world.getScreenManager().getHudScreenController().getNifty().getRenderEngine().getHeight();
		if (world.getInputManager().getCursorPosition().x <= 1){
			addHorizontalLook(turnOnMax);
		}
		else if (world.getInputManager().getCursorPosition().x >= screenWidth-1){
			addHorizontalLook(-turnOnMax);
		}
		if (world.getInputManager().getCursorPosition().y <= 1){
			addVerticalLook(-turnOnMax);
		}
		else if (world.getInputManager().getCursorPosition().y >= screenHeight-1){
			addVerticalLook(turnOnMax);
		}*/

		// camera rotation
		if (isInFirstPerson && !isInInventory) {
			Quaternion rotation = new Quaternion().fromAngleAxis(MOUSE_LOOK_SPEED * horizontalCameraRotation, Vector3f.UNIT_Y);
			rotation.multLocal(viewDirection);
			physics.setViewDirection(viewDirection);

			float y = camNode.getCamera().getDirection().getY();
			if (y > GIMBAL_LOCK_UP && verticalCameraRotation > 0) {
				verticalCameraRotation = 0;
			} else if (y < GIMBAL_LOCK_DOWN && verticalCameraRotation < 0) {
				verticalCameraRotation = 0;
			}

			rotation = new Quaternion().fromAngleAxis(MOUSE_LOOK_SPEED * verticalCameraRotation, Vector3f.UNIT_X);
			rotation.multLocal(camNode.getLocalRotation());
			camNode.setLocalRotation(rotation);

			horizontalCameraRotation = 0;
			verticalCameraRotation = 0;
		} else {
			if (isMovingLeft()) {
				Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * 2 * tpf * turnSpeedMult, Vector3f.UNIT_Y);
				rotateL.multLocal(viewDirection);
			}
			if (isMovingRight()) {
				Quaternion rotateR = new Quaternion().fromAngleAxis(-FastMath.PI * 2 * tpf * turnSpeedMult, Vector3f.UNIT_Y);
				rotateR.multLocal(viewDirection);
			}

			physics.setViewDirection(viewDirection);
		}

		// auto target
		if (target != null) {
			Vector3f dir = target.getLocation().subtract(getLocation());
			setDirection(dir);
			physics.setViewDirection(dir);
		}

		// chest access
		if (isInContainer && getLocation().distance(containerLoc) > MAX_DIST_FROM_CONTAINER) {
			isInContainer = false;
			isInInventory = false;
			containerLoc = null;

			horizontalCameraRotation = 0;
			verticalCameraRotation = 0;

			for (PlayerInventoryObserver obs : invObservers)
				obs.hideChest();
		}

		// muzzle flash effect
		if (firing) {
			timeSinceFire += tpf;

			verticalCameraRotation -= kickDown;

			if (timeSinceFire >= FIRE_EFFECT_TIME) {
				geometry.removeLight(fireEffect);
				fireEffect = null;
				firing = false;
				timeSinceFire = 0;
			}
		}

		// auto target
		if (currentTarget!=null && currentTarget.getStats().getStat(Stat.HEALTH) <= 0){
			currentTarget = null;
			target();
		}
	}

	/**
	 * Make the Player's first person camera look left by the passed amount.
	 * @param amount the amount to look left by
	 */
	public void addHorizontalLook(float amount) {
		horizontalCameraRotation += amount;
	}

	/**
	 * Make the Player's first person camera look up by the passed amount.
	 * @param amount the amount to look up by
	 */
	public void addVerticalLook(float amount) {
		verticalCameraRotation += amount;
	}

	/**
	 * Initiates the left-click function of the player, making them attack with
	 * their currently equipped weapon.
	 */
	public void primaryAction() {
		if (equipment.getWeapon() == null) return;

		if (fireEffect != null) geometry.removeLight(fireEffect);

		fireEffect = new PointLight();
		fireEffect.setColor(MUZZLE_FLASH_COLOR);
		fireEffect.setRadius(MUZZLE_FLASH_RADIUS);
		fireEffect.setPosition(getLocation().subtract(getDirection().mult(2f)));

		setState(ActorState.ATTACKING);

		// play the attack but do not do damage on clients
		if (world != null && world.getWorldType() != WorldType.CLIENT) {
			if (equipment.getWeapon().attack(this)) {
				verticalCameraRotation += 0.005;
				kickUp = equipment.getWeapon().getRecoil ();
				kickDown = (RECOIL_RECOVER * kickUp) / RECOIL_RECOVER_STEPS;

				geometry.addLight(fireEffect);
				firing = true;
				timeSinceFire = 0;
			}
		}
	}

	/**
	 * Initiates the right-click function of the player.
	 */
	public void secondaryAction() {
		System.out.println("Your coordinates: "+getLocation());
		if(world != null)
			world.playerSecondaryAction(getLocation(), getDirection());
		// world.spawnMob();
	}

	/**
	 * Initiates a player pick action, in which the player will add an item to
	 * their inventory if they are looking at it and it is close enough to them.
	 */
	public void pick() {
		if (isInFirstPerson) {
			world.playerPick(camNode.getWorldTranslation(), camNode.getCamera().getDirection());
		} else {
			getWorld().playerPick(getLocation(), getDirection());
		}
	}

	/**Sets the chase cam to the minimum distance and heightens
	 * the sensitivity of the cam. Called by input listener.
	 */
	public void setFirstPersonCam(){
		if (!getWorld().getScreenManager().getHudScreenController().chatFocused()){
			isInFirstPerson = !isInFirstPerson;
			if(isInFirstPerson){
				chaseCam.setEnabled(false);
				camNode = new CameraNode("cam",camera);
				((Node)getMesh()).attachChild(camNode);
				camNode.setLocalTranslation(new Vector3f(0,4,0));
				camNode.setLocalRotation(new Quaternion(0,1,0,0));
			}
			else{
				((Node)getMesh()).detachChild(camNode);
				chaseCam.setEnabled(true);
				chaseCam.setTrailingEnabled(true);
			}
			world.getScreenManager().getHudScreenController().hideShowCrossHairs(isInFirstPerson);
		}
	}

	/**
	 * @return the player's camera in first-person mode
	 */
	public CameraNode getFirstPersonCam () {
		return camNode;
	}

	/**
	 * Initiates the player target-select action, which finds a suitable Mob and
	 * locks on to it.
	 */
	public void target() {
		currentTarget = world.getTarget(this);
		mobObserver.update(currentTarget);
	}

	/**
	 * Initiates the player opening or closing their inventory in first person mode. If
	 * the player is not in first person mode, throws an IllegalStateException.
	 */
	public void toggleInventory () {
		if (!getWorld().getScreenManager().getHudScreenController().chatFocused()){
			getWorld().getScreenManager().getHudScreenController().hideShowInventory("", null);
			isInInventory = !isInInventory;

			if (!isInInventory) {
				HudScreenController hsc = world.getScreenManager().getHudScreenController();

				if (hsc.isEquipOpen()) {
					hsc.hideShowEquipScreen("", null);
				}

				if (hsc.isInventoryOpen()) {
					hsc.hideShowInventory("", null);
				}
			}

			// these stop the camera flipping out when the mouse gets locked again.
			horizontalCameraRotation = 0;
			verticalCameraRotation = 0;
		}
	}

	/**
	 * Toggle with the player equipment screen is open or closed.
	 */
	public void toggleEquip() {
		if (!getWorld().getScreenManager().getHudScreenController().chatFocused()){
			getWorld().getScreenManager().getHudScreenController().hideShowEquipScreen("", null);
			isInEquip = !isInEquip;
		}
	}

	/**
	 * Indicate that the player's focus is now on the chat, and movement should be
	 * disabled.
	 */
	public void showChat(){
		if (world != null )
			world.getScreenManager().getHudScreenController().showChat();

	}


	// INVENTORIES AND EQUIPMENT

	/**
	 * Attempt to equip the provided Item and returns true if the item was
	 * equipped.
	 *
	 * @param item
	 *            the Item to equip
	 * @return true if the item was successfully added, false otherwise
	 */
	public boolean equip(Item item) {
		if (!(item instanceof Equippable)) return false;

		EquipType type = ((Equippable)item).getEquipType();
		// return value isn't used.
		equipment.equip(item, type);
		stats.equip(item);
		if (world != null && this == world.getPlayer()) world.observeEquipItem(item, true);

		return true;
	}

	/**
	 * Unequips the provided item.
	 *
	 * @param item
	 *            the Item to unequip
	 */
	public void unequip(Item item) {
		equipment.unequip(item);
		stats.unequip(item);
		if (world != null && this == world.getPlayer()) world.observeEquipItem(item, false);
	}

	/**
	 * @return the Inventory of this Player
	 */
	@Override
	public Inventory getContainerInventory() {
		return inventory;
	}

	/**
	 * Add the provided item to this player's inventory.
	 *
	 * @param item
	 *            the Item to add
	 */
	public boolean addItem(Item item) {
		return inventory.add(item);
	}

	// WORLD INTEGRATION

	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		equipment.linkToWorld(world);
		world.getPhysicsSpace().addTickListener(inventory);
		super.linkToWorld(world, location, id);
	}

	@Override
	protected Node getNodeToAttach() {
		return world.getNode();
	}

	/**
	 * Notify the Player if they are looking in a container.
	 * @param isInContainer if the Player is looking in a container or not
	 * @param containerLoc the location of the container
	 */
	public void setInContainer(boolean isInContainer, Vector3f containerLoc) {
		this.isInContainer = isInContainer;
		this.containerLoc = containerLoc;
	}

	/**
	 * @return true if this Player is looking in a container, false otherwise
	 */
	public boolean isInContainer() {
		return isInContainer;
	}

	// SAVING AND LOADING

	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		InputCapsule ic = arg0.getCapsule(this);
		viewDirection = (Vector3f) ic.readSavable("viewDir", null);
		lookDirection = (Vector3f) ic.readSavable("lookDir", null);
		inventory = (Inventory) ic.readSavable("inventory", null);
		equipment = (PlayerEquipment) ic.readSavable("equipment", null);

		for (PlayerInventoryObserver obs : invObservers)
			inventory.addObserver(obs);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);

		OutputCapsule oc = arg0.getCapsule(this);
		oc.write(lookDirection, "lookDir", null);
		oc.write(viewDirection, "viewDir", null);
		oc.write(inventory, "inventory", null);
		oc.write(equipment, "equipment", null);
	}

	// ANIMATION

	/*
	 * @Override public void setState(ActorState a) { if (a == null) return;
	 *
	 * switch (a) { case ATTACKING:
	 *
	 * case MOVING:
	 *
	 * case STANDING:
	 *
	 * default:
	 *
	 * } }
	 */

	@Override
	protected void onStateChange() {
	}

	@Override
	public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {

	}

	@Override
	public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {

	}

	@Override
	public boolean canAccess() {
		return false;
	}

	@Override
	public void setCanAccess(boolean access) {
	}

	public void displayChest(Inventory inventory2) {
		isInInventory = true;
		for (PlayerInventoryObserver o : invObservers)
			o.displayChest(inventory2);
	}

	/**
	 * Bring the Player's camera in and out of first person.
	 */
	public void toggleFirstPerson() {
		isInFirstPerson = !isInFirstPerson;
	}

	/**
	 * Set this Player's camera to the passed Camera
	 * @param cam the Camera to set
	 */
	public void setCamera (Camera cam) {
		this.camera = cam;
		if(camNode != null)
			camNode.setCamera(cam);
	}

	/**
	 * Sets this Player's AudioListener to the passed value
	 * @param audioListener the listener to set
	 */
	public void setAudioListener(Listener audioListener){
		this.audioListener = audioListener;
	}

	/**
	 * Set this Player's ChaseCamera to the passed value.
	 * @param chase the ChaseCamera to set
	 */
	public void setChaseCam(ChaseCamera chase) {
		this.chaseCam = chase;
	}

	/**
	 * @return true if this Player is in first person mode, false otherwise
	 */
	public boolean isFirstPerson() {
		return isInFirstPerson;
	}

	/**
	 * @return true if this Player is looking in their inventory, false otherwise
	 */
	public boolean isInInventory () {
		return isInInventory;
	}

	/**
	 * @return the Mob this Player is currently targetting
	 */
	public Mob getCurrentTarget(){
		return this.currentTarget;
	}

	/**
	 * Increment the number of Mobs this Player has hit.
	 */
	public void incrementHits(){
		hitCounter++;
	}

	/**
	 * @return the number of Mobs this Player has hit.
	 */
	public int hitCount(){
		return hitCounter;
	}

	@Override
	public boolean isInvisible() {
		return isInvisible;
	}

	/**
	 * Set whether this Player should not be targeted by mobs.
	 * @param invisible whether this Player should not be targeted by mobs.
	 */
	public void setInvisible (boolean invisible) {
		this.isInvisible = invisible;
	}
}
