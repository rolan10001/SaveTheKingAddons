package fr.rolan.stk.listeners.game.finish;

import static fr.rolan.api.game.GameSettings.PLAYERS;
import static fr.rolan.api.game.GameSettings.PLAYERS_LIST;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.player.IUser;
import fr.rolan.stk.STKPlugin;
import fr.rolan.tools.NMSMethod;
import io.papermc.lib.PaperLib;

public class Finish implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage("§a+ §8» §7"+player.getName()+" §8[§e"+Bukkit.getOnlinePlayers().size()+"§8/§e99§8]");
		if(PLAYERS_LIST.contains(player.getUniqueId())) {
			PLAYERS++;
			if(UHCAPI.get().getSettings().F3) {NMSMethod.enableF3(player);}else {NMSMethod.disableF3(player);}
			event.setJoinMessage("§a+ §8» §7"+player.getName()+" §8[§e"+PLAYERS_LIST.size()+"§8/§e99§8]");
		}else {
			IUser user = UHCAPI.get().getUser(player);
			user.setSpectator(true);
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage("§7§l▏ §7La partie a déjà commencé, vous pouvez toujours la regarder en tant que spectateur.");
			event.setJoinMessage(null);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		event.setQuitMessage("§c- §8» §7"+player.getName()+" §8[§e"+(Bukkit.getOnlinePlayers().size()-1)+"§8/§e99§8]");
		if(PLAYERS_LIST.contains(player.getUniqueId())) {
			event.setQuitMessage("§c- §8» §7"+player.getName()+" §8[§e"+(PLAYERS_LIST.size()-1)+"§8/§e99§8]");
			PLAYERS--;
		}else {
			event.setQuitMessage(null);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		IUser user = UHCAPI.get().getUser(player);
		for(Player players : Bukkit.getOnlinePlayers())
			players.sendMessage((UHCAPI.get().getPermissions().isAdmin(player.getUniqueId()) ? "§c§lADMIN ▏ " : UHCAPI.get().getPermissions().isStaff(player.getUniqueId()) ? "§a§lSTAFF ▏ " : UHCAPI.get().getPermissions().isHost(player.getUniqueId()) ? "§e§lHOST ▏ " : "")+user.getTeam().getPrefix()+player.getName()+" §8§l» "+((player.hasPermission("host.use")) ? "§r" : "§7")+((event.getMessage().startsWith("&") && player.hasPermission("host.use")) ? ChatColor.translateAlternateColorCodes('&', event.getMessage()) : event.getMessage()));
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
		event.setFoodLevel(25);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(event.getItemDrop().getItemStack().hasItemMeta() && event.getItemDrop().getItemStack().getItemMeta().hasDisplayName())
			if(event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals("§bArena") || event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals("§a§lConfigurez la partie"))
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getCurrentItem() == null || event.getCurrentItem().getType() == null) return;
		
		if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName())
			if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§bArena") || event.getCurrentItem().getItemMeta().getDisplayName().equals("§a§lConfigurez la partie"))
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(player.getLocation().getY() > 0)
			return;
		PaperLib.teleportAsync(player, Bukkit.getWorld("Lobby").getSpawnLocation());
	}
	
	@EventHandler
	public void onExplose(EntityExplodeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if(event.getEntity() instanceof Creature || event.getEntity() instanceof Slime)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(player.isOp())
			return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if(player.isOp())
			return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPhysical(BlockPhysicsEvent event) {
	    if(event.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK) || event.getBlock().getType().equals(Material.CACTUS) || event.getBlock().getType().equals(Material.DOUBLE_PLANT)) {
	    	event.setCancelled(true); 
	    }
	}
	  
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType().equals(Material.SOIL)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		IUser user = UHCAPI.get().getUser(player);
		event.setRespawnLocation(new Location(Bukkit.getWorld("Lobby"), -687.0, 47.1, -262.0, 90.0F, 0.0F));
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.setGameMode(GameMode.SPECTATOR);
				user.setSpectator(true);
				user.setDied(true);
				user.setDiamondArmor(0);
			}
		}.runTaskLater(STKPlugin.getInstance(), 2);
	}
}
