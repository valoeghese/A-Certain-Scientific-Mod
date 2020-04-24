package tk.valoeghese.tknm.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.common.ability.ある能力のカーヂナルの要素;
import tk.valoeghese.tknm.common.ability.能力;

public class とある科学のモド implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("A Certain Scientific Mod");
	public static final ComponentType<ACertainComponent> A_CERTAIN_COMPONENT =
			ComponentRegistry.INSTANCE.registerIfAbsent(from("a_certain"), ACertainComponent.class)
			.attach(EntityComponentCallback.event(PlayerEntity.class), ある能力のカーヂナルの要素::new);

	@Override
	public void onInitialize() {
		能力.ensureInit();
	}

	public static Identifier from(String name) {
		return new Identifier("tknm", name);
	}
}
