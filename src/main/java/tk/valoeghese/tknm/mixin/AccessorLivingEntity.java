package tk.valoeghese.tknm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(LivingEntity.class)
public interface AccessorLivingEntity {
	@Invoker("onStatusEffectRemoved")
	void invokeOnStatusEffectRemoved(StatusEffectInstance effect);
}
