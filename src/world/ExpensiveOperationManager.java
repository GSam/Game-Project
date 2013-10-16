package world;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import world.ai.AI;

/**
 * Some AI operations take a lot of time.
 * This class allocates ticks to expensive operations, to spread out lag spikes. 
 * 
 * It maintains both a queue of AIs that are waiting, and a set of waiting AIs
 * (so that no AI gets more than one queue slot)
 * 
 * @author Alex Campbell 300252131
 */
public class ExpensiveOperationManager {
	
	private Set<AI> queuedSet = new HashSet<AI>();
	private Queue<AI> queue = new LinkedList<AI>();
	
	private int nLeftThisTick = 1;
	private boolean hasDequeuedThisTick = false;
	
	public boolean canRun(AI ai) {
		if(queue.peek() == ai) {
			queuedSet.remove(ai);
			return true;
		}
		if(queue.peek() == null && nLeftThisTick > 0) {
			nLeftThisTick--;
			hasDequeuedThisTick = true;
			return true;
		}
		if(queuedSet.add(ai))
			queue.add(ai);
		return false;
	}

	public void update(float tpf) {
		if(!hasDequeuedThisTick) {
			queue.poll(); // AI that was scheduled didn't show up that tick
		}
		nLeftThisTick = 1;
		hasDequeuedThisTick = false;
	}

}
