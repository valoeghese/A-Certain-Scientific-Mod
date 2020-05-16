package tk.valoeghese.tknm.api.ability;

import net.minecraft.nbt.CompoundTag;

public final class NoneAbilityUserData implements AbilityUserData {
	@Override
	public CompoundTag toTag() {
		return null;
	}

	@Override
	public void fromTag(CompoundTag tag) {
	}
}
