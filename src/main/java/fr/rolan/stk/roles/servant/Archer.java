package fr.rolan.stk.roles.servant;

import static fr.rolan.api.gui.GuiManager.*;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotation.NotNull;
import org.jetbrains.annotation.Nullable;

import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.enumstk.Category;
import fr.rolan.stk.manager.STKGameManager;
import fr.rolan.stk.rolesattributs.Power;
import fr.rolan.stk.rolesattributs.RolesImpl;

public class Archer extends RolesImpl implements Power {
	
	private final Category category = Category.SERVANT;
	private boolean power = true;
	
	public Archer(STKGameManager game, @NotNull UUID uuid) {
		super(game, uuid);
	}

	@Override
	public String getDescription() {
		return STKPlugin.getInstance().getSTKConfig().getString("stk.role.archer.description");
	}

	@Override
	public String getDisplay() {
		return STKPlugin.getInstance().getSTKConfig().getString("stk.role.archer.display");
	}

	@Override
	public boolean isCategory(@NotNull Category paramCategory) {
		return paramCategory.equals(category);
	}

	@Override
	public @NotNull Category getCategory() {
		return category;
	}

	@Override
	public void recoverPotionEffect(@NotNull Player player) {
		return;
	}
	
	@Override
	public @Nullable Player recoverPower() {
		Player player = super.recoverPower();
		if(player == null)
			return null;
		player.setMaxHealth(16.0D);
		player.setHealth(16.0D);
		return player;
	}
	
	@Override
	public void stolen(@NotNull UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if(player == null)
			return;
		player.setMaxHealth(16.0D);
		player.setHealth(16.0D);
		player.getInventory().setHelmet(setMetaInItem(new ItemStack(Material.CHAINMAIL_HELMET), "§fCasque du Servant", null));
		player.getInventory().setChestplate(setMetaInItem(new ItemStack(Material.IRON_CHESTPLATE), "§fPlastron du Servant", null));
		player.getInventory().setLeggings(setMetaInItem(new ItemStack(Material.CHAINMAIL_LEGGINGS), "§fPantalon du Servant", null));
		player.getInventory().setBoots(setMetaInItem(new ItemStack(Material.IRON_BOOTS), "§fBottes du Servant", null));
		player.getInventory().addItem(setMetaInItem(new ItemStack(Material.STONE_SWORD), "§fEpée du Servant", null));
		player.getInventory().addItem(setMetaInItem(new ItemStack(Material.WOOD_PICKAXE), "§fPioche du Servant", null));
		try {
			for(ItemStack it : game.getStuffs().getStuffRoles().get(getDisplay())) {
				if(player.getInventory().firstEmpty() == -1) {
					player.getWorld().dropItem(player.getLocation(), it);
					continue;
				}
				player.getInventory().addItem(it);
				player.updateInventory();
			}
		}catch(Exception e) {}
		player.updateInventory();
	}

	@Override
	public String getColor() {
		return "§9";
	}

	@Override
	public void setPower(boolean paramBoolean) {
		power = paramBoolean;
	}

	@Override
	public boolean hasPower() {
		return power;
	}
}
