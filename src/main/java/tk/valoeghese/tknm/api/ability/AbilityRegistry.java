package tk.valoeghese.tknm.api.ability;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.Identifier;

public final class AbilityRegistry {
	private AbilityRegistry() {
		// NO-OP
	}

	private static final BiMap<Identifier, Ability> ABILITY_TYPES = HashBiMap.create();

	public static Ability register(Identifier id, Ability ability) {
		ABILITY_TYPES.put(id, ability);
		return ability;
	}

	public static Ability getAbility(Identifier id) {
		return ABILITY_TYPES.get(id);
	}

	public static Identifier getRegistryId(Ability ability) {
		return ABILITY_TYPES.inverse().get(ability);
	}
}
