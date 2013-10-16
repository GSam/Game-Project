package test.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.jme3.asset.AssetManager;

import savefile.SaveUtils;
import world.ActorStatObserver;
import world.ActorStats;
import world.StatModification;
import world.entity.item.Item;
import world.entity.item.ItemInfo;
import world.entity.item.Stat;
import static org.junit.Assert.*;

/**
 * @author Alex Campbell 300252131
 */
public class ActorStatsTests {
	private static class TestObserver implements ActorStatObserver {
		@Override
		public void update(Stat stat, float amount) {
			this.stat = stat;
			this.amount = amount;
		}
		private Stat stat;
		private float amount;
		public void check(Stat stat, float amount) {
			assertEquals(stat, this.stat);
			assertTrue(this.amount == amount);
		}
	}
	
	@Test
	public void testNullaryConstructor() {
		ActorStats s = new ActorStats();
		s.initialise();
		assertTrue(s.getStat(Stat.DAMAGE) == 0);
		assertTrue(s.getStat(Stat.ENERGY) == 0);
	}
	
	@Test
	public void testMapConstructor() {
		Map<Stat, Float> map = new HashMap<Stat, Float>();
		map.put(Stat.DAMAGE, 1f);
		map.put(Stat.ENERGY, 2f);
		
		ActorStats s = new ActorStats(map);
		s.initialise();
		assertTrue(s.getStat(Stat.DAMAGE) == 1);
		assertTrue(s.getStat(Stat.ENERGY) == 2);
		assertTrue(s.getStat(Stat.MAXHEALTH) == 0);
	}
	
	@Test
	public void testArrayConstructor() {
		ActorStats s = new ActorStats(
				new Stat[] {Stat.DAMAGE, Stat.ENERGY},
				new float[] {1, 2});
		s.initialise();
		assertTrue(s.getStat(Stat.DAMAGE) == 1);
		assertTrue(s.getStat(Stat.ENERGY) == 2);
		assertTrue(s.getStat(Stat.MAXHEALTH) == 0);
	}
	
	@Test
	public void testArrayConstructorDiffLength() {
		try {
			new ActorStats(new Stat[] {Stat.DAMAGE}, new float[] {});
			fail("should throw IAE when arrays have different lengths");
		} catch(IllegalArgumentException e) {
		}
	}
	
	@Test
	public void testArrayConstructorNull() {
		try {
			new ActorStats(new Stat[] {Stat.DAMAGE}, null);
			fail("should throw NPE when argument is null");
		} catch(NullPointerException e) {
		}
		
		try {
			new ActorStats(null, new float[] {1});
			fail("should throw NPE when argument is null");
		} catch(NullPointerException e) {
		}
	}
	
	@Test
	public void testMapConstructorNull() {
		try {
			new ActorStats(null);
			fail("should throw NPE when map is null");
		} catch(NullPointerException e) {
		}
	}
	
	ActorStats as = new ActorStats(new Stat[] {Stat.DAMAGE, Stat.HEALTH}, new float[] {1, 1});
	{as.initialise();}
	TestObserver obs = new TestObserver();
	
	@Test
	public void testModStat() {
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 1);
		as.modStat(Stat.DAMAGE, 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 2);
		as.modStat(Stat.DAMAGE, 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 3);
		as.modStat(Stat.DAMAGE, -1);
		assertTrue(as.getStat(Stat.DAMAGE) == 2);
		as.modStat(Stat.DAMAGE, -2);
		assertTrue(as.getStat(Stat.DAMAGE) == 0);
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		
		assertTrue(as.getStat(Stat.ENERGY) == 0);
		as.modStat(Stat.ENERGY, 1000);
		assertTrue(as.getStat(Stat.ENERGY) == 1000);
	}
	
	@Test
	public void testMultStat() {
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 1);
		as.multStat(Stat.DAMAGE, 2);
		assertTrue(as.getStat(Stat.DAMAGE) == 2);
		as.multStat(Stat.DAMAGE, 2);
		assertTrue(as.getStat(Stat.DAMAGE) == 4);
		as.multStat(Stat.DAMAGE, 0.5f);
		assertTrue(as.getStat(Stat.DAMAGE) == 2);
		as.multStat(Stat.DAMAGE, 0.25f);
		assertTrue(as.getStat(Stat.DAMAGE) == 0.5f);
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		
		assertTrue(as.getStat(Stat.ENERGY) == 0);
		as.multStat(Stat.ENERGY, 1000);
		assertTrue(as.getStat(Stat.ENERGY) == 0);
	}
	
	@Test
	public void testResetStat() {
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 1);
		assertTrue(as.getStat(Stat.ENERGY) == 0);
		as.modStat(Stat.HEALTH, 2);
		as.modStat(Stat.DAMAGE, 2);
		as.modStat(Stat.ENERGY, 2);
		assertTrue(as.getStat(Stat.HEALTH) == 3);
		assertTrue(as.getStat(Stat.DAMAGE) == 3);
		assertTrue(as.getStat(Stat.ENERGY) == 2);
		as.resetStat(Stat.HEALTH);
		as.resetStat(Stat.DAMAGE);
		as.resetStat(Stat.ENERGY);
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 1);
		assertTrue(as.getStat(Stat.ENERGY) == 0);
	}
	
	@Test
	public void testSaveAndLoad() {
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 1);
		assertTrue(as.getStat(Stat.ENERGY) == 0);
		as.modStat(Stat.HEALTH, 2);
		as.modStat(Stat.DAMAGE, 2);
		as.modStat(Stat.ENERGY, 2);
		assertTrue(as.getStat(Stat.HEALTH) == 3);
		assertTrue(as.getStat(Stat.DAMAGE) == 3);
		assertTrue(as.getStat(Stat.ENERGY) == 2);
		
		as = SaveUtils.fromBytes(SaveUtils.toBytes(as), ActorStats.class, null);
		
		assertTrue(as.getStat(Stat.HEALTH) == 3);
		assertTrue(as.getStat(Stat.DAMAGE) == 3);
		assertTrue(as.getStat(Stat.ENERGY) == 2);
		as.resetStat(Stat.HEALTH);
		as.resetStat(Stat.DAMAGE);
		as.resetStat(Stat.ENERGY);
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 1);
		assertTrue(as.getStat(Stat.ENERGY) == 0);
	}
	
	@Test
	public void testObserveSet() {
		as.addObserver(obs);
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		as.setStat(Stat.HEALTH, 2);
		assertTrue(as.getStat(Stat.HEALTH) == 2);
		obs.check(Stat.HEALTH, 2);
	}
	
	@Test
	public void testObserveMod() {
		as.addObserver(obs);
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		as.modStat(Stat.HEALTH, 2);
		assertTrue(as.getStat(Stat.HEALTH) == 3);
		obs.check(Stat.HEALTH, 3);
	}
	
	@Test
	public void testObserveMult() {
		as.addObserver(obs);
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		as.setStat(Stat.HEALTH, 2);
		assertTrue(as.getStat(Stat.HEALTH) == 2);
		obs.check(Stat.HEALTH, 2);
		as.multStat(Stat.HEALTH, 4);
		assertTrue(as.getStat(Stat.HEALTH) == 8);
		obs.check(Stat.HEALTH, 8);
	}
	
	@Test
	public void testUnobserve() {
		as.addObserver(obs);
		as.setStat(Stat.HEALTH, 2);
		obs.check(Stat.HEALTH, 2);
		as.removeObserver(obs);
		as.setStat(Stat.HEALTH, 3);
		obs.check(Stat.HEALTH, 2);
	}
	
	@Test
	public void testEquipAndUnequip() {
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 1);
		assertTrue(as.getStat(Stat.ENERGY) == 0);
		
		final StatModification istats = new StatModification(new Stat[] {Stat.HEALTH, Stat.ENERGY}, new float[] {3, 6});
		
		Item item = new Item() {
			@Override protected void makeMesh(AssetManager assetManager) {}
			@Override protected StatModification makeItemStats() {return istats;}
			@Override protected ItemInfo makeItemInfo() {return null;}
			@Override protected String getImage() {return null;}
			@Override public StatModification getItemStats() {return istats;}
			@Override public void getPreloadAssets(Set<String> assets) {}
		};
		
		as.equip(item);
		
		assertTrue(as.getStat(Stat.HEALTH) == 4);
		assertTrue(as.getStat(Stat.DAMAGE) == 1);
		assertTrue(as.getStat(Stat.ENERGY) == 6);
		
		as.unequip(item);
		
		assertTrue(as.getStat(Stat.HEALTH) == 1);
		assertTrue(as.getStat(Stat.DAMAGE) == 1);
		assertTrue(as.getStat(Stat.ENERGY) == 0);
	}
}
