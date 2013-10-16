package world.effects;

import com.jme3.export.Savable;

import world.Actor;
import world.World;

/**
 * An Effect represents any change to an Actor. This is a general system that
 * can be used to affect an Actor in an arbitrary way, for example damaging them
 * or slowing them down.
 * 
 * This system increases the complexity of the game logic somewhat, but is motivated
 * by the need to have an flexible system to affect Actors that consolidates the code
 * for a change in one place and can easily be extended.
 * 
 * @author Tony 300242775
 */
public abstract class Effect implements Savable {
	protected World world;
	
	/**
	 * Called by World.makeEffect to initialize this effect for the given world.
	 * This cannot be in the constructor, as loading requires a nullary constructor.
	 * @param world The game world.
	 */
	public void linkToWorld(World world) {
		this.world = world;
	}

	/**
	 * Applies this Effect to the passed Actor, changing its state in some way.
	 * @param actor the Actor to apply this Effect to
	 */
	public abstract void apply (Actor actor);

	/**
	 * If this Effect must be manually started, calling this method starts
	 * the logic for this Effect.
	 */
	public abstract void start ();

	/**
	 * Called every game tick.
	 * @param tpf The amount of time for this tick.
	 */
	public void update (float tpf) {};

	/**
	 * Called before the effect is removed from the world.
	 */
	protected void onDestroy() {};
	
	/**
	 * Calling this method removes this effect from the game world in all
	 * respects. This method is guaranteed to call the protected onDestroy
	 * method.
	 */
	public void destroy () {
		world.destroyEffect(this);
		onDestroy ();
	}
}
