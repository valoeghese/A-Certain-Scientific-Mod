package tk.valoeghese.tknm.common.ability;

import tk.valoeghese.tknm.util.FloatRandom;
import tk.valoeghese.tknm.util.RandomUtils;

// https://www.desmos.com/calculator/amkiawswds
// Lots of this code is originally from my Misaka Bot repo
// yes the Misaka Bot code was GPL and this is LGPL
// but I wrote it so I'm not gonna get sued aren't I
public final class AbilityUserStats {
	public AbilityUserStats() {
		this.potentialAbility = 0.2f + ABILITY_RANDOM.nextFloat();
	}

	public float xp = 0.0f; // xp for calculations
	private int abilityLevel; // from 0 to 5
	public final float potentialAbility;

	public void calculateLevel() {
		this.abilityLevel = (int) Math.floor((3 * this.potentialAbility * Math.log10(this.xp + 1.0f)) + (2 * this.potentialAbility));

		if (this.abilityLevel > 5) {
			this.abilityLevel = 5;
		}
	}

	private static final FloatRandom ABILITY_RANDOM = RandomUtils.naturalDistribution(0.5f, 0.5f);
}
