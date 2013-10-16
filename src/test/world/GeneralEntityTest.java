package test.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.After;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import world.AbstractEntity;
import world.Entity;
import world.Player;
import world.RigidEntity;
import world.World;
import world.WorldType;
import world.ai.StandardZombie;
import world.entity.item.consumable.ConsumableFactory;
import world.entity.item.container.Wallet;
import world.entity.item.equippable.EquippableFactory;
import world.entity.item.equippable.Shotgun;
import world.entity.item.equippable.SimpleGun;
import world.entity.item.equippable.SniperRifle;
import world.entity.item.gizmo.Push;
import world.entity.item.gizmo.SolarConduit;
import world.entity.item.gizmo.StasisFieldGenerator;
import world.entity.item.gizmo.StealthField;
import world.entity.item.miscellaneous.GarmingsNetwork;
import world.entity.item.miscellaneous.ScottsMoonWalkBoots;
import world.entity.item.miscellaneous.UselessBlueToken;
import world.entity.item.miscellaneous.UselessRedToken;
import world.entity.item.torch.TorchFactory;
import world.entity.mob.FastZombie;
import world.entity.mob.Gnome;
import world.entity.mob.Mob;
import world.entity.mob.SlowZombie;
import world.entity.mob.StrongZombie;
import world.entity.staticentity.ContainerFactory;
import world.entity.staticentity.DoorFactory;
import world.entity.staticentity.LockableFactory;
import world.entity.staticentity.RedDot;
import world.entity.staticentity.StaticTorch;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;


/**
 * @author Alex Campbell 300252131
 */
@RunWith(Parameterized.class)
public class GeneralEntityTest extends GameTest {
	
	private static interface EntFactory {
		public AbstractEntity create();
	}

	@Parameters(name="{0}")
	public static Collection<Object[]> testParams() {
		Collection<Object[]> rv = new ArrayList<Object[]>();
		rv.add(new Object[] {GhostEntity.class});
		rv.add(new Object[] {FastZombie.class});
		rv.add(new Object[] {SlowZombie.class});
		rv.add(new Object[] {StrongZombie.class});
		rv.add(new Object[] {Gnome.class});
		rv.add(new Object[] {Player.class});
		rv.add(new Object[] {Wallet.class});
		rv.add(new Object[] {Push.class});
		rv.add(new Object[] {SolarConduit.class});
		rv.add(new Object[] {Wallet.class});
		rv.add(new Object[] {StasisFieldGenerator.class});
		rv.add(new Object[] {StealthField.class});
		rv.add(new Object[] {GarmingsNetwork.class});
		rv.add(new Object[] {world.entity.item.miscellaneous.Map.class});
		rv.add(new Object[] {ScottsMoonWalkBoots.class});
		rv.add(new Object[] {UselessBlueToken.class});
		rv.add(new Object[] {UselessRedToken.class});
		rv.add(new Object[] {RedDot.class});
		rv.add(new Object[] {StaticTorch.class});
		rv.add(new Object[] {Shotgun.class});
		rv.add(new Object[] {SimpleGun.class});
		rv.add(new Object[] {SniperRifle.class});
		
		for(final DoorFactory.Instance instance : DoorFactory.Instance.values()) {
			rv.add(new Object[] {new EntFactory() {@Override public AbstractEntity create() {
				return DoorFactory.getDoorInstance(instance, new Vector3f(1,1,1), 0);
			} @Override public String toString() {
				return "DoorFactory "+instance;
			}}});
		}
		for(final ContainerFactory.Instance instance : ContainerFactory.Instance.values()) {
			rv.add(new Object[] {new EntFactory() {@Override public AbstractEntity create() {
				return ContainerFactory.getContainerInstance(instance, new Vector3f(1,1,1), 0);
			} @Override public String toString() {
				return "ContainerFactory "+instance;
			}}});
		}
		for(final ConsumableFactory.Instance instance : ConsumableFactory.Instance.values()) {
			rv.add(new Object[] {new EntFactory() {@Override public AbstractEntity create() {
				return ConsumableFactory.getConsumableInstance(instance);
			} @Override public String toString() {
				return "ConsumableFactory "+instance;
			}}});
		}
		for(final EquippableFactory.Instance instance : EquippableFactory.Instance.values()) {
			rv.add(new Object[] {new EntFactory() {@Override public AbstractEntity create() {
				return EquippableFactory.getEquippableInstance(instance);
			} @Override public String toString() {
				return "EquippableFactory "+instance;
			}}});
		}
		for(final TorchFactory.Instance instance : TorchFactory.Instance.values()) {
			rv.add(new Object[] {new EntFactory() {@Override public AbstractEntity create() {
				return TorchFactory.getTorchInstance(instance);
			} @Override public String toString() {
				return "TorchFactory "+instance;
			}}});
		}
		for(final LockableFactory.Instance instance : LockableFactory.Instance.values()) {
			rv.add(new Object[] {new EntFactory() {@Override public AbstractEntity create() {
				return LockableFactory.getLockableInstance(instance, new Vector3f(1, 1, 1), 0);
			} @Override public String toString() {
				return "LockableFactory "+instance;
			}}});
		}
		
		
		// TODO WorldObject
		// TODO LockableFactory
		
		return rv;
	}

	AbstractEntity ent; // The entity being tested

	public GeneralEntityTest(Object classOrFactory) throws Exception {
		super(WorldType.SP);
		if(classOrFactory instanceof Class)
			this.ent = ((Class<?>)classOrFactory).asSubclass(AbstractEntity.class).newInstance();
		else
			this.ent = ((EntFactory)classOrFactory).create();
		if(this.ent instanceof Mob) ((Mob)this.ent).setAI(new StandardZombie()); // otherwise NPE
	}

	@Before
	public void before() throws Exception {
		start();

		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				world.addEntity(ent, new Vector3f(0, 10, 0));
				return null;
			}
		});
	}

	@After
	public void after() throws Exception {
		stop();
	}

	@Test
	public void testValidState() throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				// The entity has a unique non-negative ID.
				int myID = ent.getEntityID();
				for(Entity e : world.getEntitiesOfClass(Entity.class))
					if(e != ent)
						assertTrue(e.getEntityID() != myID);

				assertNotNull(ent.getLocation());

				assertNotNull(ent.getDirection());

				Spatial mesh = ent.getMesh();
				assertNotNull(mesh);
				assertNotNull(mesh.getParent());
				// The mesh must be a child of one of the root nodes
				assertTrue(mesh.getParent() == world.getNode() || mesh.getParent() == world.getMobNode() || mesh.getParent() == world.getRigidNode());

				// If the entity has physics, it must be added to the physics space.
				assertTrue(ent.getPhysics() == null || ent.getPhysics().getPhysicsSpace() == world.getPhysicsSpace());

				assertTrue(ent.getWorld() == world);

				return null;
			}
		});
	}

	private void assertInWorld() {
		Spatial mesh = ent.getMesh();
		assertTrue(mesh.getParent() == world.getNode() || mesh.getParent() == world.getMobNode() || mesh.getParent() == world.getRigidNode());
		assertTrue(ent.getPhysics().getPhysicsSpace() == world.getPhysicsSpace());
	}

	@Test
	public void testLocation () throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				for(int k = 0; k < 5; k++) {
					Vector3f loc = getRandomVector();

					ent.setLocation(loc);
					assertEquals(ent.getLocation(), loc);

					assertInWorld();
				}

				// test with (float,float,float) version
				for(int k = 0; k < 5; k++) {
					Vector3f loc = getRandomVector();

					ent.setLocation(loc.x, loc.y, loc.z);
					assertEquals(ent.getLocation(), loc);

					assertInWorld();
				}

				return null;
			}
		});
	}

	@Test
	public void testChangeLocation () throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				for(int k = 0; k < 5; k++) {
					Vector3f delta = getRandomVector();
					Vector3f expect = ent.getLocation().add(delta);

					ent.changeLocation(delta);
					assertEquals(ent.getLocation(), expect);

					assertInWorld();
				}

				// test with (float,float,float) version
				for(int k = 0; k < 5; k++) {
					Vector3f delta = getRandomVector();
					Vector3f expect = ent.getLocation().add(delta);

					ent.changeLocation(delta.x, delta.y, delta.z);
					assertEquals(ent.getLocation(), expect);

					assertInWorld();
				}

				return null;
			}
		});
	}

	@Test
	public void testRemoval() throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				world.removeEntity(ent);
				assertNull(ent.getPhysics().getPhysicsSpace());
				assertNull(ent.getMesh().getParent());
				return null;
			}
		});
	}

	@Test
	public void testHalfRemoveFromWorld() throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				assertInWorld();
				ent.removeFromNode();
				ent.removeFromPhysicsSpace();
				assertNull(ent.getPhysics().getPhysicsSpace());
				assertNull(ent.getMesh().getParent());
				assertTrue(world.getEntitiesOfClass(Entity.class).contains(ent));
				return null;
			}
		});
	}

	@Test
	public void testRemoveFromWorld() throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				assertInWorld();
				world.removeEntity(ent);
				assertNull(ent.getPhysics().getPhysicsSpace());
				assertNull(ent.getMesh().getParent());
				assertFalse(world.getEntitiesOfClass(Entity.class).contains(ent));
				return null;
			}
		});
	}

	@Test
	public void testRemoveAndAttachNode() throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Node parent = ent.getMesh().getParent();
				assertInWorld();
				ent.removeFromNode();
				assertNull(ent.getMesh().getParent());
				assertTrue(ent.getPhysics().getPhysicsSpace() == world.getPhysicsSpace());
				ent.attachToNode();
				assertTrue(ent.getMesh().getParent() == parent);
				assertTrue(ent.getPhysics().getPhysicsSpace() == world.getPhysicsSpace());
				return null;
			}
		});
	}

	@Test
	public void testRemoveAndAddPhysicsSpace() throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Node parent = ent.getMesh().getParent();
				assertInWorld();
				ent.removeFromPhysicsSpace();
				assertNull(ent.getPhysics().getPhysicsSpace());
				assertTrue(ent.getMesh().getParent() == parent);
				ent.addToPhysicsSpace();
				assertTrue(ent.getPhysics().getPhysicsSpace() == world.getPhysicsSpace());
				assertTrue(ent.getMesh().getParent() == parent);
				return null;
			}
		});
	}

	@Test
	public void testReaddToWorld() throws Exception {
		testHalfRemoveFromWorld();

		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				assertNull(ent.getPhysics().getPhysicsSpace());
				assertNull(ent.getMesh().getParent());
				ent.attachToNode();
				ent.addToPhysicsSpace();
				assertInWorld();
				return null;
			}
		});
	}

	@Test
	public void testInvalidLinkToWorld() throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				try {
					ent.linkToWorld(null, Vector3f.ZERO, 0);
					fail("linkToWorld(null world) should throw NPE");
				} catch(NullPointerException e) {
				}

				try {
					ent.linkToWorld(world, null, 0);
					fail("linkToWorld(null loc) should throw NPE");
				} catch(NullPointerException e) {
				}

				try {
					ent.linkToWorld(world, Vector3f.ZERO, 0);
					fail("linkToWorld(normal) should throw IllegalStateException (already linked)");
				} catch(IllegalStateException e) {
				}

				try {
					ent.linkToWorld(world, Vector3f.ZERO, -1);
					fail("linkToWorld(negative id) should throw IllegalArgumentException");
				} catch(IllegalArgumentException e) {
				}

				return null;
			}
		});
	}

	@Test
	public void testInvalidUnlinkFromWorld() throws Exception {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					ent.unlinkFromWorld(null);
					fail("unlinkFromWorld(null) should fail with NPE");
				} catch(NullPointerException e) {
				}

				try {
					ent.unlinkFromWorld(new World());
					fail("unlinkFromWorld(wrong world) should fail with IllegalArgumentException");
				} catch(IllegalArgumentException e) {
				}

				world.removeEntity(ent);
				try {
					ent.unlinkFromWorld(world);
					fail("unlinkFromWorld(world) should fail with IllegalStateException (already unlinked)");
				} catch(IllegalStateException e)  {
				}

				return null;
			}
		});
	}
}

class GhostEntity extends RigidEntity {
	@Override
	protected float getWeight() {
		return 100;
	}
	
	@Override
	public void getPreloadAssets(Set<String> assets) {
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = new Geometry ("test entity", new Box(10, 10, 10));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.5f,0,1,1));
        geometry.setMaterial(mat);
	}
}
