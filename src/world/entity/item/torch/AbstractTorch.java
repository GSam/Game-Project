package world.entity.item.torch;

import java.io.IOException;

import world.Activatable;
import world.Player;
import world.StatModification;
import world.World;
import world.WorldType;
import world.effects.SlowInRadius;
import world.entity.item.Item;
import world.entity.item.ItemInfo;
import world.entity.item.RightClickable;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * AbstractTorch represents a torch in the game world, that slows mobs near it and lights
 * the surrounding area.
 *
 * @author Tony 300242775
 */
public abstract class AbstractTorch extends Item implements Activatable, RightClickable {
	private static final Vector3f LIGHT_OFFSET = new Vector3f(0,10,0);

	/**
	 * Whether this torch is currently emitting light or not
	 */
	protected boolean isActive;
	private boolean isPlaced;

	/**
	 * The path to the mesh of this Torch
	 */
	protected String meshPath;

	private float slowRadius;
	private float slowPower;
	private SlowInRadius slow;

	private float lightRadius;
	private ColorRGBA lightColor;
	private PointLight light;

	/**
	 * @param col the colour of this torch
	 * @param radius the radius of the light of this torch
	 * @param power the strength of the slow effect of this torch
	 * @param range the range of the slow effect of this torch
	 * @param infoStats an array of information Stats to describe this torch
	 * @param infoValues the associated information with infoStats
	 * @param meshPath a path to the mesh for this torch to use, or null
	 */
	public AbstractTorch (ColorRGBA col, float radius, float power, float range, Stat[] infoKeys, String[] infoValues, String meshPath) {
		this.slowRadius = radius;
		this.slowPower = power;
		this.lightColor = col;
		this.lightRadius = range;
		this.meshPath = meshPath;
		this.info = new ItemInfo(infoKeys, infoValues);
	}

	@Override
	public void rightClick() {
		isPlaced = true;
		isActive = false; // gets switched to true

		destroyPhysics(world.getPhysicsSpace());
		makePhysics (world.getPhysicsSpace ());

		Vector3f loc = getInventory().getOwner().getLocation();
		setLocation(loc);
		attachToNode();

		if (!(inventory.getOwner() instanceof Player)) throw new IllegalStateException ("mob right clicked!");
		Player player = (Player) inventory.getOwner();

		inventory.removeItem(this);
		inventory = null;

		activate (player);
	}

	@Override
	public float getWeight () {
		return isPlaced ? 0 : DEFAULT_ITEM_WEIGHT;
	}

	@Override
	public void activate(Player player) {
		if (!isPlaced) return;
		isActive = !isActive;

		if (isActive) {
			light = new PointLight();
			light.setColor(lightColor);
			light.setRadius(lightRadius);
			light.setPosition(getLocation().add(LIGHT_OFFSET));

			world.getNode().addLight(light);

			slow = new SlowInRadius(getLocation(), slowRadius, slowPower);

			// if you are not the client, create the slow effect
			if(world.getWorldType() != WorldType.CLIENT)
				world.makeLocalEffect(slow);

		} else {
			onDeactivate();
		}
	}

	/**
	 * An optional method that will be called when this Activatable is activated.
	 */
	protected void onActivate () {}

	/**
	 * An optional method that will be called when this Activatable is deactivated.
	 */
	protected void onDeactivate () {
		if (world.getWorldType() != WorldType.CLIENT)
			world.destroyEffect(slow);
		world.getNode().removeLight(light);
	}

	@Override
	protected abstract void makeMesh(AssetManager assetManager);

	@Override
	public void onPick () {
		isPlaced = false;
		if (isActive) onDeactivate ();
		removeFromNode ();
	}

	@Override
	protected abstract String getImage();

	@Override
	protected StatModification makeItemStats() {
		Stat keys[] = new Stat[] {};
		float values[] = new float[] {};
		return new StatModification(keys, values);
	}

	@Override
	protected ItemInfo makeItemInfo() {
		return info;
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		arg0.getCapsule(this).write(isActive, "active", false);
		arg0.getCapsule(this).write(isPlaced, "placed", false);
		arg0.getCapsule(this).write(meshPath, "meshPath", null);

		arg0.getCapsule(this).write(slowRadius, "radius", 0f);
		arg0.getCapsule(this).write(slowPower, "power", 0f);
		arg0.getCapsule(this).write(lightColor, "color", null);

		arg0.getCapsule(this).write(info, "info", null);

	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		InputCapsule ic = arg0.getCapsule(this);
		isActive = ic.readBoolean("active", false);
		isPlaced = ic.readBoolean("placed", false);
		meshPath = ic.readString("meshPath", null);
		slowRadius = ic.readFloat("radius", 0f);
		slowPower = ic.readFloat("power", 0f);
		lightColor = (ColorRGBA)ic.readSavable("color", null);
		info = (ItemInfo)ic.readSavable("info", null);
	}

	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		super.linkToWorld(world, location, id);
		if (isActive) {
			light = new PointLight();
			light.setColor(lightColor);
			light.setRadius(lightRadius);
			light.setPosition(getLocation().add(LIGHT_OFFSET));
			world.getNode().addLight(light);
		}
	}
}
