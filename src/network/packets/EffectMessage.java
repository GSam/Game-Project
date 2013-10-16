package network.packets;

import savefile.SaveUtils;
import world.World;
import world.effects.Effect;
import network.interfaces.PacketHandler;

import com.jme3.network.serializing.Serializable;

/**
 * Message to inform the client of an effect to be created in the world. Carries
 * data for the effect as a byte array.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class EffectMessage extends GameUpdateMessage {
	public byte[] data;
	
	public EffectMessage(){
	}

	public EffectMessage(Effect e) {
		super(true);
		this.data = SaveUtils.toBytes(e);
	}
	
	public Effect getEffect(World w) {
		return SaveUtils.fromBytes(data, Effect.class, w.getAssetManager());
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}
}
