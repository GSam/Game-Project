package world.ai;

import java.io.IOException;
import java.util.HashSet;

import world.PhysicsUtilities;
import world.World;
import world.entity.mob.Mob;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * A simple algorithm that finds not-too-bad paths in a world with little
 * y-variation. It doesn't utilise path nodes in the world.
 * 
 * @author Tony 300242775
 */
public class NoNodePathing implements Pathfinder, Savable {
	private static final int MAX_RECURSION = 40;
	private static final float Y_SLICE = 4;
	private static final int NUM_RAYS = 8;
	private static final float ANGLE = 2 * FastMath.PI / NUM_RAYS;
	private static final Vector3f[] rays = new Vector3f[NUM_RAYS];;

	private float stepSize;
	private HashSet<Vector3f> visited;

	static {
		for (int i=0; i < NUM_RAYS; i++)
			rays[i] = new Vector3f (FastMath.cos(i * ANGLE), 0, FastMath.sin(i * ANGLE));
	};

	@Override
	public PathNode path (Vector3f to, Mob mob) {
		stepSize = mob.getLocation().distance(to) / 5;
		visited = new HashSet<Vector3f> ();
		return getPath(to, mob, 0, null);
	}

	private PathNode getPath (Vector3f to, Mob mob, int recursion, PathNode head) {
		if (recursion == MAX_RECURSION) {
			return head;
		}

		Vector3f ml = mob.getLocation();
		float radius = mob.getRadius();
		to.y = Y_SLICE;

		World world = mob.getWorld();

		// if there's direct LoS, just head toward the location
		if(PhysicsUtilities.nearest(ml, to.subtract(ml), world.getNode()) > ml.distance(to)) {
			//	mob.getPhysics().setWalkDirection(to.subtract(ml).normalize().mult(MOVE_SPEED));
			return new PathNode (to, head, world);
		}

		// otherwise start backtracking from the player to find a suitable route.
		Vector3f best = null;
		Vector3f from = null;
		float minDist = Float.MAX_VALUE;

		// calculate the nearest intersection for each ray.
		for (Vector3f ray : rays) {
			float intersect = PhysicsUtilities.nearest(to, ray, world.getNode());

			float distToMl = to.add(ray.mult(stepSize)).distance(ml);
			if (distToMl < minDist && (intersect >= (stepSize + radius) || intersect < 0)) {
				from = to.add(ray.mult(stepSize));
				if (!visited.contains(from)) {
					best = ray;
					minDist = distToMl;
				}
			}
		}

		if (best == null) {
			//World.sop("no path");
			return null;
		}

		visited.add(from);
		from.y = Y_SLICE;

		//return new PathNode (from, getPath(from, mob, recursion+1), world);
		return getPath (from, mob, recursion+1, new PathNode (to, head, world));
	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
	}
}
