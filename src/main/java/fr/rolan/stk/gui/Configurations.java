package fr.rolan.stk.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.events.SaveConfigurationEvent;
import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.manager.STKSettings;

public class Configurations implements Listener {
	
	public Configurations() {
		Bukkit.getPluginManager().registerEvents(this, STKPlugin.getInstance());
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getInventory().getName().equals("§8» §eConfigurations")) return;
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
			if(event.getCurrentItem().getItemMeta().hasLore() && event.getCurrentItem().getItemMeta().getLore().size() >= 2 && event.getCurrentItem().getItemMeta().getLore().get(1).equals("§7» §cMode de Jeu : §fSave The King") && event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§e")) {
				if(event.getClick().equals(ClickType.LEFT)) {
					load(event.getCurrentItem().getItemMeta().getDisplayName());
					try {
						for(Field f : STKPlugin.getInstance().gui.getClass().getDeclaredFields()) {
							f.setAccessible(true);
							f.set(STKPlugin.getInstance().gui, null);
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}else if(event.getClick().equals(ClickType.RIGHT)) {
					erase(player, event.getCurrentItem().getItemMeta().getDisplayName());
				}
			} 
		}
	}
	
	@EventHandler
	public void onSaveConfiguration(SaveConfigurationEvent event) {
		if(UHCAPI.get().getSettings().GAMEMODE.equals(fr.rolan.api.game.enums.GAMEMode.OTHER) && UHCAPI.get().getSettings().GAMEMODE.getName().equals("Save The King"))
			saveConfig(event.getPlayer(), event.getName());
	}
	
	private void load(String name) {
		File repertoire = new File(STKPlugin.getInstance().getDataFolder()+File.separator+"configs"+File.separator+UHCAPI.get().getSettings().HOST.toString());
		File[] files = repertoire.listFiles();
		if(files == null)
			return;
		loadConfig(UHCAPI.get().getSettings().HOST, name.substring(2));
	}
	
	private void saveConfig(Player player, String name) {
		File file = new File(STKPlugin.getInstance().getDataFolder()+File.separator+"configs"+File.separator+UHCAPI.get().getSettings().HOST.toString()+File.separator+name+".json");
		if(!new File(STKPlugin.getInstance().getDataFolder()+File.separator+"configs").exists())
			new File(STKPlugin.getInstance().getDataFolder()+File.separator+"configs").mkdirs();
		File repertoire = new File(STKPlugin.getInstance().getDataFolder()+File.separator+"configs"+File.separator+UHCAPI.get().getSettings().HOST.toString());
		if(!repertoire.exists())
			repertoire.getParentFile().mkdirs();
		File[] files = repertoire.listFiles();
		if(files == null || files.length < 21) {
			save(file, serialize(STKPlugin.getInstance().s));
			player.sendMessage("§7▏ §aConfiguration STK sauvegardée avec succès.");
		}
	}
	
	private void erase(Player player, String name) {
		File repertoire = new File(STKPlugin.getInstance().getDataFolder()+File.separator+"configs"+File.separator+UHCAPI.get().getSettings().HOST.toString());
		File[] files = repertoire.listFiles();
		if(files == null)
			return;
		File file = new File(STKPlugin.getInstance().getDataFolder()+File.separator+"configs"+File.separator+UHCAPI.get().getSettings().HOST.toString()+File.separator+name.substring(2)+".json");
		if(file.delete())
			player.sendMessage("§7▏ §aLa Configuration STK a été effacé avec succès.");
		else
			player.sendMessage("§7▏ §cEchec lors de l'effacement de la Configuration STK.");
	}
	
	private void createFile(File file) throws IOException{
		if(!file.exists()) {
			if(file.getParentFile().mkdirs())
				System.out.println("[UHCCore] Create Parent Directory for "+file.getName());
			if(file.createNewFile())
				System.out.println("[UHCCore] Create "+file.getName());
		}
	}
	
	private void save(File file, String text) {
		try {
			createFile(file);
		}catch(IOException e) {
			e.printStackTrace();
		}
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(text);
			fw.flush();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadConfig(UUID uuid, String name) {
		File file = new File(STKPlugin.getInstance().getDataFolder()+File.separator+"configs"+File.separator+uuid.toString()+File.separator+name+".json");
		STKPlugin.getInstance().s = deserialize(loadContent(file));
	}
	
	private String loadContent(File file) {
		if(file.exists())
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))){
				StringBuilder text = new StringBuilder();
				String line;
				while((line = reader.readLine()) != null)
					text.append(line);
				return text.toString();
			}catch(IOException e) {
				e.printStackTrace();
			}
		return "";
	}
	
	private Gson gson() {
		return (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	}
	
	private String serialize(STKSettings config) {
		return gson().toJson(config);
	}
	
	private STKSettings deserialize(String json) {
		return (STKSettings)gson().fromJson(json, STKSettings.class);
	}
}