package tk.valoeghese.tknm.common.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RampageDressAbility extends ElectromasterAbility {
	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage, ElectromasterAbility.Data data) {
		if (level > 0) {
			if (player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
				boolean charged = ElectromasterAbility.CHARGED.getBoolean(player.getUuid());

				if (charged) {
					player.setMovementSpeed(player.getMovementSpeed() / 3);
				} else {
					player.setMovementSpeed(player.getMovementSpeed() * 3);
				}

				return ElectromasterAbility.performAlterCharge(world.getTime(), player, charged ? CHARGE_OFF : CHARGE_ON);
			}
		}

		return null;
	}
}
