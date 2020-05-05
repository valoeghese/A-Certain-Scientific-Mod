package tk.valoeghese.tknm.common.ability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.OrderedList;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.ability.AbilityUserAttack;
import tk.valoeghese.tknm.client.abilityrenderer.ElectromasterAbilityRenderer;

public class ElectromasterAbility extends Ability {
	@Override
	public AbilityRenderer createAbilityRenderer() {
		return new ElectromasterAbilityRenderer();
	}

	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float levelProgress, byte usage) {
		long time = world.getTime();
		UUID uuid = player.getUuid();

		// Charge
		if (!TO_CHARGE.isEmpty()) {
			Set<Map.Entry<UUID, Long>> currentSet = new HashSet<>(TO_CHARGE.entrySet());

			for (Map.Entry<UUID, Long> entry : currentSet) {
				if (entry.getValue() < time) {
					CHARGED.put(entry.getKey(), true);
					TO_CHARGE.remove(entry.getKey());
				}
			}
		}

		if (CHARGED.getBoolean(uuid)) {
			return this.performRailgun(world, player, level, levelProgress);
		} else {
			if (!TO_CHARGE.containsKey(uuid)) {
				return performAlterCharge(time, player, CHARGE_ON);
			} else {
				return performAlterCharge(time, player, CHARGE_EQUAL);
			}
		}
	}

	private static final long CHARGE_DELAY = (long) (1.25 * 20); // TODO based on ability level
	private static final Map<UUID, Long> TO_CHARGE = new HashMap<>();
	private static final Object2BooleanArrayMap<UUID> CHARGED = new Object2BooleanArrayMap<>();

	private int[] performRailgun(World world, PlayerEntity player, int level, float levelProgress) {
		double distance = 50.0;
		double sqrDistance = distance * distance;
		double maxDistance = Math.sqrt(sqrDistance * 2); // pythagoras theorem

		Vec3d sourcePos = player.getPos().add(0, 1.25, 0);

		// perform trig calculations once per ability use for speed
		double calcYaw = Math.toRadians(player.yaw - 180);
		double calcPitch = Math.toRadians(-player.pitch);
		double sinYaw = Math.sin(calcYaw);
		double cosYaw = Math.cos(calcYaw);
		double sinPitch = Math.sin(calcPitch);
		double cosPitch = Math.cos(calcPitch);

		// order entities by distance in case one blocks the ability (imagine breaker?)
		Object2FloatMap<LivingEntity> distanceLookup = new Object2FloatArrayMap<>();
		OrderedList<LivingEntity> entities = new OrderedList<>(distanceLookup::getFloat);

		// iterate over possible targeted entities within the said distance
		for (LivingEntity le : world.getEntities(
				LivingEntity.class,
				new Box(sourcePos, sourcePos.add(1, 1, 1)).expand(maxDistance),
				le -> le != player
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
			// temporary - soon will activate with metal items
			// also will add a coin item :wink:
			float damage = level > 4 ? 11 + (int) levelProgress : 9;

			if (AbilityUserAttack.post(player, le, damage, DamageSource.GENERIC, null)) {
				break;
			}
		}

		// uses up charge
		CHARGED.put(player.getUuid(), false);

		// pass distance (i.e. length of ray) on to the renderer
		return new int[] {
				(USAGE_RAILGUN << 2) | CHARGE_OFF,
				Float.floatToIntBits((float) distance)
		};
	}

	private static int[] performAlterCharge(long time, PlayerEntity user, int altering) {
		switch (altering) {
		case CHARGE_OFF:
			CHARGED.put(user.getUuid(), false);
			break;
		case CHARGE_ON:
			TO_CHARGE.put(user.getUuid(), time + CHARGE_DELAY);
			break;
		}

		return new int[] {
				(USAGE_NONE << 2) | altering
		};
	}

	public static final int USAGE_NONE = 0;
	public static final int USAGE_RAILGUN = 1;

	public static final int CHARGE_EQUAL = 0b00;
	public static final int CHARGE_OFF = 0b01;
	public static final int CHARGE_ON = 0b10;
}
