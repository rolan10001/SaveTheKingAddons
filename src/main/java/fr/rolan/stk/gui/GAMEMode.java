package fr.rolan.stk.gui;

import static fr.rolan.api.gui.GuiManager.*;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.events.MenuConstructorEvent;
import fr.rolan.api.game.team.Teams;
import fr.rolan.stk.STKPlugin;

public class GAMEMode implements Listener {
	
	public GAMEMode() {
		Bukkit.getPluginManager().registerEvents(this, STKPlugin.getInstance());
	}
	
	@EventHandler
	public void onMenuConstructor(MenuConstructorEvent event) {
		if(event.getInventory().equals(UHCAPI.get().getGuiManager().GAMEMODE_MENU))
			UHCAPI.get().getGuiManager().GAMEMODE_MENU.setItem(22, setMetaInItem(new ItemStack(Material.DIAMOND_SWORD), "§eSave The King", null));
		else if(event.getInventory().equals(UHCAPI.get().getGuiManager().MENU) && UHCAPI.get().getSettings().GAMEMODE.getName().equals("Save The King")) {
			ItemStack it = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
			SkullMeta meta = (SkullMeta) it.getItemMeta();
			meta.setOwner("Technoblade");
			meta.setDisplayName("§bConfiguration Save The King");
			meta.setLore(Arrays.asList("", "§7Accéder aux options de Save The King", "", "§8» §eClic gauche pour ouvrir le menu"));
			it.setItemMeta(meta);
			UHCAPI.get().getGuiManager().MENU.setItem(22, it);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getCurrentItem() == null || event.getCurrentItem().getType() == null) return;
		Player player = (Player) event.getWhoClicked();
		if(event.getInventory().getName().equals("§8» §eMode de Jeu")) {
			event.setCancelled(true);
			if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
				if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§eSave The King")) {
					UHCAPI.get().getSettings().GAMEMODE = fr.rolan.api.game.enums.GAMEMode.OTHER;
					UHCAPI.get().getSettings().GAMEMODE.setName("Save The King");
					UHCAPI.get().getSettings().GAMEMODE.setDisplayName("§eSave The King");
					ItemStack it = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
					SkullMeta meta = (SkullMeta) it.getItemMeta();
					meta.setOwner("Technoblade");
					meta.setDisplayName("§bConfiguration Save The King");
					meta.setLore(Arrays.asList("", "§7Accéder aux options de Save The King", "", "§8» §eClic gauche pour ouvrir le menu"));
					it.setItemMeta(meta);
					UHCAPI.get().getGuiManager().MENU.setItem(22, it);
					player.openInventory(UHCAPI.get().getGuiManager().MENU);
					ItemStack team = new ItemStack(Material.BANNER, 1, (byte) 15);
					ItemMeta teamM = team.getItemMeta();
					teamM.setDisplayName("§eTeam");
					team.setItemMeta(teamM);
					if(UHCAPI.get().getSettings().TEAM) {
						UHCAPI.get().getSettings().TEAM = false;
						for(Player players : Bukkit.getOnlinePlayers()) {
							players.getInventory().remove(team);
							Teams.DEFAULT.addPlayer(players.getUniqueId());
							UHCAPI.get().getSettings().TEAM_FRIENDLY_FIRE = true;
							for(Teams teams : Teams.values())
								teams.setFriendlyfire(UHCAPI.get().getSettings().TEAM_FRIENDLY_FIRE);
						}
					}else {
						UHCAPI.get().getSettings().TEAM=true;
						for(Player players : Bukkit.getOnlinePlayers()) {if(!UHCAPI.get().getUser(players).isInArena()) {players.getInventory().setItem(4, team);}}
					}
					if(UHCAPI.get().getGuiManager().SETTINGS_MENU != null) {
						ItemMeta m = UHCAPI.get().getGuiManager().SETTINGS_MENU.getItem(13).getItemMeta();
						m.setLore(Arrays.asList("", "§aActivé", "", "§8» §eClic gauche pour §aactiver§7§e/§cdésactiver", "§8» §eClic droit pour ouvrir le menu"));
						UHCAPI.get().getGuiManager().SETTINGS_MENU.getItem(13).setItemMeta(m);
					}
				}
			}
		}else if(event.getInventory().getName().equals("§8» §eConfiguration")) {
			event.setCancelled(true);
			if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
				if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§bConfiguration Save The King")) {
					if(STKPlugin.getInstance().gui.STK_MENU == null) new STKMenu();
					player.openInventory(STKPlugin.getInstance().gui.STK_MENU);
				}
			}
		}else if(event.getInventory().getName().equals("§8» §eConfigurations") && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getClick().equals(ClickType.LEFT)
				&& event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§e") && event.getCurrentItem().getItemMeta().hasLore() && event.getCurrentItem().getItemMeta().getLore().get(1).equals("§7» §cMode de Jeu : §fSave The King")) {
			Bukkit.getScheduler().runTaskLater(STKPlugin.getInstance(), () -> {
				UHCAPI.get().getSettings().GAMEMODE.setName("Save The King");
				UHCAPI.get().getSettings().GAMEMODE.setDisplayName("§eSave The King");
			}, 1L);
		}
	}
}