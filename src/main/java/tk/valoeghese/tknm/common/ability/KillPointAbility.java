package tk.valoeghese.tknm.common.ability;

import java.util.function.Predicate;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Accelerator's name for the subset of the Teleporter ability that relies on others as a reference point.
 */
public class KillPointAbility extends TeleporterAbility {
	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage) {
		if (level == 0 || !player.getStackInHand(Hand.MAIN_HAND).isEmpty() || player.getHungerManager().getFoodLevel() < 1) {
			return null;
		}

		double distance = MathHelper.lerp(abilityProgress, 14.0 * level, 14.0 * (level + 1));
		LivingEntity lookingAt = lookingAt(world, player, distance);

		if (lookingAt != null) {
			Vec3d dir = lookingAt.getRotationVec(0.0f).rotateY((float) Math.PI);
			Vec3d targetPos = lookingAt.getPos().add(dir);
			Predicate<PlayerEntity> pred = level >= 3 ? (pe -> true) : (pe -> pe != player);

			return teleportTo(world, targetPos.x, targetPos.y, targetPos.z, getMaxCount(level, abilityProgress), player, anyPlayertargets(world, player, pred));
		}

		return null;
	}
}
