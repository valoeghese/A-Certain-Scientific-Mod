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
				boolean charged = ElectromasterAbility.CHARGED.getBoolean(player.getUuid());

				if (charged) {
					player.setMovementSpeed(player.getMovementSpeed() / 2);
				} else {
					player.setMovementSpeed(player.getMovementSpeed() * 2);
				}

				return ElectromasterAbility.performAlterCharge(world.getTime(), player, charged ? CHARGE_OFF : CHARGE_ON);
			}
		}

		return null;
	}

	@Override
	protected AbilityRenderer createAbilityRenderer() {
		return new ElectromasterAbilityRenderer();
	}
}
