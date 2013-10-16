package network.interfaces;

import world.Entity;
import world.World;
import world.effects.Effect;
import world.entity.item.Item;

/**
 * Defines the interface for which the world is observed by some third party,
 * specifically the network: client, server.
 *
 * Which methods are called on either side are subject to change and so this is
 * just a general world observer to cover any possible aspects which may be
 * networked.
 *
 * @author Garming Sam 300198721
 *
 */
public abstract class WorldObserver {

	// Currently the methods which the server implements

	/**
	 * Called when an entity is added.
	 *
	 * @param w
	 *            world
	 * @param e
	 *            entity
	 */
	public void onAddEntity(World w, Entity e) {
	}

	/**
	 * Called when an entity is removed.
	 *
	 * @param w
	 *            world
	 * @param e
	 *            entity
	 */
	public void onRemoveEntity(World w, Entity e) {
	}

	/**
	 * Called when an update to the day night cycle is made.
	 *
	 * @param w
	 *            world
	 * @param time
	 *            time
	 */
	public void onDayNightChange(World w, float time) {
	}

	/**
	 * Called when an effect is made in the world.
	 *
	 * @param w
	 *            world
	 * @param e
	 *            effect
	 */
	public void addEffect(World w, Effect e) {
	}

	// Currently the methods which client implements

	/**
	 * Called when an item is equipped
	 *
	 * @param w
	 *            world
	 * @param i
	 *            item
	 * @param equip
	 *            equip/unequip
	 */
	public void onEquipItem(World w, Item i, boolean equip) {
	}

	/**
	 * Called when an item is activated in the world.
	 *
	 * @param world
	 *            world
	 * @param toActivate
	 *            item activated
	 */
	public void onActivate(World world, Entity toActivate) {
	}

	/**
	 * Called when an item is picked up in the world.
	 *
	 * @param w
	 *            world
	 * @param i
	 *            item
	 */
	public void onPickItem(World w, Item i) {
	}

	/**
	 * Called when the game is won.
	 *
	 * @param w
	 *            world
	 */
	public void onGameWon(World w) {
	}

}
