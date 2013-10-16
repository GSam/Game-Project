package world;

import java.util.ArrayList;
import java.util.List;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * PhysicsUtilities is a utility class with many static methods for observing geometry-based world
 * interactions.
 * 
 * No method in this class ever changes the static of the PhysicsSpace of a given World,
 * as that would have unexpected side-effects depending on when in the physics-render cycle
 * these methods were called.
 * 
 * @author Tony 300242775
 */
public class PhysicsUtilities {

	/**
	 * Performs a raycast from the given vector in the given direction in the geometry space
	 * of the given Node. Returns true if the first collision is with the given target Entity,
	 * and false otherwise.
	 * 
	 * @param from the position to start the ray
	 * @param direction the direction to raycast in
	 * @param target the Entity to attempt to hit
	 * @param root the Node containing all Geometries to be considered
	 * @return true if the first collision of the raycast is with target, false otherwise
	 */
	public static boolean raycast(Vector3f from, Vector3f direction, Entity target, Node root) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(from, direction);
		root.collideWith(ray, results);

		if (results.size() > 0) {
			CollisionResult closest = results.getClosestCollision();
	        // while this cast is ugly, it's considered proper usage
	        // of JME's userdata system.
			Geometry geom = closest.getGeometry();
			Entity entity = geom.getUserData("entity");
			if (entity == null)
				collisionWarningMessage(geom);

			return entity == target;
		}
		return false;
	}

	/**
	 * Performs a raycast from the given vector in the given direction in the geometry space
	 * of the given Node. Returns the distance between the start of the ray and the first
	 * collision.
	 * 
	 * If there are no collisions, returns -1.
	 * 
	 * @param from the position to start the ray
	 * @param direction the direction to raycast in
	 * @param root the Node containing all Geometries to be considered
	 * @return the distance between the start of the ray and the first collision
	 */
	public static float nearest (Vector3f from, Vector3f direction, Node root) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(from, direction);
		root.collideWith(ray, results);

		for (CollisionResult result : results) {
			if (result.getGeometry().getUserData("entity") instanceof RigidEntity)
				return result.getDistance();
		}

		return -1;
	}
	
	/**
	 * Performs a raycast between two vectors, and determines if there is line-of-sight between them.
	 * This is done by determining if there are any line-of-sight blocking objects that intersect the
	 * ray between the two vectors.
	 * 
	 * @param from the position to start the ray
	 * @param to the position to 'end' the ray
	 * @param root the Node containing all Geometries to be considered
	 * @param ignoreMovableEntities If true, entities that can move will be ignored.
	 * @return true if there is line-of-sight between from and to, false otherwise
	 */
	public static boolean checkLineOfSight(Vector3f from, Vector3f to, Node root, boolean ignoreMovableEntities) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(from, to.subtract(from).normalizeLocal());
		root.collideWith(ray, results);
		
		float maxDistSq = to.distanceSquared(from);

		for (CollisionResult result : results) {
			Entity entity = (Entity)result.getGeometry().getUserData("entity");
			if (entity != null && (!ignoreMovableEntities || entity.isImmovableEntity())) {
				float dist = result.getDistance();
				if(dist*dist < maxDistSq) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Performs a raycast from the given vector in the given direction in the geometry space
	 * of the given Node. Returns a List of Entity containing all the entities the ray collided
	 * with.
	 * 
	 * @param from the position to start the ray
	 * @param direction the direction to raycast in
	 * @param root the Node containing all Geometries to be considered
	 * @return a list of every Entity that the ray intersected
	 */
	public static List<Entity> raycast(Vector3f from, Vector3f direction, Node root) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(from, direction);
		root.collideWith(ray, results);
		
		List<Entity> hit = new ArrayList<Entity> ();

		Entity entity;
		Geometry geom;
		for (CollisionResult result : results) {
			geom = result.getGeometry();
			entity = geom.getUserData("entity");
			if (entity == null)
				collisionWarningMessage(geom);
			else
				hit.add(entity);
		}

		return hit;
	}
	
	/**
	 * Performs a raycast from the given vector in the given direction in the geometry space
	 * of the given Node. Returns a List of EntityHitResult, which encodes every Entity collided
	 * with and the distance at which the collision occured.
	 * 
	 * @param from the position to start the ray
	 * @param direction the direction to raycast in
	 * @param root the Node containing all Geometries to be considered
	 * @return a List of EntityHitResult encoding the collisions 
	 */
	public static List<EntityHitResult> raycastHitResult (Vector3f from, Vector3f direction, Node root) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(from, direction);
		root.collideWith(ray, results);
		
		List<EntityHitResult> hit = new ArrayList<EntityHitResult> ();

		Entity entity;
		Geometry geom;
		for (CollisionResult result : results) {
			geom = result.getGeometry();
			entity = geom.getUserData("entity");
			if (entity == null)
				collisionWarningMessage(geom);
			else
				hit.add(new EntityHitResult(entity, result.getDistance(), result.getContactPoint()));
		}

		return hit;
	}
	
	/**
	 * A tuple representing an Entity hit by a raycst, the distance from the start
	 * at which it was hit, and the point on its geometry that it was hit at.
	 * 
	 * @author Tony 300242775
	 */
	public static class EntityHitResult {
		public final Entity entity;
		public final float distance;
		public final Vector3f hitAt;
		
		public EntityHitResult(Entity entity, float distance, Vector3f hitAt) {
			this.entity = entity;
			this.distance = distance;
			this.hitAt = hitAt;
		}
	}

	private static void collisionWarningMessage (Geometry geom) {
//		System.out.println ("---WARNING---");
//		System.out.println ("Geometry encountered in world without proper reference to its Entity.");
//		System.out.println ("Please ensure you are calling [your geometry].setUserData(\"entity\",[controlling entity]).");
//		System.out.println ("on every Geometry you make. Collisions are not guaranteed to function otherwise.");
//		System.out.println ("geometry name: " + geom);
	}

	/**
	 * Performs a raycast between the given vector and the player's location, and determines if the given
	 * location has line-of-sight of the player. 
	 * 
	 * @param location the Vector3f to test line-of-sight at
	 * @param player the player to test line-of-sight to
	 * @param root the Node containing all Geometries to be considered
	 * @return true if there is line-of-sight between location and the player, false otherwise
	 */
	public static boolean canSeePlayer(Vector3f location, Actor player, Node root) {
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(location, player.getLocation().subtract(location));
		root.collideWith(ray, results);

		for (CollisionResult result : results) {
			Entity e = result.getGeometry().getUserData("entity");
			if (e instanceof RigidEntity) {
				return false;
			} else if (e instanceof Player) {
				return true;
			}
		}

		//World.sop ("should never happen");
		return true;
	}
}
