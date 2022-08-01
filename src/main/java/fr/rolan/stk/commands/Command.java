package fr.rolan.stk.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import fr.rolan.stk.commands.utilities.CommandHelp;
import fr.rolan.stk.commands.utilities.CommandRole;

public class Command implements TabExecutor {
	private final Map<String, ICommand> listCommands = new HashMap<String, ICommand>();
	
	public Command() {
		this.listCommands.put("role", new CommandRole());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		List<String> temp = new ArrayList<>(this.listCommands.keySet());
	    if(args.length == 0)
	    	return temp; 
	    if(args.length == 1) {
	    	for(int i = 0; i < temp.size(); i++) {
	    		for(int j = 0; j < ((String)temp.get(i)).length() && j < args[0].length(); j++) {
	    			if(((String)temp.get(i)).charAt(j) != args[0].charAt(j)) {
	    				temp.remove(i);
	    				i--;
	    				break;
	    			} 
	    		} 
	    	} 
	    	return temp;
	    }
	    return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if(args.length == 0)
			return true;
		((ICommand) this.listCommands.getOrDefault(args[0], new CommandHelp())).onCommand(sender, cmd, label, args);
		return false;
	}
}