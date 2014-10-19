package helldragger.RPSGameplay;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



class Commands implements CommandExecutor
{
	/**
	 * @uml.property  name="plugin"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="cmdListener:RolePlaySpecialityEnchantments.RPSEPlugin"
	 */
	private RPSGPlugin plugin;

	public Commands(RPSGPlugin RPSEPlugin)
	{
		this.plugin = RPSEPlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args)
	{

		if (label.equalsIgnoreCase("rpg"))
		{
			
				if (args.length == 0)
				{
					sendHelp(sender);
				} 
				else
				{
					if (args[0].equalsIgnoreCase("version"))
					{
						sender.sendMessage(ChatColor.GRAY + "This server is running RPS Gameplay version " + plugin.pdf.getVersion());
						return true;
					} 
					else if (args[0].equalsIgnoreCase("reload"))
					{

						if (!Permissions.hasPermission((Player) sender, "reload"))
						{
							sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
							return true;
						}


						plugin.onReload();
						sender.sendMessage(ChatColor.GRAY + "Reloaded RPS Gameplay.");
						return true;
					}

					else if(args[0].equalsIgnoreCase("debug") && Config.DEBUG_MODE && (sender instanceof Player)){

						sender.sendMessage("---------DEBUG COMMAND USED---------");


					}
					else if(args[0].equalsIgnoreCase("godmode") && (sender instanceof Player)){



					}
					else if(args[0].equalsIgnoreCase("stats") && (sender instanceof Player)){
						StatsManager.getPlayerInfos((Player)sender).openMenu();
					}

				}

			return true;
		}

		return false;
	}

	public void sendHelp(CommandSender sender)
	{
		if (sender instanceof Player)
		{

			sender.sendMessage(ChatColor.GRAY + "/rpg reload - Reloads plugin configuration.");
			sender.sendMessage(ChatColor.GRAY + "/rpg version - Displays plugin version information.");
			sender.sendMessage(ChatColor.GRAY + "/rpg stats - Displays player's stats.");
			sender.sendMessage(ChatColor.GRAY + "/rpg debug - Displays some debug info.");


		}
		else
		{
			sender.sendMessage(ChatColor.GRAY + "/rpg reload - Reloads plugin configuration.");
			sender.sendMessage(ChatColor.GRAY + "/rpg version - Displays plugin version information.");
		}
	}

}
