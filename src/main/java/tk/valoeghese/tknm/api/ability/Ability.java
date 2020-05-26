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
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;

/**
 * Class representing an ability, usually a psychic/esper ability.
 */
public abstract class Ability<T extends AbilityUserData> {
	public Ability() {
		this.renderer = (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? 
				this.createAbilityRenderer() : null);
		if (this.renderer != null) {
			AbilityRenderPrimer.getOrCreate().renderers.add(this.renderer);
		}
	}

	/**
	 * Grants the given xp to the ability user.
	 * @param user the ability user to whom to grant xp.
	 * @param xp the amount of xp to grant, typically a small float like 0.01f.
	 * @see <a style="color: lightblue;" href="https://www.desmos.com/calculator/amkiawswds">this desmos graph</a>, which is a representation of the {@code (potential, xp) -> level} function.
	 */
	protected static void grantXP(PlayerEntity user, float xp) {
		ToaruKagakuNoMod.A_CERTAIN_COMPONENT.get(user).addXp(xp);
	}

	/**
	 * Called when the ability is removed from the specified player. i.e. the ability of the player changes.
	 * This includes when the ability changes due to NBT sync or loading data from a save.
	 */
	public void onRemovedFrom(PlayerEntity player) {
	}

	/**
	 * Adds to the ability user's exhaustion.
	 * @param user the ability user.
	 * @param multiplier the multiplier of the default exhaust.
	 */
	protected static void exhaust(PlayerEntity user, float multiplier) {
		exhaust(user, ToaruKagakuNoMod.A_CERTAIN_COMPONENT.get(user).getLevel(), multiplier);
	}

	/**
	 * Adds to the ability user's exhaustion.
	 * @param user the ability user.
	 * @param level the user's ability level.
	 * @param multiplier the multiplier of the default exhaust.
	 */
	protected static void exhaust(PlayerEntity user, int level, float multiplier) {
		user.getHungerManager().addExhaustion(0.5f * multiplier * (6 - level));
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
	 * @param data the ability specific data associated with the player.
	 * @return an int array of data which the client can use for ability rendering. If null, does not use the ability.
	 */
	@Nullable
	public  abstract int[] performAbility(World world, PlayerEntity player, int level, float abilityProgress, byte usage, T data);

	/**
	 * @return the ability renderer for this ability. Is called when this ability is constructed on the client side.
	 */
	protected abstract AbilityRenderer createAbilityRenderer();
	/**
	 * @param user the ability user.
	 * @return a new ability user data for this user.
	 */
	public abstract T createUserData(PlayerEntity user);
}
