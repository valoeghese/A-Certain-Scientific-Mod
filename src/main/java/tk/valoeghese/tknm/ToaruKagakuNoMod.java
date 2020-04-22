package tk.valoeghese.tknm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import tk.valoeghese.tknm.api.rendering.RenderHooks;
import tk.valoeghese.tknm.common.render.DebugRender;

public class ToaruKagakuNoMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("A Certain Scientific Mod");

	@Override
	public void onInitialize() {
		RenderHooks.addWorldRenderHook(new DebugRender());
	}
}
