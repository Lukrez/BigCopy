package de.lukas.bigcopy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CopyTask extends BukkitRunnable {

	public String playerName;
	public int X1;
	public int Y1;
	public int Z1;

	public int X2;
	public int Y2;
	public int Z2;

	public int markerX;
	public int markerY;
	public int markerZ;
	//private Location cm;
	public int atX;
	public int atY;
	public int atZ;
	public FileWriter fw;
	public BufferedWriter bw;
	public World world;
	public boolean copyFinished;
	public int delay = 20;
	private Stopwatch swCopy;


	public CopyTask(Project project) {
		this.world = project.getPos1().getWorld();
		this.delay = project.getDelay();
		this.playerName = project.getUser();
		this.markerX = project.getCopyCenter().getBlockX();
		this.markerY = project.getCopyCenter().getBlockY();
		this.markerZ = project.getCopyCenter().getBlockZ();
		// define positions
		if (project.getPos1().getBlockX() < project.getPos2().getBlockX()) {
			this.X1 = project.getPos1().getBlockX();
			this.X2 = project.getPos2().getBlockX();
		} else {
			this.X1 = project.getPos2().getBlockX();
			this.X2 = project.getPos1().getBlockX();
		}
		if (project.getPos1().getBlockY() < project.getPos2().getBlockY()) {
			this.Y1 = project.getPos1().getBlockY();
			this.Y2 = project.getPos2().getBlockY();
		} else {
			this.Y1 = project.getPos2().getBlockY();
			this.Y2 = project.getPos1().getBlockY();
		}
		if (project.getPos1().getBlockZ() < project.getPos2().getBlockZ()) {
			this.Z1 = project.getPos1().getBlockZ();
			this.Z2 = project.getPos2().getBlockZ();
		} else {
			this.Z1 = project.getPos2().getBlockZ();
			this.Z2 = project.getPos1().getBlockZ();
		}

		// define starting point
		this.atX = this.X1 - 1;
		this.atY = this.Y1;
		this.atZ = this.Z1;

		this.copyFinished = false;
		// define path
		
		// open writer
		this.openWriter(this.atY - this.markerY+".txt");
		this.bw = new BufferedWriter(fw);

		// Calculate size
		int blocknrs = (this.X2 - this.X1 + 1) * (this.Y2 - this.Y1 + 1) * (this.Z2 - this.Z1 + 1);
		Player player = Bukkit.getPlayer(this.playerName);
		if (player != null) {
			player.sendMessage(blocknrs + " Bl�cke zum kopieren ausgew�hlt.");
		}
	}

	public String getStatus() {
		// 
		int blocknrs = (this.X2 - this.X1 + 1) * (this.Y2 - this.Y1 + 1) * (this.Z2 - this.Z1 + 1);
		int at = (this.X2 - this.X1 + 1) * (this.Y2 - this.atY + 1) * (this.Z2 - this.Z1 + 1);
		at += (this.X2 - this.X1 + 1) * (this.Z2 - this.atZ + 1);
		at += (this.X2 - this.atX);

		return at + " Bl�cke von " + blocknrs + " kopiert. Dies sind " + at / (float) blocknrs * 100 + "%";
	}

	@Override
	public void run() {

		if (this.swCopy == null) {
			this.swCopy = new Stopwatch();
			this.swCopy.start();
		}

		Stopwatch sw = new Stopwatch();
		sw.start();

		int i = 0;
		while (i < 1000 && !this.copyFinished) {
			i++;
			this.atX++;
			if (this.atX > this.X2) {
				this.atX = this.X1;
				this.atZ++;
			}
			if (this.atZ > this.Z2) {
				this.atZ = this.Z1;
				this.atY++;
				this.closeWriter();
				this.openWriter(this.atY - this.markerY+".txt");
				
			}
			if (this.atY > this.Y2) {
				// tell task to stop and leave while loop
				this.copyFinished = true;
			} else {
				Block b = this.world.getBlockAt(this.atX, this.atY, this.atZ);
				// check for air
				if (b.getTypeId() == 0)
					continue;
				String s = "";
				s += (this.atX - this.markerX) + ":";
				s += (this.atY - this.markerY) + ":";
				s += (this.atZ - this.markerZ) + ":";
				
				s += b.getTypeId() + ":";
				s += b.getData() + "\n";
				try {
					this.bw.write(s);
				} catch (IOException e) {
					//TODO: fehler in log schreiben 
					this.copyFinished = true;
					e.printStackTrace();
				}
			}
		}
		// 	write into file
		try {
			this.bw.flush();
		} catch (IOException e) {
			this.copyFinished = true;
			e.printStackTrace();
		}
		sw.stop();
		System.out.println(sw.getElapsedTime());

		if (this.copyFinished) {
			this.closeWriter();
			this.swCopy.stop();
			System.out.println("Kopiervorgang brauchte: " + this.swCopy.getElapsedTime() + " ms");

			Player player = Bukkit.getPlayer(this.playerName);
			if (player != null) {
				player.sendMessage("Kopiervorgang erledigt.");
			}

		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this, this.delay);
		}
	}
	
	public void openWriter(String name){
		// open writer
		File path = new File(BigCopy.getInstance().getDataFolder(),"copy"+File.separator + name);
		try {
			this.fw = new FileWriter(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bw = new BufferedWriter(fw);
	}
	
	public void closeWriter(){
		try {
			this.bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.bw.close();
			this.fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
