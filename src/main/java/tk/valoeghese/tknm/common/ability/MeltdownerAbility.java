package tk.valoeghese.tknm.common.ability;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.client.abilityrenderer.MeltdownerAbilityRenderer;

public class MeltdownerAbility extends Ability {
	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage) {
		switch (usage) {
		case 1:
			if (player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
				double distance = 30.0f;
				// 10 points of on fire, 10 points of generic
				distance = Beam.launch(player.getPos(), new Vec3d(0, 2.15, 0), player, distance, false, null, () -> 8, (hit, target) -> {
					if (hit) {
						target.damage(DamageSource.ON_FIRE, 12.0f);
					}
				});
				return new int[] {
						Float.floatToIntBits((float) distance)
				};
			}
		}

		return null;
	}

	@Override
	protected AbilityRenderer createAbilityRenderer() {
		return new MeltdownerAbilityRenderer();
	}
}