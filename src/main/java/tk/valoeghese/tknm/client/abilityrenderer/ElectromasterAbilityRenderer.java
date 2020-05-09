package tk.valoeghese.tknm.client.abilityrenderer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import tk.valoeghese.tknm.common.ability.ElectromasterAbility;
import tk.valoeghese.tknm.util.MathsUtils;

public class ElectromasterAbilityRenderer implements AbilityRenderer {
	@Override
	public void renderInfo(ClientWorld world, Vec3d pos, float yaw, float pitch, UUID user, int[] data) {
		final int chargeInfo = data[0] & 0b11;
		final int mode = data[0] >> 2;

		if (mode == ElectromasterAbility.USAGE_RAILGUN) {
			this.railgunBeamManager.add(new RailgunBeam(
					new Vector3f((float)pos.getX(), (float)pos.getY() + 1.25f, (float)pos.getZ()),
					new Vector3f(0, 270 - yaw, 360 - pitch),
					Float.intBitsToFloat(data[1]),
					world.getTime() + 25));
		}

		switch (chargeInfo) {
		case ElectromasterAbility.CHARGE_OFF:
			CHARGED.put(user, false);
			TO_DISCHARGE.put(user, new Pair<>(world.getTime(), world.getTime() + (ElectromasterAbility.CHARGE_DELAY / ElectromasterAbility.DISCHARGE_PROPORTION)));
			break;
		case ElectromasterAbility.CHARGE_ON:
			TO_CHARGE.put(user, new Pair<>(world.getTime(), world.getTime() + ElectromasterAbility.CHARGE_DELAY));
			break;
		}
	}

	private final BeamRenderManager<RailgunBeam> railgunBeamManager = new BeamRenderManager<>();
	private static final Map<UUID, Pair<Long, Long>> TO_CHARGE = new HashMap<>();
	private static final Map<UUID, Pair<Long, Long>> TO_DISCHARGE = new HashMap<>();
	private static final Object2BooleanMap<UUID> CHARGED = new Object2BooleanArrayMap<>();

	public static double getOverlayStrength(UUID player, long charge) {
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
		return MathHelper.clamp(MathsUtils.progress(entry.getLeft(), charge, entry.getRight()), 0.0, 1.0);
	}

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

		CHARGED.forEach((uuid, charged) -> {
			if (charged) {
				PlayerEntity player = world.getPlayerByUuid(uuid);

				if (player != null) {
				}
			}
		});
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
}