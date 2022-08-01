package fr.rolan.stk.listeners.teleportation;

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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LobbyProtectListener implements Listener {
	
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
}