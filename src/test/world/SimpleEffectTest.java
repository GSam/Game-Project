package test.world;

import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.jme3.math.Vector3f;

import world.Actor;
import world.ActorStats;
import world.Inventory;
import world.Player;
import world.StatModification;
import world.World;
import world.WorldType;
import world.effects.DayNightStatChange;
import world.effects.SimpleDamage;
import world.effects.SlowInRadius;
import world.effects.StatChange;
import world.effects.TemporaryStatChange;
import world.entity.item.Item;
import world.entity.item.RightClickable;
import world.entity.item.Stat;
import world.entity.item.consumable.ConsumableFactory;
import world.entity.item.consumable.ConsumableFactory.Instance;
import world.entity.item.consumable.SimpleConsumable;
import world.entity.item.miscellaneous.GarmingsNetwork;

/**
 * Simple effect and consumable testing
 *
 * @author Garming Sam 300198721
 */
public class SimpleEffectTest extends GameTest {

	private Player player;

	public SimpleEffectTest() {
		super(WorldType.SP);
	}

	private class TestPlayer extends Player {
		@Override
		protected ActorStats makeStats(World world) {
			return new ActorStats(new Stat[] { Stat.HEALTH, Stat.ENERGY,
					Stat.SPEED }, new float[] { 1, 10, 10 });
		}

	}

	@Override
	protected void onstart() {

	}

	@Before
	public void before() {
		start();
	}

	@After
	public void after() {
		stop();
	}

	@Test
	public void testSimpleDamage() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				assertTrue(a.getStats().getStat(Stat.HEALTH) == 1);
				SimpleDamage d = new SimpleDamage(a, 1);
				d.linkToWorld(world);
				d.apply(a);
				assertTrue(a.getStats().getStat(Stat.HEALTH) == 0);

				return null;
			}
		});
	}

	@Test
	public void testSimpleDamage2() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				assertTrue(a.getStats().getStat(Stat.HEALTH) == 1);
				SimpleDamage d = new SimpleDamage(a, 1);
				world.makeEffect(d);
				System.out.println(a.getStats().getStat(Stat.HEALTH));
				assertTrue(a.getStats().getStat(Stat.HEALTH) == 0);
				return null;
			}
		});
	}

	@Test
	public void testStatChange1() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				assertTrue(a.getStats().getStat(Stat.HEALTH) == 1);
				StatChange c = new StatChange(new StatModification(
						new Stat[] { Stat.HEALTH }, new float[] { -1 }));
				c.apply(a);
				c.linkToWorld(world);
				c.start();
				assertTrue(a.getStats().getStat(Stat.HEALTH) == 0);
				return null;
			}
		});
	}

	@Test
	public void testStatChange2() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 10);
				StatChange c = new StatChange(new StatModification(
						new Stat[] { Stat.ENERGY }, new float[] { 10 }));
				c.apply(a);
				c.linkToWorld(world);
				c.start();
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 20);
				return null;
			}
		});
	}

	@Test
	public void testStatChange3() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 10);
				StatChange c = new StatChange(new StatModification(
						new Stat[] { Stat.ENERGY }, new float[] { 10 }));
				c.apply(a);
				world.makeEffect(c);
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 20);
				return null;
			}
		});
	}

	@Test
	public void testSlow1() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				assertTrue(a.getStats().getStat(Stat.SPEED) == 10);
				SlowInRadius s = new SlowInRadius(Vector3f.ZERO, 15, 10);
				//assertEquals(Vector3f.ZERO, s.getCentre());
				//assertEquals(15, s.getRadius(), 0.1);
				//assertEquals(10, s.getSlowAmount(), 0.1);
				s.linkToWorld(world);
				s.apply(a);
				assertEquals(a.getStats().getStat(Stat.SPEED), 100, 0.1);
				return null;
			}
		});
	}

	@Test
	public void tempStatChange1() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 10);
				TemporaryStatChange t = new TemporaryStatChange(
						new StatModification(new Stat[] { Stat.ENERGY },
								new float[] { 10 }), 100);
				t.linkToWorld(world);
				t.apply(a);
				t.start();
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 20);
				t.update(10000);
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 10);
				return null;
			}
		});
	}

	@Test
	public void tempStatChange2() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 10);
				TemporaryStatChange t = new TemporaryStatChange(
						new StatModification(new Stat[] { Stat.ENERGY },
								new float[] { 10 }), 100);
				t.apply(a);
				world.makeEffect(t);
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 20);
				world.gameTick(100000);
				assertTrue(a.getStats().getStat(Stat.ENERGY) == 10);
				return null;
			}
		});
	}

	@Test
	public void dayNightStatChange() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				assertTrue(a.getStats().getStat(Stat.SPEED) == 10);
				DayNightStatChange d = new DayNightStatChange(
						new StatModification(new Stat[] { Stat.SPEED },
								new float[] { 10 }));
				d.apply(a);
				assertTrue(a.getStats().getStat(Stat.SPEED) == 100);
				return null;
			}
		});
	}


	@Test
	public void simpleConsumable(){
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Actor a = new TestPlayer();
				world.addEntity(a, Vector3f.ZERO);
				Inventory i = new Inventory(a, 4);
				i.linkToWorld(world);
				SimpleConsumable s = new SimpleConsumable("healthvial/healthvial.scene", new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Health Vial", "Restores some health" }, new Stat[] { Stat.HEALTH }, new float[] { 100 });
				world.addEntity(s, Vector3f.ZERO);
				i.add(s);
				s.rightClick();

				assertTrue(a.getStats().getStat(Stat.HEALTH) == 101);
				return null;
			}
		});
	}

	@Test
	public void getConsumable(){
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Item i = ConsumableFactory.getConsumableInstance(Instance.HEALTH_HIGH);
				assertTrue(i instanceof RightClickable);
				return null;
			}
		});
	}
}
