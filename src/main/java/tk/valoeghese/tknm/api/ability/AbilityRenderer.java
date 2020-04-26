package tk.valoeghese.tknm.api.ability;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

/**
 * A renderer for an {@link Ability}. Is only attached to the {@link Ability} on the client.
 */
public interface AbilityRenderer {
	/**
	 * Runs every tick.
	 * @param world the world to render in.
	 */
	void render(ClientWorld world);
	/**
	 * Gives information about (usually) starting to render an ability in the world.
	 * @param pos the position of the player using the ability.
	 * @param yaw the yaw of the player using the ability.
	 * @param pitch the pitch of the player using the ability.
	 * @param usage int specifying the usage of the ability. Varies depending on the ability.
	 * @param data additional data on how the ability was used.
	 */
	void renderInfo(Vec3d pos, float yaw, float pitch, int usage, int[] data);
}
