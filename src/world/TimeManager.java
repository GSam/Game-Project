package world;

import renderer.VisualEffectsManager;
import world.effects.DayNightStatChange;
import world.entity.item.Stat;

/**
 * TimeManager controls the passage of time in a game world, and is responsible for updating
 * anything time-dependant when necessary.
 *
 * @author Tony 300242775
 */
public class TimeManager {
	/**
	 * The number of seconds per in-game minute.
	 */
    public final static int SECONDS_PER_MINUTE = 60;

    /**
     * The length of an in-game day, in in-game minutes.
     */
    public final static int DAY_LENGTH = 4;
    /**
     * The length of an in-game night, in in-game minutes.
     */
    public final static int NIGHT_LENGTH = 2;

    /**
     * The night music starts playing this many seconds before night.
     */
    public final static int NIGHT_MUSIC_ADVANCE = 0;

    /**
     * The length of a full in-game day/night cycle, in in-game minutes.
     */
    public final static int TOTAL_LENGTH = DAY_LENGTH + NIGHT_LENGTH;

    private static final DayNightStatChange dawnMod = new DayNightStatChange(new StatModification (new Stat[] {Stat.SPEED, Stat.DAMAGE}, new float[] {0.8f, 0.8f}));
    private static final DayNightStatChange dayMod = new DayNightStatChange (new StatModification (new Stat[] {Stat.SPEED, Stat.DAMAGE}, new float[] {1f, 1f}));
    private static final DayNightStatChange duskMod = new DayNightStatChange (new StatModification (new Stat[] {Stat.SPEED, Stat.DAMAGE}, new float[] {1.3f, 1.5f}));
    private static final DayNightStatChange nightMod = new DayNightStatChange (new StatModification (new Stat[] {Stat.SPEED, Stat.DAMAGE}, new float[] {1.5f, 1.5f}));
    private static final DayNightStatChange[] stageMods = new DayNightStatChange[] {dawnMod, dayMod, duskMod, nightMod};

    /**
     * Stage represents a part of the day/night cycle.
     * @author Tony 300242775
     */
    public enum Stage {DAWN, DAY, DUSK, NIGHT};
    private static final int[] stageEnds = {2, 8, 10, 15};

    private World world;
    private VisualEffectsManager vem;
    private Stage stage = Stage.DAY;
    private float time = 1;

    /**
     * @param world the World to manage the time of
     * @param vem the VisualEffectsManager to keep updated with the time
     */
    public TimeManager (World world, VisualEffectsManager vem) {
		this.vem = vem;
		this.world = world;
		world.makeEffect(stageMods[stage.ordinal()]);
	}

    /**
     * Perform one tick of time-logic, possible updating other aspects
     * of the world.
     * @param tpf the time since last update
     */
	public void update (float tpf) {
		time = (time + (tpf / SECONDS_PER_MINUTE)) % TOTAL_LENGTH;
		vem.updateEffects(time);

		if (time >= stageEnds[stage.ordinal()]) {
			stageMods[stage.ordinal()].destroy();
			stage = Stage.values()[(stage.ordinal()+1) % 4];
			world.makeEffect (stageMods[stage.ordinal()]);
			world.updateTime(time);
		}
	}

	public static boolean shouldPlayNightMusic(float time) {
		return (time + NIGHT_MUSIC_ADVANCE / (float)SECONDS_PER_MINUTE) >= /*stageEnds[Stage.DUSK.ordinal()]*/ DAY_LENGTH;
	}

	/**
	 * @return the current time, from 0 to TOTAL_LENGTH
	 */
	public float getTime() {
		return time;
	}

	/**
	 * Set the current time to the provided value.
	 * @param time the time to set to
	 */
	public void setTime(float time) {
		this.time = time;
	}

	/**
	 * @return the current Stage that this TimeManager is in
	 */
	public Stage getStage() {
		return stage;
	}
}
