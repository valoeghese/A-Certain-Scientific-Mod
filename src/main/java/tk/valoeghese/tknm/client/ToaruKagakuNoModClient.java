package tk.valoeghese.tknm.client;

import net.fabricmc.api.ClientModInitializer;
import tk.valoeghese.tknm.api.rendering.RenderHooks;
import tk.valoeghese.tknm.client.rendering.AbilityRenderer;

public class ToaruKagakuNoModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RenderHooks.addWorldPlayerRenderHook(new AbilityRenderer());
	}
}
