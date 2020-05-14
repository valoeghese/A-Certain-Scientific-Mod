package tk.valoeghese.tknm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;

@Mixin(CreeperEntity.class)
public interface AccessorCreeperEntity {
	@Accessor("CHARGED")
	static TrackedData<Boolean> getCharged() {
		throw new UnsupportedOperationException("Mixin failed to apply!");
	}
}
