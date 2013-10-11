package de.lukas.bigcopy;

import org.bukkit.DyeColor;

public enum MarkerType {
	Pos1,Pos2,
	CopyCenter,
	CopyPos1, CopyPos2, CopyPos3, CopyPos4, CopyPos5, CopyPos6, CopyPos7, CopyPos8, 
	PastePos1, PastePos2, PastePos3, PastePos4,PastePos5, PastePos6, PastePos7, PastePos8, 
	PasteCenter;
	
	public int getMaterial(){
		return 35;
	}
	
	public byte getTpye(){
		switch(this){
		case Pos1:return DyeColor.RED.getWoolData();
		case Pos2:return DyeColor.GREEN.getWoolData();
		case CopyPos1:return DyeColor.RED.getWoolData();
		case CopyPos2:return DyeColor.GREEN.getWoolData();
		case CopyPos3:return DyeColor.BLUE.getWoolData();
		case CopyPos4:return DyeColor.YELLOW.getWoolData();
		case CopyPos5:return DyeColor.RED.getWoolData();
		case CopyPos6:return DyeColor.GREEN.getWoolData();
		case CopyPos7:return DyeColor.BLUE.getWoolData();
		case CopyPos8:return DyeColor.YELLOW.getWoolData();
		case CopyCenter:return DyeColor.BLACK.getWoolData();
		case PastePos1:return DyeColor.RED.getWoolData();
		case PastePos2:return DyeColor.GREEN.getWoolData();
		case PastePos3:return DyeColor.BLUE.getWoolData();
		case PastePos4:return DyeColor.YELLOW.getWoolData();
		case PastePos5:return DyeColor.RED.getWoolData();
		case PastePos6:return DyeColor.GREEN.getWoolData();
		case PastePos7:return DyeColor.BLUE.getWoolData();
		case PastePos8:return DyeColor.YELLOW.getWoolData();
		case PasteCenter:return DyeColor.SILVER.getWoolData();
		}
		return 0;
	}
	
}

