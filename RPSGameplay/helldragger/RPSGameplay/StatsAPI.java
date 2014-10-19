package helldragger.RPSGameplay;

import java.util.Collection;
import java.util.EnumMap;

import org.bukkit.entity.Player;

public class StatsAPI {

	
	
	public static Double getStat(final Player p,final  Stats stat){
		return StatsManager.getPlayerInfos(p).getModifiedStat(stat);
	}
	
	public static void setStat(final Player p,final  Stats stat,final  Double value)
	{
		final Double defaultValue = StatsManager.getPlayerInfos(p).getStat(stat);
		StatsManager.getPlayerInfos(p).setStatModifier(stat, value - defaultValue);
	}
	
	public static EnumMap<Stats, Double> getAllModifiedStats(final Player p){
		return StatsManager.getPlayerInfos(p).getModifiedStats();
	}
	
	public static void setModifiedStats(final Player p,final  EnumMap<Stats,Double> map){
		final EnumMap<Stats,Double> baseStats = getAllStatsClean(p);
		for(Stats stat: map.keySet())
			StatsManager.getPlayerInfos(p).setStatModifier(stat, map.get(stat) - baseStats.get(stat));
	}
	
	public static EnumMap<Stats, Double> getAllStatsClean(final Player p){
		return StatsManager.getPlayerInfos(p).getStats();
	}
	
	public static void setStatsClean(final Player p,final  EnumMap<Stats,Double> map){
		StatsManager.getPlayerInfos(p).setAllStats(map);
	}
	
	public static EnumMap<Stats, Double> getAllStatsModifiers(final Player p){
		return StatsManager.getPlayerInfos(p).getStatsModifierList();
	}
	
	public static void setAllStatsModifiers(final Player p,final  EnumMap<Stats,Double> map){
		StatsManager.getPlayerInfos(p).setAllStatsModifiers(map);
	}

	public static Double getStatModifier(final Player p,final  Stats stat)
	{
		return StatsManager.getPlayerInfos(p).getStatModifier(stat);		
	}
	
	public static void setStatModifier(final Player p,final  Stats stat,final  Double modifier){
		StatsManager.getPlayerInfos(p).setStatModifier(stat, modifier);
	}
	
	public static void addStatModifier(final Player p,final Stats stat,final  Double modifier){
		Double value = StatsManager.getPlayerInfos(p).getStatModifier(stat);
		value = (value <= 0) ? 1:value;
		
		StatsManager.getPlayerInfos(p).setStatModifier(stat, value + modifier);
	}
	
	public static void addAllStatsModifiers(final Player p,final  EnumMap<Stats,Double> map){
		for(Stats stat : map.keySet())
		{
			Double value = StatsManager.getPlayerInfos(p).getStatModifier(stat);
			StatsManager.getPlayerInfos(p).setStatModifier(stat, value + map.get(stat));
		}
	}
	
	
	
	public static void removeStatModifier(final Player p,final  Stats stat,final  Double modifier){
		Double value = StatsManager.getPlayerInfos(p).getStatModifier(stat);
		StatsManager.getPlayerInfos(p).setStatModifier(stat, value - modifier);
	}
	
	public static void removeAllStatsModifiers(final Player p,final  EnumMap<Stats, Double> map){
		for(Stats stat: map.keySet())
		{
			Double value = StatsManager.getPlayerInfos(p).getStatModifier(stat);
			StatsManager.getPlayerInfos(p).setStatModifier(stat, value - map.get(stat));
		}
	}
	
	
	public static void resetStatModifier(final Player p,final  Stats stat){
		StatsManager.getPlayerInfos(p).setStatModifier(stat, 0.0);
	}
	
	public static void resetAllStatsModifiers(final Player p,final  Collection<Stats> list){
		for(Stats stat : list)
			StatsManager.getPlayerInfos(p).setStatModifier(stat, 0.0);
	}
	
	
}
