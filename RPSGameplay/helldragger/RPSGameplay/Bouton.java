package helldragger.RPSGameplay;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

enum Bouton {

	
	ATK_STATS(0, new ItemStack(Material.DIAMOND_SWORD), "Offensive stats", "Click it to see your offensive stats."),
	DEF_STATS(1,new ItemStack(Material.DIAMOND_CHESTPLATE), "Defensive stats","Click it to see your defensive stats."),
	STATUS_STATS(2,new ItemStack(Material.POTION), "Status effect stats","Click it to see your status effect stats."),
	ENV_STATS(3,new ItemStack(Material.SEEDS), "Environment stats","Click it to see your environmental stats."),
	BONUS_STATS(4,new ItemStack(Material.ENCHANTED_BOOK), "Bonus stats","Click it to see your bonus stats.");
	
	

	final int position;
	final String name;
	final String desc;
	ItemStack item;

	
	Bouton(final int pos,final ItemStack item,final String name,final String desc){
		this.position = pos;
		this.desc = desc;
		this.item = item;
		this.name = name;
	}
	
	
	
	
	
}