package helldragger.RPSGameplay;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;

class Permissions
{
	public static boolean BPERMISSIONS = false;

	public static boolean hasPermission(Player player, String node)
	{
		node = "rpsg." + node;
		if(Config.USE_PERMS)
		{
			if(BPERMISSIONS)
				return hasBPermission(player, node);
			else
			{
				return player.hasPermission(node);
			}
		}
		return true;
		
	}
	
	public static boolean hasPermissionConfig(Player player, Map<Enchantment, Integer> map)
	{
		
		return true;
	}


	public static boolean hasAbilitiePermission(Player player, String node)
	{
		return hasPermission(player, "abilities."+node);
	}
	
	private static boolean hasBPermission(Player player, String node )
	{
		return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), node);
	}
}

