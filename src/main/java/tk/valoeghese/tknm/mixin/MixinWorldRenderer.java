package tk.valoeghese.tknm.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.impl.renderer.RendererAccessImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
	@Inject(at = @At("RETURN"), method = "render")
	private void addRendering(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
		matrices.push();
		Vec3d pos = camera.getPos();
		matrices.translate(-pos.x, -pos.y + 64, -pos.z);
		matrices.scale(1, 1, 1);

		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer vc = immediate.getBuffer(RenderLayer.getSolid());

		Renderer renderer = RendererAccessImpl.INSTANCE.getRenderer();
		MeshBuilder meshBuilder = renderer.meshBuilder();
		QuadEmitter emitter = meshBuilder.getEmitter();

		emitter
		// front side
		.pos(0, 0, 0, 0)
		.pos(1, 0, 0, 1)
		.pos(2, 1, 0, 1)
		.pos(3, 1, 0, 0).emit()
		// reverse side
		.pos(0, 0, 0, 0)
		.pos(1, 1, 0, 0)
		.pos(2, 1, 0, 1)
		.pos(3, 0, 0, 1).emit();

		Mesh m = meshBuilder.build();
		List<BakedQuad>[] quadListArray = ModelHelper.toQuadLists(m);

		for (int i = 0; i < quadListArray.length; ++i) {
			for (BakedQuad bq : quadListArray[i]) {
				vc.quad(matrices.peek(), bq, 0.5f, 0.5f, 0.5f, 15728880, OverlayTexture.DEFAULT_UV);
			}
		}

		// flush
		immediate.draw(RenderLayer.getSolid());
		matrices.pop();
	}
}
