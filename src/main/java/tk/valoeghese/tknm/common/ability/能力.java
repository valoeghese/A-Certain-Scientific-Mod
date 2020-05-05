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

	private static final Ability register(String name, Ability ability) {
		return AbilityRegistry.register(ToaruKagakuNoMod.from(name), ability);
	}

	public static final Ability ELECTROMASTER = register("electromaster", new ElectromasterAbility());
}
