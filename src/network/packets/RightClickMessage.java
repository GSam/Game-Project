package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message to be sent for a right click message. This is for any items such as
 * consumables or similar things which can be invoked with a right click.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class RightClickMessage extends GameUpdateMessage {
	public int itemID;
	public int player;
	
	public RightClickMessage() {
	}
	
	public RightClickMessage(int itemID, int player){
		super(true);
		this.itemID = itemID;
		this.player = player;
	}
	
	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}

}
