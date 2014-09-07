package helldragger.RPSGameplay;

import java.util.EnumMap;



public enum Stats{
	MaximumHP("MaximumHP",false),
	HPRecovery("HPRecovery"),
	HPDrain("HPDrain"),
	
	MaximumMP("MaximumMP",false),//TODO
	MPRecovery("MPRecovery"),//TODO
	MPRefillWhenKill("MPRefillWhenKill"),//TODO
	
	
	DEF("DEF",false),
	DEFLevelBonus("DEFLevelBonus",false),
	DamageTaken("DamageTaken"),
	MagicResist("MagicResist"),
	DamageReflect("DamageReflect"),
	Evasion("Evasion"),
	DodgeChance("DodgeChance"),
	NegateStatusEffectChance("NegateStatusEffectChance"),
	PoisonResistance("PoisonResistance"),
	WeakenResistance("WeakenResistance"),
	BlindResistance("BlindResistance"),
	InvincibilityBuffChanceWhenNoBuff("InvincibilityBuffChanceWhenNoBuff"),
	
	
	
	Combo("Combo"),
	
	ATK("ATK",false),
	ATKLevelBonus("ATKLevelBonus",false),
	InstantKillChance("InstantKillChance"),
	DamageVSBosses("DamageVSBosses"),
	IgnoreDEF("IgnoreDEF"),
	PoisonChance("PoisonChance"),
	PoisonDuration("PoisonDuration"),
	BlindChance("BlindChance"),
	BlindDuration("BlindDuration"),
	WeakenChance("WeakenChance"),
	WeakenDuration("WeakenDuration"),
	StunChance("StunChance"),
	StunDuration("StunDuration"),
	Mastery("Mastery"),//TODO avec RPSW
	
	Accuracy("Accuracy"),
	CriticalChance("CriticalChance"),
	CriticalDamage("CriticalDamage"),

	AttackSpeed("AttackSpeed"),
	HitChance("HitChance"),
	DoubleHitChance("DoubleHitChance"),
	
	NearbyMonsterSpawnRate("NearbyMonsterSpawnRate"),
	
	EXPGain("EXPGain"),
	MoneyGain("MoneyGain"),
	DropRate("DropRate"),
	RareMonsterRate("RareMonsterRate"),
	EpicMonsterRate("EpicMonsterRate"),
	DeathWithoutLoot("DeathWithoutLoot");

	
	private String label;
	private String desc = "";
	private boolean percentage = true;
	private Stat stat = new Stat(100);
	
	Stats(String pLabel){
		this.label = pLabel;
		
		
	}
	
	Stats(String pLabel, boolean isPercentage){
		this.label = pLabel;
		
		this.percentage = isPercentage;
	}
	
	
	

	public String getLabel()
	{
		return this.label;
	}
	
	public double getValue()
	{
		return this.stat.getValue();
	}
	
	public double getDefaultValue()
	{
		return this.stat.getDefaultValue();
	}

	@Override
	public String toString()
	{
		return this.label;
	}
	
	static public EnumMap<Stats,Double> getNewStatsMap()
	{
		EnumMap<Stats, Double> map = new EnumMap<Stats,Double>(Stats.class);
		for(Stats stat : values())
			map.put(stat, stat.getDefaultValue());
		
		return map;
	}
	
	public boolean isPercentage()
	{
		return this.percentage;
	}
	
	public String getDescription(){
		return this.desc;
		
	}

	public void reloadValue(){
		double value = Config.BASE_STATS.get(this);
		this.stat.setDefaultValue(value);
		this.stat.setValue(value);
		
	}
	

}
