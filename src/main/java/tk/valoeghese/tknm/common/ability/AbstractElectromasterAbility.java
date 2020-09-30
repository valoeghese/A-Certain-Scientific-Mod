package tk.valoeghese.tknm.common.ability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.ability.AbilityUserData;
import tk.valoeghese.tknm.client.abilityrenderer.ElectromasterAbilityRenderer;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;
import tk.valoeghese.tknm.common.tech.CertainItems;

public abstract class AbstractElectromasterAbility extends Ability<AbstractElectromasterAbility.Data> {
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

			if (thisTime - lastTime > biriDelay) { // I need better biribiri sound effects. Like, not ripped from clips of the anime.
				biriDelay = 1800 + 200 * user.getRandom().nextInt(10); // 0ms - 1800ms, in 200ms gaps 
				LAST_BIRI_SOUND_TIME.put(uuid, thisTime);

				SoundEvent event = ToaruKagakuNoMod.biribiriSound(user.getRandom());

				world.playSound(
						null,
						user.getBlockPos().up(),
						event,
						SoundCategory.MASTER,
						0.9f + user.getRandom().nextFloat() * 0.1f,
						user.getRandom().nextFloat() * 0.16f + 1f - 0.08f);
			}
		}
	}

	@Override
	public Data createUserData(PlayerEntity user) {
		return new Data(user);
	}

	protected static int[] performAlterCharge(long time, PlayerEntity user, int altering) {
		switch (altering) {
		case CHARGE_OFF:
			CHARGED.put(user.getUuid(), false);
			TO_DISCHARGE.put(user.getUuid(), time + (CHARGE_DELAY_CONSTANT / DISCHARGE_PROPORTION));
			break;
		case CHARGE_ON:
			Ability.grantXP(user, 0.0005f);
			TO_CHARGE.put(user.getUuid(), time + CHARGE_DELAY_CONSTANT);
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

	public static final long CHARGE_DELAY_CONSTANT = (long) (1.25 * 20); // TODO based on ability level
	public static final int DISCHARGE_PROPORTION = 2;
	protected static final Map<UUID, Long> TO_CHARGE = new HashMap<>();
	protected static final Map<UUID, Long> TO_DISCHARGE = new HashMap<>();
	static final Object2BooleanArrayMap<UUID> CHARGED = new Object2BooleanArrayMap<>();
	private static final Object2LongMap<UUID> LAST_BIRI_SOUND_TIME = new Object2LongArrayMap<>();
	public static final Object2FloatMap<Item> MAGNETISABLE_ITEMS = new Object2FloatArrayMap<>();

	public static final int USAGE_NONE = 0;
	public static final int USAGE_RAILGUN = 1;
	public static final int USAGE_SHOCK = 2;
	public static final int USAGE_ULTIMATE = 3;

	public static final int CHARGE_EQUAL = 0b00;
	public static final int CHARGE_OFF = 0b01;
	public static final int CHARGE_ON = 0b10;

	// TODO per player
	private static long biriDelay = 0;

	static {
		MAGNETISABLE_ITEMS.put(Items.IRON_BARS, 0.9f);
		MAGNETISABLE_ITEMS.put(Items.IRON_BLOCK, 1.25f);
		MAGNETISABLE_ITEMS.put(Items.IRON_BOOTS, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.IRON_CHESTPLATE, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.IRON_DOOR, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.IRON_HELMET, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.IRON_HORSE_ARMOR, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.IRON_INGOT, 0.67f);
		MAGNETISABLE_ITEMS.put(Items.IRON_LEGGINGS, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.IRON_NUGGET, 0.33f);
		MAGNETISABLE_ITEMS.put(CertainItems.COIN, 0.33f);

		MAGNETISABLE_ITEMS.put(Items.IRON_ORE, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.IRON_TRAPDOOR, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.LODESTONE, 1.3f);
	}

	// TODO do charge related stuff in data instead of maps
	static class Data implements AbilityUserData {
		public Data(PlayerEntity user) {
			this.uuid = user.getUuid();
		}

		private final UUID uuid;

		@Override
		public CompoundTag toTag() {
			CompoundTag result = new CompoundTag();
			result.putBoolean("charged", CHARGED.getBoolean(this.uuid));
			return result;
		}

		@Override
		public void fromTag(CompoundTag tag) {
			if (tag != null) {
				if (tag.getBoolean("charged")) {
					TO_CHARGE.put(this.uuid, 0L);
				}
			}
		}
	}
}
