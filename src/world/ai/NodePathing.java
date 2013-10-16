package world.ai;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import world.PathNode;
import world.entity.mob.Mob;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;

/**
 * A pathfinder that uses A* on the world's pathnode grid.
 * 
 * @author Alex Campbell 300252131
 */
public class NodePathing implements Pathfinder {
	@Override
	public world.ai.PathNode path(final Vector3f to, Mob mob) {
		for(PathNode pn : mob.getWorld().getPathNodes()) {
			pn.from = null;
		}
		
		class PQEntry {
			PathNode node;
			double cost;
			PathNode from;
			
			PQEntry(PathNode node, double cost, PathNode from) {
				this.node = node;
				this.cost = cost;
				this.from = from;
			}
		}
		
		final PathNode goalNode = mob.getWorld().getClosestPathNode(to);
		final PathNode startNode = mob.getWorld().getClosestPathNode(mob.getLocation());
		//if(startNode == null/* || startNode.position.distanceSquared(mob.getLocation()) > 50*/)
			//mob.getWorld().addPathNode(mob.getLocation());
		//if(goalNode == null)
			//mob.getWorld().addPathNode(to);
		if(goalNode == null || startNode == null)
			return null;
		
		PriorityQueue<PQEntry> pq = new PriorityQueue<PQEntry>(10, new Comparator<PQEntry>() {
			@Override
			public int compare(PQEntry n1, PQEntry n2) {
				double p1 = n1.node.position.distanceSquared(goalNode.position) + n1.cost;
				double p2 = n2.node.position.distanceSquared(goalNode.position) + n2.cost;
				if(p1 > p2) return 1;
				if(p1 < p2) return -1;
				return 0;
			}
		});
		
		pq.add(new PQEntry(startNode, 0, startNode));
		
		while(!pq.isEmpty()) {
			PQEntry n = pq.poll();
			if(n.node.from != null)
				continue;
			n.node.from = n.from;
			
			if(n.node == goalNode) {
				
				PathNode pn = n.node;
				
				List<Vector3f> path = new LinkedList<Vector3f>();
				while(pn.from != pn) {
					path.add(0, pn.position);
					pn = pn.from;
				}
				path.add(0, pn.position);
				
				world.ai.PathNode rv = new world.ai.PathNode();
				world.ai.PathNode cur = null;
				for(Vector3f v : path) {
					if(cur == null) {
						cur = rv;
					} else {
						world.ai.PathNode old = cur;
						cur = new world.ai.PathNode();
						old.next = cur;
					}
					cur.loc = v;
				}
				
				return rv;
			}
			for(PathNode n2 : n.node.neighbours)
				pq.add(new PQEntry(n2, n.cost + n2.position.distance(n.node.position), n.node));
		}
		
		return null;
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
	}
	@Override
	public void write(JmeExporter ex) throws IOException {
	}
}
