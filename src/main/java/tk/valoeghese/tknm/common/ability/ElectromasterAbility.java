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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
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
		ItemStack stackInHand = player.getStackInHand(Hand.MAIN_HAND);
		updateCharge(time);
		boolean charged = CHARGED.getBoolean(uuid);

		if (charged && level > 3 && MAGNETISABLE_ITEMS.contains(stackInHand.getItem())) {
			return this.performRailgun(world, player, level, levelProgress);
		} else if (stackInHand.isEmpty() && !TO_CHARGE.containsKey(uuid) && !TO_DISCHARGE.containsKey(uuid)) {
			return performAlterCharge(time, player, charged ? CHARGE_OFF : CHARGE_ON);
		}

		return null;
	}

	public static final long CHARGE_DELAY = (long) (1.25 * 20); // TODO based on ability level
	public static final int DISCHARGE_PROPORTION = 2;
	private static final Map<UUID, Long> TO_CHARGE = new HashMap<>();
	private static final Map<UUID, Long> TO_DISCHARGE = new HashMap<>();
	private static final Object2BooleanArrayMap<UUID> CHARGED = new Object2BooleanArrayMap<>();
	public static final Set<Item> MAGNETISABLE_ITEMS = new HashSet<>();

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
			float damage = level > 4 ? 22 + (int) 2 * levelProgress : 20;

			if (AbilityUserAttack.post(player, le, damage, DamageSource.player(player), null)) {
				break;
			}
		}

		// uses up charge
		CHARGED.put(player.getUuid(), false);
		TO_DISCHARGE.put(player.getUuid(), world.getTime() + (CHARGE_DELAY / 3));

		// uses up item
		if (!player.isCreative()) {
			player.getStackInHand(Hand.MAIN_HAND).decrement(1);
		}

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
			TO_DISCHARGE.put(user.getUuid(), time + (CHARGE_DELAY / DISCHARGE_PROPORTION));
			break;
		case CHARGE_ON:
			TO_CHARGE.put(user.getUuid(), time + CHARGE_DELAY);
			break;
		}

		return new int[] {
				(USAGE_NONE << 2) | altering
		};
	}

	private static void updateCharge(long time) {
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

		// Disharge
		if (!TO_DISCHARGE.isEmpty()) {
			Set<Map.Entry<UUID, Long>> currentSet = new HashSet<>(TO_DISCHARGE.entrySet());

			for (Map.Entry<UUID, Long> entry : currentSet) {
				if (entry.getValue() < time) {
					TO_DISCHARGE.remove(entry.getKey());
				}
			}
		}
	}

	public static final int USAGE_NONE = 0;
	public static final int USAGE_RAILGUN = 1;

	public static final int CHARGE_EQUAL = 0b00;
	public static final int CHARGE_OFF = 0b01;
	public static final int CHARGE_ON = 0b10;

	static {
		MAGNETISABLE_ITEMS.add(Items.IRON_BARS);
		MAGNETISABLE_ITEMS.add(Items.IRON_BLOCK);
		MAGNETISABLE_ITEMS.add(Items.IRON_BOOTS);
		MAGNETISABLE_ITEMS.add(Items.IRON_CHESTPLATE);
		MAGNETISABLE_ITEMS.add(Items.IRON_DOOR);
		MAGNETISABLE_ITEMS.add(Items.IRON_HELMET);
		MAGNETISABLE_ITEMS.add(Items.IRON_HORSE_ARMOR);
		MAGNETISABLE_ITEMS.add(Items.IRON_INGOT);
		MAGNETISABLE_ITEMS.add(Items.IRON_LEGGINGS);
		MAGNETISABLE_ITEMS.add(Items.IRON_NUGGET);
		MAGNETISABLE_ITEMS.add(Items.IRON_ORE);
		MAGNETISABLE_ITEMS.add(Items.IRON_TRAPDOOR);
		MAGNETISABLE_ITEMS.add(Items.LODESTONE);
	}
}
