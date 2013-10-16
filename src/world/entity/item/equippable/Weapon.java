package world.entity.item.equippable;

import world.Player;

/**
 * Weapon represents an Equippable that can be used to attack.
 * 
 * @author Tony 300242775
 */
public interface Weapon extends Equippable {
	/**
	 * Runs the logic for this Weapon's attack.
	 * @param player the player who attacked with this Weapon
	 * @return true if the weapon performed an attack (not necessarily attacking anything), false otherwise
	 */
	public boolean attack (Player player);
	
	/**
	 * @return the amount of recoil on this Weapon
	 */
	public float getRecoil();
	
	/**
	 * @return the rate at which this Weapon can attack
	 */
	public float getFireSpeed();
}
