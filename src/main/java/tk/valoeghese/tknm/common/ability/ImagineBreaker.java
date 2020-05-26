package tk.valoeghese.tknm.common.ability;

import java.util.Iterator;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.ability.AbilityUserAttack;
import tk.valoeghese.tknm.api.ability.BasicAbility;
import tk.valoeghese.tknm.api.ability.DefenseResult;
import tk.valoeghese.tknm.client.abilityrenderer.ImagineBreakerRenderer;
import tk.valoeghese.tknm.common.InnateAbilityManager;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;
import tk.valoeghese.tknm.mixin.AccessorLivingEntity;

// TODO user has to raise arm to activate ability.
public class ImagineBreaker extends BasicAbility {
	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage) {
		return null;
	}

	@Override
	public void onRemovedFrom(PlayerEntity player) {
		InnateAbilityManager.INNATE_ABILITY_COMPONENT.get(player.getEntityWorld().getLevelProperties()).imagineBreaker = null;
	}

	@Override
	public void tick(MinecraftServer server, PlayerEntity user, ACertainComponent component) {
		Iterator<StatusEffectInstance> iterator = user.getActiveStatusEffects().values().iterator();

		boolean 不幸だ = false;

		while(iterator.hasNext()) {
			StatusEffectInstance effect = iterator.next();
			StatusEffect type = effect.getEffectType();

			if (type != StatusEffects.UNLUCK && type != StatusEffects.POISON /* poison is natural */) {
				((AccessorLivingEntity) user).invokeOnStatusEffectRemoved(effect);
				iterator.remove();
			} else if (type == StatusEffects.UNLUCK) {
				if (effect.getDuration() > 80) {
					不幸だ = true;
				}
			}
		}

		if (!不幸だ) {
			// 不幸だ！
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.UNLUCK, 120, 0, true, false));
		}
	}

	@Override
	public DefenseResult defendAbilityUserAttack(AbilityUserAttack attack, PlayerEntity target, Vec3d sourcePos) {
		if (attack.isNaturalAttack()) {
			return DefenseResult.HIT;
		}

		Vec3d selfPos = target.getPos();
		double distanceBetween = selfPos.distanceTo(sourcePos);

		// compute pitch difference
		float attackPitch = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(sourcePos.y - selfPos.getY(), distanceBetween)));
		float dPitch = MathHelper.abs(attackPitch + target.pitch);

		if (dPitch <= DEFENSE_PITCH_FOA_DEGREES || dPitch >= 360 - DEFENSE_PITCH_FOA_DEGREES) {
			boolean angleCanActivate = true;

			// if attack from near top or bottom then yaw doesn't play a part.
			if (!(attackPitch < -90 + DEFENSE_PITCH_FOA_DEGREES || attackPitch >= 90 - DEFENSE_PITCH_FOA_DEGREES)) {
				// compute yaw difference
				float attackYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(MathHelper.atan2(sourcePos.z - selfPos.z, sourcePos.x - selfPos.x)) - 90);
				float dYaw = MathHelper.abs(target.yaw - attackYaw);

				angleCanActivate = dYaw <= DEFENSE_YAW_FOA_DEGREES || dYaw >= 360 - DEFENSE_YAW_FOA_DEGREES;
			}

			if (angleCanActivate) {
				target.getEntityWorld().playSound(
						null,
						target.getBlockPos().up(),
						ToaruKagakuNoMod.IMAGINE_BREAKER_SOUND_EVENT,
						SoundCategory.MASTER,
						2f,
						1f);
				return DefenseResult.ABSOLUTE_STOP;
			}
		}

		return DefenseResult.HIT; // perhaps hit and stop?
	}

	@Override
	protected AbilityRenderer createAbilityRenderer() {
		return new ImagineBreakerRenderer();
	}

	// defense "field of action"
	private static final float DEFENSE_YAW_FOA_DEGREES = 30.0f;
	private static final float DEFENSE_PITCH_FOA_DEGREES = 42.0f;
}
