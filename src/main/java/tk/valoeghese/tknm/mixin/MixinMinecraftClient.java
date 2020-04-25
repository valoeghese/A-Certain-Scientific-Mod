package tk.valoeghese.tknm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import tk.valoeghese.tknm.common.とある科学のモド;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	@Redirect(
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					target = "net/minecraft/item/ItemStack.isEmpty()Z"
					),
			method = "doItemUse()V"
			)
	private boolean doItemUse(ItemStack stack) {
		PacketByteBuf 能力パッケト = new PacketByteBuf(Unpooled.buffer());
		// ability usage type. unused atm
		// TODO use it for different triggers and document
		// TODO only run this code once!
		能力パッケト.writeByte(0);
		ClientSidePacketRegistry.INSTANCE.sendToServer(
				とある科学のモド.USE_ABILITY_PACKET_ID,
				能力パッケト);
		return stack.isEmpty();
	}
}
