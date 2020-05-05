package tk.valoeghese.tknm.client;

import java.util.UUID;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.api.rendering.RenderHooks;
import tk.valoeghese.tknm.client.feature.BiribiriFeature;
import tk.valoeghese.tknm.client.rendering.AbilityRenderPrimer;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;

public class ToaruKagakuNoModClient implements ClientModInitializer {
	public static final Identifier TEXTURE_BIRIBIRI = ToaruKagakuNoMod.from("effect/biribiri");

	@Override
	public void onInitializeClient() {
		// textures
		ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> {
			registry.register(TEXTURE_BIRIBIRI);
		});

		// render hooks
		RenderHooks.addWorldRenderHook(AbilityRenderPrimer.getOrCreate());
		// other stuff
		ClientSidePacketRegistry.INSTANCE.register(ToaruKagakuNoMod.RENDER_ABILITY_PACKET_ID, (context, dataManager) -> {
			double x = dataManager.readDoubleLE();
			double y = dataManager.readDoubleLE();
			double z = dataManager.readDoubleLE();
			Vec3d pos = new Vec3d(x, y, z);
			float yaw = dataManager.readFloatLE();
			float pitch = dataManager.readFloatLE();
			Identifier abilityId = dataManager.readIdentifier();
			UUID user = dataManager.readUuid();
			int[] data = dataManager.readIntArray();

			context.getTaskQueue().execute(() -> {
				Ability ability = AbilityRegistry.getAbility(abilityId);

				if (ability != null) {
					AbilityRenderPrimer.getOrCreate().queue.add(world -> {
						ability.getRenderer().renderInfo(world, pos, yaw, pitch, user, data);
					});
				}
			});
		});
		// feature renderers
		RenderHooks.addPlayerRenderFeature(BiribiriFeature::new);
	}
}
