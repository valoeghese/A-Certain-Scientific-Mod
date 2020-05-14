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
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.client.abilityrenderer.ElectromasterAbilityRenderer;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;
import tk.valoeghese.tknm.mixin.AccessorCreeperEntity;
import tk.valoeghese.tknm.mixin.AccessorEntity;

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
	public int[] performAbility(World world, PlayerEntity player, int level, float levelProgress, byte usage) {
		long time = world.getTime();
		UUID uuid = player.getUuid();
		ItemStack stackInHand = player.getStackInHand(Hand.MAIN_HAND);
		boolean charged = CHARGED.getBoolean(uuid);
		Item itemInHand = stackInHand.getItem();

		if (charged && level > 3 && MAGNETISABLE_ITEMS.containsKey(itemInHand)) {
			return this.performRailgun(world, player, level, levelProgress, MAGNETISABLE_ITEMS.getFloat(itemInHand));
		} else if (stackInHand.isEmpty() && !TO_CHARGE.containsKey(uuid) && !TO_DISCHARGE.containsKey(uuid)) {
			if (player.isSneaking()) {
				if (level > 1) {
					return this.performShockBeam(world, player, level, levelProgress, charged);
				}
			} else {
				return performAlterCharge(time, player, charged ? CHARGE_OFF : CHARGE_ON);
			}
		}

		return null;
	}

	public static final long CHARGE_DELAY_CONSTANT = (long) (1.25 * 20); // TODO based on ability level
	public static final int DISCHARGE_PROPORTION = 2;
	private static final Map<UUID, Long> TO_CHARGE = new HashMap<>();
	private static final Map<UUID, Long> TO_DISCHARGE = new HashMap<>();
	private static final Object2BooleanArrayMap<UUID> CHARGED = new Object2BooleanArrayMap<>();
	private static final Object2LongMap<UUID> LAST_BIRI_SOUND_TIME = new Object2LongArrayMap<>();
	public static final Object2FloatMap<Item> MAGNETISABLE_ITEMS = new Object2FloatArrayMap<>();

	private int[] performShockBeam(World world, PlayerEntity player, int level, float levelProgress, boolean strong) {
		double distance = 20.0;
		distance = Beam.launch(player.getPos(), new Vec3d(0, 1.25, 0), player, distance, false, null, le -> le instanceof CreeperEntity ? 3f : (strong ? 1.5f : 1f) * (float) MathHelper.lerp(levelProgress, level * 4, (level + 1) * 4), (hit, target) -> {
			if (!(target.fallDistance > 10) && (hit || target.isWet())) {
				target.setVelocity(0, 0, 0);
			}

			if (target instanceof CreeperEntity) {
				((AccessorEntity) target).getDataTracker().set(AccessorCreeperEntity.getCharged(), true);
			}

			if (hit) {
				Ability.grantXP(player, 0.01f);
			} else {
				Ability.grantXP(player, 0.001f);
			}
		});

		if (strong) {
			CHARGED.put(player.getUuid(), false);
		}

		Ability.grantXP(player, 0.002f);

		TO_DISCHARGE.put(player.getUuid(), world.getTime() + (long) (CHARGE_DELAY_CONSTANT / 2.5));

		world.playSound(
				null,
				player.getBlockPos().up(),
				ToaruKagakuNoMod.BIRIBIRI_0_SOUND_EVENT,
				SoundCategory.MASTER,
				0.9f + player.getRandom().nextFloat() * 0.1f,
				player.getRandom().nextFloat() * 0.08f + 1f);

		return new int[] {
				(USAGE_SHOCK << 2) | (strong ? CHARGE_OFF : CHARGE_EQUAL),
				Float.floatToIntBits((float) distance)
		};
	}

	private int[] performRailgun(World world, PlayerEntity player, int level, float levelProgress, float strength) {
		double distance = 50.0;
		Vec3d addPos = new Vec3d(0, 1.25, 0);
		Vec3d playerPos = player.getPos();

		int peneration = 10;

		Vec3d beamPos = Beam.rayTraceBlock(world, playerPos.add(addPos), player, distance).getPos();
		Vec3d playerLookPos = player.getRotationVec(0.0f);

		double beamDist = distance;

		while (peneration --> 0) {
			BlockPos pos = new BlockPos(beamPos);

			if (World.isHeightInvalid(pos)) {
				break;
			}

			BlockState state = world.getBlockState(pos);
			beamDist = beamPos.distanceTo(playerPos);

			if (beamDist > distance) {
				break;
			}

			if (state.getBlock().getBlastResistance() > 1000F) { // catches obsidian and similar
				distance = beamDist;
				break;
			}

			if (state.isAir()) {
				beamPos = Beam.rayTraceBlock(world, beamPos, player, distance).getPos();
			} else {
				beamPos = beamPos.add(playerLookPos);
			}
		}

		if (peneration == 0) {
			distance = beamDist;
		}

		Vec3d landPos = Beam.rayTraceBlock(world, playerPos.add(addPos), player, distance).getPos();

		if (!World.isHeightInvalid((int) landPos.y)) {
			if (!world.getBlockState(new BlockPos(landPos)).isAir()) {
				Ability.grantXP(player, 0.01f);
				world.createExplosion(null, landPos.getX(), landPos.getY(), landPos.getZ(), strength * 4.0f, DestructionType.DESTROY);
			}
		}

		Ability.grantXP(player, strength * 0.025f);

		// the object is propelled only at launch, and afterwards its momentum is completely natural. Thus natural attack.
		distance = Beam.launch(playerPos, addPos, player, distance, true, null, target -> strength * (level > 4 ? 27 : 24) + (int) 3 * levelProgress, null);

		// uses up charge
		CHARGED.put(player.getUuid(), false);
		TO_DISCHARGE.put(player.getUuid(), world.getTime() + (long) (CHARGE_DELAY_CONSTANT / 1.5));

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

	public static final int USAGE_NONE = 0;
	public static final int USAGE_RAILGUN = 1;
	public static final int USAGE_SHOCK = 2;

	public static final int CHARGE_EQUAL = 0b00;
	public static final int CHARGE_OFF = 0b01;
	public static final int CHARGE_ON = 0b10;

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
		MAGNETISABLE_ITEMS.put(Items.IRON_ORE, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.IRON_TRAPDOOR, 1.0f);
		MAGNETISABLE_ITEMS.put(Items.LODESTONE, 1.0f);
	}
}
