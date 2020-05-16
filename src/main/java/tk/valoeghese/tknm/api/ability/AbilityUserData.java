package tk.valoeghese.tknm.api.ability;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;

/**
 * Ability specific data tied to an ability user.
 */
public interface AbilityUserData {
	/**
	 * Adds extra ability data to an NBT tag.
	 */
	@Nullable
	CompoundTag toTag();

	/**
	 * Loads ability data from an NBT tag. If no data exists (yet), will pass null.
	 * @param tag if no tag exists (yes), null. Otherwise the tag associated with this abilities' data.
	 */
	void fromTag(@Nullable CompoundTag tag);
}
