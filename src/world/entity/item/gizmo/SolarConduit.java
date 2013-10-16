package world.entity.item.gizmo;

import world.WorldType;
import world.effects.MobDamageInRadius;
import world.entity.item.ItemInfo;
import world.entity.item.ItemType;
import world.entity.item.Stat;

/**
 * A gizmo that kills everything around the player, but only in the day time.
 *
 * @author Tony 300242775
 */
public class SolarConduit extends AbstractGizmo {
	private static final float RADIUS = 50;
	private static final float DAMAGE = 1000;

	@Override
	protected void onActivate() {
		if (!world.getSun().isDay ()) return;
		if (owner != null) {
			MobDamageInRadius damage = new MobDamageInRadius(
					owner.getLocation(), RADIUS, DAMAGE);
			if (world.getWorldType() != WorldType.CLIENT)
				world.makeEffect(damage);
		}
		//damage.start();

		//damage.destroy();
	}

	@Override
	protected float energyCost () {
		return 100;
	}

	@Override
	protected void onActiveUpdate() {
	}

	@Override
	protected void onActiveEnd() {
	}

	@Override
	protected float getCooldown() {
		return 120;
	}

	@Override
	protected float getActiveTime() {
		return 1;
	}

	@Override
	protected String getImage() {
		return null;
	}

	@Override
	protected ItemInfo makeItemInfo() {
		Stat keys[] = new Stat[] {Stat.NAME};
		String values[] = new String[] {"Solar Conduit"};
		return new ItemInfo(keys, values);
	}

	@Override
	public ItemType getType() {
		return ItemType.GARMINGISH;
	}
}
