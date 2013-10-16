package network.packets;

import network.interfaces.PacketHandler;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * The basic message from which all the other messages extend from. Each message
 * carries a time-stamp and whether or not it is reliable. Reliability indicates
 * use of UDP and TCP protocols. 
 * 
 * This message carries an accept method for use in the Visitor pattern.
 * 
 * Each message has the aim of being as minimal as possible. 
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public abstract class GameUpdateMessage extends AbstractMessage {
	public long timestamp;

	public GameUpdateMessage(boolean reliable) {
		super(reliable);
		timestamp = System.currentTimeMillis();
	}

	public GameUpdateMessage() {
	}

	public abstract void accept(PacketHandler visitor);
}
