package helldragger.RPSGameplay;

public class Stat {

	private double value;
	private double defaultvalue;
	
	Stat(double defaultValue){
		this.defaultvalue = defaultValue;
		this.value = defaultValue;
	}
	
	public double getValue(){
		return this.value;
	}
	
	public double getDefaultValue(){
		return this.defaultvalue;
	}
	
	public void setValue(double value){
		this.value = value;
	}
	
	public void setDefaultValue(double value){
		this.defaultvalue = value;
	}
	
	
	
}
