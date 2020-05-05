package tk.valoeghese.tknm.common;

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
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.common.ability.ある能力のカーヂナルの要素;
import tk.valoeghese.tknm.common.ability.能力;

public class ToaruKagakuNoMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("A Certain Scientific Mod");
	public static final ComponentType<ACertainComponent> A_CERTAIN_COMPONENT =
			ComponentRegistry.INSTANCE.registerIfAbsent(from("a_certain"), ACertainComponent.class)
			.attach(EntityComponentCallback.event(PlayerEntity.class), ある能力のカーヂナルの要素::new);
	public static final Identifier USE_ABILITY_PACKET_ID = new Identifier("tknm", "nouryokutsukau");
	public static final Identifier RENDER_ABILITY_PACKET_ID = new Identifier("tknm", "render");

	@Override
	public void onInitialize() {
		能力.ensureInit();
		EntityComponents.setRespawnCopyStrategy(A_CERTAIN_COMPONENT, RespawnCopyStrategy.INVENTORY);

		CommandRegistry.INSTANCE.register(false, thing -> thing.register(
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
								))
				));

		ServerSidePacketRegistry.INSTANCE.register(USE_ABILITY_PACKET_ID, (context, dataManager) -> {
			byte usage = dataManager.readByte();
			PlayerEntity player = context.getPlayer();

			// main thread task queue
			context.getTaskQueue().execute(() -> {
				ACertainComponent component = A_CERTAIN_COMPONENT.get(player);
				Ability ability = component.getAbility();

				if (ability != null) {
					Identifier abilityId = AbilityRegistry.getRegistryId(ability);

					// do ability logic here
					int[] データ = ability.performAbility(player.world, player, component.getLevel(), component.getLevelProgress(), usage);

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
	}

	public static Identifier from(String name) {
		return new Identifier("tknm", name);
	}
}
