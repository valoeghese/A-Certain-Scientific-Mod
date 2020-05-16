package tk.valoeghese.tknm.api.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Ability with no additional user data.
 */
public abstract class BasicAbility extends Ability<NoneAbilityUserData> {
	@Override
	public NoneAbilityUserData createUserData(PlayerEntity user) {
		return new NoneAbilityUserData();
	}

	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage, NoneAbilityUserData data) {
		return this.performAbility(world, player, level, abilityProgress, usage);
	}

	protected abstract int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage);
}
