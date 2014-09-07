package helldragger.RPSGameplay;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.entity.Player;

class PlayerStats {

	private Player player;
	
	private EnumMap<Stats, Double> PlayerStats = new EnumMap<Stats,Double>(Stats.class);
	
	private EnumMap<Stats, Double> StatsModifiers = new EnumMap<Stats,Double>(Stats.class);
	
	private IconMenu menu;
	
	public List<Stats> ActiveBuffs = new ArrayList<Stats>(); 
	
	public PlayerStats(final Player player, RPSGPlugin plugin)
	{
		this.player = player;
		
		
		/*
		 * 
		 * CLASSE POUR GERER LES CLICS
		 * 
		 */
		
		
		
		for(Stats stats : Stats.values())
		{
			PlayerStats.put(stats, stats.getDefaultValue());
			StatsModifiers.put(stats,  Double.valueOf("0"));
		}
		
		StatsManager.PlayerList.put(player, this);
		
		

		this.menu = new MainMenu(this,player.getName()+"'s stats", plugin,player).getMenu();
		this.menu.setSpecificTo(player);
		
	}

	
	
	void openMenu(){
		this.menu.open(player);
	}
	
	
	public EnumMap<Stats, Double> getStats(){
		return this.PlayerStats;
	}
	

	
	public Double getStat(final Stats stat){
		return this.PlayerStats.get(stat);
	}
	
	public void setStat(final Stats key,final  double value)
	{
		if (key != null)
			this.PlayerStats.put(key, value);
	}
	
	
	public Player getPlayer() {
		return player;
	}
	
	
	public void setAllStats(final EnumMap<Stats , Double> values)
	{
		if (values != null)
			this.PlayerStats.putAll(values);
	}
	
	public void setAllStatsToDefault(final List<Stats> StatsList)
	{
		if (!StatsList.isEmpty())
		{
			for (Stats stat : StatsList)
			{
				this.PlayerStats.put(stat, stat.getDefaultValue());

			}
		}
	}
	
	public void setAllStatsModifiers(final EnumMap<Stats , Double> values)
	{
		if (values != null)
			this.StatsModifiers.putAll(values);
	}
	
	public void removeAllStatsModifiers(final EnumMap<Stats , Double> values)
	{
		if (values != null)
		{
			EnumMap<Stats, Double> removeModifiers = new EnumMap<Stats, Double>(Stats.class);
			for (Stats stat : values.keySet())
			{
				removeModifiers.put(stat ,Double.valueOf("0"));
			}
			
			this.StatsModifiers.putAll(removeModifiers);
		}
			
	}
	
	public void setStatModifier(final Stats stat, final Double modifier){
		if (StatsModifiers != null)
			StatsModifiers.put(stat, modifier);
		
	}
	
	public Double getStatModifier(final Stats stat)
	{
		if (StatsModifiers != null)
			if (StatsModifiers.keySet().contains(stat))
				return StatsModifiers.get(stat);
			
		return Double.valueOf("0");
	}
	
	
	public EnumMap<Stats,Double> getStatsModifierList(){
		if (StatsModifiers != null)
			return StatsModifiers;
		else return null;
	}
	
	
	
	public Double getModifiedStat(final Stats stat){
		return  this.PlayerStats.get(stat) + this.StatsModifiers.get(stat);
	}
	
	public EnumMap<Stats, Double> getModifiedStats()
	{
		EnumMap<Stats,Double> map = new EnumMap<Stats,Double>(Stats.class);
		for(Stats stat : this.PlayerStats.keySet())
			map.put(stat, stat.getValue() + this.StatsModifiers.get(stat));
		
		return map;	
	}



	public void addStatModifier(final Stats stat,final  Double modifier){
		Double value = this.getStatModifier(stat);
		this.setStatModifier(stat, value + modifier);
	}
	
	public void addAllStatModifier(final EnumMap<Stats,Double> map){
		for(Stats stat : map.keySet())
		{
			Double value = this.getStatModifier(stat);
			this.setStatModifier(stat, value + map.get(stat));
		}
	}

}
