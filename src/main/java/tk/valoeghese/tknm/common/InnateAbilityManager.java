package tk.valoeghese.tknm.common;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import net.minecraft.nbt.CompoundTag;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.common.ability.Abilities;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class InnateAbilityManager implements LevelComponentInitializer {
	public static final ComponentKey<InnateAbilityComponent> INNATE_ABILITY_COMPONENT =
			ComponentRegistry.getOrCreate(ToaruKagakuNoMod.from("innate_ability"), InnateAbilityComponent.class);

	public static final List<Ability<?>> INNATE_ABILITIES = Arrays.asList(Abilities.IMAGINE_BREAKER);

	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.register(INNATE_ABILITY_COMPONENT, props -> new InnateAbilityComponent());
	}

	public static class InnateAbilityComponent implements AutoSyncedComponent {
		// yes a public field not getter and setter
		@Nullable
		public UUID imagineBreaker;

		@Override
		public void readFromNbt(CompoundTag tag) {
			try {
				if (tag.getBoolean("imagine_breaker_exists")) {
					this.imagineBreaker = tag.getUuid("imagine_breaker");
				} else {
					this.imagineBreaker = null;
				}
			} catch (Throwable t) {
				t.printStackTrace();
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
		public void writeToNbt(CompoundTag tag) {
			tag = new CompoundTag();

			if (this.imagineBreaker != null) {
				tag.putUuid("imagine_breaker", this.imagineBreaker);
				tag.putBoolean("imagine_breaker_exists", true);
			} else {
				tag.putBoolean("imagine_breaker_exists", false);
			}
		}
	}
}
