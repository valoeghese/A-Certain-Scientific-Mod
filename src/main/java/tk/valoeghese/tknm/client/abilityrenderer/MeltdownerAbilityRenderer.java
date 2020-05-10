package tk.valoeghese.tknm.client.abilityrenderer;

import java.util.UUID;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.rendering.WORST;
import tk.valoeghese.tknm.client.ToaruKagakuNoModClient;

public class MeltdownerAbilityRenderer implements AbilityRenderer {
	@Override
	public void renderInfo(ClientWorld world, Vec3d pos, float yaw, float pitch, UUID user, int[] data) {
		this.meltdownerBeamManager.add(new PlasmaBeam(
				new Vector3f((float)pos.getX(), (float)pos.getY() + 2.15f, (float)pos.getZ()),
				new Vector3f(0, 270 - yaw, 360 - pitch),
				Float.intBitsToFloat(data[0]),
				world.getTime() + 32));
	}

	private final BeamRenderManager<PlasmaBeam> meltdownerBeamManager = new BeamRenderManager<>();

	@Override
	public void render(ClientWorld world) {
		this.meltdownerBeamManager.renderUpdate(world);
	}

	private static class PlasmaBeam extends Beam {
		PlasmaBeam(Vector3f pos, Vector3f rotationBase, float distance, long tickTarget) {
			super(pos, rotationBase, distance, tickTarget, 0.14f);
		}

		@Override
		void bindTexture() { // it's pale blue in the novels. deal with it.
			WORST.bindBlockTexture(ToaruKagakuNoModClient.TEXTURE_MELTDOWNER_BEAM);
		}

		@Override
		boolean render(ClientWorld world) {
			boolean result = super.render(world);
			WORST.mesh();
			WORST.basicCube();
			WORST.renderMesh(this.pos, this.rotation, new Vector3f(0.28f, 0.28f, 0.28f));
			return result;
		}
	}
}
