package tk.valoeghese.tknm.api.ability;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

public interface AbilityRenderer {
	void render(ClientWorld world, PlayerEntity player);
}
