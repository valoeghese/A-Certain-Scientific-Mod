package tk.valoeghese.tknm.common.render;

import java.util.function.BiConsumer;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import tk.valoeghese.tknm.api.rendering.WORST;
import tk.valoeghese.tknm.rendering.WORSTImpl;

public class DebugPlayerRender implements BiConsumer<ClientWorld, PlayerEntity> {
	@Override
	public void accept(ClientWorld world, PlayerEntity player) {
		WORST.mesh();
		WORST.basicCube();
		WORST.renderMeshRaw(new Vector3f((float)player.getBlockPos().getX(), (float)player.getBlockPos().getY(), (float)player.getBlockPos().getZ()), null, null);
	}
}
