package world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import world.entity.item.Item;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;

/**
 * Inventory represents the inventory of an Entity, and can be used to store Items.
 *
 * This class encapsulates all the logic for picking up an item from the world, or dropping
 * it to the world, as well as providing a hook for GUI integration.
 *
 * @author Tony 300242775
 */
public class Inventory implements Iterable<Item>, Savable, PhysicsTickListener {
	private static final float DROP_FORCE = 2000;
	private static final float DROP_DISTANCE = 20;
	private static final Vector3f ITEM_GRAVITY = new Vector3f(0,-100,0);
	private static final Vector3f EXTRA_HEIGHT = new Vector3f(0,10,0);
	private static final float MAX_WEIGHT = 200;

	/**
	 * A Set that stores all of the Items currently in this Inventory.
	 */
	protected Set<Item> items = new HashSet<Item>();
	
	private int size;
	private int[] loadedItemIDs;
	private Entity owner;
	private int loadedOwnerID = -1;
	private World world;
	private float currentWeight;
	
	private Set<InventoryObserver> observers = new HashSet<InventoryObserver>();
	
	/**
	 * Add an InventoryObserver to be notified when this Inventory changes
	 * @param obs the observer to add
	 */
	public void addObserver(InventoryObserver obs) {observers.add(obs);}
	
	/**
	 * Remove a previously added InventoryObserver.
	 * @param obs the observer to remove
	 */
	public void removeObserver(InventoryObserver obs) {observers.remove(obs);}

	private Item dropped;

	public Inventory() {} // used for loading

	/**
	 * @param owner the Entity that this is the inventory of
	 * @param size the maximum number of items this Inventory can hold
	 */
	public Inventory(Entity owner, int size) {
		this.owner = owner;
		this.size = size;
	}

	/**
	 * Link this inventory to the provided World.
	 * @param world the World to link to
	 */
	public void linkToWorld(World world) {
		this.world = world;

		// this loop won't run when loading, only if you add items before
		// linking the inventory to the world.
		// it allows you to do something like:
		//
		// inventory = new Inventory();
		// inventory.add(new SomeItem());
		// inventory.add(new OtherItem());
		//
		// this is particularly useful for giving players items at game start.
		
		Collection<Item> oldItems = new ArrayList<Item>(items);
		items.clear();
		for(Item item : oldItems) {
			world.addEntity(item, Vector3f.ZERO);
			add(item);
		}
		
		if(loadedItemIDs != null) {
			for(int i : loadedItemIDs) {
				Item item = (Item)world.getEntityByID(i);
				add(item);
			}
			loadedItemIDs = null;
		}

		if(loadedOwnerID != -1) {
			owner = world.getEntityByID(loadedOwnerID);
			loadedOwnerID = -1;
		}
	}

	/**
	 * Drops the passed item into the world near the owner of this inventory,
	 * and removes it from the inventory.
	 * @param item the Item to drop
	 */
	public void dropItem(Item item) {
		if (world == null)
			throw new IllegalStateException("Not linked to world");

		if(item == null)
			throw new NullPointerException("item is null");

		if(!items.remove(item))
			throw new IllegalArgumentException("Item is not in this inventory");

		item.setInventory(null);

		item.attachToNode();
		item.addToPhysicsSpace();
		item.getPhysics().setGravity(ITEM_GRAVITY);

		Vector3f loc = owner.getLocation().add(owner.getDirection().mult(DROP_DISTANCE).add(EXTRA_HEIGHT));
		item.setLocation(loc);
		dropped = item;
		//addToWorld(item, owner.getLocation(), owner.getDirection());
	}

	/**
	 * Add the passed item to this inventory. Throws a null pointer exception if
	 * item is null.
	 * 
	 * @param item
	 *            the Item to add
	 * @return true if the item was successfully added, false otherwise
	 */
	public boolean add(Item item) {
		if (item == null)
			throw new NullPointerException("item is null");

		if(item.getInventory() != null){
			//throw new IllegalArgumentException("item already in an inventory: "+item);
			// special case for server side picking, two people may attempt to pick up an item
			return false;
		}

		if (items.size() == size)
			return false;
		
		for(InventoryObserver o : observers)
			if(!o.addItem(item))
				return true;

		items.add(item);
		// If we are not linked to a world yet, then just add it to the items list and do nothing else.
		// We'll add it properly later (in linkToWorld).
		if(world != null) {
			item.setInventory(this);
			item.removeFromNode();
			item.removeFromPhysicsSpace();
		}
		return true;
	}

	/**
	 * Add the passed item to this inventory to the specified position.
	 * 
	 * Throws an IllegalStateException if this Inventory has not been linked
	 * to a world.
	 * 
	 * Throws a NullPointerException if the passed Item or String are null.
	 * 
	 * Throws an IllegalArgumentException if the passed Item is already in
	 * an Inventory.
	 * 
	 * @param item the Item to add
	 */
	public void addToPosition(Item item, String position) {
		if(world == null)
			throw new IllegalStateException("Not linked to world");

		if (item == null)
			throw new NullPointerException("item is null");
		
		if(position == null)
			throw new NullPointerException("position is null");

		if(item.getInventory() != null)
			throw new IllegalArgumentException("item already in an inventory");
		
		for(InventoryObserver o : observers)
			if(!o.addItemInPos(item, position))
				return;

		// add the item to the Inventory
		items.add(item);
		item.setInventory(this);
		// remove the item from the world
		item.removeFromNode();
		item.removeFromPhysicsSpace();
	}

	/**
	 * Add the passed item to this inventory.
	 * 
	 * Throws an IllegalStateException if this Inventory has not been linked
	 * to a world.
     *
	 * Throws an IllegalArgumentException if the passed Item is already in
	 * an Inventory.
	 * 
	 * @param item the Item to add
	 */
	public void addDirect(Item item) {
		if(world == null)
			throw new IllegalStateException("Not linked to world");
		if(item.getInventory() != null)
			throw new IllegalArgumentException("Item already in an inventory: "+item.getInventory().getOwner());
		items.add(item);
		item.setInventory(this);
		if(world.getPhysicsSpace().getRigidBodyList().contains(item.getPhysics())){
			item.removeFromNode();
			item.removeFromPhysicsSpace();
		}
	}

	/**
	 * Removes the passed item from this inventory, if it contains it.
	 * 
	 * Throws an IllegalStateException if this Inventory has not been linked
	 * to a world.
     *
     * Throws a NullPointerException if the passed Item is null.
     *
	 * Throws an IllegalArgumentException if the passed Item is not in this
	 * Inventory.
	 *
	 * @param item the Item to remove
	 */
	public void removeItem(Item item) {
		if(world == null)
			throw new IllegalStateException("Not linked to world");

		if(item == null)
			throw new NullPointerException("item is null");

		if (!items.contains(item))
			throw new IllegalArgumentException("item "+item+" not in this inventory (owner: "+owner+")");

		items.remove(item);
		item.setInventory(null);
	}

	@Override
	public Iterator<Item> iterator() {
		if (world == null)
			throw new IllegalStateException("Not linked to world");
		return items.iterator();
	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
		InputCapsule ic = arg0.getCapsule(this);

		loadedOwnerID = ic.readInt("owner", 0);
		loadedItemIDs = ic.readIntArray("items", null);
		size = ic.readInt("size", 0);

		items = new HashSet<Item>();
		owner = null;
		world = null;
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		if (world == null)
			throw new IllegalStateException("Not linked to world");

		OutputCapsule oc = arg0.getCapsule(this);
		oc.write(owner.getEntityID(), "owner", 0);
		oc.write(size, "size", 0);

		int[] itemIDs = new int[items.size()];
		int k = 0;
		for(Item i : items)
			itemIDs[k++] = i.getEntityID();
		oc.write(itemIDs, "items", null);
	}

	@Override
	public void physicsTick(PhysicsSpace arg0, float arg1) {
	}

	@Override
	public void prePhysicsTick(PhysicsSpace physicsSpace, float tpf) {
		if (dropped == null) return;
		Vector3f dir = owner.getDirection().normalize();
		// not perfect
		dropped.getPhysics().applyTorque (dir.mult(DROP_FORCE));
		//dropped.getPhysics().applyCentralForce (owner.getDirection().normalize().mult(DROP_FORCE));
		dropped = null;
	}

	/**
	 * @return the Entity that owns this Inventory
	 */
	public Entity getOwner(){
		return owner;
	}

	/**
	 * @return all of the Items in this Inventory
	 */
	public Set<Item> getItems() {
		return items;
	}
}
