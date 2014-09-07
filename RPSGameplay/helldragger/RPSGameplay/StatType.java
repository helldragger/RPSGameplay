package helldragger.RPSGameplay;
import java.util.ArrayList;
import java.util.List;


enum StatType {
	atkStat(Stats.ATK,
			Stats.ATKLevelBonus,
			Stats.AttackSpeed,
			Stats.Combo,
			Stats.CriticalChance,
			Stats.CriticalDamage,
			Stats.Accuracy,
			Stats.HPDrain,
			Stats.IgnoreDEF),
			
	defStat(
			Stats.MaximumHP,
			Stats.HPRecovery,
			Stats.DamageTaken,
			Stats.DEF,
			Stats.DEFLevelBonus,
			Stats.DamageReflect,
			Stats.DodgeChance,
			Stats.MagicResist,
			Stats.NegateStatusEffectChance,
			Stats.PoisonResistance,
			Stats.WeakenResistance,
			Stats.BlindResistance),
			
	envStat(Stats.DropRate,
			Stats.NearbyMonsterSpawnRate,
			Stats.RareMonsterRate,
			Stats.EpicMonsterRate,
			Stats.EXPGain,
			Stats.MPRecovery,
			Stats.MaximumMP),
	
	statusStat(Stats.PoisonChance,
			Stats.PoisonDuration,
			Stats.BlindChance,
			Stats.BlindDuration,
			Stats.WeakenChance,
			Stats.WeakenDuration,
			Stats.StunChance,
			Stats.StunDuration),
	
	bonusStat(Stats.DamageVSBosses,
			Stats.DoubleHitChance,
			Stats.Evasion,
			Stats.InvincibilityBuffChanceWhenNoBuff,
			Stats.Mastery,
			Stats.MoneyGain,
			Stats.MPRefillWhenKill);
	
	
	List<Stats> statlist = new ArrayList<Stats>();
	int length;
	StatType(Stats ... stats){
		for(Stats stat : stats)
			statlist.add(stat);
		length = statlist.size();
		
	}
}
