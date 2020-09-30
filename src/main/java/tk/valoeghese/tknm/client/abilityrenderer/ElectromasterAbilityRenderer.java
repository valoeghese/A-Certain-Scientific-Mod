package tk.valoeghese.tknm.client.abilityrenderer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.rendering.WORST;
import tk.valoeghese.tknm.client.ToaruKagakuNoModClient;
import tk.valoeghese.tknm.common.ability.AbstractElectromasterAbility;
import tk.valoeghese.tknm.util.MathsUtils;

public class ElectromasterAbilityRenderer implements AbilityRenderer {
	@Override
	public void renderInfo(ClientWorld world, Vec3d pos, float yaw, float pitch, UUID user, int[] data) {
		final int chargeInfo = data[0] & 0b11;
		final int mode = data[0] >> 2;

		switch (mode) {
		case AbstractElectromasterAbility.USAGE_RAILGUN:
			this.railgunBeamManager.add(new RailgunBeam(
					new Vector3f((float)pos.getX(), (float)pos.getY() + 1.25f, (float)pos.getZ()),
					new Vector3f(0, 270 - yaw, 360 - pitch),
					Float.intBitsToFloat(data[1]),
					world.getTime() + 25));
			break;
		case AbstractElectromasterAbility.USAGE_SHOCK:
			this.shockBeamManager.add(new ShockBeam(
					new Vector3f((float)pos.getX(), (float)pos.getY() + 1.25f, (float)pos.getZ()),
					new Vector3f(0, 270 - yaw, 360 - pitch),
					Float.intBitsToFloat(data[1]),
					world.getTime() + 10
					));
			break;
		case AbstractElectromasterAbility.USAGE_ULTIMATE:
			new Coin(world.getTime()).linkWith(user);
			break;
		}

		switch (chargeInfo) {
		case AbstractElectromasterAbility.CHARGE_OFF:
			CHARGED.put(user, false);
			TO_DISCHARGE.put(user, new Pair<>(world.getTime(), world.getTime() + (AbstractElectromasterAbility.CHARGE_DELAY_CONSTANT / AbstractElectromasterAbility.DISCHARGE_PROPORTION)));
			break;
		case AbstractElectromasterAbility.CHARGE_ON:
			TO_CHARGE.put(user, new Pair<>(world.getTime(), world.getTime() + AbstractElectromasterAbility.CHARGE_DELAY_CONSTANT));
			break;
		}
	}

	private final BeamRenderManager<RailgunBeam> railgunBeamManager = new BeamRenderManager<>();
	private final BeamRenderManager<ShockBeam> shockBeamManager = new BeamRenderManager<>();

	@Override
	public void render(ClientWorld world) {
		long time = world.getTime();

		// Charge
		if (!TO_CHARGE.isEmpty()) {
			Set<Map.Entry<UUID, Pair<Long, Long>>> currentSet = new HashSet<>(TO_CHARGE.entrySet());

			for (Map.Entry<UUID, Pair<Long, Long>> entry : currentSet) {
				if (entry.getValue().getRight() < time) {
					CHARGED.put(entry.getKey(), true);
					TO_CHARGE.remove(entry.getKey());
				}
			}
		}

		// Discharge
		if (!TO_DISCHARGE.isEmpty()) {
			Set<Map.Entry<UUID, Pair<Long, Long>>> currentSet = new HashSet<>(TO_DISCHARGE.entrySet());

			for (Map.Entry<UUID, Pair<Long, Long>> entry : currentSet) {
				if (entry.getValue().getRight() < time) {
					TO_DISCHARGE.remove(entry.getKey());
				}
			}
		}

		this.railgunBeamManager.renderUpdate(world);
		this.shockBeamManager.renderUpdate(world);

		CHARGED.forEach((uuid, charged) -> {
			if (charged) {
				PlayerEntity player = world.getPlayerByUuid(uuid);

				if (player != null) {
				}
			}
		});
	}

	public static double getOverlayStrength(UUID player, long time) {
		// if fully charged
		if (CHARGED.getOrDefault(player, false)) {
			return 1.0;
		}

		Pair<Long, Long> entry = TO_CHARGE.get(player);

		// if not building up charge
		if (entry == null) {
			entry = TO_DISCHARGE.get(player);

			// if not discharging
			if (entry == null) {
				return 0.0;
			}

			// swap for discharge
			entry = new Pair<>(entry.getRight(), entry.getLeft());
		}

		// inverse lerp with clamp
		return MathHelper.clamp(MathsUtils.progress(entry.getLeft(), time, entry.getRight()), 0.0, 1.0);
	}

	@Nullable
	public static Coin getFlippedCoin(UUID uuid) {
		return COINS.get(uuid);
	}

	private static final Map<UUID, Pair<Long, Long>> TO_CHARGE = new HashMap<>();
	private static final Map<UUID, Pair<Long, Long>> TO_DISCHARGE = new HashMap<>();
	private static final Object2BooleanMap<UUID> CHARGED = new Object2BooleanArrayMap<>();
	private static final Map<UUID, Coin> COINS = new HashMap<>();

	public static class Coin {
		public Coin(long time) {
			this.start = time;
			this.peak = this.start + 40;
			this.end = this.peak + 40;
		}

		private final long start;
		private final long peak;
		private final long end;
		private UUID uuid;

		public boolean isEnded(long time) {
			return time > this.end;
		}

		public double heightProg(long time) {
			if (time < this.peak) {
				return MathsUtils.progress(this.start, time, this.peak);
			} else {
				return MathsUtils.progress(this.peak, time, this.end);
			}
		}

		public Coin linkWith(UUID uuid) {
			COINS.put(uuid, this);
			this.uuid = uuid;
			return this;
		}

		public void remove() {
			COINS.remove(this.uuid);
		}
	}

	private static class RailgunBeam extends Beam {
		private RailgunBeam(Vector3f pos, Vector3f rotationBase, float distance, long tickTarget) {
			super(pos, rotationBase, distance, tickTarget, 0.09f);
		}

		@Override
		void bindTexture() {
			WORST.bindBlockTexture(new Identifier("block/orange_concrete"));
		}
	}

	private static class ShockBeam extends Beam {
		ShockBeam(Vector3f pos, Vector3f rotationBase, float distance, long tickTarget) {
			super(pos, rotationBase, distance, tickTarget, 1.0f);
			this.count = MathHelper.ceil(this.distance / this.thickness);
		}

		private int count;

		@Override
		void bindTexture() {
			WORST.bindBlockTexture(ToaruKagakuNoModClient.TEXTURE_BIRIBIRI_BEAM);
		}

		@Override
		boolean render(ClientWorld world) {
			WORST.mesh();
			this.bindTexture();

			for (int i = 0; i < this.count; ++i) {
				WORST.noEndsDoubleCube(null, 0.5f + i, 0.0f, 0.0f, WORST.AXIS_X);
			}

			WORST.renderMesh(this.pos, this.rotation, null);

			return world.getTime() >= this.tickTarget;
		}
	}
}