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
import tk.valoeghese.tknm.util.FloatSupplier;

final class Beam {
	/**
	 * @return the distance the beam managed to travel before being blocked.
	 */
	static double launch(Vec3d sourcePos, Vec3d posOffset, PlayerEntity sender, double distance, boolean naturalAttack, @Nullable DamageSource damageSource, FloatSupplier damageSupplier, @Nullable ExtraAbilityEffectsFunction extraEffects) {
		World world = sender.getEntityWorld();

		if (damageSource == null) {
			damageSource = DamageSource.player(sender);
		}

		sourcePos = sourcePos.add(posOffset);

		System.out.println(rayTraceBlock(world, sourcePos, sender, distance).getPos());

		List<LivingEntity> entities = rayTraceEntities(world, sourcePos, sender, distance);

		for (LivingEntity le : entities) {
			float damage = damageSupplier.getAsFloat();

			if (AbilityUserAttack.post(sender, le, sourcePos, damage, damageSource, naturalAttack, extraEffects)) {
				distance = sourcePos.distanceTo(le.getPos().add(posOffset));
				break;
			}
		}

		return distance;
	}

	static OrderedList<LivingEntity> rayTraceEntities(World world, Vec3d sourcePos, PlayerEntity sender, double distance) {
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
		for (LivingEntity le : world.getEntities(
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
				if (le.getBoundingBox().expand(0.5).contains(rayPos)) {
					// add the said entity
					distanceLookup.put(le, (float) distBetween);
					entities.add(le);
				}
			}
		}

		return entities;
	}

	private static BlockHitResult rayTraceBlock(World world, Vec3d start, PlayerEntity sender, double distance) {
		Vec3d vec3d2 = sender.getRotationVec(0.0f);
		Vec3d vec3d3 = start.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
		return world.rayTrace(new RayTraceContext(start, vec3d3, RayTraceContext.ShapeType.OUTLINE, FluidHandling.NONE, sender));
	}
}
