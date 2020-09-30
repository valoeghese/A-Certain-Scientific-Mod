package tk.valoeghese.tknm.client.feature;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.rendering.WORST;
import tk.valoeghese.tknm.client.ToaruKagakuNoModClient;
import tk.valoeghese.tknm.client.abilityrenderer.ElectromasterAbilityRenderer;
import tk.valoeghese.tknm.client.abilityrenderer.ElectromasterAbilityRenderer.Coin;

public class BiribiriFeature extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public BiribiriFeature(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vcProvider, int light,
			AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta,
			float customAngle, float headYaw, float headPitch) {
		// some code taken from HeldItemFeatureRenderer

		long ticks = entity.world.getTime();

		matrices.push();
		if (this.getContextModel().child) {
			float scale = 0.5F;
			matrices.translate(0.0D, 0.75D, 0.0D);
			matrices.scale(scale, scale, scale);
		}

		if (ElectromasterAbilityRenderer.getOverlayStrength(entity.getUuid(), entity.world.getTime()) > 0.0) {
			WORST.begin(matrices, () -> Vec3d.ZERO);
			WORST.mesh(RenderLayer.getCutout());
			WORST.bindBlockTexture(ToaruKagakuNoModClient.TEXTURE_BIRIBIRI);
			WORST.basicDoubleCube();
			matrices.translate(0.0f, 0.5f, 0.0f);
			matrices.multiply(new Quaternion(0.0f, 0.0f, 180f, true));
			matrices.scale(1.0f, entity.isSneaking() ? 1.5f : 2.0f, 1.0f);
			WORST.renderMesh();
			WORST.end();
		}

		matrices.pop();

		Coin coin = ElectromasterAbilityRenderer.getFlippedCoin(entity.getUuid());
		matrices.push();

		if (this.getContextModel().child) {
			float scale = 0.5F;
			matrices.translate(0.0D, 0.75D, 0.0D);
			matrices.scale(scale, scale, scale);
		}

		if (coin != null) {
			matrices.push();
			Arm arm = Arm.RIGHT;

			((ModelWithArms)this.getContextModel()).setArmAngle(arm, matrices);
			matrices.translate(0, -coin.heightProg(ticks), 0);
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			matrices.translate((double)((float)(arm == Arm.LEFT ? -1 : 1) / 16.0F), 0.125D, -0.625D);

			WORST.begin(matrices, () -> Vec3d.ZERO);
			WORST.mesh(RenderLayer.getCutout());
			WORST.bindBlockTexture(BLOCK_IRON_BLOCK);
			WORST.flatSquare();
			matrices.translate(0, 0.05, 0);
			matrices.multiply(new Quaternion((float) Math.PI * MathHelper.sin((float) ticks / 2.0f), 0.0f, 0.0f, false));
			matrices.scale(0.2f, 1.0f, 0.2f);
			WORST.renderMesh();
			WORST.end();
			matrices.pop();

			if (coin.isEnded(ticks)) {
				coin.remove();
			}
		}

		matrices.pop();
	}

	private static final Identifier BLOCK_IRON_BLOCK = new Identifier("block/iron_block");
}
