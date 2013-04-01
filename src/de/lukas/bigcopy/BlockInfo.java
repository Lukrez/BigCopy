package de.lukas.bigcopy;

import org.bukkit.block.Block;

public class BlockInfo {

	private int x, y, z, id, data;

	public BlockInfo(Block b) {
		this.x = b.getX();
		this.y = b.getY();
		this.z = b.getZ();
		this.id = b.getTypeId();
		this.data = b.getData();
	}

	@Override
	public String toString() {
		return this.x + ":" + this.y + ":" + this.z + ":" + this.id + ":" + this.data;
	}
}
