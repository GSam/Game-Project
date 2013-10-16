package world.entity.item;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import world.Inventory;
import world.RigidEntity;
import world.StatModification;
import world.World;

import com.jme3.asset.AssetManager;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;

/**
 * An Item represents an Entity that can be placed in an Inventory. An Item, being
 * an Entity, can also have a presence in the world when dropped (at which point it acts
 * like a regular RigidEntity).
 * 
 * Note the difference between an item's 'item weight' and its 'physics weight'. Item weight
 * is the weight taken up when an item is in an inventory, whereas the physics weight is
 * a physics space value determining the Item's Geometry's interaction with the physical world.
 * 
 * @author Tony 300242775
 */
public abstract class Item extends RigidEntity implements Iterable<ItemDisplayTuple> {
	/**
	 * The default weight of an Item in an inventory. Note this is item weight,
	 * not physics weight.
	 */
	protected static final float DEFAULT_ITEM_WEIGHT = 7;
	
	protected Inventory inventory;
	private String inventoryPosition;
	protected String name;
	private String id;
	private ItemType type;

	/**
	 * The stats of this Item. This is used to determine the effect a Consumable or
	 * Equippable has, and is defined in Item for GUI considerations. This may be
	 * empty (stats has an empty hashmap) but will never be null after linkToWorld
	 * is called.
	 */
	protected StatModification stats;
	
	/**
	 * This Item's information. This can store, for example, a name and description.
	 * This will never be null after linkToWorld is called.
	 */
	protected ItemInfo info;

	public Item () {
		name = "";
		type = ItemType.TECHNO;
		id = new Random().nextInt() + "";
	}

	// INVENTORY

	/**
	 * Returns the Inventory this Item is currently in, or null if it is in the world.
	 * @return the Inventory this Item is currently in
	 */
	public Inventory getInventory () {
		return inventory;
	}

	/**
	 * Set the inventory this item is in to the passed inventory.
	 * @param inventory the Inventory for this Item to inhabit
	 */
	public void setInventory (Inventory inventory) {
		this.inventory = inventory;
	}

	// INITIALISATION

	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		super.linkToWorld(world, location, id);
		
		stats = makeItemStats();
		info = makeItemInfo ();
		
		if(inventory != null){
			removeFromNode(); // TODO
		}
	}

	/**
	 * An alternative initialiser to linkToWorld that initialises this item in the
	 * passed inventory (so it does not appear in the world) while still correctly
	 * setting up this item's geometry and physics.
	 * @param world the World to link to
	 * @param inventory the Inventory to put this item in
	 * @param id the unique ID of this item
	 */
	public void linkToInventory (World world, Inventory inventory, int id) {
		this.world = world;
		this.entityID = id;
		makeMesh (world.getAssetManager());
		makePhysics (world.getPhysicsSpace());
		geometry.setUserData("entity", this);
		
		stats = makeItemStats();
		info = makeItemInfo ();
	}


	@Override
	public float getWeight () {
		return 4000;
	}

	@Override
	protected abstract void makeMesh(AssetManager assetManager);

	// DISPLAY

	/**
	 * Returns an Iterator that contains information about all of this item's
	 * statistics and information. Useful for display.
	 * @return an Iterator containing this Item's stats and info
	 */
	public Iterator<ItemDisplayTuple> getDisplay () {
		return new ItemIterator (stats, info);
	}

	@Override
	public Iterator<ItemDisplayTuple> iterator () {
		return new ItemIterator (stats, info);
	}

	/**
	 * @return a String corresponding to an image asset to use for this Item
	 */
	protected abstract String getImage ();

	// ITEM DETAILS

	/**
	 * Returns the weight of this item. Will be non-negative, 0 indicates
	 * 'infinite weight' (as per JMonkey standard).
	 * @return the weight of this item
	 */
	public int getItemWeight () {
		return 100;
	}

	/**
	 * Creates a new ItemStats object representing the statistics of this
	 * Item and returns it. Should only be called once.
	 * @return a new ItemStats object for this Item
	 */
	protected abstract StatModification makeItemStats();

	/**
	 * Creates a new ItemInfo object representing information about this
	 * Item and returns it. Should only be called once.
	 * @return a new ItemInfo object for this Item
	 */
	protected abstract ItemInfo makeItemInfo ();

	/**
	 * @return the ItemStatistics object describing this item's statistics
	 */

	public StatModification getItemStats() {
		return stats;
	}

	public void onPick () {};

	// SAVE LOAD

	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		name = arg0.getCapsule(this).readString("name", null);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		arg0.getCapsule(this).write(name, "name", null);
	}

	// INVENTORYITEM METHODS

	public boolean isEquippable() {return false;}


	public void setIsEquippable(boolean val) {}


	public String getName() {
		return info.getStats().get(Stat.NAME);
	}


	public void setName(String name) {
		System.out.println ("you shouldn't really be setting the name from here");
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public ItemType getType() {
		return type;
	}


	public void setStats(StatModification itstat) {
		this.stats = itstat;
	}


	public void setType(ItemType type) {
		this.type = type;
	}

	public void setInventoryPosition(String pos){
		this.inventoryPosition = pos;
	}

	/**
	 * Returns the name of the inventory slot the item is in, or null.
	 *
	 * This will be null unless the item has ever been displayed on the screen,
	 * so this method is useless outside the GUI, and especially useless on servers.
	 */
	public String getInventoryPosition(){
		return this.inventoryPosition;
	}
}

/**
 * ItemIterator is a small iterator class used to provide an easy to process hook for the GUI to
 * display an Item's statistics and information.
 *
 * @author Tony
 */
class ItemIterator implements Iterator<ItemDisplayTuple> {
	private Iterator<Entry<Stat, Float>> stats;
	private Iterator<Entry<Stat, String>> info;

	public ItemIterator (StatModification stats, ItemInfo info) {
		this.stats = stats.iterator();
		this.info = info.iterator();
	}

	@Override
	public boolean hasNext() {
		return stats.hasNext() || info.hasNext();
	}

	@Override
	public ItemDisplayTuple next() {
		if (info.hasNext()) { // always give all the info first.
			Entry<Stat, String> e = info.next();
			return new ItemDisplayTuple (e.getKey(), e.getValue());
		}

		// and then the statistics.
		Entry<Stat, Float> e = stats.next();
		return new ItemDisplayTuple (e.getKey(), e.getValue().toString());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
