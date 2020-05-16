package tk.valoeghese.tknm.common.tech;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class GakuenToshiTechMaterial implements ArmorMaterial {
	public static final ArmorMaterial INSTANCE = new GakuenToshiTechMaterial();

	@Override
	public int getDurability(EquipmentSlot slot) {
		return 2;
	}

	@Override
	public int getProtectionAmount(EquipmentSlot slot) {
		switch (slot) {
		case CHEST:
			return 3;
		case FEET:
			return 1;
		case HEAD:
			return 3;
		case LEGS:
			return 2;
		default:
			return 0;
		}
	}

	@Override
	public int getEnchantability() {
		return 0;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(Items.IRON_INGOT);
	}

	@Override
	public String getName() {
		return "gakuen_toshi_tech";
	}

	@Override
	public float getToughness() {
		return 0.0f;
	}

	@Override
	public float getKnockbackResistance() {
		return 0.0f;
	}
}
