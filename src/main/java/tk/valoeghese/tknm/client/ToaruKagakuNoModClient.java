package tk.valoeghese.tknm.client;

import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.api.rendering.RenderHooks;
import tk.valoeghese.tknm.client.feature.BiribiriFeature;
import tk.valoeghese.tknm.client.rendercore.AbilityRenderPrimer;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;
import tk.valoeghese.tknm.common.ability.UltimateAbility;

public class ToaruKagakuNoModClient implements ClientModInitializer {
	private void initialiseAPI() {
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
				Ability<?> ability = AbilityRegistry.getAbility(abilityId);

				if (ability != null) {
					AbilityRenderPrimer.getOrCreate().queue.add(world -> {
						ability.getRenderer().renderInfo(world, pos, yaw, pitch, user, data);
					});
				}
			});
		});
	}

	@Override
	public void onInitializeClient() {
		this.initialiseAPI();

		// keybinds
		ultimateAbility = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.tknm.ultimate", GLFW.GLFW_KEY_R, "category.tknm.tknm"));

		// textures
		ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(TEXTURE_BIRIBIRI);
			registry.register(TEXTURE_MELTDOWNER_BEAM);
			registry.register(TEXTURE_BIRIBIRI_BEAM);
		});

		// feature renderers
		RenderHooks.addPlayerRenderFeature(BiribiriFeature::new);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean bl = ultimateAbility.wasPressed();

			if (cooldown > 0) {
				cooldown--;

				if (bl) {
					client.player.sendMessage(new LiteralText("§cUltimate Ability is on Cooldown. " + (int) (cooldown / 20) + " seconds remaining."), true);
				}
			} else if (bl) {
				ACertainComponent component = ToaruKagakuNoMod.A_CERTAIN_COMPONENT.get(client.player);

				if (component.getLevel() > 3) { // min level 4
					Ability<?> ability = component.getAbility();

					if (ability instanceof UltimateAbility) {
						cooldown = 1000;

						PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
						// ability usage type for ultimate, if implements UltimateAbility: 2
						data.writeByte(2);
						ClientSidePacketRegistry.INSTANCE.sendToServer(
								ToaruKagakuNoMod.USE_ABILITY_PACKET_ID,
								data);
					}
				}
			}
		});
	}

	public static KeyBinding ultimateAbility;
	private static long cooldown = 0;
	public static final Identifier TEXTURE_BIRIBIRI = ToaruKagakuNoMod.from("effect/biribiri");
	public static final Identifier TEXTURE_MELTDOWNER_BEAM = ToaruKagakuNoMod.from("effect/meltdowner_beam");
	public static final Identifier TEXTURE_BIRIBIRI_BEAM = ToaruKagakuNoMod.from("effect/shock");
}
