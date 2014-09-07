package helldragger.RPSGameplay;

import java.util.EnumMap;

import org.bukkit.entity.EntityType;

class Mobs {

	static EnumMap<EntityType, EnumMap<Stats,Double>> mobs = new EnumMap<EntityType, EnumMap<Stats,Double>>(EntityType.class);
	
	static void load(){
		for(EntityType ent : EntityType.values())
			if(ent.isAlive())
				mobs.put(ent, new EnumMap<Stats,Double>(Stats.class));
	}
	
	static EnumMap<Stats, Double> getMobStats(EntityType e){
		return mobs.get(e);
	}
	
	static Double getStat(EntityType e,Stats s){
		return mobs.get(e).get(s);
	}
	
	static void setMobStats(EntityType mob, EnumMap<Stats, Double> map){
		mobs.put(mob, map);
		return ;
	}
	
	static boolean isBoss(EntityType e){
		return Config.BOSSES.contains(e);
	}
	
	static boolean isRare(EntityType e){
		return Config.RARE_MONSTERS.contains(e);
	}
	
	static boolean isEpic(EntityType e){
		return Config.EPIC_MONSTERS.contains(e);
	}
	
	
}
