package world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * PathNodes represents vertices in a graph used for the pathfinding of Mobs.
 *  
 * @author Alex Campbell 300252131
 */
public class PathNode implements Savable {
	public Vector3f position;
	public Collection<PathNode> neighbours = new HashSet<PathNode>();
	
	// used temporarily when pathfinding
	public PathNode from;
	
	public PathNode() {}
	
	private World world;
	private boolean isBlackList;
	private Set<String> specifiedIncomingIDs;
	private String id;
	
	/**
	 * Creates a PathNode.
	 * @param world The world.
	 * @param position The position of the node.
	 * @param isBlackList True if specifiedIncomingIDs is a blacklist. If false, it's a whitelist.
	 * @param specifiedIncomingIDs White- or black-list of nodes that cannot connect to this one (but this might still connect to those ones)
	 */
	public PathNode(String id, World world, Vector3f position, boolean isBlackList, Set<String> specifiedIncomingIDs) {
		this.position = position;
		this.isBlackList = isBlackList;
		this.specifiedIncomingIDs = specifiedIncomingIDs;
		this.world = world;
		this.id = id;
	}
	
	public void link() {
		for(PathNode o : world.getPathNodes()) {
			if(o != this && PhysicsUtilities.checkLineOfSight(position, o.position, world.getNode(), true)) {
				
				// add o to our neighbours set, if allowed
				boolean isBlocked = (o.specifiedIncomingIDs.contains(id) == o.isBlackList);
				if(!isBlocked) {
					boolean isReverseBlocked = (specifiedIncomingIDs.contains(o.id) == isBlackList);
					if(!isReverseBlocked) {
						neighbours.add(o);
						
						createDebugLine(world, position, o.position, isReverseBlocked);
					}
				}
			}
		}
	}
	
	private static void createDebugLine(World w, Vector3f p1, Vector3f p2, boolean isReverseBlocked) {
		Mesh m = new Mesh();
		m.setMode(Mesh.Mode.Lines);
		m.setBuffer(VertexBuffer.Type.Position, 3, new float[] {p1.x, p1.y, p1.z, p2.x, p2.y, p2.z});
		m.setBuffer(VertexBuffer.Type.Index, 2, new short[] {0, 1});
		m.updateBound();
		m.updateCounts();
		
		Geometry g = new Geometry();
		g.setMesh(m);
		g.setMaterial(new Material(w.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
		if(isReverseBlocked)
			g.getMaterial().setColor("Color", ColorRGBA.Red);
		w.getNode().attachChild(g);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		position = (Vector3f)c.readSavable("pos", null);
		neighbours = new HashSet<PathNode>(c.readSavableArrayList("adj", null));
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		c.write(position, "pos", null);
		c.writeSavableArrayList(new ArrayList<PathNode>(neighbours), "adj", null);
	}
}
