package tk.valoeghese.tknm.api.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Hooks into the World Renderer for rendering, often with {@link WORST}.
 */
public final class RenderHooks {
	private RenderHooks() {
		// NO-OP
	}

	private static final List<Consumer<ClientWorld>> WORLD_RENDER_HOOKS = new ArrayList<>();
	private static final List<BiConsumer<ClientWorld, PlayerEntity>> WORLD_PLAYER_RENDER_HOOKS = new ArrayList<>();
	private static final List<FeatureRendererFunction<AbstractClientPlayerEntity>> PLAYER_RENDER_FEATURES = new ArrayList<>();

	/**
	 * Adds a rendering hook for using WORST in the world renderer.
	 */
	public static void addWorldRenderHook(Consumer<ClientWorld> consumer) {
		WORLD_RENDER_HOOKS.add(consumer);

	}

	public static void addWorldPlayerRenderHook(BiConsumer<ClientWorld, PlayerEntity> consumer) {
		WORLD_PLAYER_RENDER_HOOKS.add(consumer);
	}

	public static void addPlayerRenderFeature(FeatureRendererFunction<AbstractClientPlayerEntity> function) {
		PLAYER_RENDER_FEATURES.add(function);
	}

	public static void renderWorldRenderHooks(ClientWorld world) {
		for (Consumer<ClientWorld> consumer : RenderHooks.WORLD_RENDER_HOOKS) {
			consumer.accept(world);
		}

		for (PlayerEntity player : world.getPlayers()) {
			for (BiConsumer<ClientWorld, PlayerEntity> consumer : RenderHooks.WORLD_PLAYER_RENDER_HOOKS) {
				consumer.accept(world, player);
			}
		}
	}

	public static void forPlayerFeatureRenderers(Consumer<FeatureRendererFunction<AbstractClientPlayerEntity>> callback) {
		for (FeatureRendererFunction<AbstractClientPlayerEntity> function : PLAYER_RENDER_FEATURES) {
			callback.accept(function);
		}
	}

	@FunctionalInterface
	public static interface FeatureRendererFunction<E extends LivingEntity> extends Function<
	LivingEntityRenderer<E, PlayerEntityModel<E>>, FeatureRenderer<E, PlayerEntityModel<E>>> {
	}
}
