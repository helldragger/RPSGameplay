package helldragger.RPSGameplay;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

class StatsManager {

	static HashMap<Player,PlayerStats> PlayerList= new HashMap<Player,PlayerStats>();

	
	public static String serialize(Player p)
	{
		String script = PlayerList.get(p).getStatsModifierList().toString();
		return script;
	}
	
	public static EnumMap<Stats, Double> deserialize(Player player)
	{
		return Util.stringToHashMap(Config.SAVED_DATA.getString("player."+player.getDisplayName()));
	}
	
	public static EnumMap<Stats, Double> getPlayerStatsList(Player player, RPSGPlugin plugin)
	{
		if(PlayerList.get(player) != null )
			return PlayerList.get(player).getStats();
		else
		{
			PlayerStats newPlayerStats = new PlayerStats(player,plugin);
			return newPlayerStats.getStats();
		}
	}
	
	public static EnumMap<Stats, Double> getPlayerStatsModifiersList(Player player, RPSGPlugin plugin)
	{
		
		if ( PlayerList.get(player) == null)
			return PlayerList.get(player).getStatsModifierList();
		else
		{
			PlayerStats newPlayerStats = new PlayerStats(player,plugin);
			return newPlayerStats.getStatsModifierList();
		}
	}
	
	public static PlayerStats getPlayerInfos(final Player p){
		
		return PlayerList.get(p);
	}
	
	public static EnumMap<Stats, Double> getPlayerModifiedStatsList(Player player, RPSGPlugin plugin)
	{
		EnumMap<Stats, Double> List = getPlayerStatsList(player,plugin);
		EnumMap<Stats, Double> ModifiersList = getPlayerStatsModifiersList(player,plugin);
		
		EnumMap<Stats, Double> ModifiedList = new EnumMap<Stats,Double>(Stats.class);
		
		if (List.keySet().containsAll(ModifiersList.keySet()) 
				&& ModifiersList.keySet().containsAll(List.keySet()))
		for (Stats stat : List.keySet())
		{
			if (ModifiersList.get(stat) != 0)
				ModifiedList.put(stat, (List.get(stat) * ModifiersList.get(stat)));
			else
				ModifiedList.put(stat, List.get(stat));
		}
		
		return ModifiedList;
	}
	
	
	public static void setStat(Player player ,Stats key, float value)
	{
		if (key != null)
			if (PlayerList.get(player ) != null)
				PlayerList.get(player).setStat(key, value);
	}
	
	
	
	
	
	
	public static void setStat(PlayerStats player ,Stats key, float value)
	{
		if (key != null)
			player.setStat(key, value);
	}
	
	
	public static void setAllStats(Player player ,EnumMap<Stats ,Double> Statslist)
	{
		if (!Statslist.isEmpty())
			if (PlayerList.get(player ) != null)
				PlayerList.get(player).setAllStats(Statslist);
	}
	
	
	
	public static void setStatToDefault(Player player ,Stats key)
	{
		if (StatsManager.PlayerList.get(player ) != null)
			if (key != null)
				PlayerList.get(player).setStat(key, key.getDefaultValue());
	}
	
	public static void setAllStatToDefault(Player player ,List<Stats> StatsList)
	{
		if (StatsManager.PlayerList.get(player ) != null)
			if (StatsList != null)
				PlayerList.get(player).setAllStatsToDefault(StatsList);
	}
	
	
	public static void setAllStatsModifiers(Player player ,EnumMap<Stats ,Double> Modifierslist)
	{
		if (!Modifierslist.isEmpty())
			if (PlayerList.get(player ) != null)
				PlayerList.get(player).setAllStatsModifiers(Modifierslist);
	}
	
	public static void removeAllStatsModifiers(Player player ,EnumMap<Stats ,Double> Modifierslist)
	{
		if (!Modifierslist.isEmpty())
			if (PlayerList.get(player ) != null)
				PlayerList.get(player).removeAllStatsModifiers(Modifierslist);
	}
	
	
	
//	public static void setBuff(PlayerStats player, Stats stat, float modifier, int duration)
//	{
//		
//		player.setStatModifier(stat, modifier);
//		
//		player.ActiveBuffs.add(stat);
//		useTimer(duration);
//		player.setStatModifier(stat, (float) 0);
//		if (player.ActiveBuffs.contains(stat));
//			player.ActiveBuffs.remove(stat);
//		return;
//		
//	}
	
	
	
	
	public static boolean randomChance(float chance)
	{
		Random rand = new Random();
		int nombreAleatoire = rand.nextInt(100) + 1;
		
		if(nombreAleatoire > chance)
			return false;		
		else
			return true;	
		
	}

	public static  boolean randomChance(int chance)
	{
		Random rand = new Random();
		int nombreAleatoire = rand.nextInt(100) + 1;
		
		if(nombreAleatoire > chance)
			return false;		
		else
			return true;	
		
	}

	public static  boolean randomChance(double chance)
	{
		Random rand = new Random();
		int nombreAleatoire = rand.nextInt(100) + 1;
		
		if(nombreAleatoire > chance)
			return false;		
		else
			return true;	
		
	}
	

	
	
	
	public static boolean isEnemy(EntityType cibleType)
	{
		if (cibleType == EntityType.BAT
				|| cibleType == EntityType.BLAZE
				|| cibleType == EntityType.CAVE_SPIDER
				|| cibleType == EntityType.CHICKEN
				|| cibleType == EntityType.COW
				|| cibleType == EntityType.CREEPER
				|| cibleType == EntityType.ENDER_DRAGON
				|| cibleType == EntityType.ENDERMAN
				|| cibleType == EntityType.GHAST
				|| cibleType == EntityType.GIANT
				|| cibleType == EntityType.HORSE
				|| cibleType == EntityType.IRON_GOLEM
				|| cibleType == EntityType.MAGMA_CUBE
				|| cibleType == EntityType.MUSHROOM_COW
				|| cibleType == EntityType.OCELOT
				|| cibleType == EntityType.PIG
				|| cibleType == EntityType.PIG_ZOMBIE
				|| cibleType == EntityType.PLAYER
				|| cibleType == EntityType.SHEEP
				|| cibleType == EntityType.SILVERFISH
				|| cibleType == EntityType.SKELETON
				|| cibleType == EntityType.SLIME
				|| cibleType == EntityType.SNOWMAN
				|| cibleType == EntityType.SPIDER
				|| cibleType == EntityType.SQUID
				|| cibleType == EntityType.VILLAGER
				|| cibleType == EntityType.WITCH
				|| cibleType == EntityType.WITHER
				|| cibleType == EntityType.WOLF
				|| cibleType == EntityType.ZOMBIE) 
			return true;
		else
			return false;
		
		
	}




	/*
	 * 
	 * Fin du stats mmanagement général.
	 * Debut de l'attribution IN GAME des effets des stats:
	 * 
	 * 
	 */
	
	

	public static  double getMobHealth(Player player, int entityID, String world)
	{
		double h = 0;
		World w = Bukkit.getWorld(world);
		
		for (Entity mob : w.getEntities())
		{
			if (mob.getEntityId() == entityID)
			{
				if(mob instanceof Damageable)
				{
					h = ((Damageable) mob).getHealth();
					
				}
				return h;
			}
		}
		return -1;
	}
	
	
	
	public static  double getMobMaxHealth(Player player, int entityID, String world)
	{
		double h = 0;
		World w = Bukkit.getWorld(world);
		
		for (Entity mob : w.getEntities())
		{
			if (mob.getEntityId() == entityID)
			{
				if(mob instanceof Damageable)
				{
					h = ((Damageable) mob).getMaxHealth();
				}
				return h;
			}
		}
		return -1;
	}
	
	
	
	
	
	
	
	
	
}
