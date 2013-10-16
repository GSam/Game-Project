package world.entity.item.equippable;

import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;

import world.Inventory;
import world.Player;
import world.StatModification;
import world.World;
import world.entity.item.ItemType;

/**
 * AbstractWeapon defines a weapon that can be equipped and fired to damage
 * Mobs in the world.
 * 
 * @author Tony 300242775
 */
public abstract class AbstractWeapon extends AbstractEquippable implements Weapon {
	/**
	 * The AudioNode used to play fire sounds.
	 */
	protected AudioNode sound;
	
	/**
	 * Whether this weapon is currently firing.
	 */
	protected boolean firing;
	private float timeSinceFire = 0;
	
	@Override
	public void linkToWorld (World world, Vector3f location, int id) {
		super.linkToWorld(world, location, id);
		makeSound (getSoundPath());
	}
	
	@Override
	public void linkToInventory (World world, Inventory inventory, int id) {
		super.linkToInventory(world, inventory, id);
		makeSound (getSoundPath());
	}
	
	/**
	 * @return the file path of a sound file to play when firing this weapon
	 */
	protected abstract String getSoundPath ();
	
	@Override
	public EquipType getEquipType() {
		return EquipType.WEAPON;
	}

	@Override
	public abstract boolean attack(Player attacking);
	
	protected void makeSound (String soundPath) {
		sound = new AudioNode(world.getAssetManager(), soundPath, false);
		sound.setPositional(false);
	    sound.setLooping(false);
	    sound.setVolume(2);
	    world.getNode().attachChild(sound);
	}
	
	@Override
	public abstract float getFireSpeed ();
	
	@Override
	public boolean isUpdatable () {
		return true;
	}
	
	@Override
	public void update (float tpf) {
		if (!firing) return;
		
		timeSinceFire += tpf;
		
		if (timeSinceFire >= getFireSpeed()) {
			timeSinceFire = 0;
			firing = false;
		}
	}
	

	@Override
	protected abstract String getImage ();

	@Override
	protected abstract StatModification makeItemStats();
	
	@Override
	public ItemType getType() {
		return ItemType.SHOTGUN;
	}
}