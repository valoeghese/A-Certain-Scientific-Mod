package tk.valoeghese.tknm.common.ability;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.OrderedList;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.ability.BasicAbility;
import tk.valoeghese.tknm.client.abilityrenderer.TeleporterAbilityRenderer;

// TODO should I add a limit to teleporter distance off ground?
public class TeleporterAbility extends BasicAbility {
	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage) {
		if (level == 0 || !player.getStackInHand(Hand.MAIN_HAND).isEmpty() || player.getHungerManager().getFoodLevel() < 1) {
			return null;
		}

		// TODO raycast entity (dist: 2 * level) and perform an entity or to-block teleport, whichever has shortest dist.

		double distance = MathHelper.lerp(abilityProgress, 14.0 * level, 14.0 * (level + 1));
		Vec3d pos = player.getPos();
		float yawRot = 0;

		if (level > 2) {
			if (player.isSneaking()) {
				yawRot = (float) Math.PI;
			}
		}

		BlockHitResult result = Beam.rayTraceBlock(world, new Vec3d(pos.x, player.getEyeY(), pos.z), player, distance, yawRot);
		Predicate<PlayerEntity> pred = level >= 4 ? (pe -> true) : (pe -> pe != player);

		if (result.getBlockPos().getY() > 0) {
			pos = result.getPos();
			return teleportTo(world, pos.getX(), pos.getY(), pos.getZ(), player, targets(world, player, pred));
		}

		return null;
	}

	protected static List<PlayerEntity> targets(World world, PlayerEntity user, Predicate<PlayerEntity> pred) {
		return world.getEntities(PlayerEntity.class, user.getBoundingBox().expand(0.5f), pred);
	}

	@Nullable
	protected static LivingEntity lookingAt(World world, PlayerEntity player, double maxDistance) {
		OrderedList<LivingEntity> ray = Beam.rayTraceEntities(
				world,
				new Vec3d(player.getPos().x, player.getEyeY(), player.getPos().z),
				0.2,
				player,
				maxDistance);

		if (!ray.isEmpty()) {
			return ray.get(0);
		}

		return null;
	}

	// TODO ability to teleport npcs as well
	protected static <T extends LivingEntity> int[] teleportTo(World world, double x, double y, double z, PlayerEntity user, List<T> entities) {
		if (entities.isEmpty()) {
			return null;
		}

		double dx = (x - user.getX());
		double dz = (z - user.getZ());

		// pass position data to renderer via the packet int[]
		IntList result = new IntArrayList();
		result.add(Float.floatToIntBits((float) dx));
		result.add(Float.floatToIntBits((float) y));
		result.add(Float.floatToIntBits((float) dz));

		for (LivingEntity entity : entities) {
			Vec3d pos = entity.getPos();
			world.playSound(
					null,
					entity.getBlockPos(),
					SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
					SoundCategory.MASTER,
					1.0f,
					1.0f);

			result.add(Float.floatToIntBits((float) pos.getX()));
			result.add(Float.floatToIntBits((float) pos.getY()));
			result.add(Float.floatToIntBits((float) pos.getZ()));

			pos = new Vec3d(dx + pos.getX(), y, dz + pos.getZ());
			world.playSound(
					null,
					new BlockPos(pos),
					SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
					SoundCategory.MASTER,
					1.0f,
					1.0f);

			Ability.grantXP(user, 0.01f);
			Ability.exhaust(user, entity == user ? 1.7f : 1.2f);
			entity.teleport(pos.getX(), pos.getY(), pos.getZ());
		}

		return result.toIntArray();
	}

	@Override
	protected AbilityRenderer createAbilityRenderer() {
		return new TeleporterAbilityRenderer();
	}
}
