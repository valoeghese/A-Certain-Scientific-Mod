package tk.valoeghese.tknm.common.ability;

import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.api.ability.AbilityUserData;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;

public final class Abilities {
	private Abilities() {
		// NO-OP
	}

	public static Ability<?> ensureInit() {
		return ELECTROMASTER;
	}

	private static final <T extends AbilityUserData> Ability<T> register(String name, Ability<T> ability, boolean addToPicker) {
		return AbilityRegistry.register(ToaruKagakuNoMod.from(name), ability, addToPicker);
	}

	public static final Ability<?> ELECTROMASTER = register("electromaster", new ElectromasterAbility(), true);
	public static final Ability<?> MELTDOWNER = register("meltdowner", new MeltdownerAbility(), true);
	public static final Ability<?> TELEPORT = register("teleporter", new TeleporterAbility(), true);
	public static final Ability<?> KILL_POINT = register("kill_point", new KillPointAbility(), true); // funni useless teleport ability
	public static final Ability<?> RAMPAGE_DRESS = register("rampage_dress", new RampageDressAbility(), true); // subset of electromaster that lets you draw more strength out of your body

	//TODO these abilities
	public static final Ability<?> TELEKINESIS = null; // ability to manipulate motion
	public static final Ability<?> PSYCHOKINESIS = null; // general psychokinesis. Combination of telekinesis, aero hand, pyrokinesis, "pressure point", electrokinesis.
	public static final Ability<?> PRESSURE_POINT = null; // Not from the series. Concentrated pressure to rip and destroy. Modeling off of yobou banka's ability to rip a truck in half, and crush someone's head by swiping his fingers.
	public static final Ability<?> AERO_HAND = null; // air jets
	public static final Ability<?> AIM_STALKER = null; // Ability to memorise and manipulate user's AIM fields. Can track memorised AIM fields no matter where they go. Make sure to check for aim stalker on server to avoid hacked clients abusing
	public static final Ability<?> PYROKINESIS = null; // fire manipulation
	public static final Ability<?> DUMMY_CHECK = null; // makes others "less aware" of your presence.
	public static final Ability<?> AUTO_REBIRTH = null; // passive regeneration effect. Takes effect even at level zero, but over a course of days at that point.
	public static final Ability<?> MENTAL_STINGER = null; // mental stinger / mental out. This'll be fun to implement.
	public static final Ability<?> CLAIRVOYANCE = null; // ESP

	// Gemstone abilities are natural, and not able to be produced artificially. Distribute differently since low chance.
	// Gemstone abilities will be generated from randomly selected generalised ability parts made more precise, and these parts will be serialised with NBT.
	public static final Ability<?> GEMSTONE = null;

	// Since imagine breaker isn't actually an ability user ability, we distribute it differently.
	public static final Ability<?> IMAGINE_BREAKER = register("imagine_breaker", new ImagineBreaker(), false);
}
