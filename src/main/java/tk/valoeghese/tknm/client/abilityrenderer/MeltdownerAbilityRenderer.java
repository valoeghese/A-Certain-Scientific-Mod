package tk.valoeghese.tknm.client.abilityrenderer;

import java.util.UUID;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.rendering.WORST;

public class MeltdownerAbilityRenderer implements AbilityRenderer {
	@Override
	public void renderInfo(ClientWorld world, Vec3d pos, float yaw, float pitch, UUID user, int[] data) {
		switch(data[0]) {
		case 0:
			this.meltdownerBeamManager.add(new PlasmaBeam(
					new Vector3f((float)pos.getX(), (float)pos.getY() + 2.15f, (float)pos.getZ()),
					new Vector3f(0, 270 - yaw, 360 - pitch),
					Float.intBitsToFloat(data[1]),
					world.getTime() + 32));
			break;
		}
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
			WORST.bindBlockTexture(new Identifier("block/light_blue_concrete"));
		}
	}
}
