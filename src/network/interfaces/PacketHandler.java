package network.interfaces;

import network.packets.*;

/**
 * Packet handler interface for handling packets on both client and server ends.
 *
 * Implements the visitor pattern. Every packet has a visit method and the
 * handler has a method to accept any type of message.
 *
 * @author Garming Sam 300198721
 *
 */
public interface PacketHandler {

	/**
	 * Handle move message. Server overwrites its data. Client partially
	 * overwrites its data.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(MoveMessage m);

	/**
	 * Handle attack message. Each side causes a player to trigger a primary
	 * action. Server rebroadcasts to everyone but the player sent from.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(AttackMessage m);

	/**
	 * Handle chat message. Client will just display the message. Server checks
	 * for any special calls like private messages and handles them
	 * appropriately.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(ChatMessage m);

	/**
	 * Handle effect message. Client and server simply play the effect.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(EffectMessage m);

	/**
	 * Handle player joining. Sets the client player to the player given in the
	 * message.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(PlayerJoinedMessage m);

	/**
	 * Handle ping message.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(PingMessage m);

	/**
	 * Handle an item transfer message between the world and an inventory.
	 * Server should rebroadcast.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(ItemTransferMessage m);

	/**
	 * Handle entity adding. Entities sent with these message are not added
	 * until the appropriate entity finish message is sent. Only the client
	 * should be handling these.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(AddEntityMessage m);

	/**
	 * Handle an entity add finish, meaning that the entities that should be
	 * added at this point should be added (on client).
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(AddEntityFinishMessage m);

	/**
	 * Handle a day night update. Sets the time to the given time on clients.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(DayNightMessage m);

	/**
	 * Handle removal of entities. Should not occur on server.
	 *
	 * @param m
	 */
	abstract void handleMessage(RemoveEntityMessage m);

	/**
	 * Handle a chest access. Based on response should the chest be hidden or
	 * displayed.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(ChestAccessMessage m);

	/**
	 * Handle an inventory to inventory transfer. Such messages should be
	 * rebroadcast for all the clients to hear.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(InventoryTransferMessage m);

	/**
	 * Handle a server save message sent from the client.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(ServerSaveMessage m);

	/**
	 * Handle an equip item message. Equips the item on the given player. Server
	 * may or may not rebroadcast.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(EquipItemMessage m);

	/**
	 * Handle a right click. Server should rebroadcast the right click to
	 * everyone else.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(RightClickMessage m);

	/**
	 * Handle an item activation. Server should rebroadcast the activation to
	 * everyone else.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(OnActivateMessage m);

	/**
	 * Handle a player setup, which should come before the player is joined.
	 * Player setup message are associated with the naming of the clients and
	 * the associated player entity id.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(PlayerSetupMessage m);

	/**
	 * Handle the game being won which is broadcast by the server.
	 *
	 * @param m
	 *            message
	 */
	abstract void handleMessage(GameWonMessage m);

	abstract void handleMessage(PlayerSpeedMessage m);
}
