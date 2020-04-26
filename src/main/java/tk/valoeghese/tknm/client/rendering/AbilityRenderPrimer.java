package tk.valoeghese.tknm.client.rendering;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import net.minecraft.client.world.ClientWorld;
import tk.valoeghese.tknm.api.ability.AbilityRenderer;

public class AbilityRenderPrimer implements Consumer<ClientWorld> {
	private AbilityRenderPrimer() {
		instance = this;
	}

	@Override
	public void accept(ClientWorld world) {
		while (!queue.isEmpty()) {
			queue.remove().accept(world);
		}

		for (AbilityRenderer renderer : this.renderers) {
			renderer.render(world);
		}
	}

	public static AbilityRenderPrimer getOrCreate() {
		return instance == null ? new AbilityRenderPrimer() : instance;
	}

	public final Queue<Consumer<ClientWorld>> queue = new LinkedList<>();
	public final List<AbilityRenderer> renderers = new ArrayList<>();
	private static AbilityRenderPrimer instance;
}
