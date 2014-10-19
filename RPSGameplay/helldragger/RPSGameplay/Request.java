package helldragger.RPSGameplay;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class Request {

	public enum Operation {
		ADDITION,
		SOUSTRACTION;
	}
	
	final private EnumMap<Stats,Double> data = new EnumMap<Stats,Double>(Stats.class);
	final private Player p;
	final private Operation op;
	
	private int ID = 1;
	
	public Request(Operation op, final Map<? extends Stats,? extends Double> map, Player p){
		this.data.putAll(map);
		this.p = p;
		this.op = op;
	}
	
	public int getID(){
		return this.ID;
	}
	
	public int setID(final int newID){
		this.ID = newID;
		return this.ID;
	}
	
	public EnumMap<Stats,Double> getData(){
		return this.data;
	}

	public Player getPlayer() {
		return p;
	}
	
	public Operation getOperation(){
		return op;
	}
}
