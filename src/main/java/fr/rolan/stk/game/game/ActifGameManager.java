package fr.rolan.stk.game.game;

import static fr.rolan.api.game.GameSettings.*;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.rolan.api.UHCAPI;
import fr.rolan.api.events.BorderEvent;
import fr.rolan.api.events.EpisodeEvent;
import fr.rolan.api.events.NoPvPEvent;
import fr.rolan.api.game.enums.GameState;
import fr.rolan.api.game.scenario.superheroes.SuperHeroes;
import fr.rolan.api.game.team.Teams;
import fr.rolan.api.player.IUser;
import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.events.RepartitionRoleEvent;
import fr.rolan.stk.listeners.game.GeneralGameManager;
import fr.rolan.stk.listeners.game.border.Border;
import fr.rolan.stk.listeners.game.finish.Finish;
import fr.rolan.stk.listeners.game.invincibility.Invincibility;
import fr.rolan.stk.listeners.game.nopvp.NoPvP;
import fr.rolan.stk.listeners.game.pvp.PvP;
import fr.rolan.tools.NMSMethod;
import fr.rolan.tools.TPS;

public class ActifGameManager extends BukkitRunnable {
	
	private final GeneralGameManager generalGameManager;
	private Invincibility invincibility;
	private NoPvP nopvp;
	private PvP pvp;
	private Border border;
	public Teams winner;
	
	public ActifGameManager() {
		this.generalGameManager = new GeneralGameManager();
		this.invincibility = new Invincibility();
		Bukkit.getPluginManager().registerEvents(invincibility, STKPlugin.getInstance());
		STKPlugin.getInstance().getTeleportationManager().unregisterListeners();
		UHCAPI.get().getGameManager().setGameState(GameState.INVINCIBILITY);
		runTaskTimer(STKPlugin.getInstance(), 20, 20);
	}
	
	public void newNoPvPState() {
		HandlerList.unregisterAll(invincibility);
		UHCAPI.get().getGameManager().setGameState(GameState.NOPVP);
		this.nopvp = new NoPvP();
		Bukkit.getPluginManager().callEvent(new NoPvPEvent());
		Bukkit.broadcastMessage("§8[§eUHC§8] §7Vous n'êtes plus invincible.");
	}
	
	public void newPvPState() {
		HandlerList.unregisterAll(nopvp);
		UHCAPI.get().getGameManager().setGameState(GameState.PVP);
		this.pvp = new PvP();
		Bukkit.broadcastMessage("§8[§eUHC§8] §cLe PvP est désormais §aactivé §c!");
		Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 0F));
	}
	
	public void newBorderState() {
		HandlerList.unregisterAll(pvp);
		UHCAPI.get().getGameManager().setGameState(GameState.BORDER);
		this.border = new Border();
		Bukkit.getPluginManager().callEvent(new BorderEvent());
		BORDER.setWarningDistance(20);
		BORDER.setSize(UHCAPI.get().getSettings().BORDER_FINAL_SIZE*2, (long) ((UHCAPI.get().getSettings().BORDER_SIZE-UHCAPI.get().getSettings().BORDER_FINAL_SIZE)/UHCAPI.get().getSettings().BORDER_SPEED));
		Bukkit.broadcastMessage("§8[§eUHC§8] §bLa réduction de la bordure est désormais §aactivée §7!");
		Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 0F));
	}
	
	public void newFinishState() {
		HandlerList.unregisterAll(border);
		UHCAPI.get().getGameManager().setGameState(GameState.FINISH);
		new Finish();
		Bukkit.broadcastMessage("§8[§eSTK§8] §fBravo à l'équipe "+winner.getDisplayName()+" qui remporte cette partie de Save The King !");
		Bukkit.getOnlinePlayers().forEach(players -> players.teleport(new Location(Bukkit.getWorld("Lobby"), -687.0, 47.1, -262.0, 90.0F, 0.0F)));
		cancel();
	}
	
	public GeneralGameManager getGeneralGameManager() {
		return generalGameManager;
	}
	
	@Override
	public void run() {
		SECOND++;
		if(SECOND == 60) {
			MINUTE++;
			MINUTES++;
			SECOND = 0;
			if(MINUTES%20 == 0) {
				EPISODE++;
				Bukkit.broadcastMessage("§8[§eUHC§8] §7Fin de l'épisode §c"+EPISODE+"§7.");
				Bukkit.getPluginManager().callEvent(new EpisodeEvent());
			}
			if(MINUTE == 60) {
				HEURE++;
				MINUTE = 0;
			}
		}
		for(Player player : Bukkit.getOnlinePlayers()) {
			IUser user = UHCAPI.get().getUser(player);
			DecimalFormat format = new DecimalFormat("00");
			NMSMethod.setTablistHeaderFooter(player, "\n§6§l» §e§l"+(UHCAPI.get().getSettings().NAME.equalsIgnoreCase("DxD UHC") ? "DxD §c§lUHC" : UHCAPI.get().getSettings().NAME)+" §6§l«\n\n  §e§lPing: §r"+NMSMethod.getPingColor(player)+"§r  §l▏§r  §e§lTPS: §a"+TPS.getTPS()+"§r  §l▏§r  §e§lJoueurs: §a"+Bukkit.getOnlinePlayers().size()+"  \n", "\n§7Groupes §8» §6"+(PLAYERS > 16 ? "5" : PLAYERS > 12 ? "4" : "3")+"\n§7Kills §8» §c"+user.getKillStreak()+"\n§7Timer §8» §e"+format.format(HEURE)+":"+format.format(MINUTE)+":"+format.format(SECOND)+"\n");
			if(UHCAPI.get().getSettings().CATEYES)
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
			if(UHCAPI.get().getSettings().PV_IN_TAB || UHCAPI.get().getSettings().PV_ON_HEAD)
				HEALTH_SCOREBOARD.setObjectiveForPlayer(player);
			STKPlugin.getInstance().getSTKGameManager().actionBar(player);
		}
		if(UHCAPI.get().getSettings().SUPER_HEROES && HEURE == 0 && MINUTES == 0 && SECOND == 10)
			Bukkit.getOnlinePlayers().forEach(player -> new SuperHeroes(player));
		if(UHCAPI.get().getSettings().INVINCIBILITY_TIMER/60 == MINUTES && UHCAPI.get().getSettings().INVINCIBILITY_TIMER%60 == SECOND)
			newNoPvPState();
		if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES && UHCAPI.get().getSettings().PVP_TIMER%60 == SECOND) {
			newPvPState();
		}else if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES+10 && UHCAPI.get().getSettings().PVP_TIMER%60 == SECOND) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7PvP activé dans §e10 §7minutes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().PVP_TIMER%60 == SECOND) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7PvP activé dans §e1 §7minute.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().PVP_TIMER%60 == 60-SECOND-30) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7PvP activé dans §e30 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().PVP_TIMER%60 == 60-SECOND-10) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7PvP activé dans §e10 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().PVP_TIMER%60 == 60-SECOND-5) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7PvP activé dans §e5 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().PVP_TIMER%60 == 60-SECOND-4) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7PvP activé dans §e4 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().PVP_TIMER%60 == 60-SECOND-3) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7PvP activé dans §e3 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().PVP_TIMER%60 == 60-SECOND-2) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7PvP activé dans §e2 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().PVP_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().PVP_TIMER%60 == 60-SECOND-1) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7PvP activé dans §e1 §7seconde.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}
		if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES && UHCAPI.get().getSettings().BORDER_TIMER%60 == SECOND) {
			//newBorderState();
		}else if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES+10 && UHCAPI.get().getSettings().BORDER_TIMER%60 == SECOND) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Réduction de la bordure dans §e10 §7minutes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().BORDER_TIMER%60 == SECOND) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Réduction de la bordure dans §e1 §7minute.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().BORDER_TIMER%60 == 60-SECOND-30) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Réduction de la bordure dans §e30 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().BORDER_TIMER%60 == 60-SECOND-10) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Réduction de la bordure dans §e10 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().BORDER_TIMER%60 == 60-SECOND-5) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Réduction de la bordure dans §e5 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().BORDER_TIMER%60 == 60-SECOND-4) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Réduction de la bordure dans §e4 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().BORDER_TIMER%60 == 60-SECOND-3) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Réduction de la bordure dans §e3 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().BORDER_TIMER%60 == 60-SECOND-2) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Réduction de la bordure dans §e2 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}else if(UHCAPI.get().getSettings().BORDER_TIMER/60 == MINUTES+1 && UHCAPI.get().getSettings().BORDER_TIMER%60 == 60-SECOND-1) {
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Réduction de la bordure dans §e1 §7secondes.");
			Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 1.0F, 1.0F));
		}
		if(STKPlugin.getInstance().s.RANDOM_ROLE && STKPlugin.getInstance().s.TIMER_ROLES/60 == MINUTES && STKPlugin.getInstance().s.TIMER_ROLES%60 == SECOND)
			Bukkit.getPluginManager().callEvent(new RepartitionRoleEvent());
		if(!STKPlugin.getInstance().s.RANDOM_ROLE && MINUTES == 5 && SECOND == 0)
			Bukkit.getPluginManager().callEvent(new RepartitionRoleEvent());
		if(UHCAPI.get().getSettings().FINALHEAL && UHCAPI.get().getSettings().FINALHEAL_TIMER/60 == MINUTES && UHCAPI.get().getSettings().FINALHEAL_TIMER%60 == SECOND) {
			Bukkit.getOnlinePlayers().forEach(player -> player.setHealth(player.getMaxHealth()));
			Bukkit.broadcastMessage("§8[§eUHC§8] §7Le scénario §dFinalHeal §7est §aactivé§7.");
		}
	}
	
}