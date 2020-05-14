package tk.valoeghese.tknm.mixin;

import javax.swing.text.html.parser.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.data.DataTracker;

@Mixin(Entity.class)
public interface AccessorEntity {
	@Accessor("dataTracker")
	DataTracker getDataTracker();
}
