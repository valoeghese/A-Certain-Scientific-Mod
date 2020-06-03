package tk.valoeghese.tknm.common;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.LevelComponentCallback;
import nerdhub.cardinal.components.api.util.sync.LevelSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.common.ability.Abilities;

public class InnateAbilityManager {
	public static final ComponentType<InnateAbilityComponent> INNATE_ABILITY_COMPONENT =
			ComponentRegistry.INSTANCE.registerIfAbsent(ToaruKagakuNoMod.from("innate_ability"), InnateAbilityComponent.class);

	public static void init() {
		LevelComponentCallback.EVENT.register((properties, components) -> components.put(INNATE_ABILITY_COMPONENT, new InnateAbilityComponent()));
	}

	public static final List<Ability<?>> INNATE_ABILITIES = Arrays.asList(Abilities.IMAGINE_BREAKER);

	public static class InnateAbilityComponent implements LevelSyncedComponent {
		// yes a public field not getter and setter
		@Nullable
		public UUID imagineBreaker;

		@Override
		public void fromTag(CompoundTag tag) {
			try {
				this.imagineBreaker = tag.getUuid("imagine_breaker");
			} catch (Throwable t) {
				this.imagineBreaker = null;
			}
		}

		@Nullable
		public Ability<?> provideInnateAbility(UUID uuid, Random rand) {
			if (ToaruConfig.instance.imagineBreakerRarity > 0) {
				if (this.imagineBreaker == null && rand.nextInt(ToaruConfig.instance.imagineBreakerRarity) == 0) {
					this.imagineBreaker = uuid;
					return Abilities.IMAGINE_BREAKER;
				}
			}

			return null;
		}

		@Override
		public CompoundTag toTag(CompoundTag tag) {
			if (this.imagineBreaker != null) {
				tag.putUuid("imagine_breaker", this.imagineBreaker);
			}
			return tag;
		}

		@Override
		public ComponentType<?> getComponentType() {
			return INNATE_ABILITY_COMPONENT;
		}
	}
}
