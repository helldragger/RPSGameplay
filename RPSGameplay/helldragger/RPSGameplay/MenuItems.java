package helldragger.RPSGameplay;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

enum MenuItems {

	OFFENSIVES_STATS("",
			"",
			new MaterialData(Material.IRON_AXE),
			Stats.ATK,
			Stats.DamageVSBosses,
			Stats.IgnoreDEF,
			Stats.Accuracy,
			Stats.AttackSpeed,
			Stats.HitChance),
			
	DEFENSIVES_STATS("",
			"",
			new MaterialData(Material.IRON_CHESTPLATE),
			Stats.DEF,
			Stats.DamageTaken,
			Stats.DEF,
			Stats.DEF,
			Stats.DEF,
			Stats.DEF,
			Stats.DEF),
	OTHERS_STATS("","",new MaterialData(Material.SLIME_BALL)),
	STATUS("","",new MaterialData(Material.SKULL)),
	ACTIVES_BONUS("","",new MaterialData(Material.POTION));
	private final String displayName;
    private final String description;
    private final MaterialData icon;
    private ItemStack[] items;
	
	private MenuItems(String displayName, String description, MaterialData icon, Stats... statList) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }
	
	

	public void addStartingItems(Player player) {
	    for (ItemStack item : items) {
	        player.getInventory().addItem(item.clone());
	    }
	}



	public MaterialData getIcon() {
	    return icon;
	}



	public String getDescription() {
	    return description;
	}



	@Override
	public String toString() {
	    return displayName;
	}
	
}
