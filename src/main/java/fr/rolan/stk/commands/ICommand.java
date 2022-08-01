package fr.rolan.stk.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface ICommand {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
}