package tk.valoeghese.tknm.common.ability;

import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;

public final class 能力 {
	private 能力() {
		// NO-OP
	}

	public static Ability ensureInit() {
		return ELECTROMASTER;
	}

	private static final Ability register(String name, Ability ability, boolean addToPicker) {
		return AbilityRegistry.register(ToaruKagakuNoMod.from(name), ability, addToPicker);
	}

	public static final Ability ELECTROMASTER = register("electromaster", new ElectromasterAbility(), true);

	// Since imagine breaker isn't actually an ability user ability, we distribute it differently.
	public static final Ability IMAGINE_BREAKER = register("imagine_breaker", new ImagineBreaker(), false);
}
