package tk.valoeghese.tknm.client.rendercore;

import java.util.function.Consumer;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import tk.valoeghese.tknm.api.rendering.WORST;

public class DebugRender implements Consumer<ClientWorld> {
	@Override
	public void accept(ClientWorld world) {
		WORST.mesh();
		WORST.bindBlockTexture(new Identifier("block/obsidian"));
		WORST.basicCube();
		int t = (int) (world.getTime() % 360);

		WORST.renderMesh(new Vector3f(0.5f, 80, 0.5f), new Quaternion(0, t, 0, true), new Vector3f(3.0f, 1.0f, 1.0f));
		// second mesh
		WORST.mesh();
		WORST.bindBlockTexture(new Identifier("block/white_wool"));
		WORST.basicCube();
		WORST.renderMesh(new Vector3f(0.5f, 50, 0.5f), new Quaternion(0, t, 0, true), new Vector3f(3.0f, 1.0f, 1.0f));
	}
}
