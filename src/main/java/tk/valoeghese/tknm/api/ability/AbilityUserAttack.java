package tk.valoeghese.tknm.api.ability;

import javax.annotation.Nullable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;

/**
 * Represents an attack from an ability user.
 */
public class AbilityUserAttack {
	private AbilityUserAttack(ACertainComponent ability, PlayerEntity user, float damage, DamageSource type) {
		this.damage = damage;
		this.user = user;
		this.ability = ability;
		this.damageType = type;
	}

	private float damage;
	public final PlayerEntity user;
	public final ACertainComponent ability;
	public final DamageSource damageType;

	public float getDamage() {
		return this.damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	/**
	 * Posts an ability user attack to the specified target.
	 * @param user the user of the ability.
	 * @param le the target.
	 * @param damage the amount of damage to deal.
	 * @param damageType the type of damage to deal (used in death messages and stuff).
	 * @return whether the attack was "blocked" by the target, and cannot continue (useful for attacks that pierce through one entity to the next). 
	 */
	public static boolean post(PlayerEntity user, LivingEntity le, float damage, DamageSource damageType, @Nullable ExtraAbilityEffectsFunction specialEffects) {
		ACertainComponent component = ToaruKagakuNoMod.A_CERTAIN_COMPONENT.get(user);
		AbilityUserAttack attack = new AbilityUserAttack(component, user, damage, damageType);

		if (le instanceof PlayerEntity) {
			ACertainComponent targetStats = ToaruKagakuNoMod.A_CERTAIN_COMPONENT.get((PlayerEntity) le);

			// special case for ability user defense.
			if (targetStats.getAbility() != null) {
				DefenseResult result = targetStats.getAbility().defendAbilityUserAttack(attack);

				boolean runSpecial = result.hit;

				if (runSpecial) {
					runSpecial &= le.damage(damageType, damage);
				}

				if (runSpecial && specialEffects != null && result != DefenseResult.ABSOLUTE_STOP) {
					specialEffects.addSpecialEffects(result.hit, le);
				}

				return result.blocking;
			}
		}

		boolean hit = le.damage(damageType, damage);

		if (hit && specialEffects != null && le.isAlive()) {
			specialEffects.addSpecialEffects(true, le);
		}

		return false;
	}

	/**
	 * Use to add special effects to an attack, such as setting the target on fire or blinding the target.
	 */
	@FunctionalInterface
	public static interface ExtraAbilityEffectsFunction {
		/**
		 * Add special effects to the targets based on the attack results.
		 * @param hit whether the attack hit the target successfully.
		 * @param target the target of the attack.
		 */
		void addSpecialEffects(boolean hit, LivingEntity target);
	}
}
