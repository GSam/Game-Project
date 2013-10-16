package world;

import java.io.IOException;

import world.effects.Effect;
import world.entity.item.Stat;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;

/**
 * An Actor represents some living being in the game world. It uses CharacterControl for physics in order
 * to achieve a simpler and more realistic implementation of a living being moving in the world.
 *
 * @author Tony 300242775
 */
@SuppressWarnings("deprecation")
public abstract class Actor extends AbstractEntity {
	private boolean movingLeft;
	private boolean movingRight;
	private boolean movingDown;
	private boolean movingUp;
	private boolean strafeLeft;
	private boolean strafeRight;

	/**
	 * Represents this Actor's stats, including their health and energy.
	 */
	protected ActorStats stats;

	/**
	 * Encodes this Actor's state for animation purposes.
	 */
	protected ActorState state = ActorState.STANDING;

	/**
	 * This Actor's physics control, which is guaranteed to be
	 * linked to its geometry if it exists but is not guaranteed
	 * to not be null.
	 */
	protected ForceCharacterControl physics;

	/**
	 * An auxiliary physics control linked to this Actor's geometry
	 * that does not change the behaviour of the Actor but monitors
	 * collisions (ie. does not react to collisions). This is used
	 * to create collisions between Actors, which JMonkey does not
	 * support by default.
	 */
	protected PhysicsControl ghost;

	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		stats = makeStats (world);
		super.linkToWorld(world, location, id);
	}

	// LOCATION AND DIRECTION METHODS

	@Override
	public Vector3f getLocation() {
		if (physics == null) throw new IllegalStateException ("physics accessed before being initialised");
		return physics.getPhysicsLocation();
		//return geometry.getLocalTranslation();
		//return geometry.getWorldTranslation();
	}

	@Override
	public void setLocation(Vector3f location) {
		if(location == null) throw new IllegalArgumentException("actors cannot have null location");
		if (physics == null) throw new IllegalStateException ("physics accessed before being initialised");
		//physics.setPhysicsLocation(location);
		physics.warp(location);
	}

	@Override
	public void changeLocation(Vector3f location) {
		if (physics == null) throw new IllegalStateException ("physics accessed before being initialised");
		Vector3f change = getLocation().add(location);
		setLocation(change);
		//geometry.setLocalTranslation(change);
	}

	@Override
	public Vector3f getDirection () {
		return physics.getViewDirection();
	}

	@Override
	public void changeLocation(float x, float y, float z) {
		if (physics == null) throw new IllegalStateException ("physics accessed before being initialised");
		changeLocation (new Vector3f(x,y,z));
	}

	// WALK DIRECTION METHODS

	/**
	 * Sets the walk direction of this Actor to the provided vector.
	 * @param direction the vector to walk in
	 */
	public void setWalkDirection (Vector3f direction) {
		System.out.println(direction);
		physics.setWalkDirection(direction);
	}

	/**
	 * @return the current walking direction of this Actor
	 */
	public Vector3f getWalkDirection () {
		return physics.getWalkDirection();
	}

	/**
	 * Sets the direction this Actor is facing to the provided vector.
	 * @param direction the direction to make the Actor face
	 */
	public void setDirection(Vector3f direction) {
		physics.setViewDirection(direction);
	}

	// GEOMETRY AND PHYSICS

	@Override
	public ForceCharacterControl getPhysics(){
		if (physics == null) throw new IllegalStateException ("physics accessed before being initialised");
		return physics;
	}

	@Override
	public void removeFromPhysicsSpace () {
		super.removeFromPhysicsSpace();
		world.getPhysicsSpace().remove(ghost);
	}


	private PhysicsTickListener physicsTickListener = new PhysicsTickListener() {

		@Override
		public void prePhysicsTick(PhysicsSpace arg0, float arg1) {
			// if an actor falls through the world, teleport it to spawn
			if(getLocation().y < -1000){
				if(getWorld().getWorldType() != WorldType.CLIENT || Actor.this == getWorld().getPlayer())
					setLocation(getWorld().getSpawnPoint());
			}
		}

		@Override
		public void physicsTick(PhysicsSpace arg0, float arg1) {

		}
	};

	@Override
	protected void makePhysics(PhysicsSpace physicsSpace) {
		// intialise the real physics control
		CollisionShape cs = new CapsuleCollisionShape(1.5f,5f,1);
		physics = new ForceCharacterControl(cs,4);
		//physics.setGravity(-200f);
		physics.setGravity(200f);
		physics.setFallSpeed(200f);
		physics.setJumpSpeed(50f);
		geometry.addControl(physics);

		// create the ghost control
		ghost = new GhostControl(cs);
		geometry.addControl(ghost);

		// put the ghost physics in a different group actor don't collide with themselves
		geometry.getControl(GhostControl.class).setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_07);
		geometry.getControl(GhostControl.class).removeCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
		geometry.getControl(GhostControl.class).addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_07);

		physicsSpace.add(ghost);
		physicsSpace.add(physics);

		physicsSpace.addTickListener(physicsTickListener);
	}

	@Override
	protected void destroyPhysics (PhysicsSpace physicsSpace) {
		removeFromPhysicsSpace();
		physicsSpace.removeTickListener(physicsTickListener);
		geometry.removeControl(physics);
		physics = null;
	}

	@Override
	protected abstract void makeMesh(AssetManager assetManager);

	// STATISTICS AND BEHAVIOUR

	/**
	 * Create an ActorStats object representing the statistics of this Actor and return it.
	 * This method should only be called once on any instance of Actor.
	 * @param world TODO
	 * @return an ActorStats object representing the statistics of this Actor
	 */
	protected abstract ActorStats makeStats (World world);

	/**
	 * @return an ActorStats object representing the statistics of this Actor
	 */
	public ActorStats getStats () {
		return stats;
	}

	@Override
	public abstract void update(float tpf);

	// ACTOR SPECIFIC

	/**
	 * Apply the passed Effect to this Actor as necessary, subject to
	 * conditions on this Actor.
	 *
	 * @param effect the Effect to apply
	 */
	public void applyEffect (Effect effect) {
		if(world == null)
			return; // most likely this actor died

		effect.apply(this);

		// If the effect kills the entity, then don't remove if you're the client
		if(world.getWorldType() != WorldType.CLIENT){
			if (stats.getStat(Stat.HEALTH) <= 0) {
				world.removeEntity(this);
			}
		}
	}

	/**
	 * @return a float representing the radius of a bounding-cylinder around this Actor
	 */
	public float getRadius () {
		return 2f;
	}

	// MOVEMENT

	/**
	 * @return true if the Actor is moving left, false otherwise
	 */
	public boolean isMovingLeft(){
		return movingLeft;
	}

	/**
	 * Set whether this Actor is moving left.
	 * @param movingLeft whether this character is moving left
	 */
	public void setMovingLeft(boolean movingLeft){
		this.movingLeft = movingLeft;
	}

	/**
	 * @return true if the Actor is moving right, false otherwise
	 */
	public boolean isMovingRight(){
		return movingRight;
	}

	/**
	 * Set whether this Actor is moving right.
	 * @param movingRight whether this character is moving right
	 */
	public void setMovingRight(boolean movingRight){
		this.movingRight = movingRight;
	}

	/**
	 * @return true if the Actor is moving down (backward), false otherwise
	 */
	public boolean isMovingDown(){
		return movingDown;
	}

	/**
	 * Set whether this Actor is moving down.
	 * @param movingDown whether this character is moving down
	 */
	public void setMovingDown(boolean movingDown){
		this.movingDown = movingDown;
	}

	/**
	 * @return true if the Actor is moving up (forward), false otherwise
	 */
	public boolean isMovingUp(){
		return movingUp;
	}

	/**
	 * Set whether this Actor is moving up.
	 * @param movingUp whether this character is moving up
	 */
	public void setMovingUp(boolean movingUp){
		this.movingUp = movingUp;
	}

	/**
	 * Set whether this Actor is strafing right.
	 * @param strafeRight whether this character is strafing right
	 */
	public void setStrafeRight(boolean strafeRight){
		this.strafeRight = strafeRight;
	}

	/**
	 * Set whether this Actor is strafing left.
	 * @param strafeRight whether this character is strafing left
	 */
	public void setStrafeLeft(boolean strafeLeft){
		this.strafeLeft = strafeLeft;
	}

	/**
	 * @return true if the Actor is strafing left, false otherwise
	 */
	public boolean isStrafeLeft(){
		return this.strafeLeft;
	}

	/**
	 * @return true if the Actor is strafing right, false otherwise
	 */
	public boolean isStrafeRight(){
		return this.strafeRight;
	}

	// ANIMATION

	/**
	 * @return the current ActorState of this Actor
	 */
	public ActorState getState(){
		return state;
	}

	/**
	 * Sets the current ActorState of this Actor to the passed ActorState
	 * and performs any necessary animation changes because of that.
	 * @param state
	 */
	public void setState (ActorState state) {
		this.state = state;
		onStateChange ();
	}

	/**
	 * This method is guaranteed to be called when this Actor's state
	 * changes.
	 */
	protected abstract void onStateChange ();

	// SAVING AND LOADING

	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		InputCapsule ic = arg0.getCapsule(this);
		movingLeft = ic.readBoolean("mleft", false);
		movingRight = ic.readBoolean("mright", false);
		movingUp = ic.readBoolean("mup", false);
		movingDown = ic.readBoolean("mdown", false);
		strafeLeft = ic.readBoolean("sleft", false);
		strafeRight = ic.readBoolean("sright", false);
		//health = ic.readInt("health", 0);
		state = ActorState.valueOf(ic.readString("state", null));
		stats = (ActorStats)ic.readSavable("stats", null);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);

		OutputCapsule oc = arg0.getCapsule(this);
		oc.write(movingLeft, "mleft", false);
		oc.write(movingRight, "mright", false);
		oc.write(movingUp, "mup", false);
		oc.write(movingDown, "mdown", false);
		oc.write(strafeLeft, "sleft", false);
		oc.write(strafeRight, "sright", false);
		//oc.write(health, "health", 0);
		oc.write(state.name(), "state", null);
		oc.write(stats, "stats", null);
	}

	/**
	 * True if this actor is invisible to zombies.
	 * Only used for players.
	 */
	public boolean isInvisible() {
		return false;
	}
}