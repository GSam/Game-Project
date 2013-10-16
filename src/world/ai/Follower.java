package world.ai;

import java.io.IOException;

import world.ActorState;
import world.PhysicsUtilities;
import world.Player;
import world.entity.item.Stat;
import world.entity.mob.Mob;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;

/**
 * Follower is a simple AI that follows a target around, utilising StupidPath
 * for pathfinding.
 * 
 * @author Tony 300242775
 */
public class Follower extends AI {
	protected static final float MOVE_SPEED = 0.5f;
	protected static final float SUCCESS_RADIUS = 5;
	private static final Vector3f zero = new Vector3f(0, 0, 0);
	private PathNode path;
	private Pathfinder pathing;
	
	private boolean isInAttack;
	private float timeSinceAttack;
	private static final float ATTACK_SPEED = 1; 

	public Follower() {
		pathing = new NodePathing();
	}

	@Override
	public void update(Mob mob, float tpf) {
		if (isInAttack) {
			timeSinceAttack += tpf;
			
			if (timeSinceAttack >= ATTACK_SPEED) {
				isInAttack = false;
				timeSinceAttack = 0;
			} else {
				return;
			}
		}
		
		if (mob.getTarget() == null) {
			//mob.setTarget(mob.getWorld().getRandomPlayer());
			mob.setTarget(mob.getWorld().getNearestPlayer(mob.getLocation()));
		}
		if (mob.getTarget() != null) {

			Vector3f playerLoc = mob.getTarget().getLocation();
			Vector3f mobLoc = mob.getLocation();

			// World.sop(mob);
			float speed = mob.getStats().getStat(Stat.SPEED);
			mob.getStats().resetStat(Stat.SPEED);

			// if the mob is close enough to the player that they shouldn't
			// move, don't pathfind instead attack.
			if (playerLoc.distanceSquared(mobLoc) < 70) {
				mob.getPhysics().setWalkDirection(zero);
				mob.getPhysics().setViewDirection(playerLoc.subtract(mobLoc));
				path = null;
				return;
			}

			// if the mob can see the player, abandon the path and go
			// straight for them
			if (PhysicsUtilities.canSeePlayer(mob.getLocation(), mob.getTarget(), mob.getWorld().getNode())) {
				path = null;
				mob.getPhysics().setWalkDirection(playerLoc.subtract(mobLoc).normalize().mult(speed));
				mob.getPhysics().setViewDirection(playerLoc.subtract(mobLoc));
				mob.setState(ActorState.MOVING);
				return;
			}

			// if we're at the end of the path, get a new one.
			if (path == null) {
				Player p = mob.getWorld().getNearestPlayer(mob.getLocation());
				path = pathing.path(p == null ? Vector3f.ZERO : p.getLocation(), mob);

				// there's been an error and we can't find a path
				if (path == null) {
					mob.setState(ActorState.STANDING);
					return;
				}
			}

			// move toward the next node
			mob.setState(ActorState.MOVING);
			mob.getPhysics().setWalkDirection(path.loc.subtract(mobLoc).normalize().mult(speed));
			mob.getPhysics().setViewDirection(path.loc.subtract(mobLoc));

			// if we're close, step the path.
			if (mob.getLocation().distance(path.loc) <= SUCCESS_RADIUS) {
				path = path.next;
			}
		}
	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
		InputCapsule ic = arg0.getCapsule(this);
		pathing = (Pathfinder) ic.readSavable("pathing", null);
		path = (PathNode) ic.readSavable("path", null);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		OutputCapsule oc = arg0.getCapsule(this);
		oc.write(pathing, "pathing", null);
		oc.write(path, "path", null);
	}
}