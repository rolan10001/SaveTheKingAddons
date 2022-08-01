package fr.rolan.stk.rolesattributs;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.jetbrains.annotation.NotNull;
import org.jetbrains.annotation.Nullable;

import fr.rolan.stk.enumstk.Category;

public interface Roles {
	String getDescription();
	
	String getDisplay();
	
	boolean isDisplay(String paramString);
	
	boolean isCategory(@NotNull Category paramCategory);
	
	@NotNull
	Category getCategory();
	
	UUID getPlayerUUID();
	
	void setPlayerUUID(@NotNull UUID paramUUID);
	
	void stolen(@NotNull UUID paramUUID);
	
	@Nullable
	Player recoverPower();
	
	void recoverPotionEffect(@NotNull Player player);
	
	String getColor();
	
	Roles publicClone();
}
