package world.entity.trigger;

import world.Entity;
import world.Player;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;

public class TriggerCollisionListener implements PhysicsCollisionListener {
	@Override
	public void collision(PhysicsCollisionEvent e) {
		Entity a = e.getNodeA().getUserData("entity");
		Entity b = e.getNodeB().getUserData("entity");
		
		if(a instanceof TriggerZone && b instanceof Player)
			((TriggerZone)a).trigger((Player)b);
		else if(a instanceof Player && b instanceof TriggerZone)
			((TriggerZone)b).trigger((Player)a);
	}
}
