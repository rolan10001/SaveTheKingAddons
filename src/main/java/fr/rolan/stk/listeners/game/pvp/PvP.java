package fr.rolan.stk.listeners.game.pvp;

import static fr.rolan.api.game.GameSettings.*;
import static fr.rolan.api.gui.GuiManager.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.events.ActionBarEvent;
import fr.rolan.api.player.IUser;
import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.enumstk.State;
import fr.rolan.stk.events.FinalDeathServantEvent;
import fr.rolan.stk.events.KingDeathEvent;
import fr.rolan.stk.manager.STKUser;
import fr.rolan.stk.roles.king.King;
import fr.rolan.stk.rolesattributs.Power;
import fr.rolan.tools.NMSMethod;

public class PvP implements Listener {
	
	public PvP() {
		Bukkit.getPluginManager().registerEvents(this, STKPlugin.getInstance());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if(block.getType().equals(Material.GRAVEL) && !player.getGameMode().equals(GameMode.CREATIVE)) {
			if(new Random().nextInt(100) < UHCAPI.get().getSettings().FLINT_DROP) {
				event.setCancelled(true);
				block.setType(Material.AIR);
				block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.FLINT));
			}
		}else if(block.getType().equals(Material.LEAVES) || block.getType().equals(Material.LEAVES_2) && !player.getGameMode().equals(GameMode.CREATIVE)) {
			if(new Random().nextInt(100) < UHCAPI.get().getSettings().APPLE_DROP) {
				event.setCancelled(true);
				block.setType(Material.AIR);
				block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.APPLE));
			}
		}else if(block.getType().equals(Material.STONE) && (block.getData() == 1 || block.getData() == 3 || block.getData() == 5) && !player.getGameMode().equals(GameMode.CREATIVE)) {
			event.setCancelled(true);
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.COBBLESTONE));
		}
	}
	
	@EventHandler
	public void onLeaves(LeavesDecayEvent event) {
		event.getBlock().getDrops().clear();
		if(new Random().nextInt(100) < UHCAPI.get().getSettings().APPLE_DROP)
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.APPLE));
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player && (UHCAPI.get().getSettings().PV_IN_TAB || UHCAPI.get().getSettings().PV_ON_HEAD)) {
			HEALTH_SCOREBOARD.setObjectiveForPlayer((Player) entity);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		if(entity instanceof Player && damager instanceof Player) {
			if(((Player) damager).hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				ItemStack it = (((Player) damager).getItemInHand() != null) ? ((Player) damager).getItemInHand() : new ItemStack(Material.AIR);
	        	int sharpnessLevel = it.containsEnchantment(Enchantment.DAMAGE_ALL) ? it.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0;
	        	double totalDamage = event.getDamage();
	        	double weaponDamage = (damager.getFallDistance() > 0 && !damager.isOnGround ()) ? ((totalDamage - 1.25D * sharpnessLevel) / (1.0D + 1.3D) - 1.0D-3.0D) : ((totalDamage - 1.25D * sharpnessLevel) / (1.0D + 1.3D) - 1.0D);
	        	double finalDamage = (weaponDamage + 1.25D * sharpnessLevel)+(weaponDamage + 1.25D * sharpnessLevel)*UHCAPI.get().getSettings().STRENGTH_VALUE/100;
	        	event.setDamage(finalDamage);
			}
		}else if(entity instanceof Player && damager instanceof Arrow && event.getCause().equals(DamageCause.PROJECTILE) && ((Projectile) damager).getShooter() instanceof Player) {
			if(UHCAPI.get().getSettings().PV_IN_TAB)
				((Player)((Projectile) damager).getShooter()).sendMessage("§8[§eUHC§8] §a"+entity.getName()+" §7est à §e"+(new DecimalFormat("##0").format(((((Player) entity).getHealth()+Math.round(((CraftPlayer) entity).getHandle().getAbsorptionHearts())-event.getFinalDamage())*5) < 0 ? 0 : (((Player) entity).getHealth()+Math.round(((CraftPlayer) entity).getHandle().getAbsorptionHearts())-event.getFinalDamage())*5))+"%");
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if(!(event.getRightClicked() instanceof Player) || !player.getGameMode().equals(GameMode.SPECTATOR)) return;
		Player target = (Player) event.getRightClicked();
		Inventory inv = Bukkit.createInventory(null, 54, "§8» §e"+target.getName());
		for(int i = 0; i < 36; i++)
			inv.setItem(i, target.getInventory().getContents()[i] == null || target.getInventory().getContents()[i].getType().equals(Material.AIR) ? new ItemStack(Material.AIR) : target.getInventory().getContents()[i]);
		for(int i = 36; i < 45; i++)
			inv.setItem(i, getGlass());
		for(int i = 45; i < 49; i++)
			inv.setItem(i, target.getInventory().getArmorContents()[i-45] == null || target.getInventory().getArmorContents()[i-45].getType().equals(Material.AIR) ? new ItemStack(Material.AIR) : target.getInventory().getArmorContents()[i-45]);
		inv.setItem(49, getGlass());
		inv.setItem(50, setMetaInItem(new ItemStack(Material.GOLDEN_APPLE), "§eVie", Arrays.asList("§7Vie : §c"+new DecimalFormat("00.00").format(target.getHealth())+"❤", "§7Saturation : §e"+target.getFoodLevel(), "§7XP : §a"+target.getLevel())));
		List<String> list = new ArrayList<String>();
		for(PotionEffect effect : target.getActivePotionEffects())
			list.add("§b"+effect.getType().getName()+" "+effect.getAmplifier()+" : §7"+(effect.getDuration()/60)+"min"+(effect.getDuration()%60)+"s");
		inv.setItem(51, setMetaInItem(new ItemStack(Material.POTION, 1, (byte) 9), "§eEffets de potion", list));
		ItemStack it = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);SkullMeta meta = (SkullMeta) it.getItemMeta();meta.setOwner(target.getName());meta.setDisplayName("§eInformations");
		List<String> lore = new ArrayList<String>();
		lore.add("§7Kills : §c"+UHCAPI.get().getUser(target).getKillStreak());
		meta.setLore(lore);
		inv.setItem(52, it);
		inv.setItem(53, getGlass());
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player && (UHCAPI.get().getSettings().PV_IN_TAB || UHCAPI.get().getSettings().PV_ON_HEAD)) {
			HEALTH_SCOREBOARD.setObjectiveForPlayer((Player) entity);
		}
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if(event.getEntity().getWorld().getName().equals("Lobby")) event.setCancelled(true);
		if(!UHCAPI.get().getSettings().HORSE && event.getEntity() instanceof Horse) event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof Enderman) {
			event.getDrops().clear();
			if(new Random().nextInt(100) < UHCAPI.get().getSettings().ENDER_PEARL_DROP) {
				event.getDrops().add(new ItemStack(Material.ENDER_PEARL));
			}
		}
	}
	
	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if(event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
			if(!UHCAPI.get().getSettings().ABSORPTION) {
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if(player.hasPotionEffect(PotionEffectType.ABSORPTION))
							player.removePotionEffect(PotionEffectType.ABSORPTION);
					}
				}.runTaskLater(STKPlugin.getInstance(), 1L);
			}else if(UHCAPI.get().getSettings().PV_IN_TAB || UHCAPI.get().getSettings().PV_ON_HEAD) {
				HEALTH_SCOREBOARD.setObjectiveForPlayer(player);
			}
		}
	}
	
	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if(UHCAPI.get().getSettings().XENOPHOBIA)
			NMSMethod.disguiseInPNJ(player);
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		IUser user = UHCAPI.get().getUser(player);
		if(UHCAPI.get().getSettings().TEAM) {
			if(event.getMessage().startsWith("!")) {
				for(Player players : Bukkit.getOnlinePlayers())
					players.sendMessage("§"+Integer.toHexString(user.getTeam().getColor())+player.getName()+" §8» §f"+(player.hasPermission("host.use") ? ChatColor.translateAlternateColorCodes('&', event.getMessage().substring(1)) : event.getMessage().substring(1)));
			}else {
				for(UUID uuid : user.getTeam().getPlayers())
					if(Bukkit.getPlayer(uuid) != null)
						Bukkit.getPlayer(uuid).sendMessage("§d§lTeam §7§l▏ §"+Integer.toHexString(user.getTeam().getColor())+player.getName()+" §8» §f"+(player.hasPermission("host.use") ? ChatColor.translateAlternateColorCodes('&', event.getMessage()) : event.getMessage()));
			}
		}else {
			for(Player players : Bukkit.getOnlinePlayers())
				players.sendMessage("§7"+player.getName()+" §8» §f"+(player.hasPermission("host.use") ? ChatColor.translateAlternateColorCodes('&', event.getMessage()) : event.getMessage()));
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		IUser user = UHCAPI.get().getUser(player);
		if(UHCAPI.get().getSettings().PV_IN_TAB || UHCAPI.get().getSettings().PV_ON_HEAD) player.setScoreboard(HEALTH_SCOREBOARD.getScoreboard());
		if(UHCAPI.get().getSettings().XENOPHOBIA) Bukkit.getOnlinePlayers().forEach(players -> NMSMethod.disguiseInPNJ(players));
		if(PLAYERS_LIST.contains(player.getUniqueId())) {
			if(UHCAPI.get().getSettings().F3) {NMSMethod.enableF3(player);}else {NMSMethod.disableF3(player);}
			event.setJoinMessage("§a+ §8» §7"+player.getName()+" §8[§e"+PLAYERS+"§8/§e99§8]");
		}else {
			event.setJoinMessage(null);
			if(!user.isSpectator()) {
				user.setSpectator(true);
				player.setGameMode(GameMode.SPECTATOR);
				player.sendMessage("§7§l▏ §7La partie a déjà commencé, vous pouvez toujours la regarder en tant que spectateur.");
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(PLAYERS_LIST.contains(player.getUniqueId())) {
			event.setQuitMessage("§c- §8» §7"+player.getName()+" §8[§e"+(PLAYERS-1)+"§8/§e99§8]");
		}else {
			event.setQuitMessage(null);
		}
	}
	
	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onActionBar(ActionBarEvent event) {
		if(!UHCAPI.get().getSettings().TEAM)
			return;
		if(!UHCAPI.get().getSettings().TEAM_DIRECTION)
			return;
		IUser user = UHCAPI.get().getUser(event.getPlayerUUID());
		if(user.isDied() || user.isSpectator())
			return;
		if(user.getTeam().getPlayers().size() <= 1)
			return;
		StringBuilder stringBuilder = new StringBuilder();
		for(UUID uuid : user.getTeam().getPlayers())
			if(Bukkit.getPlayer(uuid) != null && !uuid.equals(event.getPlayerUUID()))
				stringBuilder.append((Bukkit.getPlayer(uuid).getHealth() <= 6.0D ? "§c" : Bukkit.getPlayer(uuid).getHealth() <= 10.0D ? "§6" : Bukkit.getPlayer(uuid).getHealth() <= 12.0D ? "§e" : "§a")+Bukkit.getPlayer(uuid).getName()+": §f"+(Bukkit.getPlayer(uuid).getWorld().getName().equals(Bukkit.getPlayer(event.getPlayerUUID()).getWorld().getName()) ? (UHCAPI.get().getGameManager().updateArrow(Bukkit.getPlayer(event.getPlayerUUID()), Bukkit.getPlayer(uuid).getLocation())+" §f"+new DecimalFormat("###########0.0").format(UHCAPI.get().getGameManager().playerDistance(Bukkit.getPlayer(event.getPlayerUUID()), Bukkit.getPlayer(uuid).getLocation()))) : "?")+"   ");
		event.setActionBar(stringBuilder.toString());
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		String death = event.getDeathMessage();
		event.setDeathMessage(null);
		event.setKeepInventory(false);
		event.setKeepLevel(false);
		for(ItemStack it : event.getDrops())
			if(it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().endsWith("du Servant"))
				it.setType(Material.AIR);
		Player player = event.getEntity();
		STKUser STKuser = STKPlugin.getInstance().getUser(player);
		IUser user = UHCAPI.get().getUser(player);
		if(!STKuser.getState().equals(State.ALIVE))
			return;
		if(STKuser.getRole() instanceof King) {
			Bukkit.getPluginManager().callEvent(new KingDeathEvent(player.getUniqueId()));
		}else {
			if(!((Power) STKuser.getRole()).hasPower())
				Bukkit.getPluginManager().callEvent(new FinalDeathServantEvent(player.getUniqueId()));
		}
		if(player.getKiller() == null)
			Bukkit.broadcastMessage(death.replace(player.getName(), "§"+Integer.toHexString(user.getTeam().getColor())+player.getName()));
		else {
			Bukkit.broadcastMessage(death.replace(player.getName(), "§"+Integer.toHexString(user.getTeam().getColor())+player.getName()).replace(player.getKiller().getName(), "§"+Integer.toHexString(UHCAPI.get().getUser(player.getKiller()).getTeam().getColor())+player.getKiller().getName()));
			UHCAPI.get().getUser(player.getKiller()).setKillStreak(UHCAPI.get().getUser(player.getKiller()).getKillStreak()+1);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		IUser user = UHCAPI.get().getUser(player);
		STKUser STKuser = STKPlugin.getInstance().getUser(player);
		if(STKuser.getRole() instanceof King || !((Power)STKuser.getRole()).hasPower()) {
			event.setRespawnLocation(user.getDeathLoc());
			new BukkitRunnable() {
				
				@Override
				public void run() {
					player.setGameMode(GameMode.SPECTATOR);
					user.setSpectator(true);
					user.setDied(true);
					user.setDiamondArmor(0);
				}
			}.runTaskLater(STKPlugin.getInstance(), 2);
		}else {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					event.setRespawnLocation(user.getDeathLoc());
					player.setGameMode(GameMode.SPECTATOR);
					for(UUID u : user.getTeam().getPlayers())
						if(STKPlugin.getInstance().getUser(u).getRole() instanceof King) {
							if(Bukkit.getPlayer(u) != null)
								player.setSpectatorTarget(player);
							break;
						}
					user.setDiamondArmor(0);
				}
			}.runTaskLater(STKPlugin.getInstance(), 2);
			new BukkitRunnable() {
				int i = 11;
				@Override
				public void run() {
					NMSMethod.sendTitle(player, 5, 20, 5, "§e§l"+i, "");
					if(i == 0) {
						player.teleport(new Location(Bukkit.getWorld("Host"), 0.500, 63, 0.500));
						player.setGameMode(GameMode.SURVIVAL);
						STKuser.getRole().recoverPotionEffect(player);
						STKuser.getRole().stolen(player.getUniqueId());
						cancel();
						return;
					}
					i--;
				}
			}.runTaskTimer(STKPlugin.getInstance(), 20, 20);
		}
	}
	
	@EventHandler
	public void onKingDeath(KingDeathEvent event) {
		UUID uuid = event.getUUID();
		STKUser STKuser = STKPlugin.getInstance().getUser(uuid);
		IUser user = UHCAPI.get().getUser(uuid);
		Bukkit.getOnlinePlayers().forEach(players -> players.getWorld().playSound(new Location(players.getWorld(), players.getLocation().getX(), players.getLocation().getY(), players.getLocation().getZ()), Sound.WITHER_SPAWN, (float) 0.5F, 1F));
		Bukkit.broadcastMessage("§8[§eSTK§8] §fLe Roi de l'équipe "+user.getTeam().getDisplayName()+" §fest tombé au combat, ses serviteurs ne peuvent plus se ressusciter !");
		PLAYERS_LIST.remove(uuid);
		STKPlugin.getInstance().gameSettings.PLAYERSROLES.remove(uuid);
		STKuser.setState(State.DEATH);
		if(Bukkit.getPlayer(uuid) == null)
			return;
		Player player = Bukkit.getPlayer(uuid);
		user.setDeathArmorStuff(player.getInventory().getArmorContents());
		user.setDeathStuff(player.getInventory().getContents());
		user.setDeathLoc(player.getLocation());
		if(UHCAPI.get().getStuffManager().getStuffDeath() != null)
			for(ItemStack it : UHCAPI.get().getStuffManager().getStuffDeath()) {if(it != null && !it.getType().equals(Material.AIR)) player.getWorld().dropItemNaturally(player.getLocation(), it);}
		if(UHCAPI.get().getStuffManager().getStuffArmorDeath() != null)
			for(ItemStack it : UHCAPI.get().getStuffManager().getStuffArmorDeath()) {if(it != null && !it.getType().equals(Material.AIR)) player.getWorld().dropItemNaturally(player.getLocation(), it);}
		for(UUID u : user.getTeam().getPlayers())
			if(!(STKPlugin.getInstance().getUser(u).getRole() instanceof King))
					((Power) STKPlugin.getInstance().getUser(u).getRole()).setPower(false);
	}
	
	@EventHandler
	public void onFinalDeathServant(FinalDeathServantEvent event) {
		UUID uuid = event.getUUID();
		STKUser STKuser = STKPlugin.getInstance().getUser(uuid);
		IUser user = UHCAPI.get().getUser(uuid);
		Bukkit.getOnlinePlayers().forEach(players -> players.getWorld().playSound(new Location(players.getWorld(), players.getLocation().getX(), players.getLocation().getY(), players.getLocation().getZ()), Sound.WITHER_SPAWN, (float) 0.5F, 1F));
		Bukkit.broadcastMessage("§8[§eSTK§8] §fUn "+STKuser.getRole().getDisplay()+" du Roi de l'équipe "+user.getTeam().getDisplayName()+" §fest tombé au combat, il rejoint son maître dans le cimetière des anciens combattants !");
		PLAYERS_LIST.remove(uuid);
		STKPlugin.getInstance().gameSettings.PLAYERSROLES.remove(uuid);
		STKuser.setState(State.DEATH);
		if(Bukkit.getPlayer(uuid) == null)
			return;
		Player player = Bukkit.getPlayer(uuid);
		user.setDeathArmorStuff(player.getInventory().getArmorContents());
		user.setDeathStuff(player.getInventory().getContents());
		user.setDeathLoc(player.getLocation());
		if(UHCAPI.get().getStuffManager().getStuffDeath() != null)
			for(ItemStack it : UHCAPI.get().getStuffManager().getStuffDeath()) {if(it != null && !it.getType().equals(Material.AIR)) player.getWorld().dropItemNaturally(player.getLocation(), it);}
		if(UHCAPI.get().getStuffManager().getStuffArmorDeath() != null)
			for(ItemStack it : UHCAPI.get().getStuffManager().getStuffArmorDeath()) {if(it != null && !it.getType().equals(Material.AIR)) player.getWorld().dropItemNaturally(player.getLocation(), it);}
	}
}