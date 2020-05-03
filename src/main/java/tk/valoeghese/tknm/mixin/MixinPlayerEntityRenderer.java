package tk.valoeghese.tknm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import tk.valoeghese.tknm.api.rendering.RenderHooks;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public MixinPlayerEntityRenderer(EntityRenderDispatcher dispatcher, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(dispatcher, model, shadowRadius);
		// dummy
	}

	@Inject(
			at = @At("RETURN"),
			method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V"
			)
	private void onConstruct(EntityRenderDispatcher dispatcher, boolean bl, CallbackInfo info) {
		RenderHooks.forPlayerFeatureRenderers(f -> this.addFeature(f.apply(this)));
	}
}
