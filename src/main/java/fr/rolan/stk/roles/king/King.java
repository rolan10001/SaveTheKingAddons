package fr.rolan.stk.roles.king;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotation.NotNull;
import org.jetbrains.annotation.Nullable;

import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.enumstk.Category;
import fr.rolan.stk.manager.STKGameManager;
import fr.rolan.stk.rolesattributs.RolesImpl;

public class King extends RolesImpl {
	
	private final Category category = Category.KING;
	
	public King(STKGameManager game, @NotNull UUID uuid) {
		super(game, uuid);
	}

	@Override
	public String getDescription() {
		return STKPlugin.getInstance().getSTKConfig().getString("stk.role.king.description");
	}

	@Override
	public String getDisplay() {
		return STKPlugin.getInstance().getSTKConfig().getString("stk.role.king.display");
	}

	@Override
	public boolean isCategory(@NotNull Category paramCategory) {
		return paramCategory.equals(category);
	}

	@Override
	public @NotNull Category getCategory() {
		return category;
	}

	@Override
	public void recoverPotionEffect(@NotNull Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
	}
	
	@Override
	public @Nullable Player recoverPower() {
		Player player = super.recoverPower();
		if(player == null)
			return null;
		player.setMaxHealth(40.0D);
		player.setHealth(40.0D);
		return player;
	}
	
	@Override
	public void stolen(@NotNull UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if(player == null)
			return;
		player.setMaxHealth(40.0D);
		player.setHealth(40.0D);
	}

	@Override
	public String getColor() {
		return "§e";
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		Player player = (Player)event.getEntity();
		if(!player.getUniqueId().equals(getPlayerUUID()))
			return;
		if(!event.getRegainReason().equals(RegainReason.SATIATED) && !event.getRegainReason().equals(RegainReason.REGEN))
			return;
		event.setCancelled(true);
	}
}
