package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message to be sent when a player wishes to join the game with a name.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class PlayerSetupMessage extends GameUpdateMessage {
	public int conID;
	public String name;
	
	public PlayerSetupMessage() {
	}
	
	public PlayerSetupMessage(int conID, String name){
		super(true);
		this.conID = conID;
		this.name = name;
	}
	
	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}

}
