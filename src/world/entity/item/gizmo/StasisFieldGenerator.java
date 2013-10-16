package world.entity.item.gizmo;

import world.WorldType;
import world.effects.SlowInRadius;
import world.entity.item.ItemInfo;
import world.entity.item.Stat;

/**
 * A gizmo that freezes all mobs near the player for a short time.
 *
 * @author Tony 300242775
 */
public class StasisFieldGenerator extends AbstractGizmo {
	private static final float RADIUS = 2000;
	private static final float SLOW = 0.01f;

	private SlowInRadius slow;

	@Override
	protected void onActivate () {
		if (owner != null && owner.getWorld() != null) {
			slow = new SlowInRadius(owner.getLocation(), RADIUS, SLOW);
			// world.makeEffect(slow);
			if (world.getWorldType() != WorldType.CLIENT)
				world.makeLocalEffect(slow);
		}
	}

	@Override
	protected void onActiveUpdate () {
		if (owner != null && owner.getWorld() != null && world.getWorldType() != WorldType.CLIENT) {
			slow.setLocation(owner.getLocation());
		}
	}

	@Override
	protected void onActiveEnd () {
		if (world.getWorldType() != WorldType.CLIENT)
			world.destroyEffect(slow);
		//world.destroyEffect(slow);
	}

	@Override
	protected float energyCost () {
		return 50;
	}

	@Override
	protected float getCooldown() {
		return 10;
	}

	@Override
	protected float getActiveTime() {
		return 15;
	}

	@Override
	protected String getImage() {
		return null;
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat keys[] = new Stat[] {Stat.NAME};
		String values[] = new String[] {"Stasis Field Generator"};
		return new ItemInfo(keys, values);
	}
}
