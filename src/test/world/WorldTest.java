package test.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jme3.math.Vector3f;

import static org.junit.Assert.*;
import world.Entity;
import world.EntitySpawnData;
import world.Player;
import world.World;
import world.WorldType;
import world.entity.item.miscellaneous.Key;

/**
 * @author Alex Campbell 300252131
 */
public class WorldTest extends GameTest {
	@Before public void before() {start();}
	@After public void after() {stop();}
	
	private Entity makeTestEntity() {
		return new Key("Models/key/key.blend", "test entity");
	}
	
	private class TestWorldObserver extends network.interfaces.WorldObserver {
		List<Entity> added = new ArrayList<Entity>();
		List<Entity> removed = new ArrayList<Entity>();
		
		@Override
		public void onAddEntity(World w, Entity e) {
			assertSame(w, world);
			added.add(e);
		}
		@Override
		public void onRemoveEntity(World w, Entity e) {
			assertSame(w, world);
			removed.add(e);
		}
	}
	
	public WorldTest() {
		super(WorldType.SP);
	}
	
	@Test
	public void testGetWorldType() {
		assertTrue(world.getWorldType() == WorldType.SP);
	}
	
	@Test
	public void testNothing() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				return null;
			}
		});
	}
	
	@Test
	public void testAddEntityNull() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				
				for(Entity e : world.getEntitiesOfClass(Entity.class))
					world.removeEntity(e);
				
				assertTrue(world.getEntities().size() == 0);
				
				try {
					world.addEntity(new Player(), null);
					fail("addEntity with null location should throw NPE");
				} catch(NullPointerException e) {
					assertTrue(world.getEntities().size() == 0);
				}
				
				try {
					world.addEntity(null, getRandomVector());
					fail("addEntity with null entity should throw NPE");
				} catch(NullPointerException e) {
					assertTrue(world.getEntities().size() == 0);
				}
				
				
				return null;
			}
		});
	}
	
	@Test
	public void testAddEntities() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for(Entity e : world.getEntitiesOfClass(Entity.class))
					world.removeEntity(e);
				
				assertTrue(world.getEntities().size() == 0);
				
				Entity e1=makeTestEntity(), e2=makeTestEntity(), e3=makeTestEntity();
				
				Collection<EntitySpawnData> es = new ArrayList<EntitySpawnData>();
				es.add(new EntitySpawnData(e1, Vector3f.ZERO, 0));
				es.add(new EntitySpawnData(e2, Vector3f.ZERO, 1));
				es.add(new EntitySpawnData(e3, Vector3f.ZERO, 2));
				
				world.addEntities(es);
				
				assertTrue(world.getEntities().size() == 3);
				assertTrue(world.getEntities().contains(e1));
				assertTrue(world.getEntities().contains(e2));
				assertTrue(world.getEntities().contains(e3));
				
				es.clear();
				es.add(new EntitySpawnData(e1));
				
				try {
					world.addEntities(es);
					fail("duplicate entity in addEntities should throw ISE");
				} catch(IllegalStateException e) {
					assertTrue(world.getEntities().size() == 3);
				}
				
				es.clear();
				es.add(new EntitySpawnData(new Player(), Vector3f.ZERO, 0));
				
				try {
					world.addEntities(es);
					fail("duplicate ID in addEntities should throw ISE");
				} catch(IllegalStateException e) {
					assertTrue(world.getEntities().size() == 3);
				}
				
				return null;
			}
		});
	}
	
	@Test
	public void testAddEntityNextIDInUse() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				
				for(Entity e : world.getEntitiesOfClass(Entity.class))
					world.removeEntity(e);
				
				assertTrue(world.getEntities().size() == 0);
				
				Entity eProbe = makeTestEntity();
				world.addEntity(eProbe, Vector3f.ZERO);
				
				// predict the next entity ID, and spawn an entity with that ID without using addEntity
				int nextID = eProbe.getEntityID() + 1;
				Collection<EntitySpawnData> es = new ArrayList<EntitySpawnData>();
				es.add(new EntitySpawnData(makeTestEntity(), Vector3f.ZERO, nextID));
				world.addEntities(es);
				
				// addEntity should allocate the next unused ID
				Entity eTest = makeTestEntity();
				world.addEntity(eTest, Vector3f.ZERO);
				assertTrue(eTest.getEntityID() == nextID + 1);
				
				return null;
			}
		});
	}
	@Test
	public void testAddAndRemoveEntity() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for(Entity e : world.getEntitiesOfClass(Entity.class))
					world.removeEntity(e);
				
				TestWorldObserver obs;
				world.addObserver(obs = new TestWorldObserver());
				
				Entity e1=makeTestEntity(), e2=makeTestEntity(), e3=makeTestEntity();
				Vector3f v1 = getRandomVector(), v2 = getRandomVector(), v3 = getRandomVector();
				
				assertTrue(world.getEntities().size() == 0);
				
				world.addEntity(e1, v1);
				assertTrue(world.getEntities().contains(e1));
				assertTrue(world.getEntities().size() == 1);
				world.addEntity(e2, v2);
				assertTrue(world.getEntities().contains(e1));
				assertTrue(world.getEntities().contains(e2));
				assertTrue(world.getEntities().size() == 2);
				world.addEntity(e3, v3);
				assertTrue(world.getEntities().contains(e1));
				assertTrue(world.getEntities().contains(e2));
				assertTrue(world.getEntities().contains(e3));
				assertTrue(world.getEntities().size() == 3);
				
				try {
					world.addEntity(e3, Vector3f.ZERO);
					fail("duplicate addEntity should throw ISE");
				} catch(IllegalStateException e) {
					assertTrue(world.getEntities().size() == 3);
				}
				
				assertEquals(obs.added, Arrays.asList(e1, e2, e3));
				assertEquals(obs.removed, Arrays.asList());
				obs.added.clear();
				
				assertEquals(e1.getLocation(), v1);
				assertEquals(e2.getLocation(), v2);
				assertEquals(e3.getLocation(), v3);
				
				assertTrue(world.getEntities().size() == 3);
				world.removeEntity(e2);
				assertTrue(world.getEntities().size() == 2);
				assertTrue(world.getEntities().contains(e1));
				assertTrue(!world.getEntities().contains(e2));
				assertTrue(world.getEntities().contains(e3));
				
				assertSame(e1, world.getEntityByID(e1.getEntityID()));
				assertSame(e3, world.getEntityByID(e3.getEntityID()));
				assertNull(world.getEntityByID(e2.getEntityID()));
				
				world.removeEntity(e1);
				assertTrue(world.getEntities().size() == 1);
				world.removeEntity(e3);
				assertTrue(world.getEntities().size() == 0);
				
				assertEquals(obs.removed, Arrays.asList(e2, e1, e3));
				assertEquals(obs.added, Arrays.asList());
				obs.removed.clear();
				
				assertNull(world.getEntityByID(e1.getEntityID()));
				assertNull(world.getEntityByID(e2.getEntityID()));
				assertNull(world.getEntityByID(e3.getEntityID()));
				
				return null;
			}
		});
	}
	
	/*

	public void addEntities(Collection<EntitySpawnData> es) {
	}
	
	public void addEntity(Entity e, Vector3f location) {
	}

	public void removeEntity(Entity e) {
	}

	public void addEntity(Entity e, Vector3f location, int entityID) {
	}

	public void getStaticNode() {
	}

	public void getFreeNode() {
	}

	public void getMobNode() {
	}

	public void getPhysicsSpace() {
	}

	public void getAssetManager() {
	}

	public void getPlayerLocation() {
	}

	public void getRandomPlayer() {
	}

	public void addOtherPlayer () {
	}

	public void getNode() {
	}

	public void getPlayer() {
	}

	public void attachScreenManager(ScreenManager manager){
	}
	
	public void getScreenManager(){
	}
	
	public void setFirstPersonCam(){
	}

	public void setChaseCam(ChaseCamera chase ){
	}
	
	public void gameTick(float tpf, boolean test) {
	}

	public static void sop(Object... objs) {
	}

	public void destroyMob (Mob mob) {
	}

	public void destroyPlayer (Player player) {
	}

	public void destroyEffect (Effect effect) {
	}
	
	public void destroyGizmo (Gizmo gizmo) {
	}

	public void makeEffect(Effect effect) {
	}
	
	public void makeGizmo (Gizmo gizmo) {
	}

	public void playerPick(Vector3f location, Vector3f direction) {
	}
	
	public void playerSecondaryAction (Vector3f location, Vector3f direction) {
	}

	public void getEntitiesOfClass(Class clazz){
	}


	public void spawnMob () {
	}

	public void getTime(){
	}

	public void getPlayers(){
	}

	public void getContainers(){
	}

	public void addObserver(WorldObserver observer) {
	}

	public void observeDropItem(Item i){
	}
	
	public void observeEquipItem(Item i, boolean b){
	}

	public void setPlayer(Entity entity) {
	}

	public void getEntityByID(int eid) {
	}

	public void getPathNodes() {
	}

	public void hasLineOfSight(Vector3f from, Vector3f to) {
	}

	public void getClosestPathNode(Vector3f location) {
	}

	public void addPathNode(Vector3f location) {
	}

	public void setDay(float t) {
	}*/
}
