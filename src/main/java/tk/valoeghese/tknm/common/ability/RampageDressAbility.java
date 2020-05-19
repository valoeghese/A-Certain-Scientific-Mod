package tk.valoeghese.tknm.common.ability;

import java.util.Iterator;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;

public class RampageDressAbility extends ElectromasterAbility {
	@Override
	public void tick(MinecraftServer server, PlayerEntity user, ACertainComponent component) {
		super.tick(server, user, component);

		int level = component.getLevel();

		if (level > 0) {
			if (CHARGED.getBoolean(user)) {
				Iterator<StatusEffectInstance> iterator = user.getActiveStatusEffects().values().iterator();

				byte toAdd = 0;

				while(iterator.hasNext()) {
					StatusEffectInstance effect = iterator.next();
					StatusEffect type = effect.getEffectType();

					if (type == StatusEffects.SPEED) {
						if (effect.getDuration() < 80 && effect.getAmplifier() <= ((level - 1) / 2)) {
							toAdd |= 0b001;
						}
					} else if (level > 1 && type == StatusEffects.STRENGTH) {
						if (effect.getDuration() < 80 && effect.getAmplifier() <= ((level - 2) / 2)) {
							toAdd |= 0b010;
						}
					} else if (level > 2 && type == StatusEffects.CONDUIT_POWER) {
						if (effect.getDuration() < 80 && effect.getAmplifier() < 1) {
							toAdd |= 0b100;
						}
					}
				}

				if ((toAdd & 0b001) != 0) {
					user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 120, ((level - 1) / 2), true, false));
				}

				if ((toAdd & 0b010) != 0) {
					user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 120, ((level - 2) / 2), true, false));
				}

				if ((toAdd & 0b100) != 0) {
					user.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 120, 0, true, false));
				}

				if (user.getRandom().nextInt(10) == 0) {
					Ability.grantXP(user, 0.001f);
				}

				Ability.exhaust(user, 0.07f);
			}
		}
	}

	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage, ElectromasterAbility.Data data) {
		if (level > 0) {
			if (player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
				boolean charged = ElectromasterAbility.CHARGED.getBoolean(player.getUuid());

				// add extra abilities here: strength, speed, etc

				return ElectromasterAbility.performAlterCharge(world.getTime(), player, charged ? CHARGE_OFF : CHARGE_ON);
			}
		}

		return null;
	}
}
