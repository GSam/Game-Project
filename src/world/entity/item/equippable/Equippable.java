package world.entity.item.equippable;

import world.Entity;
import world.StatModification;

/**
 * Equippable represents an Item that can be equipped to a Player, and potentially
 * changes that Player's stats.
 * 
 * An Equippable has an EquipType, which defines the slot an Equippable can be placed
 * in. 
 * 
 * @author Tony 300242775
 */
public interface Equippable extends Entity {
	/**
	 * @return the EquipType of the Equippable
	 */
	public EquipType getEquipType ();
	
	/**
	 * @return the StatModification that this Equippable applies on equip
	 */
	public StatModification getItemStats ();
}
