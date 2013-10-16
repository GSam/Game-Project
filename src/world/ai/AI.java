package world.ai;

import world.World;
import world.entity.mob.Mob;

import com.jme3.export.Savable;

/**
 * AI classes contain the logic for controlling a Mob and making it behave
 * a certain way. This is done through the update method.
 * 
 * @author Tony 300242775
 */
public abstract class AI implements Savable {
	/**
	 * The World linked to this AI.
	 */
	protected World world;

	/**
	 * Links this AI to the passed World, so it can
	 * make decisions based on information in it.
	 * @param world the World to link to
	 */
	public void linkToWorld(World world) {
		this.world = world;
	}

	/**
	 * Perform one tick of this AI's logic on the passed Mob.
	 * 
	 * @param mob the Mob to perform AI logic on
	 * @param tpf the time since the last update
	 */
	public abstract void update(Mob mob, float tpf);
}
