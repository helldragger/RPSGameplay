package helldragger.RPSGameplay;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class MainMenu {
	
	/*
	 * ICI ON DEFINIT LES MENUS QUI PEUVENT APPARAITRE
	 * MENU PRINCIPAL -ACCES AUX CATEGORIES?
	 */

	private MainHandler listener;
	private IconMenu menu;
	private String name;
	
	public MainMenu(PlayerStats pinfos, String name, Plugin plugin,Player p) {
		listener = new MainHandler(this);
		this.name = name;
		
		menu = new IconMenu(name, Util.getChestSize(7),listener , plugin);
		String[] s2 = new String(" , ").split(",");
		
		List<String> stats = new ArrayList<String>();
		for(Stats s: StatType.atkStat.statlist)
			if(s.isPercentage())
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s) +"%");
			else
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s));
		menu.setOption(Bouton.ATK_STATS.position ,Bouton.ATK_STATS.item ,Bouton.ATK_STATS.name , stats.toArray(s2));
		
		
		stats.clear();
		for(Stats s: StatType.defStat.statlist)
			if(s.isPercentage())
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s) +"%");
			else
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s));
		menu.setOption(Bouton.DEF_STATS.position ,Bouton.DEF_STATS.item ,Bouton.DEF_STATS.name ,(String[]) stats.toArray(s2));
		
		
		stats.clear();
		for(Stats s: StatType.envStat.statlist)
			if(s.isPercentage())
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s) +"%");
			else
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s));
		menu.setOption(Bouton.ENV_STATS.position ,Bouton.ENV_STATS.item ,Bouton.ENV_STATS.name ,(String[]) stats.toArray(s2));

		
		stats.clear();
		for(Stats s: StatType.statusStat.statlist)
			if(s.isPercentage())
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s) +"%");
			else
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s));
		menu.setOption(Bouton.STATUS_STATS.position ,Bouton.STATUS_STATS.item ,Bouton.STATUS_STATS.name ,(String[]) stats.toArray(s2));

		
		stats.clear();
		for(Stats s: StatType.bonusStat.statlist)
			if(s.isPercentage())
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s) +"%");
			else
				stats.add(ChatColor.YELLOW+s.getLabel()+": "+pinfos.getModifiedStat(s));
		menu.setOption(Bouton.BONUS_STATS.position ,Bouton.BONUS_STATS.item ,Bouton.BONUS_STATS.name ,(String[]) stats.toArray(s2));

		

	}
	
	
	
	IconMenu getMenu(){
		return this.menu;
	}
	
	String getName(){
		return this.name;
	}

}
