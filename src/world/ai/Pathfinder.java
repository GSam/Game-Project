package world.ai;

import world.entity.mob.Mob;

import com.jme3.export.Savable;
import com.jme3.math.Vector3f;

/**
 * A Pathfinder contains the logic for finding valid paths for
 * NPCs from one location to another.
 * 
 * @author Tony 300242775
 */
public interface Pathfinder extends Savable {
	/**
	 * Returns a linked list of PathNodes representing a
	 * path to take for the passed Mob to get the to passed
	 * point.
	 * @param to the point to find a path to
	 * @param mob the mob to find a path for
	 * @return a linked list of path nodes representing the path
	 */
	public PathNode path (Vector3f to, Mob mob);
}