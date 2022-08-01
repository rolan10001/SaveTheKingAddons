package fr.rolan.stk.game.teleportation;

import static fr.rolan.api.game.GameSettings.BORDER;
import static fr.rolan.api.game.GameSettings.HEURE;
import static fr.rolan.api.game.GameSettings.MINUTE;
import static fr.rolan.api.game.GameSettings.PLAYERS;
import static fr.rolan.api.game.GameSettings.PLAYERS_LIST;
import static fr.rolan.api.game.GameSettings.SECOND;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.game.enums.GameState;
import fr.rolan.api.player.IUser;
import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.enumstk.State;
import fr.rolan.stk.game.generate.GenerateManager;
import fr.rolan.stk.listeners.teleportation.LobbyProtectListener;
import fr.rolan.stk.listeners.teleportation.PlayerLobbyListener;
import fr.rolan.stk.manager.STKUser;
import fr.rolan.tools.NMSMethod;
import fr.rolan.tools.TPS;
import io.papermc.lib.PaperLib;

public class TeleportationManager extends BukkitRunnable {
	
	private final LobbyProtectListener lobbyProtectListener;
	private final PlayerLobbyListener playerLobbyListener;
	List<Block> barrierList = new ArrayList<Block>();
	int i = 0;
	
	public TeleportationManager() {
		this.lobbyProtectListener = new LobbyProtectListener();
		this.playerLobbyListener = new PlayerLobbyListener();
		Bukkit.getPluginManager().registerEvents(lobbyProtectListener, STKPlugin.getInstance());
		Bukkit.getPluginManager().registerEvents(playerLobbyListener, STKPlugin.getInstance());
		GenerateManager.unregisterListeners();
		UHCAPI.get().getScoreboardManager().setScoreboard(UHCAPI.get().getSettings().NAME, setGameScoreboard());
		runTaskTimer(STKPlugin.getInstance(), 20, 20);
	}
	
	public void unregisterListeners() {
		HandlerList.unregisterAll(lobbyProtectListener);
		HandlerList.unregisterAll(playerLobbyListener);
	}
	
	public Map<Integer, String> setGameScoreboard() {
		Map<Integer, String> map = new HashMap<>();
		map.put(0, "§8§l§m                    ");
		map.put(1, "§7» §cHost : §f%host%");
		map.put(2, "§7» §cJoueur(s) : §f%players%");
		map.put(3, "§f§8§l§m                    ");
		map.put(4, "§7» §cEpisode : §f%episode%");
		map.put(5, "§7» §cTimer : §f%timerH%:%timerM%:%timerS%");
		map.put(6, "§7» §cBorder : §f%border%");
		map.put(7, "§8§8§l§m                    ");
		map.put(8, "§7» §cCentre :§f%center% §f%center_distance%");
		map.put(9, "§7» §cTeam : §f%team%");
		map.put(10, "§7» §cKills : §f%kill%");
		map.put(11, "§r§8§l§m                    ");
		return map;
	}
	
	@Override
	public void run() {
		if(i >= PLAYERS_LIST.size()) {
			cancel();
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if(!UHCAPI.get().getGameManager().isState(GameState.TELEPORTATION)) cancel();
					for(Player player : Bukkit.getOnlinePlayers()) {
						IUser user = UHCAPI.get().getUser(player);
						DecimalFormat format = new DecimalFormat("00");
						NMSMethod.setTablistHeaderFooter(player, "\n§6§l» §e§l"+(UHCAPI.get().getSettings().NAME.equalsIgnoreCase("DxD UHC") ? "DxD §c§lUHC" : UHCAPI.get().getSettings().NAME)+" §6§l«\n\n  §e§lPing: §r"+NMSMethod.getPingColor(player)+"§r  §l▏§r  §e§lTPS: §a"+TPS.getTPS()+"§r  §l▏§r  §e§lJoueurs: §a"+PLAYERS+"  \n", "\n§7Kills §8» §c"+user.getKillStreak()+"\n§7Timer §8» §e"+format.format(HEURE)+":"+format.format(MINUTE)+":"+format.format(SECOND)+"\n");
						NMSMethod.sendActionbar(player, "§8§l» §7Stabilisation des TPS en cours...");
					}
				}
			}.runTaskTimer(STKPlugin.getInstance(), 20, 20);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					for(Block bloc : barrierList)
						bloc.breakNaturally();
					if(UHCAPI.get().getSettings().XENOPHOBIA) {
						new BukkitRunnable() {
							
							@Override
							public void run() {
								Bukkit.getOnlinePlayers().forEach(player -> NMSMethod.disguiseInPNJ(player));
							}
						}.runTaskTimer(STKPlugin.getInstance(), 300, 20);
					}
					STKPlugin.getInstance().newActifGameManager();
				}
			}.runTaskLater(STKPlugin.getInstance(), 200);
			return;
		}
		if(!Bukkit.getOfflinePlayer(PLAYERS_LIST.get(i)).isOnline()) {
			PLAYERS_LIST.remove(i);
		}else {
			Player player = Bukkit.getPlayer(PLAYERS_LIST.get(i));
			PLAYERS++;
			IUser user = UHCAPI.get().getUser(player);
			STKUser dxduser = STKPlugin.getInstance().getUser(player);
			dxduser.setState(State.ALIVE);
			DecimalFormat format = new DecimalFormat("00");
			NMSMethod.setTablistHeaderFooter(player, "\n§6§l» §e§l"+(UHCAPI.get().getSettings().NAME.equalsIgnoreCase("DxD UHC") ? "DxD §c§lUHC" : UHCAPI.get().getSettings().NAME)+" §6§l«\n\n  §e§lPing: §r"+NMSMethod.getPingColor(player)+"§r  §l▏§r  §e§lTPS: §a"+TPS.getTPS()+"§r  §l▏§r  §e§lJoueurs: §a"+PLAYERS+"  \n", "\n§7Kills §8» §c"+user.getKillStreak()+"\n§7Timer §8» §e"+format.format(HEURE)+":"+format.format(MINUTE)+":"+format.format(SECOND)+"\n");
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR),
					new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
			try{player.getInventory().setContents(UHCAPI.get().getStuffManager().getStuffStart());}catch(NullPointerException e) {}
			try{player.getInventory().setArmorContents(UHCAPI.get().getStuffManager().getStuffArmorStart());}catch(NullPointerException e) {}
			player.updateInventory();
			for(PotionEffect e : player.getActivePotionEffects()) player.removePotionEffect(e.getType());
	        player.setLevel(0);
	        player.setExp(0);
	        player.setMaxHealth(20);
	        player.setHealth(20);
	        player.setFoodLevel(20);
	        player.setSaturation(20);
	        if(player.hasPermission("host.use"))
	        	player.setPlayerListName("§"+Integer.toHexString(user.getTeam().getColor())+player.getName());
	        if(UHCAPI.get().getSettings().F3) {NMSMethod.enableF3(player);}else {NMSMethod.disableF3(player);}
	        Random r = new Random();
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
			Location aléatoire = new Location(Bukkit.getWorld("Host"), x, 251, z);
			barrierList.add(new Location(Bukkit.getWorld("Host"), x, 250, z).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x+1, 250, z).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x, 250, z+1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x+1, 250, z+1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x-1, 250, z).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x, 250, z-1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x-1, 250, z-1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x+1, 250, z-1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x-1, 250, z+1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x+2, 252, z).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x+2, 252, z+1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x+2, 252, z-1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x-2, 252, z).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x-2, 252, z+1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x-2, 252, z-1).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x, 252, z+2).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x+1, 252, z+2).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x-1, 252, z+2).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x, 252, z-2).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x+1, 252, z-2).getBlock());
			barrierList.add(new Location(Bukkit.getWorld("Host"), x-1, 252, z-2).getBlock());
			new Location(Bukkit.getWorld("Host"), x, 250, z).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x+1, 250, z).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x, 250, z+1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x+1, 250, z+1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x-1, 250, z).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x, 250, z-1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x-1, 250, z-1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x+1, 250, z-1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x-1, 250, z+1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x+2, 252, z).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x+2, 252, z+1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x+2, 252, z-1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x-2, 252, z).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x-2, 252, z+1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x-2, 252, z-1).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x, 252, z+2).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x+1, 252, z+2).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x-1, 252, z+2).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x, 252, z-2).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x+1, 252, z-2).getBlock().setType(Material.BARRIER);
			new Location(Bukkit.getWorld("Host"), x-1, 252, z-2).getBlock().setType(Material.BARRIER);
			PaperLib.teleportAsync(player, aléatoire);
			Bukkit.getOnlinePlayers().forEach(players -> NMSMethod.sendActionbar(players, "§8§l» §7Téléportation des joueurs : §e"+(i+1)+"§8/§e"+PLAYERS_LIST.size()));
			i++;
		}
	}
}