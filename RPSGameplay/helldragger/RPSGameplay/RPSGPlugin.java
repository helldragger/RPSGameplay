package helldragger.RPSGameplay;


import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateResult;
import net.gravitydevelopment.updater.Updater.UpdateType;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.VaultEco;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;



public class RPSGPlugin extends JavaPlugin
{
	


	/**
	 * @uml.property  name="pm"
	 * @uml.associationEnd  
	 */
	public PluginManager pm;
	/**
	 * @uml.property  name="pdf"
	 * @uml.associationEnd  
	 */
	public PluginDescriptionFile pdf;
	public static Logger log;
	
	/**
	 * @uml.property  name="events"
	 * @uml.associationEnd  inverse="plugin:RolePlaySpecialityEnchantments.Events"
	 */
	public Events events;
	/**
	 * @uml.property  name="cmdListener"
	 * @uml.associationEnd  inverse="plugin:RolePlaySpecialityEnchantments.Commands"
	 */
	public Commands cmdListener;
	
	public Vault vault;
	
	public VaultEco vaultEco;
	
	public Economy vaultEconomy;

	
	@Override
	public void onEnable()
	{
		pm = getServer().getPluginManager();
		pdf = getDescription();
		log = Logger.getLogger("Minecraft");
		
		events = new Events(this);
		cmdListener = new Commands(this);

		pm.registerEvents(this.events, this);
		
		getCommand("rpg").setExecutor(cmdListener);
		
			

		Permissions.BPERMISSIONS = pm.getPlugin("bPermissions") != null;
		
		try
		{
			loadData();
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();

			log.warning("ERROR when trying to load configuration.");
		}

		for(Player p : Bukkit.getOnlinePlayers())
			events.initializePlayer(p);
		
		log.info("RolePlayspeciality Gameplay v" + pdf.getVersion() + " by " + pdf.getAuthors() + " is now enabled!");
	}
	
	@Override
	public void onDisable()
	{

		for(Player p : Bukkit.getOnlinePlayers())
			events.exittingPlayer(p);
		
		log.info("RolePlayspeciality Gameplay v" + pdf.getVersion() + " by " + pdf.getAuthors() + " is now disabled.");
	}
	
	public void onReload()
	{
		log.info("Reloading RolePlayspeciality Gameplay v" + pdf.getVersion() +"...");
		
		log.info("Loading plugin data...");
		try
		{
			loadData();
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
		
		log.info("Loading item data...");
		
		log.info("RolePlayspeciality Gameplay has successfully reloaded.");
	}

	private void loadData() throws IOException, InvalidConfigurationException
	{
		
		File folder = getDataFolder();

		if (!folder.exists())
		{
			folder.mkdir();
		}
		else
		{
			File oldConfigFile = new File(folder.getPath() + File.separator + "config.yml");

			if (oldConfigFile.exists())
			{
				oldConfigFile.delete();
			}

		}

		String dataPath = folder.getPath() + File.separator;

		File configFolder = new File(dataPath + "configuration");

		if (!configFolder.exists())
		{
			configFolder.mkdir();
		}
		
		Config.removeOldData(this);
		Mobs.load();
		
		Config.loadConfig(this, Config.CONFIG);
		Config.loadConfig(this, Config.GROUPS);
		Config.loadConfig(this, Config.MONSTERS);
		Config.loadConfig(this, Config.SAVED_DATA);
		Config.loadConfigValues(this);
		
		
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null)
		{
			log.info("VAULT Found, MoneyGain is now effective");
			vault = (Vault) Bukkit.getServer().getPluginManager().getPlugin("Vault");
			Config.VAULT_ENABLED = true;
		}
		
		if(Config.CHECK_UPDATES){
			Updater updater = null;
			
			if(Config.DOWNLOAD_UPDATE)
				updater = new Updater(this, 79414, this.getFile(), UpdateType.DEFAULT, true);
			else
				updater = new Updater(this, 79414, this.getFile(), UpdateType.NO_DOWNLOAD, true);
			
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			    this.getLogger().info("New version available! " + updater.getLatestName());
			    
			}else if(updater.getResult() == UpdateResult.SUCCESS)
				this.getLogger().info("New version downloaded! RPSW has updated to " + updater.getLatestName());
			
		}
		
		
	}
	
	
	
	
}

