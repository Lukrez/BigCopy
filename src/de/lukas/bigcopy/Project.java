package de.lukas.bigcopy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Project {

	private String projectName;
	private String user;
	private Status status;
	
	private CopyStatus copyStatus;
	private Location pos1;
	private Location pos2;
	private Location copyCenter;
	private Direction copyDirection;
	private CopyTask copyTask;
	private PasteTask pasteTask;
	
	private PasteStatus pasteStatus;
	private Location pasteCenter;
	private Direction pasteDirection;
	
	private int delay;

	private PositionType selectedPositionType;
	
	private boolean showMarker;
	private HashMap<MarkerType,Marker> markers;
	
	private File projectfolder;
	private File copyfolder;
	private File blocksfolder;
	private File inventoriesfolder;
	private File entitysfolder;
	private File pastefolder;
	private File config;
	
	private Rotationmatrix rotationmatrix;
	
	

	public Project(String projectName, Player user) {
		this.projectName = projectName;
		this.user = user.getName();
		this.status = Status.NEW;
		this.copyStatus = CopyStatus.NONE;
		this.pos1 = null;
		this.pos2 = null;
		this.copyCenter = null;
		this.copyDirection = Direction.UNDEFINED;
		this.copyTask = null;
		this.pasteStatus = PasteStatus.NONE;
		this.pasteCenter = null;
		this.pasteDirection = Direction.UNDEFINED;
		this.delay = 20;
		this.showMarker = true;
		this.markers = new HashMap<MarkerType, Marker>();
		

		this.createFolderStructure();
		
		if (!this.loadConfig()){
			user.sendMessage("Fehler beim Laden des Projektes, erstelle DefaultProjektwerte!");
			this.saveConfig();
		}
		this.user = user.getName();
		this.saveConfig();
	}
	
	public void createFolderStructure(){
		
		this.projectfolder = new File(BigCopy.getInstance().getDataFolder(),this.projectName);
		if (!this.projectfolder.exists()){
			this.projectfolder.mkdir();
		}
		this.copyfolder = new File(this.projectfolder,"copy");
		if (!this.copyfolder.exists()){
			this.copyfolder.mkdir();
		}
			this.blocksfolder = new File(this.copyfolder,"blocks");
			if (!this.blocksfolder.exists()){
				this.blocksfolder.mkdir();
			}
			this.inventoriesfolder = new File(this.copyfolder,"inventories");
			if (!this.inventoriesfolder.exists()){
				this.inventoriesfolder.mkdir();
			}
			this.entitysfolder = new File(this.copyfolder,"entities");
			if (!this.entitysfolder.exists()){
				this.entitysfolder.mkdir();
			}
		this.pastefolder = new File(this.projectfolder,"paste");
		if (!this.pastefolder.exists()){
			this.pastefolder.mkdir();
		}
		this.config = new File(this.projectfolder,"config.yml");
		if (!this.config.exists())
			this.saveConfig();
	}
	
	public File getProjectFolder(){
		return this.projectfolder;
	}
	
	public File getCopyFolder(){
		return this.copyfolder;
	}
	public File getPasteFolder(){
		return this.pastefolder;
	}
	
	public boolean saveConfig(){
		
		try {
			

			YamlConfiguration configYml = new YamlConfiguration();
			configYml.set("name", this.projectName);
			configYml.set("user", this.user);
			configYml.set("status", this.status.toString());
			
			configYml.set("copy.status", this.copyStatus.toString());
			configYml.set("copy.p1", Project.LocationToString(this.pos1));
			configYml.set("copy.p2", Project.LocationToString(this.pos2));
			configYml.set("copy.center", Project.LocationToString(this.copyCenter));
			configYml.set("copy.direction", this.copyDirection.toString());
			
			configYml.set("paste.status", this.pasteStatus.toString());
			configYml.set("paste.center", Project.LocationToString(this.pasteCenter));
			configYml.set("paste.direction", this.pasteDirection.toString());
			
			configYml.set("showmarker", this.showMarker);
			
			List<String> makerlist = new ArrayList<String>();
			for (Marker marker : this.markers.values()){
				makerlist.add(marker.toString());
			}
			configYml.set("markers", makerlist);
			
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(this.config));
			out.write(configYml.saveToString().getBytes());
			out.flush();
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean loadConfig(){
		try {
			System.out.println("trying to load config");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(this.config));
			YamlConfiguration ymlConfig = new YamlConfiguration();
			ymlConfig.load(in);
			this.projectName = ymlConfig.getString("name");
			this.user = ymlConfig.getString("user");
			this.status = Status.valueOf(ymlConfig.getString("status"));
			
			this.copyStatus = CopyStatus.valueOf(ymlConfig.getString("copy.status"));
			this.pos1 = Project.parseLocationFromString(ymlConfig.getString("copy.p1"));
			this.pos2 = Project.parseLocationFromString(ymlConfig.getString("copy.p2"));
			this.copyCenter = Project.parseLocationFromString(ymlConfig.getString("copy.center"));
			this.copyDirection = Direction.valueOf(ymlConfig.getString("copy.direction"));

			this.pasteStatus = PasteStatus.valueOf(ymlConfig.getString("paste.status"));
			this.pasteCenter = Project.parseLocationFromString(ymlConfig.getString("paste.center"));
			this.pasteDirection = Direction.valueOf(ymlConfig.getString("paste.direction"));
			this.showMarker = ymlConfig.getBoolean("showmarker");
			
			this.markers.clear();
			for (String s : (List<String>) ymlConfig.getList("markers")){
				Marker marker = new Marker(s);
				this.markers.put(marker.getType(),marker);
			}
			this.toggleMarkers(this.showMarker);

			System.out.println("trying to load config - suscess");
			return true;
			
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setUser(String playerName) {
		this.user = playerName;
		this.saveConfig();
	}

	public String getUser() {
		return user;
	}

	public void resetUser() {
		this.user = "";
		this.saveConfig();
	}

	// Location pos1, Location pos2, Location cm, int delay

	public void setPos1(Location pos1) {
		this.pos1 = pos1;
		this.saveConfig();
	}

	public Location getPos1() {
		return this.pos1;
	}

	public void setPos2(Location pos2) {
		this.pos2 = pos2;
		this.saveConfig();
	}

	public Location getPos2() {
		return this.pos2;
	}

	public void setCopyCenter(Location copyCenter) {
		this.copyCenter = copyCenter;
		this.saveConfig();
	}
	
	public void setCopyDirection(Direction direction){
		this.copyDirection = direction;
		this.saveConfig();
	}

	public Location getCopyCenter() {
		return this.copyCenter;
	}

	public void setPasteCenter(Location pasteCenter) {
		this.pasteCenter = pasteCenter;
		this.saveConfig();
	}
	
	public void setPasteDirection(Direction direction){
		this.pasteDirection = direction;
		this.saveConfig();
	}


	public Location getPasteCenter() {
		return this.pasteCenter;
	}

	public void setDelay(int delay) {
		this.delay = delay;
		this.saveConfig();
	}

	public int getDelay() {
		return delay;
	}

	/*public void startCopyTask() {
		this.cp = new CopyTask(this);
		this.saveConfig();
		Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this.cp, 60);
	}*/
	
	public void startNewCopyTask() {
		this.copyTask = new CopyTask(this);
		this.status = Status.COPYING;
		this.copyStatus = CopyStatus.COPYING;
		this.saveConfig();
		Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this.copyTask, 60);
	}
	
	public boolean resumeCopyTask(){
		if (this.copyTask == null)
			return false;
		Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this.copyTask, 60);
		this.status = Status.COPYING;
		this.copyStatus = CopyStatus.COPYING;
		this.saveConfig();
		return true;
	}
	
	public void stopCopyTask() {
		Bukkit.getScheduler().cancelTask(this.copyTask.getTaskId());
		this.copyTask = null;
		this.copyStatus = CopyStatus.COPYING_PAUSED;
		this.saveConfig();
	}
	
	public Direction getCopyDirection(){
		return this.copyDirection;
	}
	
	public Direction getPasteDirection(){
		return this.pasteDirection;
	}
	
	public String getInfo(){
		String s = "";
		s += "Projectname: "+this.projectName;
		s += "\nUser: "+this.user;
		s += "\nStatus: "+this.status;
		s += "\ndelay: "+this.delay;
		s += "\n\ncopy.status: "+this.copyStatus.toString();
		s += "\ncopy.pos1: "+Project.LocationToString(this.pos1);
		s += "\ncopy.pos2: "+Project.LocationToString(this.pos2);
		s += "\ncopy.center: "+Project.LocationToString(this.copyCenter);
		s += "\ncopy.direction: "+this.copyDirection.toString();
		
		s += "\n\npaste.status: "+this.pasteStatus;
		s += "\npaste.center: "+Project.LocationToString(this.pasteCenter);
		s += "\npaste.direction: "+this.pasteDirection.toString();
		return s;
	}
	
	public void setSelectedPositionType(PositionType selectedPositionType) {
		this.selectedPositionType = selectedPositionType;
	}
	
	public PositionType getSelectedPositionType(){
		return this.selectedPositionType;
	}
	
	public CopyTask getCopyTask(){
		return this.copyTask;
	}
	
	public void toggleMarkers(boolean status){
		this.showMarker = status;
		for (Marker marker : this.markers.values()){
			if (this.showMarker){
				marker.showMarker();
			} else {
				marker.showBlock();
			}
		}
		this.saveConfig();
	}
	
	public void setMarker(Location loc, MarkerType type){
		
		Marker marker = this.markers.get(type);
		if (marker != null){
			marker.showBlock();
		} else {
			marker = new Marker(loc, type);
			this.markers.put(type,marker);
		}
		if (this.showMarker){
			marker.showMarker();
		} else {
			marker.showBlock();
		}
	}
	
	public Marker getMarker(MarkerType type){
		return this.markers.get(type);
	}
	
	public void deleteMarker(MarkerType type){

		Marker marker = this.markers.get(type);
		if (marker != null){
			marker.showBlock();
			this.markers.remove(type);
		}
	}
	
	public static String LocationToString(Location loc){
		if (loc == null)
			return "";
		return loc.getWorld().getName() + ";"+loc.getBlockX() + ";"+loc.getBlockY() + ";"+loc.getBlockZ();
	}
	
	public static Location parseLocationFromString(String s){
		if (s.equals(""))
			return null;
		String[] split = s.split(";");
		if (split.length != 4){
			System.out.println("Error Parsing location: "+s);
			return null;
		}
		World w = BigCopy.getInstance().getServer().getWorld(split[0]);
		int x = Integer.parseInt(split[1]);
		int y = Integer.parseInt(split[2]);
		int z = Integer.parseInt(split[3]);
		return new Location(w,x,y,z);
	}
	
	public void calculateRotationmatrix(){
		if (this.copyDirection == Direction.UNDEFINED || this.pasteDirection == Direction.UNDEFINED){
			this.rotationmatrix = null;
			return;
		}
		this.rotationmatrix = new Rotationmatrix(this.copyDirection, this.pasteDirection);
	}
	
	public Location calcPastePosition(Location a){
		return this.calcPastePosition(a.getWorld(), a.getBlockX(), a.getBlockY(), a.getBlockZ());
	}
	
	
	public Location calcPastePosition(World w, int x, int y, int z){
		
		// calc copyvector
		int ax = x-this.copyCenter.getBlockX();
		int ay = y-this.copyCenter.getBlockY();
		int az = z-this.copyCenter.getBlockZ();
		
		return this.calcPastePositionRelative(w, ax, ay, az);
		
		
	}
	
	public Location calcPastePositionRelative(World w, int x, int y, int z){
		if (this.rotationmatrix == null){
			System.out.println("Keine rotationmatrix");
			return null;
		}
		// calc pastevector
				int bx = this.pasteCenter.getBlockX()+this.rotationmatrix.getA1()*x+this.rotationmatrix.getA2()*z;
				int by = this.pasteCenter.getBlockY()+y;
				int bz = this.pasteCenter.getBlockZ()+this.rotationmatrix.getB1()*x+this.rotationmatrix.getB2()*z;
				return new Location(w,bx,by,bz);
	}
	
	public void startNewPasteTask() {
		this.pasteTask = new PasteTask(this);
		this.status = Status.PASTING;
		this.pasteStatus = PasteStatus.PASTING;
		this.saveConfig();
		Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this.pasteTask, 60);
	}
	
	public boolean resumePasteTask(){
		/*if (this.pasteTask == null)
			return false;*/
		// TODO: Make error checks
		this.pasteTask = new PasteTask(this);
		Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this.pasteTask, 60);
		this.status = Status.PASTING;
		this.pasteStatus = PasteStatus.PASTING;
		this.saveConfig();
		return true;
	}
	
	public void stopPasteTask() {
		Bukkit.getScheduler().cancelTask(this.copyTask.getTaskId());
		this.pasteTask = null;
		this.pasteStatus = PasteStatus.PASTING_PAUSED;
		this.saveConfig();
	}
	
	public Status getStatus(){
		return this.status;
	}
	
	public void setStatus(Status status){
		this.status = status;
	}
	
	public CopyStatus getCopyStatus(){
		return this.copyStatus;
	}
	
	public void setCopyStatus(CopyStatus copyStatus){
		this.copyStatus = copyStatus;
	}
	
	public PasteStatus getPasteStatus(){
		return this.pasteStatus;
	}
	
	public void setPasteStatus(PasteStatus pasteStatus){
		this.pasteStatus = pasteStatus;
	}
	
	
}
