package tk.valoeghese.tknm.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.api.rendering.RenderHooks;
import tk.valoeghese.tknm.client.rendering.AbilityRenderPrimer;
import tk.valoeghese.tknm.common.とある科学のモド;

public class とある科学のモドClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// render hooks
		RenderHooks.addWorldRenderHook(AbilityRenderPrimer.getOrCreate());
		// other stuff
		ClientSidePacketRegistry.INSTANCE.register(とある科学のモド.RENDER_ABILITY_PACKET_ID, (context, dataManager) -> {
			double x = dataManager.readDoubleLE();
			double y = dataManager.readDoubleLE();
			double z = dataManager.readDoubleLE();
			Vec3d pos = new Vec3d(x, y, z);
			float yaw = dataManager.readFloatLE();
			float pitch = dataManager.readFloatLE();
			Identifier abilityId = dataManager.readIdentifier();
			byte usage = dataManager.readByte();
			int[] data = dataManager.readIntArray();

			context.getTaskQueue().execute(() -> {
				Ability ability = AbilityRegistry.getAbility(abilityId);

				if (ability != null) {
					AbilityRenderPrimer.getOrCreate().queue.add(world -> {
						ability.getRenderer().renderInfo(world, pos, yaw, pitch, usage, data);
					});
				}
			});
		});
	}
}
