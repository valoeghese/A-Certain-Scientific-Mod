package tk.valoeghese.tknm.client.feature;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.rendering.WORST;
import tk.valoeghese.tknm.client.ToaruKagakuNoModClient;
import tk.valoeghese.tknm.client.abilityrenderer.ElectromasterAbilityRenderer;
import tk.valoeghese.tknm.rendering.WORSTImpl;

public class BiribiriFeature extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public BiribiriFeature(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vcProvider, int light,
			AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta,
			float customAngle, float headYaw, float headPitch) {
		// stolen from HeldItemFeatureRenderer

		matrices.push();
		if (this.getContextModel().child) {
			float scale = 0.5F;
			matrices.translate(0.0D, 0.75D, 0.0D);
			matrices.scale(scale, scale, scale);
		}

		if (ElectromasterAbilityRenderer.getOverlayStrength(entity.getUuid(), entity.world.getTime()) > 0.0) {
			WORSTImpl.init(matrices, () -> Vec3d.ZERO);
			WORST.mesh();
			WORST.bindBlockTexture(ToaruKagakuNoModClient.TEXTURE_BIRIBIRI);
			WORST.basicDoubleCube();
			WORST.renderMesh(new Vector3f(0.0f, 0.5f, 0.0f), null, new Vector3f(1.0f, entity.isSneaking() ? 1.5f : 2.0f, 1.0f));
			WORSTImpl.end();
		}
		matrices.pop();
	}

}
