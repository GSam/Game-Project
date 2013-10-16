package world.entity.item.gizmo;

import world.Entity;
import world.entity.item.ItemInfo;
import world.entity.item.Stat;
import world.entity.mob.Mob;

import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

/**
 * A gizmo that prevents mobs from targeting the player for a short time.
 *
 * @author Tony 300242775
 */
public class StealthField extends AbstractGizmo {

	@Override
	protected void onActivate() {
		if(owner != null) owner.setInvisible(true);

		SceneGraphVisitor visitor = new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				Entity e = spatial.getUserData("entity");
				if (e == null) return;
				if (!(e instanceof Mob)) System.out.println("non-Mob encountered in the Mob node");

				Mob mob = (Mob) e;
				if (mob.getTarget() == owner) mob.setTarget(null);
			}
		};

		world.getMobNode().depthFirstTraversal(visitor);
	}

	@Override
	protected void onActiveUpdate() {
	}

	@Override
	protected void onActiveEnd() {
		if(owner != null) owner.setInvisible(false);
	}

	@Override
	protected float energyCost() {
		return 80;
	}

	@Override
	protected float getCooldown() {
		return 30;
	}

	@Override
	protected float getActiveTime() {
		return 20;
	}

	@Override
	protected String getImage() {
		return null;
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat keys[] = new Stat[] { Stat.NAME };
		String values[] = new String[] { "Stealth Field" };
		return new ItemInfo(keys, values);
	}
}
