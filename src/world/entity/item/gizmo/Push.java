package world.entity.item.gizmo;

import java.util.HashSet;

import world.Entity;
import world.entity.item.ItemInfo;
import world.entity.item.Stat;
import world.entity.mob.Mob;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * A gizmo that pushes all Mobs near the player away.
 *
 * @author Tony 300242775
 */
public class Push extends AbstractGizmo {
	private static final float RADIUS = 40;
	private static final float POWER = 500f;

	@Override
	protected void onActivate() {
		Vector3f centre = owner.getLocation();
		BoundingSphere sphere = new BoundingSphere(RADIUS, centre);
		Node sphereNode = new Node();
		sphereNode.setModelBound(sphere);
		world.getNode().attachChild(sphereNode);

		Node mobs = world.getMobNode();
		if (mobs.getChildren().size() == 0) return;

		HashSet<Mob> appliedTo = new HashSet<Mob>();

		CollisionResults results = new CollisionResults();
		mobs.collideWith(sphere, results);

		for (CollisionResult result : results) {
			Entity e = (Entity) result.getGeometry().getUserData("entity");
			if (!(e instanceof Mob)) continue; // includes null case
			appliedTo.add((Mob)e);
		}

		for (Mob mob : appliedTo) {
			Vector3f force = mob.getLocation().subtract(centre).normalize().mult(POWER);
			mob.getPhysics().applyCentralForce(force);
		}
	}

	@Override
	protected float energyCost () {
		return 40;
	}

	@Override
	protected void onActiveUpdate() {}

	@Override
	protected void onActiveEnd() {}

	@Override
	protected float getCooldown() {
		return 5;
	}

	@Override
	protected float getActiveTime() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	protected String getImage() {
		return null;
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat keys[] = new Stat[] {Stat.NAME};
		String values[] = new String[] {"Push Blast"};
		return new ItemInfo(keys, values);
	}
}
