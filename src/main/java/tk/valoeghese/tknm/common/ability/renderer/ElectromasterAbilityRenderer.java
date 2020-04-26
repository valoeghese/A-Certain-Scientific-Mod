package tk.valoeghese.tknm.common.ability.renderer;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;
import tk.valoeghese.tknm.api.rendering.WORST;

public class ElectromasterAbilityRenderer implements AbilityRenderer {
	@Override
	public void renderInfo(Vec3d pos, float yaw, float pitch, int usage, int[] data) {
		this.pos = new Vector3f(pos);
	}

	private Vector3f pos;

	@Override
	public void render(ClientWorld world) {
		if (this.pos != null) {
			WORST.mesh();
			WORST.bindBlockTexture(new Identifier("block/stone"));
			WORST.basicCube();
			WORST.renderMeshRaw(this.pos, null, null);
		}
	}
}
