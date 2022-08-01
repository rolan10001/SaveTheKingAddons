package fr.rolan.stk.gui;

import static fr.rolan.api.gui.GuiManager.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.events.MenuConstructorEvent;
import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.manager.RoleRegister;
import fr.rolan.stk.manager.STKGameManager;

public class STKMenu implements Listener {
	
	private final STKGameManager GAME = STKPlugin.getInstance().getSTKGameManager();
	
	public STKMenu() {
		STKPlugin.getInstance().gui.STK_MENU = Bukkit.createInventory(null, 54, "§8» §eSave The King");
		STKPlugin.getInstance().gui.STK_MENU.setItem(13, setMetaInItem(new ItemStack(Material.BEACON), "§eRôle aléatoire", Arrays.asList("", "§7Désigner les rôles aléatoirement à X", "", "§7minutes après le démarrage de la partie.", "", (STKPlugin.getInstance().s.RANDOM_ROLE ? "§aActivé":"§cDésactivé"), "", "§8» §eClic gauche pour §aactiver§e/§cdésactiver")));
		STKPlugin.getInstance().gui.STK_MENU.setItem(31, setMetaInItem(new ItemStack(Material.GOLD_HELMET), "§e"+STKPlugin.getInstance().getSTKConfig().getString("stk.role.king.display"), Arrays.asList("", "§7Le roi est le rôle le plus important,", "", "§7tant qu'il est en vie ses serviteurs", "§7peuvent ressusciter.", "", "§aActivé", "", "§8» §eCe rôle ne peut être retiré et doublé")));
		STKPlugin.getInstance().gui.STK_MENU.setItem(39, setMetaInItem(new ItemStack(Material.BOW, GAME.getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString("stk.role.archer.display")) == 0 ? 1 : GAME.getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString("stk.role.archer.display"))), "§9"+STKPlugin.getInstance().getSTKConfig().getString("stk.role.archer.display"), Arrays.asList("", "§7L'archer est un serviteur du Roi,", "", "§7il possède un arc Power 4 Infinity,", "§7cependant il possède 8 coeurs permanents.", "", (GAME.getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString("stk.role.archer.display")) > 0 ? "§aActivé":"§cDésactivé"), "", "§8» §eClic gauche pour §aajouter", "§8» §eClic droite pour §cretirer")));
		STKPlugin.getInstance().gui.STK_MENU.setItem(40, setMetaInItem(new ItemStack(Material.DIAMOND_CHESTPLATE, GAME.getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString("stk.role.soldier.display")) == 0 ? 1 : GAME.getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString("stk.role.soldier.display"))), "§9"+STKPlugin.getInstance().getSTKConfig().getString("stk.role.soldier.display"), Arrays.asList("", "§7Le soldat est un serviteur du Roi,", "", "§7il possède l'effet Résistance 1,", "§7ainsi que 12 coeurs permanents.", "", (GAME.getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString("stk.role.soldier.display")) > 0 ? "§aActivé":"§cDésactivé"), "", "§8» §eClic gauche pour §aajouter", "§8» §eClic droite pour §cretirer")));
		STKPlugin.getInstance().gui.STK_MENU.setItem(41, setMetaInItem(new ItemStack(Material.DIAMOND_PICKAXE, GAME.getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString("stk.role.explorer.display")) == 0 ? 1 : GAME.getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString("stk.role.explorer.display"))), "§9"+STKPlugin.getInstance().getSTKConfig().getString("stk.role.explorer.display"), Arrays.asList("", "§7L'explorateur est un serviteur du Roi,", "", "§7il possède l'effet Speed 1,", "§7ainsi que l'effet Haste 1.", "", (GAME.getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString("stk.role.explorer.display")) > 0 ? "§aActivé":"§cDésactivé"), "", "§8» §eClic gauche pour §aajouter", "§8» §eClic droite pour §cretirer")));
		List<RoleRegister> config = new ArrayList<RoleRegister>();
		for(RoleRegister roleRegister : STKPlugin.getInstance().getRegisterRoles()) {
			for(int i = 0; i < ((Integer) STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))).intValue(); i++)
				config.add(roleRegister);
		}
		ItemStack info = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta infoMeta = (SkullMeta) info.getItemMeta();
		infoMeta.setOwner("Information");
		infoMeta.setDisplayName("§eInformation");
		infoMeta.setLore(Arrays.asList("", "§c"+config.size()+" §erôles pour §c"+Bukkit.getOnlinePlayers().size()+" §ejoueurs"));
		info.setItemMeta(infoMeta);
		STKPlugin.getInstance().gui.STK_MENU.setItem(51, info);
		STKPlugin.getInstance().gui.STK_MENU.setItem(49, getArrowBack());
		for(int i = 0; i < 54; i++) if(STKPlugin.getInstance().gui.STK_MENU.getItem(i) == null) STKPlugin.getInstance().gui.STK_MENU.setItem(i, getGlass());
		Bukkit.getPluginManager().registerEvents(this, STKPlugin.getInstance());
		Bukkit.getPluginManager().callEvent(new MenuConstructorEvent(STKPlugin.getInstance().gui.STK_MENU));
		UHCAPI.get().getGuis().add(this);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getInventory().getName().equals("§8» §eSave The King")) return;
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
			if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§cRetour")) {
				player.openInventory(UHCAPI.get().getGuiManager().MENU);
			}else if(event.getCurrentItem().getItemMeta().getDisplayName().equals("§eRôle aléatoire")) {
				STKPlugin.getInstance().s.RANDOM_ROLE = STKPlugin.getInstance().s.RANDOM_ROLE ? false:true;
				ItemMeta meta = event.getCurrentItem().getItemMeta();
				meta.setLore(Arrays.asList("", "§7Désigner les rôles aléatoirement à X", "", "§7minutes après le démarrage de la partie.", "", (STKPlugin.getInstance().s.RANDOM_ROLE ? "§aActivé":"§cDésactivé"), "", "§8» §eClic gauche pour §aactiver§e/§cdésactiver"));
				event.getCurrentItem().setItemMeta(meta);
				if(STKPlugin.getInstance().s.RANDOM_ROLE) {
					STKPlugin.getInstance().gui.STK_MENU.setItem(13, setMetaInItem(new ItemStack(Material.BEACON), "§eRôle aléatoire", Arrays.asList("", "§7Désigner les rôles aléatoirement à X", "", "§7minutes après le démarrage de la partie.", "", (STKPlugin.getInstance().s.RANDOM_ROLE ? "§aActivé":"§cDésactivé"), "", "§8» §eClic gauche pour §aactiver§e/§cdésactiver")));
					ItemStack purple = new ItemStack(Material.BANNER, 1, (byte) 5);BannerMeta purpleM = (BannerMeta) purple.getItemMeta();purpleM.setDisplayName("§1§5- 5min");purpleM.setBaseColor(DyeColor.MAGENTA);purpleM.setPatterns(Arrays.asList(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE), new Pattern(DyeColor.MAGENTA, PatternType.BORDER), new Pattern(DyeColor.MAGENTA, PatternType.STRIPE_BOTTOM), new Pattern(DyeColor.MAGENTA, PatternType.STRIPE_TOP)));purpleM.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);purple.setItemMeta(purpleM);
					ItemStack cyan = new ItemStack(Material.BANNER, 1, (byte) 12);BannerMeta cyanM = (BannerMeta) cyan.getItemMeta();cyanM.setDisplayName("§1§b- 1min");cyanM.setBaseColor(DyeColor.CYAN);cyanM.setPatterns(Arrays.asList(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE), new Pattern(DyeColor.CYAN, PatternType.BORDER), new Pattern(DyeColor.CYAN, PatternType.STRIPE_BOTTOM), new Pattern(DyeColor.CYAN, PatternType.STRIPE_TOP)));cyanM.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);cyan.setItemMeta(cyanM);
					ItemStack green = new ItemStack(Material.BANNER, 1, (byte) 2);BannerMeta greenM = (BannerMeta) green.getItemMeta();greenM.setDisplayName("§1§2- 30s");greenM.setBaseColor(DyeColor.GREEN);greenM.setPatterns(Arrays.asList(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE), new Pattern(DyeColor.GREEN, PatternType.BORDER), new Pattern(DyeColor.GREEN, PatternType.STRIPE_BOTTOM), new Pattern(DyeColor.GREEN, PatternType.STRIPE_TOP)));greenM.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);green.setItemMeta(greenM);
					ItemStack red = new ItemStack(Material.BANNER, 1, (byte) 1);BannerMeta redM = (BannerMeta) red.getItemMeta();redM.setDisplayName("§1§c+ 5min");redM.setBaseColor(DyeColor.RED);redM.setPatterns(Arrays.asList(new Pattern(DyeColor.WHITE, PatternType.STRAIGHT_CROSS), new Pattern(DyeColor.RED, PatternType.BORDER), new Pattern(DyeColor.RED, PatternType.STRIPE_BOTTOM), new Pattern(DyeColor.RED, PatternType.STRIPE_TOP)));redM.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS); red.setItemMeta(redM);
					ItemStack orange = new ItemStack(Material.BANNER, 1, (byte) 14);BannerMeta orangeM = (BannerMeta) orange.getItemMeta();orangeM.setDisplayName("§1§6+ 1min");orangeM.setBaseColor(DyeColor.ORANGE);orangeM.setPatterns(Arrays.asList(new Pattern(DyeColor.WHITE, PatternType.STRAIGHT_CROSS), new Pattern(DyeColor.ORANGE, PatternType.BORDER), new Pattern(DyeColor.ORANGE, PatternType.STRIPE_BOTTOM), new Pattern(DyeColor.ORANGE, PatternType.STRIPE_TOP)));orangeM.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS); orange.setItemMeta(orangeM);
					ItemStack yellow = new ItemStack(Material.BANNER, 1, (byte) 11);BannerMeta yellowM = (BannerMeta) yellow.getItemMeta();yellowM.setDisplayName("§1§e+ 30s");yellowM.setBaseColor(DyeColor.YELLOW);yellowM.setPatterns(Arrays.asList(new Pattern(DyeColor.WHITE, PatternType.STRAIGHT_CROSS), new Pattern(DyeColor.YELLOW, PatternType.BORDER), new Pattern(DyeColor.YELLOW, PatternType.STRIPE_BOTTOM), new Pattern(DyeColor.YELLOW, PatternType.STRIPE_TOP)));yellowM.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS); yellow.setItemMeta(yellowM);
					STKPlugin.getInstance().gui.STK_MENU.setItem(19, purple);
					STKPlugin.getInstance().gui.STK_MENU.setItem(20, cyan);
					STKPlugin.getInstance().gui.STK_MENU.setItem(21, green);
					STKPlugin.getInstance().gui.STK_MENU.setItem(22, setMetaInItem(new ItemStack(Material.WATCH), "§eRôle", Arrays.asList("", "§7Temps avant annonce des rôles", "", "§e"+new DecimalFormat("##00").format((STKPlugin.getInstance().s.TIMER_ROLES/60))+"min "+new DecimalFormat("##00").format((STKPlugin.getInstance().s.TIMER_ROLES%60))+"s")));
					STKPlugin.getInstance().gui.STK_MENU.setItem(23, yellow);
					STKPlugin.getInstance().gui.STK_MENU.setItem(24, orange);
					STKPlugin.getInstance().gui.STK_MENU.setItem(25, red);
				}else {
					STKPlugin.getInstance().gui.STK_MENU.setItem(19, getGlass());
					STKPlugin.getInstance().gui.STK_MENU.setItem(20, getGlass());
					STKPlugin.getInstance().gui.STK_MENU.setItem(21, getGlass());
					STKPlugin.getInstance().gui.STK_MENU.setItem(22, getGlass());
					STKPlugin.getInstance().gui.STK_MENU.setItem(23, getGlass());
					STKPlugin.getInstance().gui.STK_MENU.setItem(24, getGlass());
					STKPlugin.getInstance().gui.STK_MENU.setItem(25, getGlass());
				}
			}else if(event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§1")) {
				ItemMeta meta = event.getInventory().getItem(22).getItemMeta();
				if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("§5- 5min") && STKPlugin.getInstance().s.TIMER_ROLES-300>0) {
					STKPlugin.getInstance().s.TIMER_ROLES-=300;
				}else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("§b- 1min") && STKPlugin.getInstance().s.TIMER_ROLES-60>0) {
					STKPlugin.getInstance().s.TIMER_ROLES-=60;
				}else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("§2- 30s") && STKPlugin.getInstance().s.TIMER_ROLES-30>0) {
					STKPlugin.getInstance().s.TIMER_ROLES-=30;
				}else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("§c+ 5min")) {
					STKPlugin.getInstance().s.TIMER_ROLES+=300;
				}else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("§6+ 1min")) {
					STKPlugin.getInstance().s.TIMER_ROLES+=60;
				}else if(event.getCurrentItem().getItemMeta().getDisplayName().endsWith("§e+ 30s")) {
					STKPlugin.getInstance().s.TIMER_ROLES+=30;
				}
				meta.setLore(Arrays.asList("", "§7Temps avant annonce des rôles", "", "§e"+new DecimalFormat("##00").format((STKPlugin.getInstance().s.TIMER_ROLES/60))+"min "+new DecimalFormat("##00").format((STKPlugin.getInstance().s.TIMER_ROLES%60))+"s"));
				event.getInventory().getItem(22).setItemMeta(meta);
			}else if(GAME.getRoleCount().containsKey(event.getCurrentItem().getItemMeta().getDisplayName().substring(2)) && !event.getCurrentItem().getItemMeta().getDisplayName().substring(2).equals(STKPlugin.getInstance().getSTKConfig().getString("stk.role.king.display"))) {
				int amount = GAME.getRoleCount().get(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
				if(event.getClick().equals(ClickType.LEFT)) {
					event.getCurrentItem().setAmount(amount+1);
					GAME.getRoleCount().replace(event.getCurrentItem().getItemMeta().getDisplayName().substring(2), amount+1);
					if(amount == 0) {
						ItemMeta meta = event.getCurrentItem().getItemMeta();
						List<String> list = meta.getLore();
						int i = 0;
						while(!list.get(i).equals("§cDésactivé"))
							i++;
						List<String> l = new ArrayList<>();
						for(int j = 0; j < list.size(); j++)
							if(j == i)
								l.add("§aActivé");
							else
								l.add(list.get(j));
						meta.setLore(l);
						event.getCurrentItem().setItemMeta(meta);
					}
				}else if(event.getClick().equals(ClickType.RIGHT) && amount > 0) {
					event.getCurrentItem().setAmount(amount-1 == 0 ? 1 : amount-1);
					GAME.getRoleCount().replace(event.getCurrentItem().getItemMeta().getDisplayName().substring(2), amount-1);
					if(amount == 1) {
						ItemMeta meta = event.getCurrentItem().getItemMeta();
						List<String> list = meta.getLore();
						int i = 0;
						while(!list.get(i).equals("§aActivé"))
							i++;
						List<String> l = new ArrayList<>();
						for(int j = 0; j < list.size(); j++)
							if(j == i)
								l.add("§cDésactivé");
							else
								l.add(list.get(j));
						meta.setLore(l);
						event.getCurrentItem().setItemMeta(meta);
					}
				}
				List<RoleRegister> config = new ArrayList<RoleRegister>();
				for(RoleRegister roleRegister : STKPlugin.getInstance().getRegisterRoles()) {
					for(int i = 0; i < ((Integer) STKPlugin.getInstance().getSTKGameManager().getRoleCount().get(STKPlugin.getInstance().getSTKConfig().getString(roleRegister.getKey()))).intValue(); i++)
						config.add(roleRegister);
				}
				ItemMeta meta = event.getInventory().getItem(51).getItemMeta();
				meta.setLore(Arrays.asList("", "§c"+config.size()+" §erôles pour §c"+Bukkit.getOnlinePlayers().size()+" §ejoueurs"));
				event.getInventory().getItem(51).setItemMeta(meta);
			}
		}
	}
}