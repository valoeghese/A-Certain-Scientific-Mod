package tk.valoeghese.tknm.common.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;

public class ElectromasterAbility extends Ability {
	@Override
	public AbilityRenderer createAbilityRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, byte usage) {
		return new int[0];
	}
}
