package world.entity.item.gizmo;

import world.WorldType;
import world.effects.MobDamageInRadius;
import world.entity.item.ItemInfo;
import world.entity.item.Stat;

/**
 * A gizmo that damages all mobs around the player.
 *
 * @author Tony 300242775
 */
public class TeslaCoil extends AbstractGizmo {
	private static final float RADIUS = 50;
	private static final float DAMAGE = 400;

	@Override
	protected void onActivate() {
		MobDamageInRadius damage = new MobDamageInRadius(owner.getLocation(), RADIUS, DAMAGE);
		if(world.getWorldType() != WorldType.CLIENT) world.makeEffect(damage);
		//damage.start();

		//damage.destroy();
	}

	@Override
	protected void onActiveUpdate() {
	}

	@Override
	protected void onActiveEnd() {
	}

	@Override
	protected float energyCost () {
		return 80;
	}

	@Override
	protected float getCooldown() {
		return 30;
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
		String values[] = new String[] {"Tesla Coil"};
		return new ItemInfo(keys, values);
	}
}
