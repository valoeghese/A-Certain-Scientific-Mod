package tk.valoeghese.tknm.common.ability;

import java.util.List;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.RayTraceContext.FluidHandling;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.OrderedList;
import tk.valoeghese.tknm.api.ability.AbilityUserAttack;
import tk.valoeghese.tknm.api.ability.AbilityUserAttack.ExtraAbilityEffectsFunction;
import tk.valoeghese.tknm.util.FloatFunction;

final class Beam {
	/**
	 * @return the distance the beam managed to travel before being blocked.
	 */
	static double launch(Vec3d sourcePos, Vec3d posOffset, PlayerEntity sender, double distance, boolean naturalAttack, @Nullable DamageSource damageSource, FloatFunction<LivingEntity> damageSupplier, @Nullable ExtraAbilityEffectsFunction extraEffects) {
		return launch(sourcePos, posOffset, 0.5, sender, distance, naturalAttack, damageSource, damageSupplier, extraEffects);
	}

	/**
	 * @return the distance the beam managed to travel before being blocked.
	 */
	static double launch(Vec3d sourcePos, Vec3d posOffset, double widthExpansion, PlayerEntity sender, double distance, boolean naturalAttack, @Nullable DamageSource damageSource, FloatFunction<LivingEntity> damageSupplier, @Nullable ExtraAbilityEffectsFunction extraEffects) {
		World world = sender.getEntityWorld();

		if (damageSource == null) {
			damageSource = DamageSource.player(sender);
		}

		sourcePos = sourcePos.add(posOffset);
		List<LivingEntity> entities = rayTraceEntities(world, sourcePos, widthExpansion, sender, distance);

		for (LivingEntity le : entities) {
			float damage = damageSupplier.applyAsFloat(le);

			if (AbilityUserAttack.post(sender, le, sourcePos, damage, damageSource, naturalAttack, extraEffects)) {
				distance = sourcePos.distanceTo(le.getPos().add(posOffset));
				break;
			}
		}

		return distance;
	}

	static OrderedList<LivingEntity> rayTraceEntities(World world, Vec3d sourcePos, double widthExpansion, PlayerEntity sender, double distance) {
		double sqrDistance = distance * distance;
		double maxDistance = Math.sqrt(sqrDistance * 2); // pythagoras theorem

		// perform trig calculations once per ability use for speed
		double calcYaw = Math.toRadians(sender.yaw - 180);
		double calcPitch = Math.toRadians(-sender.pitch);
		double sinYaw = Math.sin(calcYaw);
		double cosYaw = Math.cos(calcYaw);
		double sinPitch = Math.sin(calcPitch);
		double cosPitch = Math.cos(calcPitch);

		// order entities by distance in case one blocks the ability (e.g. imagine breaker)
		Object2FloatMap<LivingEntity> distanceLookup = new Object2FloatArrayMap<>();
		OrderedList<LivingEntity> entities = new OrderedList<>(distanceLookup::getFloat);

		// iterate over possible targeted entities within the said distance
		for (LivingEntity le : world.getEntitiesByClass(
				LivingEntity.class,
				new Box(sourcePos, sourcePos.add(1, 1, 1)).expand(maxDistance),
				le -> le != sender
				)) {

			Vec3d lePos = le.getBoundingBox().getCenter();
			double sqrDistBetween = lePos.squaredDistanceTo(sourcePos);

			if (sqrDistBetween < sqrDistance) {
				double distBetween = Math.sqrt(sqrDistBetween);

				// calculate point along ray at that distance
				// using trigonometry
				double dy = distBetween * sinPitch;
				double dbHorizontal = distBetween * cosPitch;

				double dx = dbHorizontal * sinYaw;
				double dz = -(dbHorizontal * cosYaw); // because mojang

				// position of ray at that distance
				Vec3d rayPos = sourcePos.add(dx, dy, dz);

				// if it's in or near the bounding box
				if (le.getBoundingBox().expand(widthExpansion).contains(rayPos)) {
					// add the said entity
					distanceLookup.put(le, (float) distBetween);
					entities.add(le);
				}
			}
		}

		return entities;
	}

	public static BlockHitResult rayTraceBlock(World world, Vec3d start, PlayerEntity sender, double distance) {
		Vec3d rotationVec = sender.getRotationVec(0.0f);
		Vec3d end = start.add(rotationVec.x * distance, rotationVec.y * distance, rotationVec.z * distance);
		return world.rayTrace(new RayTraceContext(start, end, RayTraceContext.ShapeType.OUTLINE, FluidHandling.NONE, sender));
	}

	public static BlockHitResult rayTraceBlock(World world, Vec3d start, PlayerEntity sender, double distance, float yawRot) {
		Vec3d rotationVec = sender.getRotationVec(0.0f);
		rotationVec = rotationVec.rotateY(yawRot);
		Vec3d end = start.add(rotationVec.x * distance, rotationVec.y * distance, rotationVec.z * distance);
		return world.rayTrace(new RayTraceContext(start, end, RayTraceContext.ShapeType.OUTLINE, FluidHandling.NONE, sender));
	}
}
