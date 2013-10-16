package world.entity.staticentity;


import world.Player;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;

/**
 * A simple door that opens using an animation.
 * @author Tony 300242775
 */
public class SimpleDoor extends AbstractDoor implements AnimEventListener {

    /**
     * for the channel call channel.setAnim("")
     * with "closed" for when the door is closed
     * with "close" for when you want to play the close animation
     * with "opened" for when the door is open
     * with "open" for when you want to play the open animation
     */
    private AnimControl control;
    private AnimChannel channel;

    public SimpleDoor () {}
    
    public SimpleDoor (String meshPath, Vector3f scale, float angle) {
    	super(meshPath, scale, angle);
    }
    
	@Override
	protected void onOpen(Player player) {
		isInAnimation = true;
		channel.setAnim("open");
	}

	@Override
	protected void onClose(Player player){
		isInAnimation = true;
	    channel.setAnim("close");
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		super.makeMesh(assetManager);
		
        this.control = geometry.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();
        channel.setAnim("closed");

        channel.setLoopMode(LoopMode.DontLoop);
        channel.setSpeed(1.4f);
	}

    @Override
    public void onAnimChange(AnimControl cont, AnimChannel chan, String animName){
    }

    /**
     * This is played when the animation is done, so if the animation is
     * open then we want to set the animation to open and do nothing else
     */
    @Override
    public void onAnimCycleDone(AnimControl cont, AnimChannel chan, String animName){
    	geometry.updateModelBound();
        if(animName.equals("open")){
            chan.setAnim("opened");
        }else if(animName.equals("close")){
            chan.setAnim("closed");
        }
        
        
        destroyPhysics (world.getPhysicsSpace());
		makePhysics(world.getPhysicsSpace());
    	isInAnimation = false;
    }
}
