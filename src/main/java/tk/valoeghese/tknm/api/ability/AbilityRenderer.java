package tk.valoeghese.tknm.api.ability;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

/**
 * A renderer for an {@link Ability}. Is only attached to the {@link Ability} on the client.
 */
public interface AbilityRenderer {
	/**
	 * Renders the given ability in the world.
	 * @param world the world in which to render the ability.
	 * @param player the player using the ability.
	 * @param usage int specifying the usage of the ability. Varies depending on the ability.
	 * @param data additional data on how the ability was used.
	 */
	void render(ClientWorld world, PlayerEntity player, int usage, int[] data);
}
