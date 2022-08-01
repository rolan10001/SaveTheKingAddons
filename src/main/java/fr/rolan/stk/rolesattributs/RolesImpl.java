package fr.rolan.stk.rolesattributs;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotation.NotNull;
import org.jetbrains.annotation.Nullable;

import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.manager.STKGameManager;
import fr.rolan.stk.manager.STKUser;

public abstract class RolesImpl implements Roles, Listener, Cloneable {
	
	@NotNull
	private UUID uuid;
	public final STKGameManager game;
	
	public RolesImpl(STKGameManager game, @NotNull UUID uuid) {
		this.uuid = uuid;
		this.game = game;
	}
	
	@Nullable
	public Player recoverPower() {
		Player player = Bukkit.getPlayer(uuid);
		if(player == null)
			return null;
		STKUser user = STKPlugin.getInstance().getUser(player);
		user.setKit(Boolean.valueOf(true));
		player.performCommand("k role");
		player.sendMessage(STKPlugin.getInstance().getSTKConfig().getString("stk.announcement.review_role"));
		try {
			for(ItemStack it : game.getStuffs().getStuffRoles().get(user.getRole().getDisplay())) {
				if(player.getInventory().firstEmpty() == -1) {
					player.getWorld().dropItem(player.getLocation(), it);
					continue;
				}
				player.getInventory().addItem(it);
				player.updateInventory();
			}
		}catch(Exception e) {}
		return player;
	}
	
	@NotNull
	public UUID getPlayerUUID() {
		return uuid;
	}
	
	public void setPlayerUUID(@NotNull UUID uuid) {
		this.uuid = uuid;
	}
	
	public void stolen(@NotNull UUID uuid) {}
	
	public boolean isDisplay(String s) {
		return s.equals(getDisplay());
	}
	
	public Roles publicClone() {
		try {
			return (Roles)clone();
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}