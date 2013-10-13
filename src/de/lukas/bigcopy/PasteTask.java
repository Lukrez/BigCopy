package de.lukas.bigcopy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PasteTask extends BukkitRunnable {

	public String playerName;
	public Project project;
	public int X1;
	public int Y1;
	public int Z1;

	public int X2;
	public int Y2;
	public int Z2;
	
	public int atX;
	public int atY;
	public int atZ;
	
	public int pasteCenterX;
	public int pasteCenterY;
	public int pasteCenterZ;
	
	private int copyCenterX;
	private int copyCenterY;
	private int copyCenterZ;
	
	//public int pasteAtX;
	//public int pasteAtY;
	//public int pasteAtZ;
	public int logAtX;
	public int logAtY;
	public int logAtZ;
	
	public FileReader fr;
	public BufferedReader br;
	//public FileWriter fw;
	//public BufferedWriter bw;
	public World world;
	public boolean pasteFinished;
	public boolean pasteAbort;
	public int delay = 20;
	private Stopwatch swCopy;
	
	
	private File copyFolder;
	private File blocksFolder;
	private File inventoriesFolder;
	private File entititesFolder;
	
	private File blockFile;
	private boolean pastefileExists;


	public PasteTask(Project project) {
		this.project = project;
		this.world = project.getPasteCenter().getWorld();
		this.delay = project.getDelay();
		this.playerName = project.getUser();
		this.pasteCenterX = project.getPasteCenter().getBlockX();
		this.pasteCenterY = project.getPasteCenter().getBlockY();
		this.pasteCenterZ = project.getPasteCenter().getBlockZ();
		
		this.copyCenterX = project.getCopyCenter().getBlockX();
		this.copyCenterY = project.getCopyCenter().getBlockY();
		this.copyCenterZ = project.getCopyCenter().getBlockZ();
		
		// define positions
		int cX1,cX2,cY1,cY2,cZ1,cZ2;
		
		if (project.getPos1().getBlockX() < project.getPos2().getBlockX()) {
			cX1 = project.getPos1().getBlockX();
			cX2 = project.getPos2().getBlockX();
		} else {
			cX1 = project.getPos2().getBlockX();
			cX2 = project.getPos1().getBlockX();
		}
		if (project.getPos1().getBlockY() < project.getPos2().getBlockY()) {
			cY1 = project.getPos1().getBlockY();
			cY2 = project.getPos2().getBlockY();
		} else {
			cY1 = project.getPos2().getBlockY();
			cY2 = project.getPos1().getBlockY();
		}
		if (project.getPos1().getBlockZ() < project.getPos2().getBlockZ()) {
			cZ1 = project.getPos1().getBlockZ();
			cZ2 = project.getPos2().getBlockZ();
		} else {
			cZ1 = project.getPos2().getBlockZ();
			cZ2 = project.getPos1().getBlockZ();
		}
		
		Location minPaste =  project.calcPastePosition(new Location(this.world, cX1, cY1, cZ1));
		Location maxPaste =  project.calcPastePosition(new Location(this.world, cX2, cY2, cZ2));
		
		if (minPaste == null || maxPaste == null){
			System.out.println("Pasteposition ist null!");
			return;
		}
		
		this.X1 = minPaste.getBlockX();
		this.Y1 = minPaste.getBlockY();
		this.Z1 = minPaste.getBlockZ();
		
		this.X2 = maxPaste.getBlockX();
		this.Y2 = maxPaste.getBlockY();
		this.Z2 = maxPaste.getBlockZ();
		System.out.println("PasteMin: "+this.X1+","+this.Y1+","+this.Z1);
		System.out.println("PasteMax: "+this.X2+","+this.Y2+","+this.Z2);
		
		
		
		// Check Copystructure and Files
		
		this.pasteFinished = false;
		this.pasteAbort = false;
		this.pastefileExists = false;
		
		this.copyFolder = project.getCopyFolder();
		if (!this.copyFolder.exists()){
			System.out.println("Copyordner existiert nicht!");
			this.pasteAbort = true;
			return;
		}
		this.blocksFolder = new File(this.copyFolder,"blocks");
		if (!this.blocksFolder.exists()){
			System.out.println("Blockordner existiert nicht!");
			this.pasteAbort = true;
			return;
		}
		this.inventoriesFolder = new File(this.copyFolder,"inventories");
		if (!this.inventoriesFolder.exists()){
			System.out.println("Inventoryordner existiert nicht!");
			return;
		}
		this.entititesFolder = new File(this.copyFolder,"entities");
		if (!this.entititesFolder.exists()){
			System.out.println("Entitiyordner existiert nicht!");
			this.pasteAbort = true;
			return;
		}
		
		// check paste file existence
		for (int y = this.Y1; y <= this.Y2;y++){
			String name = (y-this.pasteCenterY)+".txt";
			File yExists = new File(this.blocksFolder,name);
			if (!yExists.exists()){
				System.out.println("Blockfile "+name+" existiert nicht!");
				this.pasteAbort = true;
				return;
			}
		}
		
		

		this.openPasteReader((this.Y1-this.pasteCenterY)+".txt");
		this.atY = this.Y1;
		this.atX = this.X1;
		this.atZ = this.Z1;
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
		while (i < 1000 && !this.pasteFinished && !this.pasteAbort) {
			i++;
			
			String line = null;
			try {
				line = this.br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				this.pasteAbort = true;
				continue;
			}
			if (line == null){
				this.closePasteReader();
				
				this.atY++;
				if (this.atY > this.Y2){
					this.pasteFinished = true;
					continue;
				}
				this.openPasteReader((this.atY-this.pasteCenterY)+".txt");
				continue;
			}
			// Read line
			String[] sp = line.split(":");
			if (sp.length != 5){
				this.pasteAbort = true;
				System.out.println("Linie ist kein Block!");
				continue;
			}
			int x = Integer.parseInt(sp[0]);
			int y = Integer.parseInt(sp[1]);
			int z = Integer.parseInt(sp[2]);
			int id = Integer.parseInt(sp[3]);
			byte datavalue = Byte.parseByte(sp[4]);
			this.pasteBlock(x, y, z, id, datavalue);

		}
		sw.stop();
		System.out.println(sw.getElapsedTime());

		if (this.pasteFinished) {
			this.closePasteReader();
			this.swCopy.stop();
			System.out.println("Pastevorgang brauchte: " + this.swCopy.getElapsedTime() + " ms");

			Player player = Bukkit.getPlayer(this.playerName);
			if (player != null) {
				player.sendMessage("Pastevorgang erledigt.");
			}
		} else if (this.pasteAbort) {
			this.closePasteReader();
			this.swCopy.stop();
			System.out.println("Pastevorgang abgebrochen: " + this.swCopy.getElapsedTime() + " ms");

			Player player = Bukkit.getPlayer(this.playerName);
			if (player != null) {
				player.sendMessage("Pastevorgang abgebrochen.");
			}
			
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this, this.delay);
		}
	}
	
	
	public void openPasteReader(String name){
		if (this.pastefileExists){
			System.out.println("A paste file is already open!");
			return;
		}
		// open writer
		this.blockFile = new File(this.blocksFolder,name);
		try {
			this.fr = new FileReader(this.blockFile);
			this.br = new BufferedReader(this.fr);
			this.pastefileExists = true;
		} catch (IOException e) {
			this.pastefileExists = false;
			e.printStackTrace();
		}

	}

	public void closePasteReader(){
		if (!this.pastefileExists){
			System.out.println("No paste file is open!");
			return;
		}
		try {
			this.br.close();
			this.fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.pastefileExists = false;
	}
	
	
	private void pasteBlock(int x, int y, int z, int blockId, byte dataValue){
		
		Location loc = this.project.calcPastePositionRelative(this.world, x, y, z);
		if (loc == null)
			return;
		Block b = loc.getBlock();
		if (b == null)
			return;
		b.setTypeId(blockId);
		b.setData(dataValue);
		if (blockId == 0)
			return;
	}
	
	
	
	/*public void openWriter(String name){
		// open writer
		File path = new File(BigCopy.getInstance().getDataFolder(),"paste"+File.separator + name);
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
	}*/
}
