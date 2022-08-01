package fr.rolan.stk.commands.utilities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.rolan.stk.STKPlugin;
import fr.rolan.stk.commands.ICommand;
import fr.rolan.stk.enumstk.State;
import fr.rolan.stk.gui.RoleMenu;
import fr.rolan.stk.manager.STKUser;

public class CommandRole implements ICommand {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player))
			return true;
		Player player = (Player)sender;
		STKUser user = STKPlugin.getInstance().getUser(player);
		if(!user.isState(State.ALIVE))
			return true;
		if(user.getRole() == null) {
			if(STKPlugin.getInstance().gui.STK_ROLE == null)
				new RoleMenu();
			player.openInventory(STKPlugin.getInstance().gui.STK_ROLE);
		}else
			player.sendMessage(user.getRole().getDescription());
		return false;
	}

}
