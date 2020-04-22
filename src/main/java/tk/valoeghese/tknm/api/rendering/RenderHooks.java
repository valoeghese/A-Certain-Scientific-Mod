package tk.valoeghese.tknm.api.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.world.ClientWorld;

/**
 * Hooks into the World Renderer for rendering with {@link WORST}.
 */
public final class RenderHooks {
	private RenderHooks() {
		// NO-OP
	}

	private static final List<Consumer<ClientWorld>> WORLD_RENDER_HOOKS = new ArrayList<>();

	public static void addWorldRenderHook(Consumer<ClientWorld> consumer) {
		WORLD_RENDER_HOOKS.add(consumer);
	}

	public static void renderWorldRenderHooks(ClientWorld world) {
		for (Consumer<ClientWorld> consumer : RenderHooks.WORLD_RENDER_HOOKS) {
			consumer.accept(world);
		}
	}
}
