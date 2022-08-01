package fr.rolan.stk.gui;

import static fr.rolan.api.gui.GuiManager.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.game.team.Teams;
import fr.rolan.api.player.IUser;
import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.manager.RoleRegister;
import fr.rolan.stk.manager.STKUser;
import fr.rolan.stk.rolesattributs.Roles;

public class RoleMenu {
	
	public RoleMenu() {
		STKPlugin.getInstance().gui.STK_ROLE = Bukkit.createInventory(null, 27, "§eChoisissez un rôle");
		STKPlugin.getInstance().gui.STK_ROLE.setItem(10, setMetaInItem(new ItemStack(Material.NETHER_STALK), "§e§lKing", Arrays.asList("§8- §5Régénération non naturel.", "§8- §5Résistance 1", "§8- §5Speed 2", "§8- §5Fire-Résistance", "§8- §5Saturation", "§8- §520 Coeurs")));
		STKPlugin.getInstance().gui.STK_ROLE.setItem(12, setMetaInItem(new ItemStack(Material.DIAMOND_CHESTPLATE), "§9§lSoldat", Arrays.asList("§8- §5Régénération naturel.", "§8- §5Résistance 1", "§8- §512 Coeurs")));
		STKPlugin.getInstance().gui.STK_ROLE.setItem(14, setMetaInItem(new ItemStack(Material.IRON_SWORD), "§9§lExplorateur", Arrays.asList("§8- §5Régénération naturel.", "§8- §5Speed 1", "§8- §5Haste 1")));
		STKPlugin.getInstance().gui.STK_ROLE.setItem(16, setMetaInItem(new ItemStack(Material.BOW), "§9§lArcher", Arrays.asList("§8- §5Régénération naturel.", "§8- §5Saturation", "§8- §58 Coeurs", "§8- §5Arc Power 4 Infinity 1 Unbreakable")));
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getInventory().getName().equals("§eChoisissez un rôle")) return;
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
			if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§e§lKing")) {
				IUser u = UHCAPI.get().getUser(player);
				Teams team = u.getTeam();
				List<UUID> playersUUID = new ArrayList<UUID>(team.getPlayers());
				List<RoleRegister> config = new ArrayList<RoleRegister>();
				HashMap<Roles, Integer> r = new HashMap<>();
				for(UUID uuid : STKPlugin.getInstance().gameSettings.PLAYERSROLES.keySet())
					if(playersUUID.contains(uuid))
						if(r.containsKey(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid)))
							r.replace(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), r.get(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid))+1);
						else
							r.put(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), 1);
				for(RoleRegister roleRegister : STKPlugin.getInstance().getRegisterRoles()) {
					Roles role = null;
					for(Roles ro : r.keySet())
						if(ro.getDisplay().equals(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))) {
							role = ro;
							break;
						}
					if(role == null || STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey())).intValue() > r.get(role))
						for(int i = 0; i < ((Integer) STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))).intValue(); i++) {
							if(role != null && r.containsKey(role)) {
								r.replace(role, r.get(role)-1);
								if(r.get(role) == 0)
									r.remove(role);
								continue;
							}
							if(role == null || !r.containsKey(role))
								config.add(roleRegister);
						}
				}
				boolean b = false;
				for(RoleRegister roleRegister : config)
					if(roleRegister.getName().equals(STKPlugin.getInstance().getSTKConfig().getString("stk.role.king.display")))
						b = true;
				if(!b) {
					player.sendMessage("§cCe rôle n'est pas disponible, veuillez en choisir un autre.");
					return;
				}
				STKUser user = STKPlugin.getInstance().getUser(player.getUniqueId());
				try {
					Roles role = (Roles) STKPlugin.getInstance().getRegisterRoles().get(0).getConstructors().newInstance(new Object[] {STKPlugin.getInstance().getSTKGameManager(), player.getUniqueId()});
					Bukkit.getPluginManager().registerEvents((Listener) role, STKPlugin.getInstance());
					user.setRole(role);
					STKPlugin.getInstance().gameSettings.ROLES.add(role);
					STKPlugin.getInstance().gameSettings.PLAYERSROLES.put(player.getUniqueId(), role);
					user.getRole().recoverPotionEffect(user.getRole().recoverPower());
				}catch(InstantiationException | java.lang.reflect.InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
				player.closeInventory();
			}else if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§9§lSoldat")) {
				IUser u = UHCAPI.get().getUser(player);
				Teams team = u.getTeam();
				List<UUID> playersUUID = new ArrayList<UUID>(team.getPlayers());
				List<RoleRegister> config = new ArrayList<RoleRegister>();
				HashMap<Roles, Integer> r = new HashMap<>();
				for(UUID uuid : STKPlugin.getInstance().gameSettings.PLAYERSROLES.keySet())
					if(playersUUID.contains(uuid))
						if(r.containsKey(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid)))
							r.replace(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), r.get(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid))+1);
						else
							r.put(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), 1);
				for(RoleRegister roleRegister : STKPlugin.getInstance().getRegisterRoles()) {
					Roles role = null;
					for(Roles ro : r.keySet())
						if(ro.getDisplay().equals(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))) {
							role = ro;
							break;
						}
					if(role == null || STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey())).intValue() > r.get(role))
						for(int i = 0; i < ((Integer) STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))).intValue(); i++) {
							if(role != null && r.containsKey(role)) {
								r.replace(role, r.get(role)-1);
								if(r.get(role) == 0)
									r.remove(role);
								continue;
							}
							if(role == null || !r.containsKey(role))
								config.add(roleRegister);
						}
				}
				boolean b = false;
				for(RoleRegister roleRegister : config)
					if(roleRegister.getName().equals(STKPlugin.getInstance().getSTKConfig().getString("stk.role.soldier.display")))
						b = true;
				if(!b) {
					player.sendMessage("§cCe rôle n'est pas disponible, veuillez en choisir un autre.");
					return;
				}
				STKUser user = STKPlugin.getInstance().getUser(player.getUniqueId());
				try {
					Roles role = (Roles) STKPlugin.getInstance().getRegisterRoles().get(2).getConstructors().newInstance(new Object[] {STKPlugin.getInstance().getSTKGameManager(), player.getUniqueId()});
					Bukkit.getPluginManager().registerEvents((Listener) role, STKPlugin.getInstance());
					user.setRole(role);
					STKPlugin.getInstance().gameSettings.ROLES.add(role);
					STKPlugin.getInstance().gameSettings.PLAYERSROLES.put(player.getUniqueId(), role);
					user.getRole().recoverPotionEffect(user.getRole().recoverPower());
				}catch(InstantiationException | java.lang.reflect.InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
				player.closeInventory();
			}else if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§9§lExplorateur")) {
				IUser u = UHCAPI.get().getUser(player);
				Teams team = u.getTeam();
				List<UUID> playersUUID = new ArrayList<UUID>(team.getPlayers());
				List<RoleRegister> config = new ArrayList<RoleRegister>();
				HashMap<Roles, Integer> r = new HashMap<>();
				for(UUID uuid : STKPlugin.getInstance().gameSettings.PLAYERSROLES.keySet())
					if(playersUUID.contains(uuid))
						if(r.containsKey(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid)))
							r.replace(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), r.get(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid))+1);
						else
							r.put(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), 1);
				for(RoleRegister roleRegister : STKPlugin.getInstance().getRegisterRoles()) {
					Roles role = null;
					for(Roles ro : r.keySet())
						if(ro.getDisplay().equals(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))) {
							role = ro;
							break;
						}
					if(role == null || STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey())).intValue() > r.get(role))
						for(int i = 0; i < ((Integer) STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))).intValue(); i++) {
							if(role != null && r.containsKey(role)) {
								r.replace(role, r.get(role)-1);
								if(r.get(role) == 0)
									r.remove(role);
								continue;
							}
							if(role == null || !r.containsKey(role))
								config.add(roleRegister);
						}
				}
				boolean b = false;
				for(RoleRegister roleRegister : config)
					if(roleRegister.getName().equals(STKPlugin.getInstance().getSTKConfig().getString("stk.role.explorer.display")))
						b = true;
				if(!b) {
					player.sendMessage("§cCe rôle n'est pas disponible, veuillez en choisir un autre.");
					return;
				}
				STKUser user = STKPlugin.getInstance().getUser(player.getUniqueId());
				try {
					Roles role = (Roles) STKPlugin.getInstance().getRegisterRoles().get(1).getConstructors().newInstance(new Object[] {STKPlugin.getInstance().getSTKGameManager(), player.getUniqueId()});
					Bukkit.getPluginManager().registerEvents((Listener) role, STKPlugin.getInstance());
					user.setRole(role);
					STKPlugin.getInstance().gameSettings.ROLES.add(role);
					STKPlugin.getInstance().gameSettings.PLAYERSROLES.put(player.getUniqueId(), role);
					user.getRole().recoverPotionEffect(user.getRole().recoverPower());
				}catch(InstantiationException | java.lang.reflect.InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
				player.closeInventory();
			}else if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§9§lArcher")) {
				IUser u = UHCAPI.get().getUser(player);
				Teams team = u.getTeam();
				List<UUID> playersUUID = new ArrayList<UUID>(team.getPlayers());
				List<RoleRegister> config = new ArrayList<RoleRegister>();
				HashMap<Roles, Integer> r = new HashMap<>();
				for(UUID uuid : STKPlugin.getInstance().gameSettings.PLAYERSROLES.keySet())
					if(playersUUID.contains(uuid))
						if(r.containsKey(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid)))
							r.replace(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), r.get(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid))+1);
						else
							r.put(STKPlugin.getInstance().gameSettings.PLAYERSROLES.get(uuid), 1);
				for(RoleRegister roleRegister : STKPlugin.getInstance().getRegisterRoles()) {
					Roles role = null;
					for(Roles ro : r.keySet())
						if(ro.getDisplay().equals(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))) {
							role = ro;
							break;
						}
					if(role == null || STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey())).intValue() > r.get(role))
						for(int i = 0; i < ((Integer) STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))).intValue(); i++) {
							if(role != null && r.containsKey(role)) {
								r.replace(role, r.get(role)-1);
								if(r.get(role) == 0)
									r.remove(role);
								continue;
							}
							if(role == null || !r.containsKey(role))
								config.add(roleRegister);
						}
				}
				boolean b = false;
				for(RoleRegister roleRegister : config)
					if(roleRegister.getName().equals(STKPlugin.getInstance().getSTKConfig().getString("stk.role.archer.display")))
						b = true;
				if(!b) {
					player.sendMessage("§cCe rôle n'est pas disponible, veuillez en choisir un autre.");
					return;
				}
				STKUser user = STKPlugin.getInstance().getUser(player.getUniqueId());
				try {
					Roles role = (Roles) STKPlugin.getInstance().getRegisterRoles().get(1).getConstructors().newInstance(new Object[] {STKPlugin.getInstance().getSTKGameManager(), player.getUniqueId()});
					Bukkit.getPluginManager().registerEvents((Listener) role, STKPlugin.getInstance());
					user.setRole(role);
					STKPlugin.getInstance().gameSettings.ROLES.add(role);
					STKPlugin.getInstance().gameSettings.PLAYERSROLES.put(player.getUniqueId(), role);
					user.getRole().recoverPotionEffect(user.getRole().recoverPower());
				}catch(InstantiationException | java.lang.reflect.InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
				player.closeInventory();
			}
		}
	}
}