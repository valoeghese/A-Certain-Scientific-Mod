package tk.valoeghese.tknm.common.render;

import java.util.function.Consumer;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import tk.valoeghese.tknm.api.rendering.WORST;

public class DebugRender implements Consumer<ClientWorld> {
	@Override
	public void accept(ClientWorld t) {
		WORST.mesh();
		WORST.basicCube();
		WORST.renderMeshRaw(new Vector3f(0.5f, 64, 0.5f), false, null, null);
	}
}
