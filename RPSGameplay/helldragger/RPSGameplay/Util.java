package helldragger.RPSGameplay;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;


class Util
{
	public boolean isNegative(Number num){
		return (num.toString().contains("-"));
	}
	
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	
	static List<Integer> getCommaSeparatedInteger(String s){
		
		String[] s2 = s.replace("[", "").replace("]", "").replace(" ", "").split(",");
		List<Integer> list = new ArrayList<Integer>();
		
		if(s2.length > 0)
		{
			for(String string : s2)
			{		
				list.add(Integer.parseInt(string));
			}
		}
		return list;
	}
	
	
	static int getChestSize(int emplacements){
		int taille = 0;
		while (taille < emplacements)
			taille = taille + 9;
		return taille;
	}
	
	static List<String> descriptionToLines(String desc){
		List<String> result = new ArrayList<String>();
		int lenght = 40;
		if (desc != null)
			for(int i = 0; lenght * i < desc.length(); i++)
			{
				
				
				if((i+1)*40 >= desc.length())
					result.add(desc.substring(i*lenght, desc.length()-1));
				else
					{
					if(desc.substring(i*lenght, (i+1)*lenght).endsWith(""+ChatColor.COLOR_CHAR))
						desc.replaceAll(""+ChatColor.COLOR_CHAR,  (" "+ChatColor.COLOR_CHAR));

					result.add(desc.substring(i*lenght, (i+1)*lenght));
					}
			}	
		return result;
	}
	
	public static EnumMap<Stats, Double> stringToHashMap(String s)
	{
		EnumMap<Stats,Double> map = new EnumMap<Stats,Double>(Stats.class);
		for(Stats stat: Stats.values())
		{
			map.put(stat, stat.getDefaultValue());
		}
		
		if(s == null)
			return map;
		s = s.replace("{", " ").replace("[", " ").replace("(", " ").replace("}", " ").replace("]", " ").replace(")", " ");
		String[] cases = s.split(",");
		for(String equation : cases)
		{
			String[] values = equation.split("=");
			map.put(Stats.valueOf(values[0]), Double.valueOf(values[1]));
		}
		return map;
	}
	
	public static EntityType stringToEntityType(String s)
	{
		s = s.toUpperCase().replaceAll(" ", "_");
		
		while(s.startsWith("_") || s.endsWith("_"))
			if(s.startsWith("_"))
				s = s.substring(1);
			else if (s.endsWith("_"))
				s = s.substring(0,s.length()-2);
				
		return EntityType.valueOf(s);
	}
	
	
	public static String capitalizeFirst(String string, char divider)
	{
		String div = String.valueOf(divider);

		String[] words = string.split(div);

		string = "";

		for (int i = 0; i < words.length - 1; i++)
		{
			string += words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase() + " ";
		}

		string += words[words.length - 1].substring(0, 1).toUpperCase()
				+ words[words.length - 1].substring(1).toLowerCase();

		return string;
	}

	public static List<String> getCommaSeperatedValues(String string)
	{
		List<String> list = new ArrayList<String>();

		if (string.startsWith("none") || string.isEmpty())
			return list;
		
		string = string.toUpperCase();
		string.replaceAll(" ", "_");
		
		String[] values = string.split(",");

		for (int i = 0; i < values.length; i++)
		{
			String value = values[i];

			if (value.startsWith("_"))
			{
				value = value.substring(1);
			}

			list.add(value);
		}

		return list;
	}

	public static void dropExperience(Location loc, int expToDrop, int expPerOrb)
	{
		World world = loc.getWorld();

		int maxOrbs = expToDrop / expPerOrb;

		for (int orb = 0; orb < maxOrbs; orb++)
		{
			((ExperienceOrb) world.spawn(loc, ExperienceOrb.class)).setExperience(expPerOrb);
		}
	}

	public static String searchListForString(List<String> list, String string, String def)
	{
		if (list == null) return def;
		
		for (String s : list)
		{
			if (ChatColor.stripColor(s).startsWith(ChatColor.stripColor(string)))
			{
				return s;
			}
		}

		return def;
	}
	
	public static String searchListForString(List<String> list, String string, String def, String prefix)
	{
		if (list == null) return def;
		
		for (String line : list)
		{
			String strippedLine = ChatColor.stripColor(line);
			
			if (strippedLine.startsWith(prefix))
			{
				return line.substring(prefix.length());
			}
			
			if (strippedLine.startsWith(ChatColor.stripColor(string)))
			{
				return line;
			}
		}

		return def;
	}
	
	public static int searchListForStringID(List<String> list, String string, int def)
	{
		if (list == null) return def;
		
		ListIterator<String> i = list.listIterator();
		
		while (i.hasNext())
		{
			String s = i.next();
			if (ChatColor.stripColor(s).startsWith(ChatColor.stripColor(string)))
			{
				return i.nextIndex() - 1;
			}
		}
		
		return def;
	}
	
	public static int searchListForStringID(List<String> list, String string, int def, String prefix)
	{
		if (list == null) return def;
		
		ListIterator<String> i = list.listIterator();
		
		while (i.hasNext())
		{
			String s = i.next();
			if (ChatColor.stripColor(s).startsWith(prefix + ChatColor.stripColor(string)))
			{
				return i.nextIndex() - 1;
			}
		}
		
		return def;
	}
	
	public static ChatColor getSafeChatColor(String color, ChatColor def)
	{
		for (ChatColor c : ChatColor.values())
		{
			if (color.equalsIgnoreCase(c.name()))
			{
				return c;
			}
		}
		
		return def;
	}
	
	public static int getLevelOnCurve(int min, int max, double ratio)
	{
		Random rand = new Random();
		int level = 1;
		
		int roll = rand.nextInt(100) + 1;
		
		for (int i = min; i <= max; i++)
		{			
			if (roll <= (ratio / i) * 100)
			{
				//System.out.println("i: " + i + " needed: " + (ratio / i * 100) + " roll: " + roll);
				level = i;
			}
			else
			{
				return level;
			}
		}
		
		return level;
	}
	
	
	public static void printlnObj(PrintStream printer, Object...objects)
	{
		String line = "";
		
		for (Object obj : objects)
		{
			line += obj.toString() + ":";
		}
		
		printer.println(line);
	}
}