package tk.valoeghese.tknm.common.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.ability.AbilityUserAttack;
import tk.valoeghese.tknm.api.ability.DefenseResult;
import tk.valoeghese.tknm.client.abilityrenderer.ImagineBreakerRenderer;

// TODO user has to raise arm to activate ability.
public class ImagineBreaker extends Ability {
	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage) {
		return null;
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

		if (dPitch <= DEFENSE_FOA_DEGREES || dPitch >= 360 - DEFENSE_FOA_DEGREES) {
			boolean angleCanActivate = true;

			// if attack from near top or bottom then automatic win
			if (!(attackPitch < -90 + DEFENSE_FOA_DEGREES || attackPitch >= 90 - DEFENSE_FOA_DEGREES)) {
				// compute yaw difference
				float attackYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(MathHelper.atan2(sourcePos.z - selfPos.z, sourcePos.x - selfPos.x)) - 90);
				float dYaw = MathHelper.abs(target.yaw - attackYaw);

				angleCanActivate = dYaw <= DEFENSE_FOA_DEGREES || dYaw >= 360 - DEFENSE_FOA_DEGREES;
			}

			if (angleCanActivate) {
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
	private static final float DEFENSE_FOA_DEGREES = 30.0f;
}
