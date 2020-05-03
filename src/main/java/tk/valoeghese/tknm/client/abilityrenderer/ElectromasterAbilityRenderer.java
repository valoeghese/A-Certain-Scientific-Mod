package tk.valoeghese.tknm.client.abilityrenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.rendering.WORST;
import tk.valoeghese.tknm.common.ability.ElectromasterAbility;

public class ElectromasterAbilityRenderer implements AbilityRenderer {
	@Override
	public void renderInfo(ClientWorld world, Vec3d pos, float yaw, float pitch, UUID user, int[] data) {
		final int chargeInfo = data[0] & 0b11;
		final int mode = data[0] >> 2;

		if (mode == ElectromasterAbility.USAGE_RAILGUN) {
			this.railguns.add(new RailgunEntry(
					new Vector3f((float)pos.getX(), (float)pos.getY() + 1.25f, (float)pos.getZ()),
					new Quaternion(0, 270 - yaw, 360 - pitch, true),
					Float.intBitsToFloat(data[1]),
					world.getTime() + 40));
		}

		switch (chargeInfo) {
		case ElectromasterAbility.CHARGE_OFF:
			CHARGED.put(user, false);
			break;
		case ElectromasterAbility.CHARGE_ON:
			CHARGED.put(user, true);
			break;
		}
	}

	private final List<RailgunEntry> railguns = new ArrayList<>();
	private static final Object2BooleanMap<UUID> CHARGED = new Object2BooleanArrayMap<>();

	@Override
	public void render(ClientWorld world) {
		// for every railgun beam, if they exist
		if (!this.railguns.isEmpty()) {
			int i = this.railguns.size();

			while (--i >= 0) {
				if (this.railguns.get(i).render(world)) {
					this.railguns.remove(i);
				}
			}
		}

		CHARGED.forEach((uuid, charged) -> {
			if (charged) {
				PlayerEntity player = world.getPlayerByUuid(uuid);

				if (player != null) {
				}
			}
		});
	}

	private static class RailgunEntry {
		private RailgunEntry(Vector3f pos, Quaternion rotation, float distance, long tickTarget) {
			this.pos = pos;
			this.rotation = rotation;
			this.distance = distance;
			this.tickTarget = tickTarget;
		}

		Vector3f pos;
		Quaternion rotation;
		final float distance;
		final long tickTarget;

		private boolean render(ClientWorld world) {
			WORST.mesh();
			WORST.bindBlockTexture(new Identifier("block/orange_concrete"));
			WORST.basicCube(null, 0.5f, 0, 0);
			WORST.renderMesh(this.pos, this.rotation, new Vector3f(this.distance, 0.12f, 0.12f));
			return world.getTime() >= this.tickTarget;
		}
	}
}