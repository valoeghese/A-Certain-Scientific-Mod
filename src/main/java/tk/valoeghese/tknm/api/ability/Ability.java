package tk.valoeghese.tknm.api.ability;

import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.client.rendercore.AbilityRenderPrimer;

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
	 * @param target the user of this ability that is being targeted.
	 * @param sourcePos the position of the source of the attack.
	 * @return whether the result hit.
	 */
	public DefenseResult defendAbilityUserAttack(AbilityUserAttack attack, PlayerEntity target, Vec3d sourcePos) {
		return DefenseResult.HIT;
	}

	/**
	 * Ticks the ability on the server for a specific user. Default implementation does nothing.
	 * @param server the server instance.
	 * @param user the user of this ability.
	 * @param component the component containing information about this user's instance of the ability.
	 */
	public void tick(MinecraftServer server, PlayerEntity user, ACertainComponent component) {
	}

	/**
	 * Perform the given ability.
	 * @param world the world in which the ability was used.
	 * @param player the player using the ability.
	 * @param level the level of the ability.
	 * @param abilityProgress the progress of the ability user from the current level to the next, from 0.0-1.0.
	 * @param usage the int usage identifier of how the ability was triggered. Currently, 1 represents a a player right click.
	 * @return an int array of data which the client can use for ability rendering. If null, does not use the ability.
	 */
	@Nullable
	public abstract int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage);

	/**
	 * @return the ability renderer for this ability. Is called when this ability is constructed on the client side.
	 */
	protected abstract AbilityRenderer createAbilityRenderer();
}
