package world;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.math.Vector3f;

/**
 * ActorCollisionListener interacts with the PhysicsSpace of a World to
 * produce the bumping effect between Actors.
 * 
 * @author Tony 300242775
 */
public class ActorCollisionManager implements PhysicsCollisionListener{
	private static final float PUSHBACK = 0.5f;
	
	@Override
	public void collision(PhysicsCollisionEvent pce) {
		if ( !(pce.getNodeA().getUserData("entity") instanceof Actor && pce.getNodeB().getUserData("entity") instanceof Actor) ) return;
		
		Actor first = (Actor) (pce.getNodeA().getUserData("entity"));
		Actor second = (Actor) (pce.getNodeB().getUserData("entity"));
		
		if(first instanceof Player && second instanceof Player)
			return;
		
		if(second instanceof Player)
			playerCollision(first, (Player)second, pce);
		else if(first instanceof Player)
			playerCollision((Player)first, second, pce);
		else
			mobCollision(first, second, pce);
	}
	
	private static void playerCollision(Player a, Actor b, PhysicsCollisionEvent e) {
		b.changeLocation(e.getPositionWorldOnA().subtract(e.getPositionWorldOnB()));
	}
	
	private static void playerCollision(Actor a, Player b, PhysicsCollisionEvent e) {
		a.changeLocation(e.getPositionWorldOnB().subtract(e.getPositionWorldOnA()));
	}
	
	private static void mobCollision(Actor a, Actor b, PhysicsCollisionEvent e) {
		Vector3f aOffset = e.getPositionWorldOnB().subtract(e.getPositionWorldOnA()).multLocal(PUSHBACK);
		a.changeLocation(aOffset);
		b.changeLocation(aOffset.multLocal(-1));
	}
}
