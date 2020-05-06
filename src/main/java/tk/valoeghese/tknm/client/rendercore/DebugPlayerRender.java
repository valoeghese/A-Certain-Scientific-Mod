package tk.valoeghese.tknm.client.rendercore;

import java.util.function.BiConsumer;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import tk.valoeghese.tknm.api.rendering.WORST;

public class DebugPlayerRender implements BiConsumer<ClientWorld, PlayerEntity> {
	@Override
	public void accept(ClientWorld world, PlayerEntity player) {
		WORST.mesh();
		WORST.bindBlockTexture(new Identifier("block/stone"));
		WORST.basicCube();
		WORST.renderMesh(new Vector3f((float)player.getX(), (float)player.getY() + 2.5f, (float)player.getZ()), null, null);
	}
}
