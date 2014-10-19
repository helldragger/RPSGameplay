package helldragger.RPSGameplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;



class Config
{


	public static ConfigFile CONFIG = new ConfigFile("config.yml");
	public static ConfigFile GROUPS = new ConfigFile("monsterTypes.yml");
	public static ConfigFile MONSTERS = new ConfigFile("monsterStats.yml");

	public static ConfigFile SAVED_DATA = new ConfigFile("ps.yml");

	public static boolean USE_PERMS;
	public static boolean SHOW_DAMAGE_DEALT;
	public static List<Integer> COMBO_BURST = new ArrayList<Integer>();

	public static boolean DEBUG_MODE;

	public static List<EntityType> BOSSES = new ArrayList<EntityType>();
	public static List<EntityType> EPIC_MONSTERS = new ArrayList<EntityType>();
	public static List<EntityType> RARE_MONSTERS = new ArrayList<EntityType>();

	public static String EPIC_LABEL = "";
	public static ChatColor EPIC_COLOR = ChatColor.GREEN;
	public static String RARE_LABEL = "";
	public static ChatColor RARE_COLOR = ChatColor.YELLOW;
	public static String BOSS_LABEL = "";
	public static ChatColor BOSS_COLOR = ChatColor.LIGHT_PURPLE;


	public static float FIRST_EFFECT_CRAFTING_CHANCE = 8;
	public static float SECOND_EFFECT_CRAFTING_CHANCE = 3;


	public static boolean VAULT_ENABLED = false;
	public static int COMBO_RESET_TIME = 10;
	public static int SPAWN_RATE_LAPS = 20;
	public static int MONSTERS_SPAWNED_CAP = 200;
	public static int NEARBY_MONSTERS_SPAWNING_CAP = 25;

	/*
	 * Stats de base
	 * 
	 */
	public static EnumMap<Stats,Double> BASE_STATS = new EnumMap<Stats,Double>(Stats.class);
	static {
		for(Stats stat : Stats.values())
			BASE_STATS.put(stat, 0.0);
	}


	public static boolean DOWNLOAD_UPDATE = false;
	public static boolean CHECK_UPDATES = true;



	public static void loadConfig(RPSGPlugin plugin, ConfigFile config) throws IOException,
	InvalidConfigurationException
	{


		String dataPath = plugin.getDataFolder() + File.separator;

		File configFile = new File(dataPath + config.getName());

		if (!configFile.exists())
		{
			configFile.createNewFile();
		}

		ConfigFile defaultConfig = null;

		if(!(config.getFileName() == Config.SAVED_DATA.getName()))
		{
			defaultConfig = new ConfigFile(config.getName());
			defaultConfig.load(plugin.getResource(config.getName()));
		}
		config.load(configFile);
		if(!(config.getFileName() == Config.SAVED_DATA.getName()))
		{
			config.setDefaults(defaultConfig);
		}
		config.options().copyDefaults(true);
		config.save(configFile);
	}

	public static void loadConfigValues(RPSGPlugin plugin) throws IOException, InvalidConfigurationException
	{

		//chargement des fichiers
		String dataPath = plugin.getDataFolder() + File.separator;


		File configFile = new File( dataPath  + GROUPS.getName());

		if (!configFile.exists())
		{
			configFile.createNewFile();
		}

		GROUPS.load(configFile);
		GROUPS.setFile(configFile);

		configFile = new File( dataPath + CONFIG.getName());

		if (!configFile.exists())
		{
			configFile.createNewFile();
		}

		CONFIG.load(configFile);
		CONFIG.setFile(configFile);

		configFile = new File( dataPath + MONSTERS.getName());

		if (!configFile.exists())
		{
			configFile.createNewFile();
		}

		MONSTERS.load(configFile);
		MONSTERS.setFile(configFile);






		USE_PERMS = CONFIG.getBoolean("general.use permissions",true);
		DEBUG_MODE = CONFIG.getBoolean("general.debug mode enabled",false);
		SHOW_DAMAGE_DEALT = CONFIG.getBoolean("general.show damage dealt",false);
		CHECK_UPDATES = CONFIG.getBoolean("general.check for updates",true);
		DOWNLOAD_UPDATE = CONFIG.getBoolean("general.download new updates",false);

		MONSTERS_SPAWNED_CAP = CONFIG.getInt("stats.max spawned monsters cap",200);
		NEARBY_MONSTERS_SPAWNING_CAP  = CONFIG.getInt("stats.max spawning monsters cap near a player",25);
		SPAWN_RATE_LAPS = CONFIG.getInt("stats.monsters spawn rate in ticks",3) * 4;
		COMBO_BURST = Util.getCommaSeparatedInteger(CONFIG.getString("stats.combo bursts","5,10,15,20,25,35,45,50,75,100"));
		COMBO_RESET_TIME = CONFIG.getInt("stats.combo reset time in secs",2) * 4;
		/*
		 * STATS DEFAULT VALUES
		 */
		
		for(Stats stat : Stats.values())
		{
			if(stat == Stats.MaximumHP)
				BASE_STATS.put( stat, CONFIG.getDouble("stats.default stats."+stat.getLabel()) % 2 );
			else
				BASE_STATS.put( stat, CONFIG.getDouble("stats.default stats."+stat.getLabel()) );
			
			stat.reloadValue();
		}
			



		RARE_LABEL = GROUPS.getString("rare label", "");
		EPIC_LABEL = GROUPS.getString("epic label", "");
		BOSS_LABEL = GROUPS.getString("boss label", "");

		RARE_COLOR = Util.getSafeChatColor(GROUPS.getString("rare color", ""), ChatColor.YELLOW);
		EPIC_COLOR = Util.getSafeChatColor(GROUPS.getString("epic color", ""), ChatColor.GREEN);
		BOSS_COLOR = Util.getSafeChatColor(GROUPS.getString("boss color", ""), ChatColor.LIGHT_PURPLE);



		List<String> bossNames = GROUPS.getStringList("Bosses");
		if (BOSSES.isEmpty())
		{
			List<String> defaultBossesList = new ArrayList<String>();

			defaultBossesList.add(EntityType.ENDER_DRAGON.name());
			defaultBossesList.add(EntityType.WITHER.name());

			GROUPS.set("Bosses", defaultBossesList);
		}
		else
			for(String s : bossNames)
				if(Util.stringToEntityType(s) instanceof EntityType)
					if(Util.stringToEntityType(s).isAlive() & Util.stringToEntityType(s).isSpawnable())
						BOSSES.add(Util.stringToEntityType(s));


		List<String> epicNames = GROUPS.getStringList("Epic monsters");
		if (EPIC_MONSTERS.isEmpty())
		{
			List<String> defaultEpicList = new ArrayList<String>();

			defaultEpicList.add(EntityType.SILVERFISH.name());
			defaultEpicList.add(EntityType.ENDERMAN.name());
			defaultEpicList.add(EntityType.WITHER_SKULL.name());
			defaultEpicList.add(EntityType.GHAST.name());

			GROUPS.set("Epic monsters", defaultEpicList);
		}
		else
			for(String s : epicNames)
				if(Util.stringToEntityType(s) instanceof EntityType)
					if(Util.stringToEntityType(s).isAlive() & Util.stringToEntityType(s).isSpawnable())
						EPIC_MONSTERS.add(Util.stringToEntityType(s));


		List<String> rareNames = GROUPS.getStringList("Rare monsters");
		if (RARE_MONSTERS.isEmpty())
		{
			List<String> defaultRareList = new ArrayList<String>();

			defaultRareList.add(EntityType.CAVE_SPIDER.name());
			defaultRareList.add(EntityType.SLIME.name());
			defaultRareList.add(EntityType.MAGMA_CUBE.name());
			defaultRareList.add(EntityType.BLAZE.name());

			GROUPS.set("Rare monsters", defaultRareList);
		}
		else
			for(String s : rareNames)
				if(Util.stringToEntityType(s) instanceof EntityType)
					if(Util.stringToEntityType(s).isAlive() & Util.stringToEntityType(s).isSpawnable())
						RARE_MONSTERS.add(Util.stringToEntityType(s));

		/*
		 * STATS DES
		 * MONSTRES
		 * 
		 */

		for(EntityType ent : EntityType.values())
		{
			EnumMap<Stats,Double> map = Stats.getNewStatsMap();
			if (MONSTERS.getConfigurationSection(ent.name().toUpperCase().replace(' ', '_')) != null)
				for(String key : MONSTERS.getConfigurationSection(ent.name()).getValues(false).keySet())
				{
					if (Stats.valueOf(key) instanceof Stats)
						map.put( Stats.valueOf(key) 
								, Double.valueOf(MONSTERS.getConfigurationSection( ent.name() ).getString(key) ) );
				}

			Mobs.setMobStats(ent, map);
		}
		/*
		 * ON SAUVEGARDE LE TOUT
		 * 
		 */

		GROUPS.save();
		CONFIG.save();
	}

	public static int getDeathExperience(RPSGPlugin plugin, EntityType type)
	{
		String name = type.name().replace('_', ' ').toLowerCase();
		int exp = plugin.getConfig().getInt("general.experience per kill." + name);

		if (exp >= 6)
			return exp;
		else
			return 6;
	}


	public static void removeOldData(RPSGPlugin plugin)
	{
		try
		{
			deleteOldConfigs(plugin);
		} 
		catch (IOException | InvalidConfigurationException e)
		{

		}

	}

	private static void deleteOldConfigs(RPSGPlugin plugin) throws IOException, InvalidConfigurationException
	{
		File folder = plugin.getDataFolder();

		new File(folder.getPath() + File.separator + "config.yml").delete();

		for (File file : new File(folder.getPath() + File.separator + "configuration").listFiles())
		{
			if(!file.getName().equalsIgnoreCase(SAVED_DATA.getName()))
				file.delete();
		}
	}

	public static void savePlayerStats(Player player , RPSGPlugin plugin) throws IOException, InvalidConfigurationException {
		// TODO Auto-generated method stub

		File folder = plugin.getDataFolder();

		String dataPath = folder.getPath() + File.separator + "configuration" + File.separator;

		File configFile = new File(dataPath + SAVED_DATA.getName());

		if (!configFile.exists())
		{
			configFile.createNewFile();
		}

		SAVED_DATA.load(configFile);
		SAVED_DATA.setFile(configFile);


		SAVED_DATA.set("Player."+player.getDisplayName(), StatsManager.serialize(player));
		SAVED_DATA.save();

	}

	public static void loadPlayerStats(Player player,RPSGPlugin plugin) throws FileNotFoundException, IOException, InvalidConfigurationException {
		/*
		 * TODO
		 *  si pas dans le fichier de sauvegarde,
		 *  	on lui cree un nouveau panel de stats
		 *  sinon 
		 *  	on les charge en mémoire:
		 *  -ajout des stats presentes ou non 
		 *      dans une liste de stat a completer 
		 *      si pas complete ou erronée
		 *  -ajout de cette liste dans la liste liant
		 *      les panneaux de stats a leur joueur.
		 *  
		 */


		File folder = plugin.getDataFolder();

		String dataPath = folder.getPath() + File.separator + "configuration" + File.separator;

		File configFile = new File(dataPath + SAVED_DATA.getName());

		if (!configFile.exists())
		{
			configFile.createNewFile();
		}

		SAVED_DATA.load(configFile);
		SAVED_DATA.setFile(configFile);




		SAVED_DATA.load(SAVED_DATA.getFile());
		if(SAVED_DATA.get("Player."+player.getDisplayName()) != null )
		{

			/*
			 * chargement
			 * 
			 */
			PlayerStats playerNewStats = new PlayerStats(player,plugin);

			for(Stats stat : playerNewStats.getStats().keySet())
			{
				if( playerNewStats.getStat(stat) == null )
				{
					playerNewStats.setStat(stat, stat.getDefaultValue());
				}
			}

			playerNewStats.setAllStatsModifiers(StatsManager.deserialize(player));

			StatsManager.PlayerList.put(player, playerNewStats);
		}
		else 
		{

			PlayerStats playerNewStats = new PlayerStats(player,plugin);

			for(Stats stat : playerNewStats.getStats().keySet())
			{
				if( playerNewStats.getStat(stat) == null )
				{
					playerNewStats.setStat(stat, stat.getDefaultValue());
				}
			}

			if(!StatsManager.PlayerList.keySet().contains(player) )
				StatsManager.PlayerList.put(player, playerNewStats);

		}



	}
}