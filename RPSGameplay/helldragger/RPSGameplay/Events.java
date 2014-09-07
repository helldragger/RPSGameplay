package helldragger.RPSGameplay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_7_R1.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


class Events implements Listener
{

	/*
	 * Classes inherentes
	 * 
	 * 
	 */

	
	
	class LifeVerifier extends BukkitRunnable {

		@Override
		public void run() {
			for(Player p : Bukkit.getOnlinePlayers())
			{
				EnumMap<Stats, Double> pstats =  StatsManager.getPlayerInfos(p).getModifiedStats();

				if(pstats.containsKey(Stats.MaximumHP))
				{
					double maxlife = Config.BASE_STATS.get(Stats.MaximumHP) * pstats.get(Stats.MaximumHP); 
					if( p.getMaxHealth() != maxlife & maxlife > 0)
						p.setMaxHealth(maxlife);
				}

			}

		}

	}

	class AttackTimer extends BukkitRunnable implements Listener{

		private HashMap<Player,Integer> attackTiming = new HashMap<Player,Integer>();

		private Events e;

		AttackTimer(Events e){
			this.e = e;	
			for(Player p : Bukkit.getOnlinePlayers())
				attackTiming.put(p, 0);
		}


		@Override
		public void run() {
			
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(!attackTiming.containsKey(p))
					addPlayer(p);
				
			}
			
			
			if(attackTiming.size() < e.comboCounter.size())
				for(Player p : e.comboCounter.keySet())
					if(!attackTiming.containsKey(p))
						attackTiming.put(p, ((Double) (Config.BASE_STATS.get(Stats.AttackSpeed) * StatsManager.getPlayerInfos(p).getModifiedStat(Stats.AttackSpeed) % 100)).intValue() );

			for(Player p : attackTiming.keySet())//pour chaque joueur
				if(attackTiming.get(p) > 0) 
					//tant que la vitesse d'attaque n'est pas revenu au decompte normal, il augmente.
					attackTiming.put(p, attackTiming.get(p) - 1);
		}

		void addPlayer(Player p){
			attackTiming.put(p, 0);
		}
		
		@EventHandler(priority = EventPriority.LOW)
		void onAttack(EntityDamageByEntityEvent ev){
			if (ev.getDamager() instanceof Player)
			{
				double atkSpeed = (Config.BASE_STATS.get(Stats.AttackSpeed) * StatsManager.getPlayerInfos((Player) ev.getDamager()).getModifiedStat(Stats.AttackSpeed) % 100);//definition de la vitesse d'attaque par defaut
				if (attackTiming.get((Player) ev.getDamager()) > 0)
					ev.setCancelled(true);
				else
					attackTiming.put((Player) ev.getDamager(), ((Double)atkSpeed).intValue());
			}
		}

		@EventHandler(priority = EventPriority.HIGH)
		void onArmSwing(PlayerAnimationEvent ev){
			Player p = ev.getPlayer();			

			if (attackTiming.get(p) > 0)
				ev.setCancelled(true);

		}

	}


	class SpawnerTimer extends BukkitRunnable implements Listener{

		private HashMap<Player, List<UUID>> nearbyMonsters = new HashMap<Player, List<UUID>>();


		SpawnerTimer(){
			for(Player p : Bukkit.getOnlinePlayers())
				connectingPlayer(p);

			for(org.bukkit.World w : Bukkit.getWorlds())
			{
				if(w.getDifficulty() != Difficulty.PEACEFUL)
				{
					w.setTicksPerMonsterSpawns(Config.SPAWN_RATE_LAPS);
					w.setMonsterSpawnLimit(Config.MONSTERS_SPAWNED_CAP);
				}

			}
		}


		@Override
		public void run() {
			for(Player p : Bukkit.getOnlinePlayers())
				if (!nearbyMonsters.containsKey(p))
					connectingPlayer(p);

			for(Player p : nearbyMonsters.keySet())
				if (!p.isOnline())
					nearbyMonsters.remove(p);
		}

		@EventHandler
		public void onMobDeath(EntityDeathEvent e){


			for(Player p : nearbyMonsters.keySet())
				if(nearbyMonsters.get(p).contains(e.getEntity().getUniqueId()))
				{
					List<UUID> newList = nearbyMonsters.get(p);
					newList.remove(e.getEntity().getUniqueId());
					nearbyMonsters.put(p, newList);
				}
		}

		@EventHandler
		public void onMobSpawn(CreatureSpawnEvent e){

			if(e.getSpawnReason() != SpawnReason.NATURAL)
				return;

			Player nearPlayer = null;
			Location mobloc = e.getLocation();
			double distance = -2L;
			for(Player p : Bukkit.getOnlinePlayers())
				if(p.getLocation().distance(mobloc) < distance || distance < 0)
				{
					nearPlayer = p;
					distance = p.getLocation().distance(mobloc);
				}

			//verif si la limite est atteinte ou non

			if(nearbyMonsters.get(nearPlayer).size() >= getPlayerSpwnRateLimit(nearPlayer))
			{
				e.setCancelled(true);
				return;
			}

			// verif si le mob est rare ou epic ou non
			EnumMap<Stats, Double> pstats = StatsManager.getPlayerInfos(nearPlayer).getModifiedStats();
			if(!Mobs.isRare(e.getEntityType()) & !Mobs.isEpic(e.getEntityType()) & !Mobs.isBoss(e.getEntityType()))
			{
				EntityType ent = null;
				if(StatsManager.randomChance(pstats.get(Stats.RareMonsterRate)))
				{
					ent = Config.RARE_MONSTERS.get(Util.randInt(1, Config.RARE_MONSTERS.size() -1));
					e.setCancelled(true);
				}
				else if(StatsManager.randomChance(pstats.get(Stats.EpicMonsterRate)))
				{
					ent = Config.EPIC_MONSTERS.get(Util.randInt(1, Config.EPIC_MONSTERS.size() -1));
					e.setCancelled(true);
				}
				else 
				{
					ent = e.getEntityType();
				}

				if(e.isCancelled())
					Bukkit.getPluginManager().callEvent(new CreatureSpawnEvent((LivingEntity) nearPlayer.getWorld().spawnEntity(mobloc, ent), SpawnReason.CUSTOM));

			}

			if(!e.isCancelled())
			{
				if(Mobs.isEpic(e.getEntityType()))
				{
					if(e.getEntity().getCustomName() != null)
						e.getEntity().setCustomName(e.getEntity().getCustomName()+ Config.EPIC_COLOR +Config.EPIC_LABEL+e.getEntityType().name());
					else
						e.getEntity().setCustomName(Config.EPIC_COLOR +Config.EPIC_LABEL+e.getEntityType().name());
					e.getEntity().setCustomNameVisible(true);
				}
				else if(Mobs.isRare(e.getEntityType()))
				{
					if(e.getEntity().getCustomName() != null)
						e.getEntity().setCustomName(e.getEntity().getCustomName()+ Config.RARE_COLOR +Config.RARE_LABEL+e.getEntityType().name());
					else
						e.getEntity().setCustomName(Config.RARE_COLOR +Config.RARE_LABEL+e.getEntityType().name());
					e.getEntity().setCustomNameVisible(true);
				}
				else if(Mobs.isBoss(e.getEntityType()))
				{
					if(e.getEntity().getCustomName() != null)
						e.getEntity().setCustomName(e.getEntity().getCustomName()+ Config.BOSS_COLOR +Config.BOSS_LABEL+e.getEntityType().name());
					else
						e.getEntity().setCustomName(Config.BOSS_COLOR +Config.BOSS_LABEL+e.getEntityType().name());
					e.getEntity().setCustomNameVisible(true);
				}
				List<UUID> newList = nearbyMonsters.get(nearPlayer);
				newList.add(e.getEntity().getUniqueId());
				nearbyMonsters.put(nearPlayer, newList);

			}
		}


		void exittingPlayer(Player p){
			nearbyMonsters.remove(p);
		}

		void connectingPlayer(Player p){
			nearbyMonsters.put( p , new ArrayList<UUID>());
		}

		double getPlayerSpwnRateLimit(Player p){

			if(StatsManager.getPlayerInfos(p).getModifiedStat(Stats.NearbyMonsterSpawnRate) != null)
				return (Config.NEARBY_MONSTERS_SPAWNING_CAP * StatsManager.getPlayerInfos(p).getModifiedStat(Stats.NearbyMonsterSpawnRate));
			else
				return Config.NEARBY_MONSTERS_SPAWNING_CAP;

		}



	}

	class ComboVerifier extends BukkitRunnable{

		private HashMap<Player,Integer> comboTiming = new HashMap<Player, Integer>();
		private HashMap<Player,Integer> lastCombo = new HashMap<Player,Integer>();



		private Events events;

		ComboVerifier(Events e){
			this.events = e;
		}


		@Override
		public void run() {
			for(Player p : Bukkit.getOnlinePlayers())
				if(!comboTiming.containsKey(p))
					comboTiming.put(p,1);


			for(Player p : events.comboCounter.keySet())
			{
				if (!lastCombo.containsKey(p))
					addPlayer(p);


				final int time = comboTiming.get(p);
				final int lastcmbo = lastCombo.get(p);
				//le joueur est present

				if(events.comboCounter.get(p) != lastcmbo)//si le dernier combo enregistré est different de l'actuel
					comboChanged(p);//on remet le temps a zero et on sauvegarde celui ci

				else//Si au contraire c'est toujours le meme
				{

					if(lastcmbo != 0)//si le dernier combo est different de zero (pas de combo)
						comboTiming.put(p,time + 1);//on augmente le temps de son combo de 1.

					if(time >= Config.COMBO_RESET_TIME)//si le temps défini est dépassé pour le combo
						resetCombo(p);//on le remet a zero et on envoie au joueur un signal qu'il est terminé
				}
			}
		}

		void addPlayer(Player p){
			lastCombo.put(p, events.comboCounter.get(p));
			comboTiming.put(p, 0);
		}

		void resetCombo(Player p){
			p.sendMessage(ChatColor.YELLOW+""+comboCounter.get(p)+" combo hit!");
			comboTiming.put(p, 0);
			comboCounter.put(p, 0);
		}

		void comboChanged(Player p){
			comboTiming.put(p, 0);
			lastCombo.put(p, comboCounter.get(p));
		}

	}



	/*
	 * 
	 * CLASSE EVENT
	 * 
	 */


	private RPSGPlugin plugin;

	private HashMap<LivingEntity, Boolean> doubleHit = new HashMap<LivingEntity, Boolean>();

	private HashMap<Player, Integer> comboCounter = new HashMap<Player, Integer>();

	private HashMap<Player, Float> initialspeed = new HashMap<Player,Float>();

	/*
	 * COMPATIBILITE MC MMO POUR LES COUPS CRITIQUES ET AUTRES MISES A JOUR DES STATS
	 * --RECUPERER LES SOURCES POUR CONTINUER--
	 * 
	 */

	//	public void onPlayerLevelUp(final McMMOPlayerLevelUpEvent event)
	//	{
	//		Player player = event.getPlayer();
	//		SkillType skill = event.getSkill();
	//		
	//	}



	public Events(final RPSGPlugin RPSEPlugin)
	{
		plugin = RPSEPlugin;
		Bukkit.getScheduler().runTaskTimer(plugin, new ComboVerifier(this), 0L, 5L);
		Bukkit.getScheduler().runTaskTimer(plugin, new LifeVerifier(), 0L, 5L);
		Bukkit.getScheduler().runTaskTimer(plugin, new SpawnerTimer(),0L,5L);		

		AttackTimer atktimer = new AttackTimer(this);
		Bukkit.getPluginManager().registerEvents(atktimer, plugin);
		Bukkit.getScheduler().runTaskTimer(plugin, atktimer,0L,1L);

	}


	@EventHandler
	public void onConnect(final PlayerJoinEvent event)
	{
		initializePlayer(event.getPlayer());
	}


	@EventHandler
	public void onDisconnect(final PlayerQuitEvent event)
	{
		exittingPlayer(event.getPlayer());
	}





	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		RequestChecker.verifyRequest();
		
		
		Player p = e.getPlayer();
		float speed = ((Double) (0.2 * StatsManager.getPlayerInfos(p).getModifiedStat(Stats.Evasion) % 100)).floatValue(); 

		if(speed != (0.2 * Stats.Evasion.getDefaultValue() % 100) && speed != p.getWalkSpeed())			
			if( speed > 1)
				p.setWalkSpeed(1);
			else if( speed < -1)
				p.setWalkSpeed(-1);
			else 
				p.setWalkSpeed(speed);

	}




	@EventHandler
	public void onHealthRecovering(EntityRegainHealthEvent event)
	{
		RequestChecker.verifyRequest();
		
		if(event.getEntityType().equals(EntityType.PLAYER))
			if(event.getRegainReason() == RegainReason.SATIATED
			||event.getRegainReason() == RegainReason.EATING
			||event.getRegainReason() == RegainReason.REGEN)
			{
				if (StatsManager.getPlayerModifiedStatsList((Player) event.getEntity(), plugin).containsKey(Stats.HPRecovery))
					event.setAmount(event.getAmount() * StatsManager.getPlayerModifiedStatsList((Player) event.getEntity(), plugin).get(Stats.HPRecovery) % 100);
			}
	}


	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e)
	{
		RequestChecker.verifyRequest();
		
		if (!Mobs.getMobStats(e.getEntityType()).isEmpty())
		{
			LivingEntity ent = e.getEntity();
			EnumMap<Stats,Double> stats = Mobs.getMobStats(e.getEntityType());
			if(stats.containsKey(Stats.MaximumHP))
				if(stats.get(Stats.MaximumHP) > 0)
					ent.setMaxHealth(stats.get(Stats.MaximumHP));
			
		}
	}


	
	
	@EventHandler
	public void onDamage(final EntityDamageByEntityEvent event)
	{
		RequestChecker.verifyRequest();
		
		if(event.isCancelled())
			return;
		
		double degatsDeBase = event.getDamage();
		double degatsBonus = 0;
		double degatsTotal = 0;
		
		double defIgnorée = 0;
		boolean isBoss = false;
		if (  Mobs.isBoss(event.getEntityType()))
			isBoss = true;
		boolean atkPlayer = false;
		boolean defPlayer = false;

		LivingEntity attaquant = null;
		LivingEntity attaqué = null;

		double ATK = 0;
		double DEF = 0;
		
		EnumMap<Stats,Double> atkstats = null;
		EnumMap<Stats,Double> defstats = null;
		
		if(event.getEntity() instanceof LivingEntity)
		{
			attaqué = (LivingEntity) event.getEntity();
			if(attaqué instanceof Player)
			{
				defPlayer = true;
				defstats = StatsManager.getPlayerInfos((Player)attaqué).getModifiedStats();
			
				DEF = StatsManager.getPlayerInfos( (Player)attaqué ).getModifiedStat(Stats.DEF)
						+ (( ((Player) attaqué).getLevel() / 2)
								* ( StatsManager.getPlayerInfos((Player) attaqué).getModifiedStat(Stats.DEFLevelBonus) ) );
			
			}
			else
			{
				defstats = Mobs.getMobStats(attaqué.getType());

				if(defstats.containsKey(Stats.DEF))
					DEF = defstats.get(Stats.DEF);
			}
			
		}
			
		if(event.getDamager() instanceof LivingEntity 
				|| event.getDamager() instanceof Arrow)
		{
			if( event.getDamager() instanceof Arrow)
				attaquant = ((Arrow) event.getDamager()).getShooter();
			else
				attaquant = (LivingEntity) event.getDamager();
			
			if(attaquant instanceof Player)
			{
				atkPlayer = true;
				atkstats = StatsManager.getPlayerInfos((Player)attaquant).getModifiedStats();
				
				ATK = StatsManager.getPlayerInfos((Player)attaquant).getModifiedStat(Stats.ATK) 
						+ ( (((Player)attaquant).getLevel() / 2)
								* ( StatsManager.getPlayerInfos((Player)attaquant).getModifiedStat(Stats.ATKLevelBonus) ) );
				
			}
			else
			{
				atkstats = Mobs.getMobStats(attaquant.getType());
				degatsDeBase = 0;
				
				if(atkstats.containsKey(Stats.ATK))
					ATK = atkstats.get(Stats.ATK) ;
				
				if(atkstats.containsKey(Stats.IgnoreDEF))
					defIgnorée = atkstats.get(Stats.IgnoreDEF);
			}
			
			
		}
		/*
		 * PARTIE DE L'EVENT RECUPERATION JOUEURS
		 * 
		 * degats = degatsdebase%2 + ((ATK*1,4 - (DEF-defignorée)*1,2) + bonuses) - idem*damagetaken%100
		 * 
		 * PARTIE EVENT COUP REUSSI OU NON
		 */
		
		
		if (atkstats != null)
		{
			

			if (atkstats.containsKey(Stats.HitChance))
			{
				if(!StatsManager.randomChance(atkstats.get(Stats.HitChance)))
				{
					if(atkPlayer)
						((Player) attaquant).sendMessage(ChatColor.GOLD+"You miss!");
					event.setCancelled(true);
					return;
				}
				else//ajout a son combo
				{
					if(atkPlayer)
					{
						comboCounter.put(((Player) attaquant), (comboCounter.get(((Player) attaquant)) + 1)) ;
						if(Config.COMBO_BURST.contains(comboCounter.get(((Player) attaquant))))
						{

							((Player) attaquant).sendMessage(ChatColor.AQUA+""+comboCounter.get(((Player) attaquant))+" Hits!");
							((Player) attaquant).getWorld().playEffect(((Player) attaquant).getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
						}
					}
						


					//verification si le coup est fatal ou pas
					if(atkstats.containsKey(Stats.InstantKillChance))
						if(StatsManager.randomChance(atkstats.get(Stats.InstantKillChance)))
						{
							if(atkPlayer)
								((Player) attaquant).sendMessage(ChatColor.GOLD+"FATALITY!!!");
							Bukkit.getPluginManager().callEvent(new EntityDeathEvent((LivingEntity) event.getEntity(),null));
							return;	

						}

					//verif si c'est un double coup
					if( !doubleHit.containsKey(attaquant) & atkstats.containsKey(Stats.DoubleHitChance))
					{
						
						if(StatsManager.randomChance(atkstats.get(Stats.DoubleHitChance)))
						{
							doubleHit.put(attaquant, null);
							if(atkPlayer)
								((Player) attaquant).sendMessage(ChatColor.GOLD+"DOUBLE HIT!!");
							Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(event.getDamager(),event.getEntity(), event.getCause(), event.getDamage()));
						}
					}
					else if(doubleHit.containsKey(attaquant))
						doubleHit.remove(attaquant);
				}
			}



			//defignorée = def * ignoré % 100
			
			degatsDeBase = degatsDeBase % 2 +(ATK*1.4 - (DEF - DEF * defIgnorée % 100)*1.2);
			
			//degats bonus =SOMME (degatsdebase * bonus) - degats de base
			


			//si coup critique, bonus aux degats initials
			if(atkstats.containsKey(Stats.CriticalChance) & atkstats.containsKey(Stats.CriticalDamage))
				if(StatsManager.randomChance(atkstats.get(Stats.CriticalChance)))
				{
					if(atkPlayer)
						((Player) attaquant).sendMessage(ChatColor.GOLD+"Critical hit!!");
					degatsDeBase = degatsDeBase  + (degatsDeBase * atkstats.get(Stats.CriticalDamage) % 100);
					
				}


			/*
			 * stats qui suivent = bonus
			 * bonus = (ini * bonusmod) - ini
			 * 
			 * 
			 */

			
			if(atkstats.containsKey(Stats.DamageVSBosses) & isBoss)
				degatsBonus = degatsBonus + ( ( degatsDeBase * atkstats.get(Stats.DamageVSBosses) ) % 100);

			if(atkstats.containsKey(Stats.Combo) & atkPlayer)
				degatsBonus = degatsBonus + ( ( degatsDeBase * atkstats.get(Stats.Combo) % 100 ) * comboCounter.get((Player)attaquant ) );

			/*
			 * BUFFS PART
			 * 
			 * -POISON
			 * -WEAKEN
			 * -STUN (slow + slowdigging)
			 * -BLIND
			 * 
			 */

			if(atkstats.containsKey(Stats.BlindChance))
				if(StatsManager.randomChance(atkstats.get(Stats.BlindChance)))
				{
					boolean avoided = false;

					if(defstats.containsKey(Stats.NegateStatusEffectChance))
					{
						if(StatsManager.randomChance(defstats.get(Stats.NegateStatusEffectChance)))
							avoided = true;
					}

					if(avoided)
					{
						if(atkPlayer)
							((Player) attaquant).sendMessage(ChatColor.GOLD+"You tried to blind your opponent but he resist!");
						if(defPlayer)
							((Player) attaqué).sendMessage(ChatColor.GOLD+"You resisted to blindness!");
					}
					else
					{
						if(atkPlayer)
							((Player) attaquant).sendMessage(ChatColor.GOLD+"You blinded your opponent!");
						if(atkstats.containsKey(Stats.BlindDuration))
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ((Double)(40 * atkstats.get(Stats.BlindDuration))).intValue(), 5));
						else
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 5));
					}
				}

			if(atkstats.containsKey(Stats.PoisonChance))
				if(StatsManager.randomChance(atkstats.get(Stats.PoisonChance)))
				{
					boolean avoided = false;

					if(defstats.containsKey(Stats.NegateStatusEffectChance))
					{
						if(StatsManager.randomChance(defstats.get(Stats.NegateStatusEffectChance)))
							avoided = true;
					}

					if(avoided)
					{
						if(atkPlayer)
							((Player) attaquant).sendMessage(ChatColor.GOLD+"You tried to poison your opponent but he resist!");
						if(defPlayer)
							((Player) attaqué).sendMessage(ChatColor.GOLD+"You resisted to poison!");
					}
					else
					{
						if(atkPlayer)
							((Player) attaquant).sendMessage(ChatColor.GOLD+"You poisoned your opponent!");
						if(atkstats.containsKey(Stats.PoisonDuration))
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.POISON, ((Double)(40 * atkstats.get(Stats.PoisonDuration))).intValue(), 0));
						else
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 0));
					}
				}

			if(atkstats.containsKey(Stats.WeakenChance))
				if(StatsManager.randomChance(atkstats.get(Stats.WeakenChance)))
				{
					boolean avoided = false;

					if(defstats.containsKey(Stats.NegateStatusEffectChance))
					{
						if(StatsManager.randomChance(defstats.get(Stats.NegateStatusEffectChance)))
							avoided = true;
					}

					if(avoided)
					{
						if(atkPlayer)
							((Player) attaquant).sendMessage(ChatColor.GOLD+"You tried to weak your opponent but he resist!");
						if(defPlayer)
							((Player) attaqué).sendMessage(ChatColor.GOLD+"You resisted to weakness!");
					}
					else
					{
						if(atkPlayer)
							((Player) attaquant).sendMessage(ChatColor.GOLD+"You weaken your opponent!");
						if(atkstats.containsKey(Stats.WeakenDuration))
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ((Double)(40 * atkstats.get(Stats.WeakenDuration))).intValue(), 0));
						else 
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0));
					}
				}


			if(atkstats.containsKey(Stats.StunChance))
				if(StatsManager.randomChance(atkstats.get(Stats.StunChance)))
				{
					boolean avoided = false;

					if(defstats.containsKey(Stats.NegateStatusEffectChance))
					{
						if(StatsManager.randomChance(defstats.get(Stats.NegateStatusEffectChance)))
							avoided = true;
					}

					if(avoided)
					{
						if(atkPlayer)
							((Player) attaquant).sendMessage(ChatColor.GOLD+"You tried to stun your opponent but he resist!");
						if(defPlayer)
							((Player) attaqué).sendMessage(ChatColor.GOLD+"You resisted to stuntness!");
					}
					else
					{
						if(atkPlayer)
							((Player) attaquant).sendMessage(ChatColor.GOLD+"You stuned your opponent!");
						if(atkstats.containsKey(Stats.WeakenDuration))
						{
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,((Double)(40 * atkstats.get(Stats.WeakenDuration))).intValue(), 20));
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ((Double)(40 * atkstats.get(Stats.WeakenDuration))).intValue(), 20));
						}
						else
						{
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 20));
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 20));

						}
					}
				}


			/*
			 * Fin des bonus
			 * 
			 */

			degatsTotal = (degatsDeBase + degatsBonus);//unification bonus degats initiaux
			if(atkstats.containsKey(Stats.Accuracy))//ajout de la précision.
				degatsTotal = degatsTotal * atkstats.get(Stats.Accuracy) % 100;

			

			if(atkstats.containsKey(Stats.HPDrain))//vol de vie
				if(atkstats.get(Stats.HPDrain) != 0){
					double vie = attaquant.getHealth();
					double maxlife = attaquant.getMaxHealth();
					double viegagnée = vie + (degatsTotal * atkstats.get(Stats.HPDrain) % 100);

					if(viegagnée < 0)
						viegagnée = 0.0;
					
					if(viegagnée > maxlife)
					{
						attaquant.setHealth(maxlife);
					}	
					else{
						attaquant.setHealth(viegagnée);
					}

					if(atkPlayer)
						((Player) attaquant).sendMessage(ChatColor.GOLD+"You stole "+viegagnée+" HP!");
				}

		}

		/*
		 * PARTIE DE L'EVENT CONCERNANT CELUI RECEVANT LES DOMMAGES
		 * 
		 */


		
			/*
			 * Action
			 * Recuperation des stats du perso attaqué
			 * verification des stats défensifs
			 * si présents ou non par defaut,
			 * modification des degats.
			 * 
			 */
			if(defPlayer)
				if(comboCounter.get(attaqué) > 0)
				{
					((Player)attaqué).sendMessage(ChatColor.YELLOW+""+comboCounter.get(attaqué)+" combo hit!");
					comboCounter.put(((Player)attaqué), 0);
				}



			if (defstats != null)
			{

				/*
				 * 
				 * 
				 * degats = degatsArme%2 + ((ATK*1,4 - (DEF-ignrdef)*1,2) + bonuses) - idem*damagetaken%100
				 * 
				 * 
				 * 1. on recupere les joueurs s'ils en sont.
				 * 2. s'ils en sont, on recupere leurs stats.
				 */

				if(defstats.containsKey(Stats.DamageTaken))
					degatsTotal = degatsTotal * defstats.get(Stats.DamageTaken) % 100;

				if(defstats.containsKey(Stats.MagicResist))
				{}

				if(defstats.containsKey(Stats.DamageReflect))
					if(event.getDamager() instanceof Entity)
						if (event.getDamager() instanceof LivingEntity)
							attaquant.damage(degatsTotal * defstats.get(Stats.DamageReflect) % 100);
						

				if(defstats.containsKey(Stats.InvincibilityBuffChanceWhenNoBuff))				
					if(attaqué.getActivePotionEffects().isEmpty())
						if(StatsManager.randomChance(defstats.get(Stats.InvincibilityBuffChanceWhenNoBuff)))
							attaqué.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100));
			}
		

			if(degatsTotal < 0)
				degatsTotal = 0;
			
		
			event.setDamage( degatsTotal );
			
			if(Config.SHOW_DAMAGE_DEALT) 
				if(atkPlayer)
					((Player) attaquant).sendMessage(ChatColor.RED+"You deals "+ degatsTotal +" damage!");

	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onDeath(final EntityDeathEvent event)
	{
		RequestChecker.verifyRequest();
		
		/*
		 *
		 *Concernant le joueur tué.
		 * si sa chance de mourir sans perdre son stuff est differente de 0, 
		 * on tente sa chance, si c'est bon, il meurt sans dropper son stuff,
		 * sinon, tant pis.
		 *
		 */
		if (event.getEntity().getType() == EntityType.PLAYER)
		{
			Player player = (Player) event.getEntity();
			if (StatsManager.PlayerList.get(player) != null)
			{
				if (StatsManager.getPlayerModifiedStatsList(player,plugin).containsKey(Stats.DeathWithoutLoot))
				{
					if ( StatsManager.getPlayerModifiedStatsList(player,plugin).get(Stats.DeathWithoutLoot) != Stats.DeathWithoutLoot.getDefaultValue())

					{


						boolean hasPermission = Permissions.hasAbilitiePermission(player, "protection");


						if (hasPermission)
						{
							if (event.getDrops().size() != 0)
							{
								for (ItemStack item : event.getDrops())
								{
									player.getInventory().addItem(item);
								}
							}
							event.getDrops().clear();

							player.setExp(event.getDroppedExp());
							event.setDroppedExp(0);

							player.sendMessage(ChatColor.GOLD+"Your items has been protected.");
						}

					}
				}
			}
		}


		/*
		 * Concernant le joueur qui a tué
		 * multiplie son stat d'xpGain par le gain d'xp 
		 * et pareil pour l'argent par vault.
		 * 
		 */

		if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

			if (damageEvent.getDamager() instanceof EntityPlayer)
			{
				Player player = (Player) damageEvent.getDamager();



				if(StatsManager.PlayerList.get(player) != null)
				{

					//gain d'xp
					if(StatsManager.getPlayerModifiedStatsList(player,plugin).containsKey(Stats.EXPGain))
						event.setDroppedExp(( (Double) (event.getDroppedExp() * StatsManager.PlayerList.get(player).getModifiedStat(Stats.EXPGain))).intValue());


					//gain d'argent

					if (StatsManager.getPlayerModifiedStatsList(player,plugin).containsKey(Stats.MoneyGain))
						if(plugin.vaultEconomy.hasAccount(player.getDisplayName()))
						{


							//CLASSE LISTENER

							class MoneyListener extends BukkitRunnable{

								private final RPSGPlugin plugin;
								private final double montant; 
								private final Player p;
								private final double multiplier;

								MoneyListener(RPSGPlugin plugin,Player p ,Double d,Double multiplier) {
									this.plugin = plugin;
									this.montant = d;
									this.p = p;
									this.multiplier = multiplier;
								}

								@Override
								public void run() {
									if(plugin.vaultEconomy.hasAccount(p.getName()))
										if (plugin.vaultEconomy.getBalance(p.getName()) > montant)
										{
											double gain = (plugin.vaultEconomy.getBalance(p.getName()) - montant);
											double bonus = (gain * multiplier) - gain;
											if(bonus > 0)
											{
												p.sendMessage(ChatColor.GOLD+"You gain a bonus of "+plugin.vaultEconomy.format(bonus)+"!");
												plugin.vaultEconomy.depositPlayer(p.getName(), bonus);	
											}
										}
										else this.cancel();
								}
							}

							//CLASSE FINIE

							final double balance = plugin.vaultEconomy.getBalance(player.getDisplayName());

							Bukkit.getScheduler().runTaskLater(plugin, new MoneyListener(plugin, player, balance, StatsManager.getPlayerModifiedStatsList(player,plugin).get(Stats.MoneyGain)),2);
						}



					//Drop rate
					if (StatsManager.getPlayerModifiedStatsList(player,plugin).containsKey(Stats.DropRate))
					{
						if (event.getEntity().getType() != EntityType.PLAYER)
						{

							int nombrededoubles = 0 ;
							if (!event.getDrops().isEmpty())
							{
								event.getDrops();

								for(ItemStack item : event.getDrops())
								{
									if (StatsManager.randomChance(StatsManager.getPlayerModifiedStatsList(player,plugin).get(Stats.DropRate)))
									{
										nombrededoubles++;
										event.getDrops().add(item);
									}
								}
							}

							if(Config.DEBUG_MODE) player.sendMessage("DEBUG : "+nombrededoubles+" items supplémentaires lootés");

						}
					}


					if (StatsManager.getPlayerModifiedStatsList(player,plugin).containsKey(Stats.MPRefillWhenKill))
					{
						// TODO mp refill  recuperer MAGIC SPELL
					}

				}

			}
		}
	}

	void initializePlayer(Player player)
	{
		if (!StatsManager.PlayerList.containsKey(player))
			try {
				Config.loadPlayerStats(player,plugin);
			} catch (IOException
					| InvalidConfigurationException e) {

				plugin.getLogger().warning("FAILED TO LOAD "+Config.SAVED_DATA.getFileName());
				e.printStackTrace();
			}


		if (! comboCounter.containsKey(player))
			comboCounter.put(player, 0);

		if (! initialspeed.containsKey(player))
			initialspeed.put(player, player.getWalkSpeed());
	}


	void exittingPlayer(Player player)
	{
		if (StatsManager.PlayerList.containsKey(player))
		{
			try {
				Config.savePlayerStats(player,plugin);
				StatsManager.PlayerList.remove(player);
			} catch (IOException | InvalidConfigurationException e) {

				plugin.getLogger().warning("FAILED TO SAVE "+Config.SAVED_DATA.getFileName());
				e.printStackTrace();
			}
		}

		if (doubleHit.containsKey(player))
			doubleHit.remove(player);

		if (comboCounter.containsKey(player))
			comboCounter.remove(player);

		if (initialspeed.containsKey(player))
			initialspeed.remove(player);
	}




}
