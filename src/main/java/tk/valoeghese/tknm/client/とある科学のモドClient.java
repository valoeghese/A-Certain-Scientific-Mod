package tk.valoeghese.tknm.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import tk.valoeghese.tknm.api.rendering.RenderHooks;
import tk.valoeghese.tknm.client.rendering.AbilityRenderer;

public class とある科学のモドClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// render hooks
		RenderHooks.addWorldPlayerRenderHook(new AbilityRenderer());
		// key callbacks
		ClientTickCallback.EVENT.register(client -> {
			// TODO waspressed?
			//if (client.options.keyUse.isPressed()) {
				
			//}
		});
	}
}
