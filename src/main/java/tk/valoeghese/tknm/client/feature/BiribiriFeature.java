package tk.valoeghese.tknm.client.feature;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
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
		boolean bl = entity.getMainArm() == Arm.RIGHT;
		ItemStack itemStack = bl ? entity.getOffHandStack() : entity.getMainHandStack();
		ItemStack itemStack2 = bl ? entity.getMainHandStack() : entity.getOffHandStack();
		if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
			matrices.push();
			if (this.getContextModel().child) {
				float scale = 0.5F;
				matrices.translate(0.0D, 0.75D, 0.0D);
				matrices.scale(scale, scale, scale);
			}

			WORSTImpl.init(matrices, () -> Vec3d.ZERO);
			WORSTImpl.end();
			matrices.pop();
		}
	}

}
