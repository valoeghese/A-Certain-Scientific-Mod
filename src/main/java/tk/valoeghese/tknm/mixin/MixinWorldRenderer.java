package tk.valoeghese.tknm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;
import tk.valoeghese.tknm.api.rendering.WORST;
import tk.valoeghese.tknm.rendering.WORSTImpl;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
	@Shadow
	private ClientWorld world;

	@Inject(at = @At("RETURN"), method = "render")
	private void addRendering(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
		// start
		WORSTImpl.init(matrices, camera);
		// vertices
		WORST.mesh();
		WORST.flatSquare();
		WORST.renderMesh(new Vector3f(0.5f, 64, 0), null, null);
		// end
		WORSTImpl.end();
	}
}
