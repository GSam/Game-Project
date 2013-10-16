package world;

import java.util.Collection;

import world.entity.item.miscellaneous.Key;

/**
 * A Lockable represents anything that can be locked and need a Key to
 * open.
 * 
 * A Lockable can be unlocked by many Keys, and a Key can unlock many
 * Lockables. 
 * 
 * @author Tony 300242775
 */
public interface Lockable {
	
	/**
	 * Allow the passed Key to unlock this Lockable.
	 * @param key the Key to add
	 */
	public void addKey (Key key);
	
	/**
	 * @return all the keys that can unlock this Lockable
	 */
	public Collection<Key> getKeys();
	
	/**
	 * Attempt to unlock this Lockable. The passed Inventory
	 * is checked to see if it contains any Key that unlocks
	 * this Lockable and, if so, unlocks, if not, doesn't.
	 * 
	 * @param inventory the Inventory to attempt to unlock with
	 * @return true if this Lockable was successfully unlocked, false otherwise
	 */
	public boolean unlock (Inventory inventory);
}
