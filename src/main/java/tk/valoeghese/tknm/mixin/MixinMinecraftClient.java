package tk.valoeghese.tknm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import tk.valoeghese.tknm.common.とある科学のモド;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	@Inject(
			at = @At("HEAD"),		
			method = "handleInputEvents()V"
			)
	private void markReady(CallbackInfo info) {
		// because doItemUse is called twice :irritatered:
		this.tknm$ready = true;
	}

	private boolean tknm$ready = false;

	@Redirect(
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					target = "net/minecraft/item/ItemStack.isEmpty()Z"
					),
			method = "doItemUse()V"
			)
	private boolean doItemUse(ItemStack stack) {
		if (this.tknm$ready) {
			if (((MinecraftClient) (Object) this).player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
				this.tknm$ready = false;

				PacketByteBuf 能力パッケト = new PacketByteBuf(Unpooled.buffer());
				// ability usage type. unused atm
				// TODO use it for different triggers and document
				// TODO only run this code once!
				能力パッケト.writeByte(0);
				ClientSidePacketRegistry.INSTANCE.sendToServer(
						とある科学のモド.USE_ABILITY_PACKET_ID,
						能力パッケト);
			}
		}
		return stack.isEmpty();
	}
}
