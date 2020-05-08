package tk.valoeghese.tknm.api;

import javax.annotation.Nullable;

import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import tk.valoeghese.tknm.api.ability.Ability;

public interface ACertainComponent extends EntitySyncedComponent {
	/**
	 * @return the stats of this component as Text.
	 */
	Text stats();
	/**
	 * @param xp the amount of xp to add.
	 * @return the new total amount of xp.
	 */
	float addXp(float xp);
	/**
	 * @return the ability of this user, if they are an ability user.
	 */
	@Nullable Ability getAbility();
	/**
	 * @return the ability level, ignoring whether or not they are an ability user.
	 */
	int getLevel();
	/**
	 * @return the progress towards the next level.
	 */
	float getLevelProgress();
	/**
	 * @param abilityUser whether the player is an ability user.
	 */
	void setAbilityUser(boolean abilityUser);
	/**
	 * @return the player to which this component is attached.
	 */
	@Override
	PlayerEntity getEntity();
	/**
	 * Sets the ability to the specified ability.
	 */
	void setAbility(Ability ability);
}
