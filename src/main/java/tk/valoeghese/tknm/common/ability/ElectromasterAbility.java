package tk.valoeghese.tknm.common.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.common.ability.renderer.ElectromasterAbilityRenderer;

public class ElectromasterAbility extends Ability {
	@Override
	public AbilityRenderer createAbilityRenderer() {
		return new ElectromasterAbilityRenderer();
	}

	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float levelProgress, byte usage) {
		double distance = 50.0;
		double maxDistance = Math.sqrt((distance * distance) * 2);

		for (LivingEntity le : world.getEntities(
				LivingEntity.class,
				new Box(player.getPos(), player.getPos().add(1, 1, 1)).expand(maxDistance),
				le -> true
				)) {
		}

		return new int[] {
				Float.floatToIntBits((float) distance)
		};
	}
}
