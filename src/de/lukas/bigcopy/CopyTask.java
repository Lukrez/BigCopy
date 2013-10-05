package de.lukas.bigcopy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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

	public int centerX;
	public int centerY;
	public int centerZ;
	
	public int atX;
	public int atY;
	public int atZ;
	public FileWriter fw;
	public BufferedWriter bw;
	public World world;
	public boolean copyFinished;
	public boolean copyAbort;
	public int delay = 20;
	private Stopwatch swCopy;
	private File taskYml;
	private File copyFolder;
	private File blocksFolder;
	private File inventoriesFolder;
	private File entititesFolder;
	private boolean writerOpen = false;


	public CopyTask(Project project) {
		this.world = project.getPos1().getWorld();
		this.delay = project.getDelay();
		this.playerName = project.getUser();
		this.centerX = project.getCopyCenter().getBlockX();
		this.centerY = project.getCopyCenter().getBlockY();
		this.centerZ = project.getCopyCenter().getBlockZ();
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
		this.copyAbort = false;
		// define path
		
		// open writer
		this.openWriter(this.atY - this.centerY+".txt");
		this.bw = new BufferedWriter(fw);

		// Calculate size
		int blocknrs = (this.X2 - this.X1 + 1) * (this.Y2 - this.Y1 + 1) * (this.Z2 - this.Z1 + 1);
		Player player = Bukkit.getPlayer(this.playerName);
		if (player != null) {
			player.sendMessage(blocknrs + " Blöcke zum kopieren ausgewählt.");
		}
		this.copyFolder = project.getCopyFolder();
		this.blocksFolder = new File(this.copyFolder,"blocks");
		if (!this.blocksFolder.exists()){
			this.blocksFolder.mkdir();
		}
		this.inventoriesFolder = new File(this.copyFolder,"inventories");
		if (!this.inventoriesFolder.exists()){
			this.inventoriesFolder.mkdir();
		}
		this.entititesFolder = new File(this.copyFolder,"entities");
		if (!this.entititesFolder.exists()){
			this.entititesFolder.mkdir();
		}
		this.taskYml = new File(this.copyFolder,"task.yml");
		
		
	}
	
	public void saveTaskFile(){
		YamlConfiguration ymlTask = new YamlConfiguration();
		ymlTask.set("atX",  this.atX);
		ymlTask.set("atY",  this.atY);
		ymlTask.set("atZ",  this.atZ);
		try {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(this.taskYml));
			out.flush();
			out.close();
			out.write(ymlTask.saveToString().getBytes());
		} catch (IOException e) {
			this.copyAbort = true;
			e.printStackTrace();
		}
	}
	
	
	public void loadTaskFile(){
		if (!this.taskYml.exists())
			return;
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(this.taskYml));
			YamlConfiguration ymlTask = new YamlConfiguration();
			ymlTask.load(in);
			this.atX = ymlTask.getInt("atX");
			this.atX = ymlTask.getInt("atX");
			this.atX = ymlTask.getInt("atX");			
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	

	public String getStatus() {
		// 
		int blocknrs = (this.X2 - this.X1 + 1) * (this.Y2 - this.Y1 + 1) * (this.Z2 - this.Z1 + 1);
		int at = (this.X2 - this.X1 + 1) * (this.Y2 - this.atY + 1) * (this.Z2 - this.Z1 + 1);
		at += (this.X2 - this.X1 + 1) * (this.Z2 - this.atZ + 1);
		at += (this.X2 - this.atX);

		return at + " Blöcke von " + blocknrs + " kopiert. Dies sind " + at / (float) blocknrs * 100 + "%";
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
		while (i < 1000 && !this.copyFinished && !this.copyAbort) {
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
				if (this.atY <= this.Y2)
					this.openWriter(this.atY - this.centerY+".txt");
				
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
				s += (this.atX - this.centerX) + ":";
				s += (this.atY - this.centerY) + ":";
				s += (this.atZ - this.centerZ) + ":";
				
				s += b.getTypeId() + ":";
				s += b.getData() + "\n";
				try {
					this.bw.write(s);
				} catch (IOException e) {
					//TODO: fehler in log schreiben 
					this.copyFinished = true;
					e.printStackTrace();
				}
				this.saveTaskFile();
			}
		}
		// 	write into file
		try {
			this.bw.flush();
		} catch (IOException e) {
			this.copyAbort = true;
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

		} else if (this.copyAbort){
			this.closeWriter();
			this.swCopy.stop();
			System.out.println("Kopiervorgang abgebrochen.");
			Player player = Bukkit.getPlayer(this.playerName);
			if (player != null) {
				player.sendMessage("Kopiervorgang abgebrochen.");
			}
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this, this.delay);
		}
	}
	
	public void openWriter(String name){
		if (this.writerOpen){
			System.out.println("A writer is already open");
			return;
		}
		// open writer
		File path = new File(this.blocksFolder,name);
		try {
			this.fw = new FileWriter(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bw = new BufferedWriter(fw);
	}
	
	public void closeWriter(){
		if (!this.writerOpen){
			System.out.println("No writer needs closing.");
			return;
		}
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
