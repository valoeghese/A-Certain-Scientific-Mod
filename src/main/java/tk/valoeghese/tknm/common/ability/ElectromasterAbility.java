package tk.valoeghese.tknm.common.ability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;
import tk.valoeghese.tknm.common.tech.CertainItems;
import tk.valoeghese.tknm.mixin.AccessorCreeperEntity;
import tk.valoeghese.tknm.mixin.AccessorEntity;;

public class ElectromasterAbility extends AbstractElectromasterAbility implements UltimateAbility {
	@Override
	public void tick(MinecraftServer server, PlayerEntity user, ACertainComponent component) {
		super.tick(server, user, component);

		UUID uuid = user.getUuid();

		if (COINS.containsKey(uuid)) {
			Coin coin = COINS.get(uuid);

			if (coin.isEnded(user.world.getTime())) {
				ToaruKagakuNoMod.sendClientData(AbilityRegistry.getRegistryId(this),
						user,
						performRailgun(
								user.world,
								user,
								component.getLevel(),
								component.getLevelProgress(),
								MAGNETISABLE_ITEMS.getFloat(CertainItems.COIN) * coin.remove() + 0.67f));
			}
		}
	}

	@Override
	public int[] performAbility(World world, PlayerEntity player, int level, float levelProgress, byte usage, Data data) {
		if (level == 0) {
			return null;
		}

		long time = world.getTime();
		UUID uuid = player.getUuid();
		ItemStack stackInHand = player.getStackInHand(Hand.MAIN_HAND);
		boolean charged = CHARGED.getBoolean(uuid);
		Item itemInHand = stackInHand.getItem();

		switch (usage) {
		case 1:
			if (charged && level > 3 && MAGNETISABLE_ITEMS.containsKey(itemInHand)) {
				// TODO do something else and let railgun just be the special ability - some form of non-railgun projectile?
				return this.performRailgun(world, player, level, levelProgress, MAGNETISABLE_ITEMS.getFloat(itemInHand));
			} else if (stackInHand.isEmpty() && !TO_CHARGE.containsKey(uuid) && !TO_DISCHARGE.containsKey(uuid)) {
				if (player.isSneaking()) {
					if (level > 1 || charged) {
						// TODO level 1 has other abilities, not this
						// when should I add climbing on magnetic objects?
						return this.performShockBeam(world, player, level, levelProgress, charged && (level > 1));
					}
				} else {
					return performAlterCharge(time, player, charged ? CHARGE_OFF : CHARGE_ON);
				}
			}
			break;
		case 2: // ultimate
			if (level > 3 && stackInHand.getItem() == CertainItems.COIN) {
				int count = stackInHand.getCount();
				stackInHand.decrement(count);
				return performUltimateRailgun(time, uuid, charged, count);
			}
			break;
		}

		return null;
	}

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

		if (strong || level == 1) { // TODO this will also change
			CHARGED.put(player.getUuid(), false);
		}

		Ability.grantXP(player, 0.002f);
		Ability.exhaust(player, level, 1.0f);

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
		Ability.exhaust(player, level, 1.5f);

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

	private static int[] performUltimateRailgun(long time, UUID user, boolean charged, int count) {
		new Coin(time, count).linkWith(user);
		TO_CHARGE.put(user, time + 300);

		return new int[] {
				(USAGE_ULTIMATE << 2) | (charged ? CHARGE_EQUAL : CHARGE_ON)
		};
	}

	private static final Map<UUID, Coin> COINS = new HashMap<>();
	public static final int ANIM_HALF_TICKS = 15;

	private static class Coin {
		public Coin(long time, int count) {
			this.start = time;
			this.end = this.start + 2 * ANIM_HALF_TICKS;
			this.count = count;
		}

		private final long start;
		private final long end;
		private final int count;
		private UUID uuid;

		public boolean isEnded(long time) {
			return time > this.end;
		}

		public Coin linkWith(UUID uuid) {
			COINS.put(uuid, this);
			this.uuid = uuid;
			return this;
		}

		public int remove() {
			COINS.remove(this.uuid);
			return this.count;
		}
	}
}
