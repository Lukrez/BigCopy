package de.lukas.bigcopy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
	
	
	//public int pasteAtX;
	//public int pasteAtY;
	//public int pasteAtZ;
	public int logAtX;
	public int logAtY;
	public int logAtZ;
	

	public World world;
	public boolean pasteFinished;
	public boolean pasteAbort;
	public int delay = 20;
	private Stopwatch swCopy;
	
	
	private File copyFolder;
	private File copyBlocksFolder;
	private File copyInventoriesFolder;
	private File copyEntititesFolder;
	
	private File copyBlockFile;
	public FileReader copyBlockFileReader;
	public BufferedReader copyBlockBufferedReader;
	private boolean copyBlockFileExists;

	
	private File pasteFolder;
	private File pasteBlocksFolder;
	private File pasteInventoriesFolder;
	private File pasteEntititesFolder;
	
	private File pasteBlockFile;
	public FileWriter pasteBlockFileWriter;
	public BufferedWriter pasteBlockBufferedWriter;
	private boolean pasteBlockFileOpen;
	
	private boolean makeStone;
	private boolean groundLayer;
	
	private Blockrotation blockrotation;
	private int rotationtimes;

	public PasteTask(Project project) {
		this.project = project;
		this.world = project.getPasteCenter().getWorld();
		this.delay = project.getDelay();
		this.playerName = project.getUser();
		this.pasteCenterX = project.getPasteCenter().getBlockX();
		this.pasteCenterY = project.getPasteCenter().getBlockY();
		this.pasteCenterZ = project.getPasteCenter().getBlockZ();
		
		// define positions
		
		Location p1 =  project.calcPastePosition(project.getPos1());
		Location p2 =  project.calcPastePosition(project.getPos2());
		if (p1 == null || p2  == null){
			System.out.println("Pasteposition ist null!");
			return;
		}
		int cX1,cX2,cY1,cY2,cZ1,cZ2;
		
		if (p1.getBlockX() < p2.getBlockX()) {
			cX1 = p1.getBlockX();
			cX2 = p2.getBlockX();
		} else {
			cX1 = p2.getBlockX();
			cX2 = p1.getBlockX();
		}
		if (p1.getBlockY() < p2.getBlockY()) {
			cY1 = p1.getBlockY();
			cY2 = p2.getBlockY();
		} else {
			cY1 = p2.getBlockY();
			cY2 = p1.getBlockY();
		}
		if (p1.getBlockZ() < p2.getBlockZ()) {
			cZ1 = p1.getBlockZ();
			cZ2 = p2.getBlockZ();
		} else {
			cZ1 = p2.getBlockZ();
			cZ2 = p1.getBlockZ();
		}
		
		
		Location minPaste =  new Location(this.world, cX1, cY1, cZ1);
		Location maxPaste =  new Location(this.world, cX2, cY2, cZ2);
		
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
		this.copyBlockFileExists = false;
		
		this.copyFolder = project.getCopyFolder();
		if (!this.copyFolder.exists()){
			System.out.println("Copyordner existiert nicht!");
			this.pasteAbort = true;
			return;
		}
		this.copyBlocksFolder = new File(this.copyFolder,"blocks");
		if (!this.copyBlocksFolder.exists()){
			System.out.println("Blockordner existiert nicht!");
			this.pasteAbort = true;
			return;
		}
		this.copyInventoriesFolder = new File(this.copyFolder,"inventories");
		if (!this.copyInventoriesFolder.exists()){
			System.out.println("Inventoryordner existiert nicht!");
			return;
		}
		this.copyEntititesFolder = new File(this.copyFolder,"entities");
		if (!this.copyEntititesFolder.exists()){
			System.out.println("Entitiyordner existiert nicht!");
			this.pasteAbort = true;
			return;
		}
		
		
		this.pasteFolder = project.getPasteFolder();
		this.pasteBlocksFolder = new File(this.pasteFolder,"blocks");
		if (!this.pasteBlocksFolder.exists()){
			this.pasteBlocksFolder.mkdir();
		}
		this.pasteInventoriesFolder = new File(this.pasteFolder,"inventories");
		if (!this.pasteInventoriesFolder.exists()){
			this.pasteInventoriesFolder.mkdir();
		}
		this.pasteEntititesFolder = new File(this.pasteFolder,"entities");
		if (!this.pasteEntititesFolder.exists()){
			this.pasteEntititesFolder.mkdir();
		}
		
		// check paste file existence
		for (int y = this.Y1; y <= this.Y2;y++){
			String name = (y-this.pasteCenterY)+".txt";
			File yExists = new File(this.copyBlocksFolder,name);
			if (!yExists.exists()){
				System.out.println("Blockfile "+name+" existiert nicht!");
				this.pasteAbort = true;
				return;
			}
		}
		
		

		this.openCopyBlockReader((this.Y1-this.pasteCenterY)+".txt");
		this.openPasteBlockWriter(this.Y1+".tx");
		this.atX = this.X1-1;
		this.atY = this.Y1-1;
		this.atZ = this.Z1;
		
		this.makeStone = true;
		this.groundLayer = true;
		
		// create blockrotation class
		this.blockrotation = new Blockrotation();
		
		// calulate rotationtimes
		this.rotationtimes = this.project.getCopyDirection().getNr()-this.project.getPasteDirection().getNr();
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
			
			if (this.makeStone){
				this.atX++;
				if (this.atX > this.X2) {
					this.atX = this.X1;
					this.atZ++;
				}
				if (this.atZ > this.Z2) {
					this.atZ = this.Z1;
					this.atX = this.X1-1;
					if (this.groundLayer){
						this.groundLayer = false;
						this.atY++;
						this.closePasteBlockWriter();
						this.openPasteBlockWriter(this.atY+1+".txt");
					} else {
						this.makeStone = false;
					}
					continue;
				}
				if (this.atY < this.Y2){
					this.removeOriginalBlock(this.atX, this.atY+1, this.atZ);					
				}
				continue;
			}
			
			String line = null;
			try {
				line = this.copyBlockBufferedReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				this.pasteAbort = true;
				continue;
			}
			if (line == null){
				this.closeCopyBlockReader();
				
				this.atY++;
				if (this.atY > this.Y2){
					this.pasteFinished = true;
					continue;
				}
				this.openCopyBlockReader((this.atY-this.pasteCenterY)+".txt");
				if (this.atY < this.Y2){
					this.atX = this.X1-1;
					this.atZ = this.Z1;
					this.makeStone = true;
					this.closePasteBlockWriter();
					this.openPasteBlockWriter(this.atY+1+".txt");
				}
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
		//System.out.println(sw.getElapsedTime());

		if (this.pasteFinished) {
			this.closeCopyBlockReader();
			this.swCopy.stop();
			System.out.println("Pastevorgang brauchte: " + this.swCopy.getElapsedTime() + " ms");

			Player player = Bukkit.getPlayer(this.playerName);
			if (player != null) {
				player.sendMessage("Pastevorgang erledigt.");
			}
		} else if (this.pasteAbort) {
			this.closeCopyBlockReader();
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
	
	
	public void openCopyBlockReader(String name){
		if (this.copyBlockFileExists){
			System.out.println("A paste file is already open!");
			return;
		}
		// open writer
		this.copyBlockFile = new File(this.copyBlocksFolder,name);
		try {
			this.copyBlockFileReader = new FileReader(this.copyBlockFile);
			this.copyBlockBufferedReader = new BufferedReader(this.copyBlockFileReader);
			this.copyBlockFileExists = true;
		} catch (IOException e) {
			this.copyBlockFileExists = false;
			e.printStackTrace();
		}

	}

	public void closeCopyBlockReader(){
		if (!this.copyBlockFileExists){
			System.out.println("No paste file is open!");
			return;
		}
		try {
			this.copyBlockBufferedReader.close();
			this.copyBlockFileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.copyBlockFileExists = false;
	}
	
	
	private void pasteBlock(int x, int y, int z, int blockId, byte dataValue){
		
		Location loc = this.project.calcPastePositionRelative(this.world, x, y, z);
		if (loc == null)
			return;
		Block b = loc.getBlock();
		if (b == null)
			return;
		dataValue = this.blockrotation.getRotatedDataValue(blockId, dataValue, this.rotationtimes);
		b.setTypeId(blockId);
		b.setData(dataValue);
		if (blockId == 0)
			return;
	}
	
	private void removeOriginalBlock(int x, int y, int z){
		Location loc = new Location(this.world, x, y, z);
		Block b = loc.getBlock();
		if (b == null)
			return;
		String s = "";
		s += x + ":";
		s += y + ":";
		s += z + ":";
		
		s += b.getTypeId() + ":";
		s += b.getData() + "\n";
		
		try {
			this.pasteBlockBufferedWriter.write(s);
		} catch (IOException e) {
			//TODO: fehler in log schreiben 
			this.pasteAbort = true;
			this.project.setPasteStatus(PasteStatus.ERROR_WHILE_PASTING);
			e.printStackTrace();
			return;
		}
		
		// set Block to Stone
		b.setType(Material.STONE);
		b.setData((byte) 0);
		
	}
	
	

	
	public void openPasteBlockWriter(String name){
		if (this.pasteBlockFileOpen){
			System.out.println("A writer is already open");
			return;
		}
		// open writer
		System.out.println("opening pasteBlockWriter: "+name);
		this.pasteBlockFile = new File(this.pasteBlocksFolder,name);
		try {
			this.pasteBlockFileWriter = new FileWriter(this.pasteBlockFile);
			System.out.println("new blockfile "+this.pasteBlockFile.getAbsolutePath());
			this.pasteBlockBufferedWriter = new BufferedWriter(this.pasteBlockFileWriter);
			this.pasteBlockFileOpen = true;
		} catch (IOException e) {
			this.pasteAbort = true;
			this.project.setPasteStatus(PasteStatus.ERROR_WHILE_PASTING);
			e.printStackTrace();
		}
	}
	
	public void flushPasteBlockWriter(){
		if (!this.pasteBlockFileOpen)
			return;
		System.out.println("flushing pasteBlockWriter: ");
		try {
			this.pasteBlockBufferedWriter.flush();
		} catch (IOException e) {
			this.pasteAbort = true;
			this.project.setPasteStatus(PasteStatus.ERROR_WHILE_PASTING);
			e.printStackTrace();
		}
	}
	
	public void closePasteBlockWriter(){
		if (!this.pasteBlockFileOpen){
			System.out.println("No writer needs closing.");
			return;
		}
		this.flushPasteBlockWriter();
		System.out.println("closing pasteBlockWriter: ");
		try {
			this.pasteBlockBufferedWriter.close();
			this.pasteBlockFileWriter.close();
			System.out.println("closing blockfile "+this.pasteBlockFile.getAbsolutePath());
			this.pasteBlockFileOpen = false;
		} catch (IOException e) {
			this.pasteAbort = true;
			this.project.setPasteStatus(PasteStatus.ERROR_WHILE_PASTING);
			e.printStackTrace();
		}
	}
}
