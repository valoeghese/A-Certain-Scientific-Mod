package tk.valoeghese.tknm.api;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.text.Text;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityUserData;

import javax.annotation.Nullable;

public interface ACertainComponent extends AutoSyncedComponent {
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
	@Nullable Ability<?> getAbility();
	/**
	 * @return the ability-specific data associated with this abilities' user.
	 */
	AbilityUserData getData();
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
	 * Sets the ability to the specified ability.
	 */
	void setAbility(Ability<?> ability);
	/**
	 * @return whether the player is an ability user. All levels, including zero, return true as long as the player has activated ability user status.
	 */
	boolean isAbilityUser();
}
