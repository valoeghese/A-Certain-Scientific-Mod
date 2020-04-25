package tk.valoeghese.tknm.api.ability;

import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Class representing an ability, usually a psychic/esper ability.
 */
// TODO add passive ability
public abstract class Ability {
	public Ability() {
		this.renderer = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
				? this.createAbilityRenderer() : null;
	}

	private final AbilityRenderer renderer;

	/**
	 * @return the {@link AbilityRenderer ability renderer} for this ability, if on the client. Otherwise null.
	 */
	@Nullable
	public final AbilityRenderer getRenderer() {
		return this.renderer;
	}

	/**
	 * Perform the given ability.
	 * @param world the world in which the ability was used.
	 * @param player the player using the ability.
	 * @param level the level of the ability.
	 * @param usage the int usage identifier of how the ability was triggered.
	 * @return an int array of data which the client can use for ability rendering.
	 */
	public abstract int[] performAbility(World world, PlayerEntity player, int level, byte usage);
	/**
	 * @return the ability renderer for this ability. Is called when this ability is constructed on the client side.
	 */
	public abstract AbilityRenderer createAbilityRenderer();
}
