package tk.valoeghese.tknm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;
import tk.valoeghese.tknm.rendering.WORSTImpl;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
	@Inject(at = @At("RETURN"), method = "render")
	private void addRendering(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
		// start
		WORSTImpl.init(matrices, camera);
		// vertices
		WORSTImpl.mesh();
		WORSTImpl.vertex(0, 0, 0);
		WORSTImpl.vertex(0, 0, 1);
		WORSTImpl.vertex(1, 0, 1);
		WORSTImpl.vertex(1, 0, 0);
		WORSTImpl.renderMesh(new Vector3f(0.5f, 64, 0), null);
		// end
		WORSTImpl.end();
	}
}
