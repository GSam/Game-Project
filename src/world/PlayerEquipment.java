package world;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import world.entity.item.Item;
import world.entity.item.equippable.EquipType;
import world.entity.item.equippable.Equippable;
import world.entity.item.equippable.Weapon;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

/**
 * PlayerEquipment represents what is currently equipped on the Player. It
 * is used for calculating stat changes to the player as well as keeping track
 * 
 * @author Tony 300242775
 */
public class PlayerEquipment implements Iterable<Equippable>, Savable {
	private HashMap <EquipType, Equippable> equipped;
	private Map<EquipType, Integer> loadedEntityIDs;
	
	/**
	 * Equips an item.
	 * 
	 * Throws a NullPointerException if any passed argument is null.
	 * Throws an IllegalArgumentException if the Item is not Equippable.
	 * Throws an IllegalArgumentException if the Item cannot be placed in a
	 * slot of the provided EquipType.
	 * 
	 * @param item The item.
	 * @param slot The slot to equip in.
	 * @return The previous item equipped in that slot.
	 */
	public Equippable equip (Item item, EquipType slot) {
		if(slot == null)
			throw new NullPointerException("slot is null");
		if(item == null)
			throw new NullPointerException("item is null");
		if(!(item instanceof Equippable))
			throw new IllegalArgumentException("item is not Equippable. item: "+item);
		if(((Equippable)item).getEquipType() != slot)
			throw new IllegalArgumentException("Wrong slot (item is for "+((Equippable)item).getEquipType()+", equipped in "+slot+"). Item: "+item);
		return equipped.put(slot, (Equippable)item);
	}
	
	/**
	 * Returns the Equippable equipped of the passed EquipType.
	 * 
	 * This returns an Item, not an Equippable, for ease of use with
	 * NifyGUI.
	 * 
	 * @param type the equipment slot to return the equipped Equippable of 
	 * @return the equipped Equippable of the passed EquipType
	 */
	public Item getEquipped(EquipType type){
		return (Item) equipped.get(type);
	}
	
	/**
	 * Unequip the passed Item. This takes an Item instead of an
	 * equippable for ease of use with NiftyGUI.
	 * 
	 * Throw a NullPointerException if the passed Item is null.
	 * 
	 * @param item the Item to remove
	 * @return true if the Item was removed, false otherwise
	 */
	public boolean unequip(Item item) {
		if(item == null)
			throw new NullPointerException("item is null");
		for (Map.Entry<EquipType, Equippable> entry : equipped.entrySet()) {
			if (entry.getValue().equals(item)) {
				equipped.remove(entry.getKey());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Link this PlayerEquipment to the passed World, effectively constructing this
	 * object if it is loaded from a save file.
	 * @param w the World to link to
	 */
	public void linkToWorld(World w) {
		equipped = new HashMap<EquipType, Equippable>();
		if(loadedEntityIDs != null) {
			for(Map.Entry<EquipType, Integer> entry : loadedEntityIDs.entrySet()) {
				Equippable ent = (Equippable)w.getEntityByID(entry.getValue());
				if(ent == null)
					throw new RuntimeException("Loaded PlayerEquipment with entity ID "+entry.getValue()+" but entity does not exist in world");
				equipped.put(entry.getKey(), ent);
			}
			loadedEntityIDs = null;
		}
	}

	@Override
	public Iterator<Equippable> iterator() {
		return equipped.values().iterator();
	}

	public Weapon getWeapon() {
		Equippable weapon = equipped.get(EquipType.WEAPON);
		if (weapon != null && !(weapon instanceof Weapon)) throw new IllegalStateException ("equipped weapon isn't instance of Weapon");
		return (Weapon) weapon;
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		loadedEntityIDs = new HashMap<EquipType, Integer>();
		equipped = null;
		for(EquipType t : EquipType.values()) {
			int id = c.readInt(t.name(), Integer.MIN_VALUE);
			if(id != Integer.MIN_VALUE)
				loadedEntityIDs.put(t, id);
		}
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		for(Map.Entry<EquipType, Equippable> e : equipped.entrySet()) {
			c.write(((Entity)e.getValue()).getEntityID(), e.getKey().name(), Integer.MIN_VALUE);
		}
	}
}
