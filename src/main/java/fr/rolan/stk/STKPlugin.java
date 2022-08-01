package fr.rolan.stk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.events.StartedManagerConstructorEvent;
import fr.rolan.stk.game.game.ActifGameManager;
import fr.rolan.stk.game.teleportation.TeleportationManager;
import fr.rolan.stk.gui.Configurations;
import fr.rolan.stk.commands.Command;
import fr.rolan.stk.enumstk.Category;
import fr.rolan.stk.game.generate.GenerateManager;
import fr.rolan.stk.gui.GAMEMode;
import fr.rolan.stk.gui.STKGuiManager;
import fr.rolan.stk.manager.RoleRegister;
import fr.rolan.stk.manager.STKGameManager;
import fr.rolan.stk.manager.STKGameSettings;
import fr.rolan.stk.manager.STKSettings;
import fr.rolan.stk.manager.STKUser;
import fr.rolan.stk.roles.king.King;
import fr.rolan.stk.roles.servant.Archer;
import fr.rolan.stk.roles.servant.Explorer;
import fr.rolan.stk.roles.servant.Soldier;

public class STKPlugin extends JavaPlugin {
	
	private final Map<UUID, STKUser> users = new HashMap<UUID, STKUser>();
	private final List<RoleRegister> rolesRegister = new ArrayList<>();
	private static STKPlugin instance;
	public STKSettings s;
	public STKGuiManager gui;
	public STKGameSettings gameSettings;
	private FileConfiguration stkConfig = new YamlConfiguration();
	private STKGameManager gameManager;
	private TeleportationManager teleportationManager;
	private ActifGameManager actifGameManager;
	
	public static STKPlugin getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		File stkFile = new File(getDataFolder(), "stk.yml");
		if(!stkFile.exists()) {
			stkFile.getParentFile().mkdirs();
			saveResource("stk.yml", false);
		}
		try {
			stkConfig.load(stkFile);
		}catch(IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		this.s = new STKSettings();
		this.gui = new STKGuiManager();
		this.gameSettings = new STKGameSettings();
		getCommand("king").setExecutor((CommandExecutor) new Command());
		this.gameManager = new STKGameManager();
		registerRole();
		new GAMEMode();
		new Configurations();
	}
	
	public FileConfiguration getSTKConfig() {
		return stkConfig;
	}
	
	private final void registerRole() {
		try {
			(new RoleRegister("stk.role.king.display")).registerRole(King.class).addCategory(Category.KING).create();
			(new RoleRegister("stk.role.archer.display")).registerRole(Archer.class).addCategory(Category.SERVANT).create();
			(new RoleRegister("stk.role.explorer.display")).registerRole(Explorer.class).addCategory(Category.SERVANT).create();
			(new RoleRegister("stk.role.soldier.display")).registerRole(Soldier.class).addCategory(Category.SERVANT).create();
			for(RoleRegister roleRegister : getRegisterRoles())
				getSTKGameManager().getRoleCount().put(getSTKConfig().getString(roleRegister.getKey()), roleRegister.getKey().equals("stk.role.king.display") ? 1 : 0);
		}catch(NoSuchMethodException e) {
    		e.printStackTrace();
    	}
	}
	
	public Collection<STKUser> getSTKUsers() {
		return users.values();
	}
	
	public STKUser getUser(UUID uuid) {
		for(STKUser user : getSTKUsers())
			if(user.getUUID().equals(uuid))
				return user;
		return null;
	}
	
	public STKUser getUser(Player player) {
		for(STKUser user : getSTKUsers())
			if(user.getUUID().equals(player.getUniqueId()))
				return user;
		return null;
	}
	
	public TeleportationManager getTeleportationManager() {
		return teleportationManager;
	}
	
	public void newTeleportationManager() {
		this.teleportationManager = new TeleportationManager();
	}
	
	public ActifGameManager getActifGameManager() {
		return actifGameManager;
	}
	
	public void newActifGameManager() {
		this.actifGameManager = new ActifGameManager();
	}
	
	public STKGameManager getSTKGameManager() {
		return gameManager;
	}
	
	public List<RoleRegister> getRegisterRoles() {
		return this.rolesRegister;
	}
	
	@EventHandler
	public void onStartedManagerConstructor(StartedManagerConstructorEvent event) {
		if(UHCAPI.get().getSettings().GAMEMODE.equals(fr.rolan.api.game.enums.GAMEMode.OTHER) && UHCAPI.get().getSettings().GAMEMODE.getName().equals("Save The King"))
			UHCAPI.get().getStartedManager().setClassManager(GenerateManager.class);
	}
}
