package world.entity.item.gizmo;

import java.util.Set;

import world.Player;
import world.StatModification;
import world.entity.item.Item;
import world.entity.item.ItemInfo;
import world.entity.item.ItemType;
import world.entity.item.RightClickable;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;

/**
 * AbstractGizmo represents a technological gizmo that can be right clicked in
 * a Player's inventory to have some helpful effect.
 * 
 * @author Tony 300242775
 */
public abstract class AbstractGizmo extends Item implements RightClickable {
	private float timeSinceActive;
	private float timeActive;
	private boolean canActivate = true;
	private boolean isActive;
	
	/**
	 * The Player who has this gizmo in their Inventory.
	 */
	protected Player owner;
	
	@Override
	public void rightClick() {
		owner = (Player) inventory.getOwner();
		float energy = owner.getStats().getStat(Stat.ENERGY);
		if (!canActivate || energy < energyCost()) return;
		
		isActive = true;
		canActivate = false;
		

		onActivate ();
		
		owner.getStats().modStat(Stat.ENERGY, -energyCost ());
	}
	
	/**
	 * This method is guaranteed to be called when the gizmo is activated.
	 */
	protected abstract void onActivate ();
	
	@Override
	public void update (float tpf) {
		if (isActive) {
			timeActive += tpf;
			onActiveUpdate ();

			if (timeActive >= getActiveTime()) {
				isActive = false;
				timeActive = 0;
				canActivate = false;
				onActiveEnd ();
			}
		}

		if (canActivate) return;

		timeSinceActive += tpf;
		if (timeSinceActive >= getCooldown()) {
			timeSinceActive = 0;
			canActivate = true;
		}
	}
	
	/**
	 * @return the amount of energy this gizmo costs to activate
	 */
	protected abstract float energyCost ();
	
	/**
	 * This method is guaranteed to be called on each update tick
	 * while this gizmo is active.
	 */
	protected abstract void onActiveUpdate ();
	
	/**
	 * This method is guaranteed to be called when this gizmo
	 * stops being active.
	 */
	protected abstract void onActiveEnd ();
	
	/**
	 * @return the minimum time between activation of this gizmo, in seconds
	 */
	protected abstract float getCooldown ();
	
	/**
	 * @return the time this gizmo is active for, in seconds
	 */
	protected abstract float getActiveTime ();
	
	@Override
	public void getPreloadAssets(Set<String> assets) {
		assets.add("torch/torch.scene");
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = assetManager.loadModel("torch/torch.scene");
	}

	@Override
	protected abstract String getImage();

	@Override
	protected StatModification makeItemStats() {
		Stat keys[] = new Stat[] {};
		float values[] = new float[] {};
		return new StatModification(keys, values);
	}

	@Override
	protected abstract ItemInfo makeItemInfo();
	
	@Override
	public boolean isUpdatable() {
		return true;
	}
	
	@Override
	public ItemType getType() {
		return ItemType.GIZMO;
	}
}
