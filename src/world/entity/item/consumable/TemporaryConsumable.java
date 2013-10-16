package world.entity.item.consumable;

import world.Player;
import world.WorldType;
import world.effects.Effect;
import world.effects.TemporaryStatChange;
import world.entity.item.Stat;

/**
 * TemporaryConsumable is similar to SimpleConsumable in that it applies a direct
 * stat change to the consumer, however the TemporaryConsumable's change last for
 * a defined duration and is reverted at the end of that duration.
 * 
 * @author Tony 300242775
 */
public class TemporaryConsumable extends SimpleConsumable {
	private float effectLength;

	public TemporaryConsumable () {}

	/**
	 * @param meshPath the path to this item's in-world mesh
	 * @param infoStats an array of defined info stats
	 * @param infoValues the associated values of those info stats
	 * @param statStats an array of stats to apply a change to
	 * @param statValues the associated values to change the consumer's stats by
	 * @param duration the duration the stat change should last for
	 */
	public TemporaryConsumable(String meshPath, Stat[] infoStat, String[] infoValue, Stat[] stats, float[] amounts, float effectLength) {
		super(meshPath, infoStat, infoValue, stats, amounts);
		this.effectLength = effectLength;
	}

	@Override
	public void rightClick () {
		//ActorStats stats = ((Actor)getInventory().getOwner()).getStats();
		Player player = ((Player)getInventory().getOwner());

		getInventory().removeItem(this);

		Effect change = new TemporaryStatChange(getItemStats(), effectLength);
		player.applyEffect(change);
		if(world.getWorldType() != WorldType.CLIENT) world.removeEntity(this);
	}
}
