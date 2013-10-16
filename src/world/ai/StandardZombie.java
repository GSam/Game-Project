package world.ai;

import java.io.IOException;

import world.ActorState;
import world.PhysicsUtilities;
import world.Player;
import world.effects.SimpleDamage;
import world.entity.item.Stat;
import world.entity.mob.Mob;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;

/**
 * A normal zombie AI that paths to a target player and attacks them.
 * 
 * @author Tony Butler-Yeoman 300242775
 * @author Alex Campbell 300252131
 */
public class StandardZombie extends AI {
	protected static final float MOVE_SPEED = 0.5f;
	protected static final float SUCCESS_RADIUS = 5;
	private static final Vector3f zero = new Vector3f(0, 0, 0);
	private PathNode path;
	private Pathfinder pathing;
	
	private float timeSinceAttack;
	private float timeSinceRepath;
	private static final float ATTACK_SPEED = 1;
	private static final float REPATH_INTERVAL = 5;

	public StandardZombie() {
		pathing = new NodePathing();
	}
	
	private static enum State {
		/** Finding a target */
		RETARGET,
		/** Going to target - pathfinding, LOS checking */
		MOVE_TO,
		/** Going to target along a path */
		MOVE_TO_PATH,
		/** Going directly towards target (no obstacles) */
		MOVE_TO_DIRECT,
		/** Attacking target (actually cooldown after attack) */
		ATTACK
	}
	private State state = State.RETARGET;
	
	private Vector3f stuckPos = new Vector3f();
	private float stuckTime = 0;

	@Override
	public void update(Mob mob, float tpf) {
		float speed = mob.getMobSpeed();

		if(mob.getTarget() == null || mob.getTarget().isRemovedFromWorld())
			state = State.RETARGET;
		

		if(state == State.ATTACK) {
			Vector3f target = mob.getTarget().getLocation();
			Vector3f mobLoc = mob.getLocation();
			
			mob.getPhysics().setWalkDirection(Vector3f.ZERO);
			timeSinceAttack += tpf;
			if (timeSinceAttack >= ATTACK_SPEED || target.distanceSquared(mobLoc) > 100) {
				state = State.MOVE_TO;
				timeSinceAttack = 0;
			} else {
				return;
			}
		}
		
		if(state == State.MOVE_TO && (mob.getTarget().isInvisible() || !(mob.getTarget() instanceof Player)))
			state = State.RETARGET;
		
		if(state == State.RETARGET) {
			mob.setTarget(mob.getWorld().getNearestPlayer(mob.getLocation()));
			state = State.MOVE_TO;
			
			if(mob.getTarget() == null || mob.getTarget().isRemovedFromWorld()) {
				mob.setTarget(mob.getWorld().getNearestEntity(mob.getLocation(), Mob.class, mob));
				if(mob.getTarget() == null || mob.getTarget().isRemovedFromWorld()) {
					state = State.RETARGET;
					return;
				}
			}
		}
		
		if(state == State.MOVE_TO) {
			PathNode oldPath = path;
			
			timeSinceRepath = 0;
			path = null;
			
			Vector3f target = mob.getTarget().getLocation();
			Vector3f mobLoc = mob.getLocation();
			
			// if the mob is close enough to the player that they shouldn't
			// move, don't pathfind instead attack.
			if (target.distanceSquared(mobLoc) < 70) {
				mob.getPhysics().setWalkDirection(zero);
				mob.getPhysics().setViewDirection(target.subtract(mobLoc).setY(0));
				
				state = State.ATTACK;
				timeSinceAttack = 0;
				mob.setState(ActorState.ATTACKING);
				
				world.makeEffect(new SimpleDamage(mob.getTarget(), 0));
				return;
			}

			// if the mob can see the player, and is not higher or lower, abandon the path and go
			// straight for them
			final double MAX_HEIGHT_DIFF = 15;
			if(!world.getExpensiveOperationManager().canRun(this)) {
				path = oldPath;
			} else {
				if (Math.abs(mob.getLocation().y - mob.getTarget().getLocation().y) < MAX_HEIGHT_DIFF && PhysicsUtilities.checkLineOfSight(mob.getLocation(), mob.getTarget().getLocation().add(new Vector3f(0, 10, 0)), mob.getWorld().getNode(), false)) {
					state = State.MOVE_TO_DIRECT;
				
				} else {
					path = pathing.path(target, mob);
					if(path != null && path.next != null) {
						if(oldPath != null && path.next.loc.equals(oldPath.loc))
							path = path.next; // otherwise, zombies sometimes go back to the previous path node, if it's closest.
						state = State.MOVE_TO_PATH;
					} else
						state = State.MOVE_TO_DIRECT;
				}
			}
			
		}
		
		if(state == State.MOVE_TO_DIRECT || state == State.MOVE_TO_PATH) {
			timeSinceRepath += tpf;
			if(timeSinceRepath >= REPATH_INTERVAL) {
				state = State.MOVE_TO;
			}
			
			Vector3f target = mob.getTarget().getLocation();
			Vector3f mobLoc = mob.getLocation();
			
			if (target.distanceSquared(mobLoc) < 50) {
				state = State.MOVE_TO;
				
			} else if(state == State.MOVE_TO_DIRECT) {
				target = mob.getTarget().getLocation();
			
			} else if(state == State.MOVE_TO_PATH) {
				if (path == null) {
					state = State.MOVE_TO;
				} else {
					// move toward the next node
					target = path.loc;
					
					// if we're close, step the path.
					if (mob.getLocation().distance(path.loc) <= SUCCESS_RADIUS) {
						path = path.next;
						
						if(path == null)
							state = state.MOVE_TO;
					}
				}
			}
			
			mob.getPhysics().setWalkDirection(target.subtract(mobLoc).normalize().mult(speed));
			mob.getPhysics().setViewDirection(target.subtract(mobLoc).setY(0));
			
			if(mob.getLocation().distanceSquared(stuckPos) < 10000*speed*speed) {
				stuckTime += tpf;
				if(stuckTime > 2) {
					mob.getPhysics().jump();
				}
			} else {
				stuckPos = mob.getLocation().clone();
				stuckTime = 0;
			}
			
			mob.setState(ActorState.MOVING);
		}
	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
		InputCapsule ic = arg0.getCapsule(this);
		pathing = (Pathfinder) ic.readSavable("pathing", null);
		path = (PathNode) ic.readSavable("path", null);
		state = State.values()[ic.readInt("state", 0)];
		timeSinceAttack = ic.readFloat("tsa", 0);
		timeSinceRepath = ic.readFloat("tsr", 0);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		OutputCapsule oc = arg0.getCapsule(this);
		oc.write(pathing, "pathing", null);
		oc.write(path, "path", null);
		oc.write(state.ordinal(), "state", 0);
		oc.write(timeSinceAttack, "tsa", 0);
		oc.write(timeSinceRepath, "tsr", 0);
	}
}