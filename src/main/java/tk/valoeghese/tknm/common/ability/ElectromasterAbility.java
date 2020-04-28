package tk.valoeghese.tknm.common.ability;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.OrderedList;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.common.ability.renderer.ElectromasterAbilityRenderer;

public class ElectromasterAbility extends Ability {
	@Override
	public AbilityRenderer createAbilityRenderer() {
		return new ElectromasterAbilityRenderer();
	}

	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float levelProgress, byte usage) {
		double distance = 50.0;
		double sqrDistance = distance * distance;
		double maxDistance = Math.sqrt(sqrDistance * 2);

		Vec3d pos = player.getPos();
		double x0 = pos.getX();
		double y0 = pos.getY();
		double z0 = pos.getZ();

		Object2FloatMap<LivingEntity> distanceCache = new Object2FloatArrayMap<>();
		OrderedList<LivingEntity> entities = new OrderedList<>(distanceCache::getFloat);

		for (LivingEntity le : world.getEntities(
				LivingEntity.class,
				new Box(pos, pos.add(1, 1, 1)).expand(maxDistance),
				le -> le != player
				)) {

			Vec3d otherPos = le.getPos();

			double sqrDistBetween = otherPos.squaredDistanceTo(pos);

			if (sqrDistBetween < sqrDistance) {
				// if they're too close to the ability user add them as well
				// may remove this if I get a better algorithm
				if (sqrDistBetween < 0.5 * 0.5) {
					distanceCache.put(le, (float) Math.sqrt(sqrDistBetween));

					entities.add(le);
				}

				double x1 = otherPos.getX();
				double z1 = otherPos.getZ();
				float yaw = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(z1 - z0, x1 - x0)) - 90);
				float dYaw = MathHelper.abs(yaw - player.yaw);

				// degree accuracy
				if (dYaw <= 5 || dYaw >= 360 - 5) {
					double distBetween = Math.sqrt(sqrDistBetween);
					float pitch = MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(otherPos.getY() - y0, distBetween)));
					float dPitch = MathHelper.abs(pitch + player.pitch);
					System.out.println(dPitch);
					// degree accuracy
					if (dPitch <= 10 || dPitch >= 360 - 10) {
						distanceCache.put(le, (float) distBetween);

						entities.add(le);
					}
				}
			}
		}

		for (LivingEntity le : entities) {
			le.damage(DamageSource.MAGIC, 1);
		}

		return new int[] {
				Float.floatToIntBits((float) distance)
		};
	}
}
