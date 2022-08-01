package fr.rolan.stk.manager;

import static fr.rolan.api.game.GameSettings.BORDER;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.rolan.api.events.ActionBarEvent;
import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.enumstk.State;
import fr.rolan.tools.NMSMethod;
import io.papermc.lib.PaperLib;

public class STKGameManager {
	
	private Map<String, Integer> roleCount = new HashMap<String, Integer>();
	private final StuffManager stuff = new StuffManager();
	private final Random r = new Random(System.currentTimeMillis());
	
	public StuffManager getStuffs() {
		return this.stuff;
	}
	
	public Map<String, Integer> getRoleCount() {
		return this.roleCount;
	}
	
	public Random getRandom() {
		return this.r;
	}
	
	public void teleportRandom(Player player) {
		int x;
        int z;
        int tp = r.nextInt(4);
		if(tp == 0) {
			x = (0 + (r.nextInt((int) BORDER.getSize() /2)) - 25);
			z = (0 + (r.nextInt((int) BORDER.getSize() /2)) - 25);
		}else if(tp == 1) {
			x = (0 - (r.nextInt((int) BORDER.getSize() /2)) + 25);
			z = (0 - (r.nextInt((int) BORDER.getSize() /2)) + 25);
		}else if(tp == 2) {
			x = (0 + (r.nextInt((int) BORDER.getSize() /2)) - 25);
			z = (0 - (r.nextInt((int) BORDER.getSize() /2)) + 25);
		}else {
			x = (0 - (r.nextInt((int) BORDER.getSize() /2)) + 25);
			z = (0 + (r.nextInt((int) BORDER.getSize() /2)) - 25);
		}
		x+=Bukkit.getWorld("Host").getSpawnLocation().getBlockX();
		z+=Bukkit.getWorld("Host").getSpawnLocation().getBlockZ();
		PaperLib.teleportAsync(player, new Location(Bukkit.getWorld("Host"), x, Bukkit.getWorld("Host").getHighestBlockYAt(x, z)+2, z));
	}
	
	public void actionBar(Player player) {
		UUID playerUUID = player.getUniqueId();
		if(!STKPlugin.getInstance().getUser(playerUUID).isState(State.ALIVE))
			return;
		ActionBarEvent actionBarEvent = new ActionBarEvent(playerUUID, "");
		Bukkit.getPluginManager().callEvent(actionBarEvent);
		NMSMethod.sendActionbar(player, actionBarEvent.getActionBar());
	}
}
