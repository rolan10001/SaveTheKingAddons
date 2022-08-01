package fr.rolan.stk.game.generate;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.event.HandlerList;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.game.enums.GameState;
import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.listeners.generate.LobbyProtectListener;
import fr.rolan.stk.listeners.generate.PlayerLobbyListener;

public class GenerateManager {
	
	private static LobbyProtectListener lobbyProtectListener;
	private static PlayerLobbyListener playerLobbyListener;
	
	public GenerateManager() {
		lobbyProtectListener = new LobbyProtectListener();
		playerLobbyListener = new PlayerLobbyListener();
		Bukkit.getPluginManager().registerEvents(lobbyProtectListener, STKPlugin.getInstance());
		Bukkit.getPluginManager().registerEvents(playerLobbyListener, STKPlugin.getInstance());
		UHCAPI.get().getStartedManager().unregisterListeners();
		UHCAPI.get().getGameManager().setGameState(GameState.GENERATE);
		try {
			Bukkit.unloadWorld("Host", false);
			FileUtils.deleteDirectory(new File("Host"));
		}catch (Exception e) {}
		Bukkit.broadcastMessage("§7§l▏ §aGénération du monde");
		try {
			FileUtils.copyDirectory(new File(Bukkit.getWorldContainer()+File.separator+"Map"+File.separator+"STK"), new File(Bukkit.getWorldContainer()+File.separator));
			new File(Bukkit.getWorldContainer()+File.separator+"STK").renameTo(new File("Host"));
		} catch (IOException e) {
			Bukkit.broadcastMessage("§7§l▏ §cUne erreur est une survenue.");
			return;
		}
		new WorldCreator("Host").createWorld();
		Bukkit.getWorld("Host").setGameRuleValue("doFireTick", "false");
		Bukkit.getWorld("Host").setGameRuleValue("reducedDebugInfo", "true");
		Bukkit.getWorld("Host").setGameRuleValue("naturalRegeneration", "true");
		Bukkit.getWorld("Host").setGameRuleValue("randomTickSpeed", "3");
		Bukkit.broadcastMessage("§7§l▏ §aGénération du monde terminée.");
		STKPlugin.getInstance().newTeleportationManager();
	}
	
	public static void unregisterListeners() {
		HandlerList.unregisterAll(lobbyProtectListener);
		HandlerList.unregisterAll(playerLobbyListener);
	}
}
