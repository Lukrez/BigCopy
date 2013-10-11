package de.lukas.bigcopy;

import org.bukkit.Location;

public class Marker {

	private Location loc;
	private MarkerType marker;
	private int blockId;
	private byte blockData;
	
	public Marker(Location loc, MarkerType marker){
		this.loc = loc;
		this.blockId = loc.getBlock().getTypeId();
		this.blockData = loc.getBlock().getData();
		this.marker = marker;
	}
	
	public Marker(String s){
		String[] split1 = s.split("#");
		if (split1.length != 3)
			return;
		this.loc = Project.parseLocationFromString(split1[0]);
		this.marker = MarkerType.valueOf(split1[1]);
		String[] split2 = split1[2].split(";");
		if (split2.length != 2)
			return;
		this.blockId = Integer.parseInt(split2[0]);
		this.blockData = Byte.parseByte(split2[1]);
		
	}
	
	public MarkerType getType(){
		return this.marker;
	}
	
	public String toString(){
		return Project.LocationToString(loc)+"#"+marker.toString()+"#"+this.blockId+";"+this.blockData;
	}
	
	public void showMarker(){
		this.loc.getBlock().setTypeId(this.marker.getMaterial());
		this.loc.getBlock().setData(this.marker.getTpye());
	}
	
	public void showBlock(){
		this.loc.getBlock().setTypeId(this.blockId);
		this.loc.getBlock().setData(this.blockData);
	}
	
	public Location getLocation(){
		return this.loc;
	}
}
