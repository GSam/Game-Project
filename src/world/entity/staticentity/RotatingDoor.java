package world.entity.staticentity;

import world.Player;
import world.World;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * A door that rotates about the vertical axis.
 * @author Alex Campbell 300252131
 */
public class RotatingDoor extends AbstractDoor {
	private static final float SPEED = 0.5f; // seconds to open/close 
    
    private int moveDir = 0;
    private float position; // 0 closed, 1 open
    private float panWhenClosed = (float)Math.PI/2, panWhenOpen = 0;
	
    public RotatingDoor () {}
    
    public RotatingDoor (String meshPath, Vector3f scale, float angle) {
    	super(meshPath, scale, angle);
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
		position += moveDir * tpf / SPEED;
		//position += tpf;
		if(position < 0) {moveDir = 0; position = 0;}
		if(position > 1) {moveDir = 0; position = 1;}
		
		Quaternion rotation = new Quaternion(new float[] {0, position * (panWhenOpen - panWhenClosed) + panWhenClosed + angle, 0});
		physics.setPhysicsRotation(rotation);
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
	    geometry = assetManager.loadModel("door/door.mesh.xml");
        
        geometry.setLocalScale(10f);
        
        Spatial doorModel = geometry;
        Node transNode = new Node();
        Node rotNode = new Node();
        
        doorModel.setLocalTranslation(-5.2f, 0, 0);
        transNode.attachChild(doorModel);
        rotNode.attachChild(transNode);
        rotNode.setLocalScale(scale);
        
        geometry = rotNode;
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
