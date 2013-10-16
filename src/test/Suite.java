package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import test.network.PlayerDataTest;
import test.world.ActorStatsTests;
import test.world.GeneralEntityTest;
import test.world.InventoryTests;
import test.world.ItemStatsTests;
import test.world.PlayerEquipmentTests;
import test.world.WorldTest;

/**
 * @author Alex Campbell 300252131
 */
@RunWith(org.junit.runners.Suite.class)
@SuiteClasses({GeneralEntityTest.class, WorldTest.class, InventoryTests.class, ActorStatsTests.class,
	ItemStatsTests.class, PlayerEquipmentTests.class, PlayerDataTest.class})
public class Suite {}