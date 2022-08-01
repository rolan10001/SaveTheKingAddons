package fr.rolan.stk.manager;

import static fr.rolan.api.gui.GuiManager.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.rolan.stk.STKPlugin;

public class StuffManager {
	
	private Map<String, List<ItemStack>> stuff = new HashMap<String, List<ItemStack>>();
	
	public Map<String, List<ItemStack>> getStuffRoles() {
		return stuff;
	}
	
	public StuffManager() {
		ItemStack it = setMetaInItem(new ItemStack(Material.BOW), "§e§lL'Arc du Servant", null);
		ItemMeta meta = it.getItemMeta();
		meta.spigot().setUnbreakable(true);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 4, true);
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		it.setItemMeta(meta);
		getStuffRoles().put(STKPlugin.getInstance().getSTKConfig().getString("stk.role.archer.display"), Arrays.asList(it));
	}
}
