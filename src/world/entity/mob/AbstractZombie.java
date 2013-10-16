package world.entity.mob;

import world.ActorState;
import world.Entity;
import world.World;
import world.WorldType;
import world.entity.item.Stat;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * AbstractZombie is an implementation of Mob that unifies zombie animation and
 * timing code.
 * 
 * @author Tony 300242776
 */
public abstract class AbstractZombie extends Mob {
	private AudioNode sound;
	private AnimControl animControl;
	private AnimChannel animch_arms, animch_legs;
	
	private String walkAnim = null, attackAnim = null, armIdleAnim = null;
	
	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		super.linkToWorld(world, location, id);
		
		if(world.getWorldType() != WorldType.SERVER) {
			int n = 1+(int)(Math.random()*6);
			sound = new AudioNode(world.getAssetManager(), "Sounds/zombies/zombie_"+n+".wav", false);
			sound.setPositional(true);
		    sound.setLooping(false);
		    sound.setLocalTranslation(new Vector3f(0, 0, 0));
		    sound.setVolume(2);
		    ((Node)geometry).attachChild(sound);
		}
	}
	
	protected void initAnim(Spatial meshNode, String walkName, String attackName, String armIdleName) {
		walkAnim = walkName;
		attackAnim = attackName;
		armIdleAnim = armIdleName;
		animControl = meshNode.getControl(AnimControl.class);
		
		if(animControl == null)
			return;
		
		animch_arms = animControl.createChannel();
		animch_legs = animControl.createChannel();
		
		animch_arms.addFromRootBone(animControl.getSkeleton().getBone("upback"));
		animch_arms.setAnim(armIdleAnim);
		animch_arms.setLoopMode(LoopMode.Loop);
		animch_arms.setSpeed(1.0f);
		
		animch_legs.addFromRootBone(animControl.getSkeleton().getBone("thigh.l"));
		animch_legs.addFromRootBone(animControl.getSkeleton().getBone("thigh.r"));
		animch_legs.setAnim(walkAnim);
		animch_legs.setLoopMode(LoopMode.Loop);
		animch_legs.setSpeed(7.0f * getStats().getStat(Stat.SPEED));
	}
	
	//private float audioTimer = (float)Math.random()*10;
	
	private boolean hasAttackAnim = false;
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		boolean isAttacking = getState() == ActorState.ATTACKING;
		
		if(isAttacking != hasAttackAnim) {
			hasAttackAnim = isAttacking;
			animch_arms.setAnim(isAttacking ? attackAnim : armIdleAnim, 0.3f);
		}
		
		animch_legs.setSpeed(7.0f * getMobSpeed());
		
		Entity me = world.getPlayer();
		if(me != null && sound != null) {
			float audioInterval;
			
			float dist = me.getLocation().distance(getLocation());
			
			/*if(dist < 300) {
				audioInterval = dist * 0.1f;
				
				sound.setPitch(isAttacking && dist < 30 ? 2 : 1);
				
				audioTimer += tpf;
				if(audioTimer >= audioInterval) {
					audioTimer -= audioInterval - Math.random() + 0.5;
					sound.playInstance();
				}
			}*/
		}
		
	}
	
	@Override
	public void unlinkFromWorld(World world) {
		if(sound != null)
			sound.stop();
		super.unlinkFromWorld(world);
	}
}
