package network;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.logging.Level;

import savefile.SaveUtils;

import network.packets.Packets;

import world.World;
import world.WorldType;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.kernel.KernelException;
import com.jme3.system.*;

/**
 * Main server application with its own game loop running. Runs head-less and
 * broadcasts positions of all players and mobs every 0.1 seconds.
 *
 * @author Garming Sam 300198721
 *
 */
public class ServerMain extends SimpleApplication {
	private Server server;
	private GameServer gameServer;
	private World world;
	private boolean spawn = true;

	// records the last time positions were broadcast and data was saved
	private long last = System.currentTimeMillis();
	private long lastSave = System.currentTimeMillis();

	/**
	 * Construct a server main application with default port.
	 *
	 * @throws IOException
	 */
	public ServerMain() throws IOException{
		this(6143);
	}

	/**
	 * Construct a server main application.
	 *
	 * @param port
	 *            port to use
	 * @throws IOException
	 */
	public ServerMain(int port) throws IOException{
		Packets.register();
		server = Network.createServer(port);
	}

	@Override
	public void simpleInitApp() {
		System.out.println(cam);

		// load game data if possible
		File f1 = new File("player-data.sav");
		File f2 = new File("server.sav");
		PlayerData playerData;
		if(f1.exists() && f2.exists()){
			world = SaveUtils.load(assetManager, f2, World.class);
			playerData = SaveUtils.load(assetManager, f1, PlayerData.class);
		} else {
			world = new World();
			playerData = new PlayerData();
		}

		world.attachToGame(stateManager, assetManager, listener, cam, WorldType.SERVER, viewPort);

		rootNode.attachChild(world.getNode());

		gameServer = new GameServer(server, world, this, playerData);
		server.addMessageListener(gameServer, Packets.classes);
		server.addConnectionListener(gameServer);

		// can't appear to catch this error anywhere else since this is asynchronous
		// JME doesn't appear to implement an error listener for server like clients do
		try {
			server.start();
			System.out.println("READY");
		} catch(KernelException e){
			if(e.getCause() instanceof BindException){
				System.out.println("Error starting server. Address in use.");
			} else {
				System.out.println("Unknown error occurred. Server halted.");
			}
		}

	}

	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
		ServerMain app;
		try {
			if(args != null && args.length == 1){
				try {
					int port = Integer.parseInt(args[0]);
					app = new ServerMain(port);
				} catch (NumberFormatException e) {
					app = new ServerMain();
				}
			} else {
				app = new ServerMain();
			}
			app.start(JmeContext.Type.Headless);
		} catch (IOException e) {
			System.out.println("Error starting server.");
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		server.close();
	}

	@Override
	public void simpleUpdate(float tpf) {
		// only tick the game if players are present
		if(gameServer.hasConnectedPlayers()){
			world.gameTick(tpf);
		}
		long temp = System.currentTimeMillis();
		if(temp - last > 100){
			//if (server.hasConnections()) {
			if(gameServer.hasConnectedPlayers()){
				gameServer.broadcastPlayerPosition();
				gameServer.updateMobs();
			}
			last = temp;
		}
		// save the game periodically every 5 minutes
		if(temp - lastSave > 300000){
			gameServer.save();
			lastSave = temp;
		}
	}

	/**
	 * Toggle spawns on the server.
	 */
	public void toggleSpawn() {
		spawn = !spawn;
		world.setEnableMobSpawning(spawn);
	}
}
