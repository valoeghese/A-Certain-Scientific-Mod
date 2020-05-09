package tk.valoeghese.tknm.common.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.client.abilityrenderer.MeltdownerAbilityRenderer;

public class MeltdownerAbility extends Ability {
	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage) {
		switch (usage) {
		case 0:
			double distance = 100.0f;
			distance = Beam.launch(world, player.getPos(), new Vec3d(0, 2.15, 0), player, distance, false, () -> 20);
			return new int[] {
					Float.floatToIntBits((float) distance)
			};
		}

		return null;
	}

	@Override
	protected AbilityRenderer createAbilityRenderer() {
		return new MeltdownerAbilityRenderer();
	}
}
