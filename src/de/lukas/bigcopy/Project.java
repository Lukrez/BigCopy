package de.lukas.bigcopy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.io.File;

public class Project {

	private String projectName;
	private String user;
	private Location pos1;
	private Location pos2;
	private Location copyMarker;
	private Location pasteMarker;
	private int delay;
	private CopyTask cp;
	private PositionType selectedPositionType; 

	public Project(String projectName) {
		this.projectName = projectName;
		this.selectedPositionType = PositionType.DEFAULT;
	}
	
	public void createFolderStructure(){
		
		File  projectfolder = new File(BigCopy.getInstance().getDataFolder(),this.projectName);
		if (!projectfolder.exists()){
			projectfolder.mkdir();
		}
		File  copyfolder = new File(projectfolder,"copy");
		if (!copyfolder.exists()){
			copyfolder.mkdir();
		}
			File  blocksfolder = new File(copyfolder,"blocks");
			if (!blocksfolder.exists()){
				blocksfolder.mkdir();
			}
			File  inventoriesfolder = new File(copyfolder,"inventories");
			if (!inventoriesfolder.exists()){
				inventoriesfolder.mkdir();
			}
			File  entitysfolder = new File(copyfolder,"entities");
			if (!entitysfolder.exists()){
				entitysfolder.mkdir();
			}
		File  pastefolder = new File(projectfolder,"paste");
		if (!pastefolder.exists()){
			pastefolder.mkdir();
		}
		/*TODO: create config*/
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setUser(String playerName) {
		this.user = playerName;
	}

	public String getUser() {
		return user;
	}

	public void resetUser() {
		this.user = "";
	}

	// Location pos1, Location pos2, Location cm, int delay

	public void setPos1(Location pos1) {
		this.pos1 = pos1;
	}

	public Location getPos1() {
		return this.pos1;
	}

	public void setPos2(Location pos2) {
		this.pos2 = pos2;
	}

	public Location getPos2() {
		return this.pos2;
	}

	public void setCopyMarker(Location copyMarker) {
		this.copyMarker = copyMarker;
	}

	public Location getCopyMarker() {
		return this.copyMarker;
	}

	public void setPasteMarker(Location pasteMarker) {
		this.pasteMarker = pasteMarker;
	}

	public Location getPasteMarker() {
		return this.pasteMarker;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}

	public void startCopyTask() {
		this.cp = new CopyTask(this);
		Bukkit.getScheduler().scheduleSyncDelayedTask(BigCopy.getInstance(), this.cp, 60);
	}
	
	public void stopCopyTask() {
		Bukkit.getScheduler().cancelTask(this.cp.getTaskId());
		this.cp = null;
	}
	
	public String getStatus(){
		return "";
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
	
	
}
