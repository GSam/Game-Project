package world;

import java.io.IOException;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * An abstract implementation of Entity that handles boilerplate utility
 * methods.
 *
 * @author Tony 300242775
 */
public abstract class AbstractEntity implements Entity, Savable {
	protected Spatial geometry;
	protected boolean visible;
	protected World world;
	protected int entityID = -1;

	// WORLD LINK METHODS

	/**
	 * Initialises this entity with respect to the specified world.
	 */
	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		if (world == null)
			throw new NullPointerException("world cannot be null");
		if (location == null)
			throw new NullPointerException("location cannot be null");
		if (id < 0)
			throw new IllegalArgumentException("id cannot be negative");
		if (this.world != null)
			throw new IllegalStateException("Already linked to world");

		this.entityID = id;
		this.world = world;

		makeMesh(world.getAssetManager());
		if(geometry == null)
			throw new AssertionError("makeMesh didn't create geometry for "+this);
		setUserDataRecursive(geometry);
		//world.getNode().attachChild(geometry); now done per entity type
		attachToNode ();
		
		makePhysics(world.getPhysicsSpace());
		if(getPhysics() == null)
			throw new AssertionError("makePhysics didn't create physics for "+this);

		// must happen after physics creation
		setLocation (location);

		geometry.setLocalTranslation(location);

		// this is used for collision detection, when we can easily find the Geometry
		// but we need a way to get back to the actual object to properly interact with it.
		geometry.setUserData("entity", this);
	}

	/**
	 * Removes this entity from the specified world, including deconstructing
	 * its physcis. This method should not be called more than once on any
	 * instance of Entity.
	 */
	@Override
	public void unlinkFromWorld(World world) {
		if (world == null)
			throw new NullPointerException("world cannot be null");
		if (this.world == null)
			throw new IllegalStateException("Not linked to world");
		if (world != this.world)
			throw new IllegalArgumentException("not linked to specified world");

		if(getMesh().getParent() != null)
			removeFromNode ();
		if(getPhysics().getPhysicsSpace() != null)
			removeFromPhysicsSpace ();

		this.world = null;
	}

	/**
	 * Returns the world this entity has been linked to, or throws an IllegalStateException
	 * is this entity has not been linked to a world.
	 *
	 * @return the World this entity is linked to
	 */
	public World getWorld() {
		if (world == null)
			throw new IllegalStateException("Not linked to world");
		return world;
	}

	// GEOMETRY AND PHYSICS CREATORS AND GETTERS

	@Override
	public abstract PhysicsControl getPhysics();

	@Override
	public Spatial getMesh() {
		if(geometry == null) throw new IllegalStateException("geometry not initialised");
		return geometry;
	}

	/**
	 * Make a Geometry for this Entity and set it. Used internally,
	 * do not call (internally this method should only be called once
	 * per instance).
	 *
	 * @param assetManager the AssetManager to load a Geometry from
	 */
	protected abstract void makeMesh(AssetManager assetManager);

	/**
	 * Make a PhysicsControl for this Entity and set it. Used internally,
	 * do not call (internally this method should only be called once
	 * per instance).
	 *
	 * @param physicsSpace the PhysicsSpace to attach to
	 */
	protected abstract void makePhysics(PhysicsSpace physicsSpace);

	/**
	 * Destroy the physics control for this object, removing its physics
	 * presence from the game world. Used internally, do not call.
	 * 
	 * @param physicsSpace the physicsSpace to remove from
	 */
	protected abstract void destroyPhysics (PhysicsSpace physicsSpace);

	// LOCATION AND DIRECTION RELATED

	@Override
	public abstract Vector3f getLocation();

	@Override
	public abstract Vector3f getDirection();

	@Override
	public abstract void changeLocation(float dx, float dy, float dz);

	@Override
	public abstract void changeLocation(Vector3f change);

	@Override
	public void setLocation(float x, float y, float z) {
		setLocation(new Vector3f(x, y, z));
	}

	@Override
	public abstract void setLocation(Vector3f location);

	// GEOMETRY AND PHYSICS ATTACHING / DETACHING

	protected abstract Node getNodeToAttach ();

	@Override
	public void attachToNode () {
		getNodeToAttach().attachChild(geometry);
	}

	@Override
	public void removeFromNode() {
		if (geometry == null)
			return;
		if (geometry.getParent() == null)
			return;

		geometry.getParent().detachChild(geometry);
	}

	@Override
	public void removeFromPhysicsSpace () {
		if (getPhysics() == null) throw new IllegalStateException ("physics not initialised.");
		if (world == null) throw new IllegalStateException ("entity not linked to world.");
		//if (getPhysics().getPhysicsSpace() == null) throw new IllegalStateException("not added to physics space");

		if (getPhysics().getPhysicsSpace() != null)
			world.getPhysicsSpace().remove(getPhysics());
	}

	@Override
	public void addToPhysicsSpace () {
		if (getPhysics() == null) throw new IllegalStateException ("physics not initialised.");
		if (world == null) throw new IllegalStateException ("entity not linked to world.");
		if (getPhysics().getPhysicsSpace() != null) throw new IllegalStateException("already added to physics space");

		world.getPhysicsSpace().add(getPhysics());
	}

	// OTHER

	@Override
	public abstract void update(float tpf);

	// HELPER AND SAVE/LOAD METHODS

	@Override
	public int getEntityID() {
		if(entityID == -1) throw new IllegalStateException("Entity ID not set");
		return entityID;
	}

	private void setUserDataRecursive(Spatial s) {
		if(s instanceof Node)
			for(Spatial t : ((Node)s).getChildren())
				setUserDataRecursive(t);
		s.setUserData("entity", this);
	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
		InputCapsule ic = arg0.getCapsule(this);
		visible = ic.readBoolean("visible", false);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		OutputCapsule oc = arg0.getCapsule(this);
		oc.write(visible, "visible", false);
	}

	@Override
	public boolean isImmovableEntity() {
		return false;
	}

	/**
	 * This method is called before the entity is removed from the world.
	 */
	@Override
	public void onDestroy() {
	}
	
	@Override
	public String toString() {
		return getClass().getName()+":"+entityID;
	}
	
	@Override
	public boolean isUpdatable() {
		return false;
	}

	@Override
	public boolean isRemovedFromWorld() {
		return world == null;
	}
}
