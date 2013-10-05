package de.lukas.bigcopy;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public enum Direction {
EAST, NORTH,WEST,SOUTH,UNDEFINED;

public static Direction parseDirection(Location loc){
	Vector v = loc.getDirection();
	int a = (int)Math.round(v.getX());
	int b = (int)Math.round(v.getZ());
	
	if (a == 1 && b == 0)
		return EAST;
	if (a == 0 && b == -1)
		return NORTH;
	if (a == -1 && b == 0)
		return WEST;
	if (a == 0 && b == 1)
		return SOUTH;
	return UNDEFINED;
}

}


