package world.entity.staticentity;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import world.Activatable;
import world.Inventory;
import world.Lockable;
import world.Player;
import world.RigidEntity;
import world.World;
import world.entity.item.Item;
import world.entity.item.miscellaneous.Key;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * AbstractStaticLockedActivator represents any non-autonomous object (static is used in the
 * JMonkey sense of the word) that can be locked and activated.
 * 
 * An AbstractStaticLockedActivator can only be unlocked once (requiring a Key), and
 * after that can be opened and closed at will.
 * 
 * This class largely exists to implement the common ground between doors and chests,
 * which have very similar behaviour.
 * 
 * @author Tony 300242775
 */
public abstract class AbstractStaticLockedActivator extends RigidEntity implements Lockable, Activatable {
	/**
	 * Represents whether an AbstractStaticLockedActivator is locked or not. 
	 * @author Tony 300242775
	 */
	public enum LockedState {
		LOCKED, UNLOCKED
	};

	private boolean isOpen;
	/**
	 * True if this object is currently in an animation, false otherwise.
	 */
	protected boolean isInAnimation;
	private LockedState state = LockedState.LOCKED;
	private HashSet<Key> keys = new HashSet<Key>();
	private int[] savedKeyIDs = null;

	private String meshPath;
	
	/**
	 * The scale of this object.
	 */
	protected Vector3f scale;
	
	/**
	 * The angle in the XZ-plane which this object is rotated by.
	 */
	protected float angle;

	public AbstractStaticLockedActivator() {}

	public AbstractStaticLockedActivator(String meshPath, Vector3f scale, float angle) {
		this.angle = angle;
		this.scale = scale;
		this.meshPath = meshPath;
	}

	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		super.linkToWorld(world, location, id);

		if (savedKeyIDs != null) {
			for (int keyid : savedKeyIDs) {
				Key k = (Key) world.getEntityByID(keyid);
				if (k == null) throw new AssertionError("no key found with ID:" + keyid + " to open " + this);
				keys.add(k);
			}
			savedKeyIDs = null;
		}
	}

	/**
	 * @return the angle which this Entity is rotated by, in radians
	 */
	public float getAngle(){
		return angle;
	}

	/**
	 * @param angle the angle, in radians, to set the rotation of this Entity to
	 */
	public void setAngle(float angle){
		this.angle = angle;
	}

	@Override
	public void addKey(Key key) {
		keys.add(key);
	}

	@Override
	public Collection<Key> getKeys() {
		return keys;
	}

	@Override
	public boolean unlock(Inventory inventory) {
		if (keys.size() == 0) return true;

		for (Item item : inventory) {
			if (keys.contains(item)) {
				state = LockedState.UNLOCKED;
				onUnlock();
				return true;
			}
		}
		return false;
	}

	@Override
	public void activate(Player player) {
		if (isInAnimation) return;
		if (state == LockedState.UNLOCKED || (state == LockedState.LOCKED && unlock(player.getContainerInventory()))) {
			isOpen = !isOpen;
			if (isOpen) onOpen(player);
			else
				onClose(player);
		}
	}

	/**
	 * This method is guaranteed to be called when this AbstractStaticLockedActivator
	 * is opened.
	 * @param player the Player that opened this AbstractStaticLockedActivator
	 */
	protected abstract void onOpen(Player player);

	/**
	 * This method is guaranteed to be called when this AbstractStaticLockedActivator
	 * is closed.
	 * @param player the Player that closed this AbstractStaticLockedActivator
	 */
	protected abstract void onClose(Player player);

	/**
	 * This method is guaranteed to be called when this AbstractStaticLockedActivator
	 * is unlocked
	 * @param player the Player that unlocked this AbstractStaticLockedActivator
	 */
	protected void onUnlock() {
	}; // optional method

	@Override
	public void getPreloadAssets(Set<String> assets) {
		if(meshPath != null)
			assets.add(meshPath);
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		if (meshPath != null) {
			geometry = assetManager.loadModel(meshPath);
		} else {
			geometry = new Geometry ("abstract static locked activator", new Box(3, 3, 3));
	        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	        mat.setColor("Color", new ColorRGBA(0,1,0,1));
	        geometry.setMaterial(mat);
		}

        geometry.setLocalScale(scale);
        geometry.getLocalRotation().addLocal(new Quaternion().fromAngleNormalAxis(angle, Vector3f.UNIT_Y));
	}

	@Override
	protected float getWeight() {
		return 0;
	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
        InputCapsule c = arg0.getCapsule(this);

		state = LockedState.valueOf(c.readString("lockstate", null));
		savedKeyIDs = c.readIntArray("keys", null);
        scale = (Vector3f)c.readSavable("scale", null);
        angle = c.readFloat("angle", 0.222f);
        meshPath = c.readString("meshPath", null);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
        OutputCapsule c = arg0.getCapsule(this);

		c.write(state.name(), "lockstate", null);

		int[] keyIDs = new int[keys.size()];
		int k = 0;
		for (Key key : keys)
			keyIDs[k++] = key.getEntityID();

		c.write(keyIDs, "keys", null);

        c.write(scale, "scale", null);
        c.write(angle, "angle", 0.222f);
        c.write(meshPath, "meshPath", null);
	}
}
