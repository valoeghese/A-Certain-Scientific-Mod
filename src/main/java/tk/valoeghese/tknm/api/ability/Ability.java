package tk.valoeghese.tknm.api.ability;

import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import tk.valoeghese.tknm.client.rendering.AbilityRenderPrimer;

/**
 * Class representing an ability, usually a psychic/esper ability.
 */
// TODO add passive ability
public abstract class Ability {
	public Ability() {
		this.renderer = (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? 
				this.createAbilityRenderer() : null);
		if (this.renderer != null) {
			AbilityRenderPrimer.getOrCreate().renderers.add(this.renderer);
		}
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
	 * @param attack representation of the attack.
	 * @return whether the result hit.
	 */
	public DefenseResult defendAbilityUserAttack(AbilityUserAttack attack) {
		return DefenseResult.HIT;
	}

	/**
	 * Perform the given ability.
	 * @param world the world in which the ability was used.
	 * @param player the player using the ability.
	 * @param level the level of the ability.
	 * @param abilityProgress the progress of the ability user from the current level to the next, from 0.0-1.0.
	 * @param usage the int usage identifier of how the ability was triggered.
	 * @return an int array of data which the client can use for ability rendering. If null, does not use the ability.
	 */
	@Nullable
	public abstract int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage);
	/**
	 * @return the ability renderer for this ability. Is called when this ability is constructed on the client side.
	 */
	protected abstract AbilityRenderer createAbilityRenderer();
}
