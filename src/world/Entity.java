package world;

import java.util.Set;

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * An Entity represents any physical object that could be (but not necessarily is) in the game world.
 * For example the ground, a house, or the player are all Entities.
 *
 * @author Tony 300242775
 */
public interface Entity extends Savable {

	/**
	 * Called from World.addEntity.
	 * @param world The world the entity is being added to.
	 * @param location The location the entity will be placed at.
	 * @param id The entity's new ID.
	 */
	public void linkToWorld(World world, Vector3f location, int id);

	/**
	 * Called from World.removeEntity.
	 * @param world The world the entity is being removed from.
	 */
	public void unlinkFromWorld(World world);

	/**
	 * @return a vector representing the absolute location of this entity.
	 */
	public Vector3f getLocation ();

	/**
	 * @return a vector representing the direction this entity is facing, may be (0,0,0).
	 */
	public Vector3f getDirection ();

	/**
	 * Increase/decrease the current (x,y,z) position of this entity
	 * by the specified amounts.
	 *
	 * @param dx the change in x-position
	 * @param dy the change in y-position
	 * @param dz the change in z-position
	 */
	public void changeLocation (float dx, float dy, float dz);

	/**
	 * Add the provided vector to the location of this Entity
	 *
	 * @param change the Vector3f to change the location by
	 */
	public void changeLocation(Vector3f change);

	/**
	 * Set the current (x,y,z) position of this entity
	 * to the given values.
	 *
	 * @param x the x-position to move to
	 * @param y the y-position to move to
	 * @param z the z-position to move to
	 */
	public void setLocation (float x, float y, float z);

	/**
	 * Set the current (x,y,z) position of this entity
	 * to the given vector
	 *
	 * @param location the vector to set the location to
	 */
	public void setLocation (Vector3f location);

	/**
	 * @return A Spatial containing the mesh of this entity
	 */
	public Spatial getMesh();

	/**
	 * @return the PhysicsControl linked to this entity's geometry
	 */
	public PhysicsControl getPhysics ();

	/**
	 * Attach this entity's Spatial to a node defined by the
	 * entity itself.
	 * @throws IllegalStateException
	 */
	public void attachToNode () throws IllegalStateException;

	/**
	 * Removes this entity's physics control from the linked world's physics space,
	 * or throws an IllegalStateException if the physics has not been initialised or
	 * the entity has not been linked to a world.
	 */
	public void removeFromPhysicsSpace ();

	/**
	 * Adds this entity's physics control to the physics space of the linked
	 * world, or throws an IllegalStateException if the physics has not been initialised
	 * or the entity has not been linked to a world.
	 */
	public void addToPhysicsSpace ();

	/**
	 * Remove this entity's Spatial from its parent node.
	 */
	public void removeFromNode();

	/**
	 * Notify this Entity that it should update.
	 * Only called if isUpdatable returns true.
	 *
	 * @param tpf the time between the previous frame and this one
	 */
	public void update (float tpf);
	
	/**
	 * If this returns false, update will not be called each tick.
	 * This is an efficiency measure.
	 * 
	 * @return whether to update this entity.
	 */
	public boolean isUpdatable();

	/**
	 * Entities have IDs, which are non-negative integers.
	 * No two entities in the same World have the same ID.
	 * @return this entity's ID.
	 */
	public int getEntityID();

	/**
	 * @return true if this entity will never move - i.e. it is a static part of the world.
	 */
	public boolean isImmovableEntity();

	/**
	 * This method is called before the entity is removed from the world.
	 */
	public void onDestroy();

	/**
	 * @return true if this entity is not linked to a world, false otherwise
	 */
	public boolean isRemovedFromWorld();

	/**
	 * Adds required assets to a set. Used for preloading.
	 * @param assets The set to add required assets to.
	 */
	public void getPreloadAssets(Set<String> assets);
}
