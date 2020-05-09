package tk.valoeghese.tknm.common.ability;

import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;

public final class Abilities {
	private Abilities() {
		// NO-OP
	}

	public static Ability ensureInit() {
		return ELECTROMASTER;
	}

	private static final Ability register(String name, Ability ability, boolean addToPicker) {
		return AbilityRegistry.register(ToaruKagakuNoMod.from(name), ability, addToPicker);
	}

	public static final Ability ELECTROMASTER = register("electromaster", new ElectromasterAbility(), true);
	public static final Ability MELTDOWNER = register("meltdowner", new MeltdownerAbility(), true);

	//TODO these abilities
	public static final Ability TELEKINESIS = null;
	public static final Ability TELEPORT = null;
	public static final Ability KILL_POINT = null; // funni useless teleport ability
	public static final Ability RAMPAGE_DRESS = null;
	public static final Ability AIM_STALKER = null; // make sure to check for aim stalker on server to avoid hacked clients abusing
	public static final Ability PYROKINESIS = null;
	public static final Ability DUMMY_CHECK = null;
	public static final Ability AUTO_REBIRTH = null;

	// Since imagine breaker isn't actually an ability user ability, we distribute it differently.
	public static final Ability IMAGINE_BREAKER = register("imagine_breaker", new ImagineBreaker(), false);
}
