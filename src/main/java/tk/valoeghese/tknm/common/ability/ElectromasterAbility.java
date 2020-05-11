package tk.valoeghese.tknm.common.ability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.client.abilityrenderer.ElectromasterAbilityRenderer;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;

public class ElectromasterAbility extends Ability {
	@Override
	public AbilityRenderer createAbilityRenderer() {
		return new ElectromasterAbilityRenderer();
	}

	@Override
	public void tick(MinecraftServer server, PlayerEntity user, ACertainComponent component) {
		UUID uuid = user.getUuid();
		World world = user.getEntityWorld();
		updateCharge(world.getTime());

		if (CHARGED.getBoolean(uuid)) {
			long thisTime = System.currentTimeMillis();
			long lastTime = LAST_BIRI_SOUND_TIME.applyAsLong(uuid);

			if (thisTime - lastTime > biriDelay) {
				biriDelay = 1800 + 200 * user.getRandom().nextInt(10); // 0ms - 1800ms, in 200ms gaps 
				LAST_BIRI_SOUND_TIME.put(uuid, thisTime);

				SoundEvent event = ToaruKagakuNoMod.biribiriSound(user.getRandom());

				world.playSound(
						null,
						user.getBlockPos().up(),
						event,
						SoundCategory.MASTER,
						0.9f + user.getRandom().nextFloat() * 0.1f + (event == ToaruKagakuNoMod.BIRIBIRI_0_SOUND_EVENT ? -0.5f : 0.0f),
						user.getRandom().nextFloat() * 0.08f + 1f);
			}
		}
	}

	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float levelProgress, byte usage) {
		long time = world.getTime();
		UUID uuid = player.getUuid();
		ItemStack stackInHand = player.getStackInHand(Hand.MAIN_HAND);
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
	private static final Object2LongMap<UUID> LAST_BIRI_SOUND_TIME = new Object2LongArrayMap<>();
	public static final Set<Item> MAGNETISABLE_ITEMS = new HashSet<>();

	private int[] performRailgun(World world, PlayerEntity player, int level, float levelProgress) {
		double distance = 50.0;
		// the object is propelled only at launch, and afterwards its momentum is completely natural. Thus natural attack.
		distance = Beam.launch(player.getPos(), new Vec3d(0, 1.25, 0), player, distance, true, null, () -> level > 4 ? 22 + (int) 2 * levelProgress : 20, null);

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

	private static long biriDelay = 0;

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
