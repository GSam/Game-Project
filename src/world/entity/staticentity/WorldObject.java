package world.entity.staticentity;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import world.PathNode;
import world.RigidEntity;
import world.entity.trigger.SpawnZombieAction;
import world.entity.trigger.TriggerZone;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

/**
 * WorldObject is a simple, immovable, RigidEntity that cannot be interacted with. It provides a
 * simple way to load static assets into the world, such as houses or the terrain.
 * @author scott
 */
public class WorldObject extends RigidEntity {
    private String meshfile;
    private Vector3f scale;
    private boolean cull = true;
    private float angle;

    public WorldObject() {}

    public WorldObject(String meshfile, Vector3f vecScale, float scale, float angle, boolean cull){
        this.meshfile = meshfile;
        this.scale = (vecScale != null ? vecScale : new Vector3f(scale, scale, scale));
        this.cull = cull;
        this.angle = angle;
    }

    @Override
	public void getPreloadAssets(Set<String> assets) {
    	assets.add(meshfile);
	}

	@Override
    protected void makeMesh(AssetManager assetManager){
        geometry = assetManager.loadModel(meshfile);
        geometry.setLocalScale(scale);
        geometry.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(angle, Vector3f.UNIT_Y));
        
        deleteSpecialNodes();

        ((Node)geometry).depthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                if(spatial instanceof LightNode)
                    spatial.getParent().detachChild(spatial);
                if(!cull && spatial instanceof Geometry){
                    ((Geometry)spatial).getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
                }

            }
        });
    }

    @Override
    protected void makePhysics(PhysicsSpace physicsSpace){
        if(world.USE_SUPERFAST_PHYSICS)
            physics = new RigidBodyControl(CollisionShapeFactory.createBoxShape(geometry), 0);
        else
            physics = new RigidBodyControl(getWeight());
        geometry.addControl(physics);
        physicsSpace.add(physics);
    }

    @Override
    public boolean isImmovableEntity() {
        return true;
    }

    @Override
    public void read(JmeImporter arg0) throws IOException {
        super.read(arg0);
        InputCapsule c = arg0.getCapsule(this);
        meshfile = c.readString("meshfile", null);
        scale = (Vector3f)c.readSavable("scale", null);
        cull = c.readBoolean("cull", true);
        angle = c.readFloat("angle", 0.222f);
    }

    @Override
    public void write(JmeExporter arg0) throws IOException {
        super.write(arg0);
        OutputCapsule c = arg0.getCapsule(this);
        c.write(meshfile, "meshfile", null);
        c.write(scale, "scale", null);
        c.write(cull, "cull", true);
        c.write(angle, "angle", 0.222f);
    }

    @Override
    protected float getWeight() {
        return 0;
    }
    
    public float getAngle(){
        return angle;
    }
    
    public void setAngle(float angle){
        this.angle = angle;
    }
    
    private void deleteSpecialNodes() {
    	for(Spatial s : ((Node)geometry).descendantMatches("pathnode.*"))
    		s.removeFromParent();
    	for(Spatial s : ((Node)geometry).descendantMatches("trigger.*"))
    		s.removeFromParent();
    	for(Spatial s : ((Node)geometry).descendantMatches("spawner.*"))
    		s.removeFromParent();
    }

	public void addSpecialNodes() {
		Node tempCopy = (Node)world.getAssetManager().loadModel(meshfile);
		tempCopy.setLocalScale(geometry.getLocalScale());
		tempCopy.setLocalRotation(geometry.getLocalRotation());
		tempCopy.setLocalTranslation(geometry.getLocalTranslation());
		geometry.getParent().attachChild(tempCopy);
        
        for(Spatial s : tempCopy.descendantMatches("pathnode.*")){
            if(world.getUsePathNodes()){
            	
            	String id = null;
            	boolean isBlackList = true;
            	// either a black- or white-list of connected IDs, depending on isBlackList
            	Set<String> specifiedIncomingIDs = new HashSet<String>();
            	
            	if(s.getName().contains("?")) {
            		String data = s.getName().substring(s.getName().indexOf('?')+1);
            		
            		if(!data.contains("=")) {
            			id = data;
            		} else {
            			id = data.substring(0, data.indexOf('='));
            			data = data.substring(data.indexOf('=') + 1);
            			
            			if(isBlackList = data.startsWith("!")) {
            				data = data.substring(1);
            			}
            			
            			for(String sid : data.split(","))
            				specifiedIncomingIDs.add(sid);
            		}
            		
            	} else {
            		// no extra info in the name
            		id = null;
            	}
            	
        		world.addPathNode(new PathNode(id, world, s.getWorldTranslation(), isBlackList, specifiedIncomingIDs));
            }
            s.getParent().detachChild(s); // don't care about pathnode nodes
                                          // after we've taken their locations
        }
		
		Map<String, TriggerZone> tzones = new HashMap<String, TriggerZone>();
		
		for(Spatial s : tempCopy.descendantMatches("trigger.*\\?.*")) {
			
			TriggerZone tz = new TriggerZone(s);
			world.addEntity(tz, s.getWorldTranslation());
			
			String name = s.getName().substring(s.getName().indexOf('?')+1);
			tzones.put(name, tz);
		}
		
    	for(Spatial s : tempCopy.descendantMatches("spawner.*")) {
    		String name = s.getName().substring(s.getName().indexOf('?')+1);
			TriggerZone tz = tzones.get(name);
			if(tz == null)
				throw new AssertionError("mapping error: no trigger zone '"+name+"' for spawner node named '"+s.getName()+"'");
			tz.addAction(new SpawnZombieAction(s.getWorldTranslation()));
    	}
    	
    	tempCopy.removeFromParent();
	}

}
