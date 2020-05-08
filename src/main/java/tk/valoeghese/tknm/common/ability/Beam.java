package tk.valoeghese.tknm.common.ability;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.OrderedList;
import tk.valoeghese.tknm.api.ability.AbilityUserAttack;
import tk.valoeghese.tknm.util.FloatSupplier;

class Beam {
	/**
	 * @return the distance the beam managed to travel before being blocked.
	 */
	static double launch(World world, Vec3d sourcePos, PlayerEntity sender, double distance, FloatSupplier damageSupplier) {
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

		for (LivingEntity le : entities) {
			float damage = damageSupplier.getAsFloat();

			if (AbilityUserAttack.post(sender, le, sourcePos, damage, DamageSource.player(sender), null)) {
				distance = sourcePos.distanceTo(le.getPos().add(0, 1.25, 0));
				break;
			}
		}

		return distance;
	}
}
