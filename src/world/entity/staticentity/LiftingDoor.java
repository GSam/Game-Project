package world.entity.staticentity;

import java.io.IOException;

import world.Player;
import world.World;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * A door that moves upwards to open.
 * @author Alex Campbell 300252131
 */
public class LiftingDoor extends AbstractDoor {
private static final float SPEED = 0.5f; // seconds to open/close

    private int moveDir = -1;
    private float position; // 0 closed, 1 open
    private Vector3f posWhenClosed, posWhenOpen;

    public LiftingDoor () {}

    public LiftingDoor (String meshPath, Vector3f scale, float angle) {
    	super(meshPath, scale, angle);
    }

    @Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		InputCapsule c = arg0.getCapsule(this);
		moveDir = c.readInt("moveDir", 0);
		position = c.readFloat("doorpos", -1);
		posWhenClosed = (Vector3f)c.readSavable("closedpos", null);
		posWhenOpen = (Vector3f)c.readSavable("openpos", null);
	}

    @Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		OutputCapsule c = arg0.getCapsule(this);
		c.write(moveDir, "moveDir", 0);
		c.write(position, "doorpos", -1);
		c.write(posWhenClosed, "closedpos", null);
		c.write(posWhenOpen, "openpos", null);
	}

	@Override
	protected void onOpen(Player player) {
		moveDir = 1;
	}

	@Override
	protected void onClose(Player player) {
		moveDir = -1;
	}

	@Override
	public boolean isUpdatable() {
		return true;
	}

	@Override
	public void update(float tpf) {
		if(posWhenOpen == null) {
			posWhenOpen = physics.getPhysicsLocation();
			posWhenClosed = posWhenOpen.add(new Vector3f(0, -35, 0));
		}
		if(moveDir == 0) return;

		position += moveDir * tpf / SPEED;
		//position += tpf;
		if(position < 0) {moveDir = 0; position = 0;}
		if(position > 1) {moveDir = 0; position = 1;}

		physics.setPhysicsLocation(posWhenClosed.add(posWhenOpen.subtract(posWhenClosed).multLocal(position)));
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
	    geometry = assetManager.loadModel("door/door.mesh.xml");

        geometry.setLocalScale(10f);

        Spatial doorModel = geometry;
        Node transNode = new Node();
        Node scaleNode = new Node();

        doorModel.setLocalTranslation(-5.2f, 0, 0);
        transNode.attachChild(doorModel);
        scaleNode.attachChild(transNode);
        scaleNode.setLocalScale(scale);
        scaleNode.setLocalRotation(new Quaternion(new float[] {0, angle, 0}));

        geometry = scaleNode;
	}

	@Override
	protected void makePhysics(PhysicsSpace physicsSpace) {
		super.makePhysics(physicsSpace);
		if(!World.HOPPING_DOORS) {
			physics.setKinematic(true);
			physics.setKinematicSpatial(false);
		}
	}

	@Override
	protected float getWeight() {
		return 1;
	}
}
