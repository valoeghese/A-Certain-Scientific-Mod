package tk.valoeghese.tknm.common.tech;

import static net.minecraft.entity.EquipmentSlot.HEAD;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;

public class CertainItems {
	public static final Item ABILITY_THING = Registry.register(
			Registry.ITEM,
			ToaruKagakuNoMod.from("ability_thing"),
			new ArmorItem(
					GakuenToshiTechMaterial.INSTANCE,
					HEAD,
					new Item.Settings().group(ItemGroup.TOOLS)
					));
}
