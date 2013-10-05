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
	private CopyTask cp;
	
	private PasteStatus pasteStatus;
	private Location pasteCenter;
	private Direction pasteDirection;
	
	private int delay;

	private PositionType selectedPositionType;
	
	private boolean showMarker;
	private HashMap<MarkerType,Marker> markers;
	
	File projectfolder;
	File copyfolder;
	File blocksfolder;
	File inventoriesfolder;
	File entitysfolder;
	File pastefolder;
	File config;
	
	

	public Project(String projectName, Player user) {
		this.projectName = projectName;
		this.user = user.getName();
		this.status = Status.NEW;
		this.copyStatus = CopyStatus.NONE;
		this.pos1 = null;
		this.pos2 = null;
		this.copyCenter = null;
		this.copyDirection = Direction.UNDEFINED;
		this.cp = null;
		this.pasteStatus = PasteStatus.NONE;
		this.pasteCenter = null;
		this.pasteDirection = Direction.UNDEFINED;
		this.delay = 1000;
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
			//TODO: something is not loading
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

	public void startCopyTask() {
		this.cp = new CopyTask(this);
		this.saveConfig();
		Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this.cp, 60);
	}
	
	public void stopCopyTask() {
		this.saveConfig();
		Bukkit.getScheduler().cancelTask(this.cp.getTaskId());
		this.cp = null;
	}
	
	public String getStatus(){
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
		return this.cp;
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
	
	
}
