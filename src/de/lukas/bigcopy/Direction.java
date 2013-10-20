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

public int getXComponent(){
	switch(this){
	case EAST: return 1;
	case WEST: return -1;
	case NORTH: return 0;
	case SOUTH: return 0;
	default:
		return 0;
	}
}
public int getZComponent(){
	switch(this){
	case EAST: return 0;
	case WEST: return 0;
	case NORTH: return -1;
	case SOUTH: return 1;
	default:
		return 0;
	}
}

public Direction rotate(){
	switch(this){
	case EAST: return NORTH;
	case NORTH: return WEST;
	case WEST: return SOUTH;
	case SOUTH: return EAST;
	default:
		return UNDEFINED;
	}
}

public int getNr(){
	switch(this){
	case EAST: return 0;
	case NORTH: return 3;
	case WEST: return 2;
	case SOUTH: return 1;
	default:
		return 0;
	}
}

}


