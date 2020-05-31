package tk.valoeghese.tknm.common;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import net.fabricmc.loader.api.FabricLoader;
import tk.valoeghese.zoesteriaconfig.api.ZoesteriaConfig;
import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;
import tk.valoeghese.zoesteriaconfig.api.deserialiser.Comment;
import tk.valoeghese.zoesteriaconfig.api.template.ConfigTemplate;

public class ToaruConfig {
	public ToaruConfig() {
		try {
			// IO
			CONFIG_FILE.createNewFile();
			WritableConfig config = ZoesteriaConfig.loadConfigWithDefaults(CONFIG_FILE, defaults());
			config.writeToFile(CONFIG_FILE);

			// set values
			this.imagineBreakerRarity = config.getIntegerValue("specialAbilities.imagineBreakerRarity");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public final int imagineBreakerRarity;

	private static final ConfigTemplate defaults() {
		return ConfigTemplate.builder()
				.addContainer("specialAbilities", c -> c
						.addComment(new Comment(" Controls the rate of imagine breaker being provided as an ability."))
						.addComment(new Comment(" 0 disables imagine breaker. For other, positive, numbers, it is a 1/n chance to a player to be the imagine breaker."))
						.addDataEntry("imagineBreakerRarity", "1")
						)
				.build();
	}

	private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDirectory(), "toaru.cfg");
	public static ToaruConfig instance = new ToaruConfig();
}
