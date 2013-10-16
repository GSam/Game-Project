package world.ai;

import java.io.IOException;

import world.World;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;

/**
 * This is a simple savable linked list of locations.
 * Note that this is different from world.PathNode
 * 
 * @author Tony 300242775
 */
public class PathNode implements Savable {
	public Vector3f loc;
	public PathNode next;
	
	public PathNode() {}

	public PathNode(Vector3f loc, PathNode next, World world) {
		this.loc = loc;
		this.next = next;
	}
	
	@Override
	public void read(JmeImporter arg0) throws IOException {
		InputCapsule ic = arg0.getCapsule(this);
		loc = (Vector3f)ic.readSavable("loc", null);
		next = (PathNode)ic.readSavable("next", null);
	}
	
	@Override
	public void write(JmeExporter arg0) throws IOException {
		OutputCapsule oc = arg0.getCapsule(this);
		oc.write(loc, "loc", null);
		oc.write(next, "next", null);
	}
}