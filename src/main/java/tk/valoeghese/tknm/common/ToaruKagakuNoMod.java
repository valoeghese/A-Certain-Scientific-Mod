package tk.valoeghese.tknm.common;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;

import io.netty.buffer.Unpooled;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import nerdhub.cardinal.components.api.util.EntityComponents;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.common.ability.Abilities;
import tk.valoeghese.tknm.common.ability.ある能力のカーヂナルの要素;
import tk.valoeghese.tknm.common.tech.CertainItems;

public class ToaruKagakuNoMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("A Certain Scientific Mod");
	// component
	public static final ComponentType<ACertainComponent> A_CERTAIN_COMPONENT =
			ComponentRegistry.INSTANCE.registerIfAbsent(from("a_certain"), ACertainComponent.class);

	// packets
	public static final Identifier USE_ABILITY_PACKET_ID = from("nouryokutsukau");
	public static final Identifier RENDER_ABILITY_PACKET_ID = from("render");

	// sounds
	public static final Identifier IMAGINE_BREAKER_SOUND_ID = from("imagine_breaker");
	public static final Identifier BIRIBIRI_0_SOUND_ID = from("biribiri_0");
	public static final Identifier BIRIBIRI_1_SOUND_ID = from("biribiri_1");
	public static final Identifier BIRIBIRI_2_SOUND_ID = from("biribiri_2");
	public static final SoundEvent IMAGINE_BREAKER_SOUND_EVENT = new SoundEvent(IMAGINE_BREAKER_SOUND_ID);
	public static final SoundEvent BIRIBIRI_0_SOUND_EVENT = new SoundEvent(BIRIBIRI_0_SOUND_ID);
	public static final SoundEvent BIRIBIRI_1_SOUND_EVENT = new SoundEvent(BIRIBIRI_1_SOUND_ID);
	public static final SoundEvent BIRIBIRI_2_SOUND_EVENT = new SoundEvent(BIRIBIRI_2_SOUND_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Setting up \"A Certain Scientific Mod!\"");
		long time = System.currentTimeMillis();

		// abilities
		Abilities.ensureInit();
		// components
		EntityComponents.setRespawnCopyStrategy(A_CERTAIN_COMPONENT, RespawnCopyStrategy.ALWAYS_COPY);
		InnateAbilityManager.init();
		EntityComponentCallback.event(PlayerEntity.class).register((player, components) -> components.put(A_CERTAIN_COMPONENT, new ある能力のカーヂナルの要素(player)));

		// commands
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(
					CommandManager.literal("とある")
					.then(CommandManager.literal("デバッグ")
							.requires(src -> src.hasPermissionLevel(2))
							.then(CommandManager.literal("能力者")
									.then(CommandManager.argument("能力者", EntityArgumentType.player())
											.executes(context -> {
												PlayerEntity entity = EntityArgumentType.getPlayer(context, "能力者");
												context.getSource().sendFeedback(
														A_CERTAIN_COMPONENT.get(entity).stats(),
														false);
												return 1;
											})))
							.then(CommandManager.literal("能力")
									.then(CommandManager.argument("能力者", EntityArgumentType.player())
											.then(CommandManager.argument("能力", IdentifierArgumentType.identifier())
													.executes(context -> {
														PlayerEntity entity = EntityArgumentType.getPlayer(context, "能力者");
														A_CERTAIN_COMPONENT.get(entity).setAbility(AbilityRegistry.getAbility(IdentifierArgumentType.getIdentifier(context, "能力")));
														return 1;
													}))))
							.then(CommandManager.literal("けいけんち足す")
									.then(CommandManager.argument("能力者", EntityArgumentType.player())
											.then(CommandManager.argument("すう", FloatArgumentType.floatArg())
													.executes(context -> {
														PlayerEntity entity = EntityArgumentType.getPlayer(context, "能力者");
														context.getSource().sendFeedback(
																new LiteralText(String.valueOf(A_CERTAIN_COMPONENT.get(entity).addXp(FloatArgumentType.getFloat(context, "すう")))),
																false);
														return 1;
													})))
									)
							.then(CommandManager.literal("能力者です")
									.then(CommandManager.argument("能力者です", BoolArgumentType.bool())
											.then(CommandManager.argument("能力者", EntityArgumentType.player())
													.executes(context -> {
														PlayerEntity entity = EntityArgumentType.getPlayer(context, "能力者");
														A_CERTAIN_COMPONENT.get(entity).setAbilityUser(BoolArgumentType.getBool(context, "能力者です"));
														return 1;
													}))
											.executes(context -> {
												Entity entity = context.getSource().getEntity();
												if (entity instanceof PlayerEntity) {
													A_CERTAIN_COMPONENT.get((PlayerEntity) entity).setAbilityUser(BoolArgumentType.getBool(context, "能力者です"));
													return 1;
												} else {
													return 0;
												}
											}))
									)
							));
		});

		// config
		ToaruConfig.instance.hashCode(); // force the static initialiser to run

		// items
		// force it to initialise
		CertainItems.ABILITY_THING.hashCode();

		// loot
		final int storeroomChance = 47;
		final int altarChance = 39;

		LootTableLoadingCallback.EVENT.register((resources, loot, id, table, setter) -> {
			boolean b = LootTables.STRONGHOLD_CORRIDOR_CHEST.equals(id);

			if (b || LootTables.STRONGHOLD_CROSSING_CHEST.equals(id)) {
				int chance = b ? altarChance : storeroomChance;

				FabricLootPoolBuilder custom = FabricLootPoolBuilder.builder()
						.rolls(ConstantLootTableRange.create(1))
						.withEntry(EmptyEntry.Serializer()
								.weight(100 - chance)
								.build())
						.withEntry(ItemEntry.builder(CertainItems.ABILITY_THING)
								.weight(chance)
								.build());

				table.withPool(custom.build());
			}
		});

		// packets
		ServerSidePacketRegistry.INSTANCE.register(USE_ABILITY_PACKET_ID, (context, dataManager) -> {
			byte usage = dataManager.readByte();
			PlayerEntity player = context.getPlayer();

			// main thread task queue
			context.getTaskQueue().execute(() -> {
				ACertainComponent component = A_CERTAIN_COMPONENT.get(player);
				@SuppressWarnings("rawtypes")
				Ability ability = component.getAbility();

				if (ability != null) {
					Identifier abilityId = AbilityRegistry.getRegistryId(ability);

					// slight xp by default for level 0s. Perhaps one day they'll level up.
					if (component.getLevel() == 0) {
						component.addXp(0.002f);
					}

					// do ability logic here
					@SuppressWarnings("unchecked")
					int[] データ = ability.performAbility(player.world, player, component.getLevel(), component.getLevelProgress(), usage, component.getData());

					if (データ != null) {
						// send packets
						PacketByteBuf パッキト = new PacketByteBuf(Unpooled.buffer());
						パッキト.writeDoubleLE(player.getX());
						パッキト.writeDoubleLE(player.getY());
						パッキト.writeDoubleLE(player.getZ());
						パッキト.writeFloatLE(player.yaw);
						パッキト.writeFloatLE(player.pitch);
						パッキト.writeIdentifier(abilityId);
						パッキト.writeUuid(player.getUuid());
						パッキト.writeIntArray(データ);

						PlayerStream.around(player.world, player.getPos(), 420.0).forEach(pe -> {
							ServerSidePacketRegistry.INSTANCE.sendToPlayer(pe, RENDER_ABILITY_PACKET_ID, パッキト);
						});
					}
				}
			});
		});

		// sounds
		Registry.register(Registry.SOUND_EVENT, IMAGINE_BREAKER_SOUND_ID, IMAGINE_BREAKER_SOUND_EVENT);

		// tick event
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
				ACertainComponent component = A_CERTAIN_COMPONENT.get(player);

				if (player.inventory.getArmorStack(3).getItem() == CertainItems.ABILITY_THING) {
					// firstly, u gonna be slow in such clunky tech
					// I would manually change movement speed
					// but this is good enough for now
					boolean add = !player.hasStatusEffect(StatusEffects.SLOWNESS);

					if (!add) {
						if (player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier() < 3) {
							player.removeStatusEffect(StatusEffects.SLOWNESS);
							add = true;
						}
					}

					if (add) {
						player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5, 3, false, false));
					}

					// or sitting for long enough, if I can figure that out. probably a property or sth.
					if (player.isSleeping()) {
						// basically, from what I know, it takes time to make an ability user in the toaru universe,
						// and it involves a bunch of drug injects and alterations to their brain and stuff.
						// thus I picked sleeping is required. See note above if statement for alternatives I have thought about (and actually thought about before this version).
						if (!component.isAbilityUser()) {
							component.setAbilityUser(true);
						}
					}
				}

				Ability<?> ability = component.getAbility();

				if (ability != null) {
					ability.tick(server, player, component);
				}
			}
		});

		LOGGER.info("Set up \"A Certain Scientific Mod\" in " + (System.currentTimeMillis() - time) + "ms.");
	}

	public static Identifier from(String name) {
		return new Identifier("tknm", name);
	}

	public static SoundEvent biribiriSound(Random rand) {
		switch (rand.nextInt(12)) {
		case 0:
			return BIRIBIRI_2_SOUND_EVENT;
		default:
			return BIRIBIRI_1_SOUND_EVENT;
		}
	}
}
