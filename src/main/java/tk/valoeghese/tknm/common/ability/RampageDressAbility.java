package tk.valoeghese.tknm.common.ability;

import static tk.valoeghese.tknm.common.ability.ElectromasterAbility.CHARGE_OFF;
import static tk.valoeghese.tknm.common.ability.ElectromasterAbility.CHARGE_ON;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.ability.BasicAbility;
import tk.valoeghese.tknm.client.abilityrenderer.ElectromasterAbilityRenderer;

public class RampageDressAbility extends BasicAbility {
	@Override
	protected int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage) {
		if (level > 0) {
			if (player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
				return ElectromasterAbility.performAlterCharge(world.getTime(), player, ElectromasterAbility.CHARGED.getBoolean(player.getUuid()) ? CHARGE_OFF : CHARGE_ON);
			}
		}

		return null;
	}

	@Override
	protected AbilityRenderer createAbilityRenderer() {
		return new ElectromasterAbilityRenderer();
	}
}
