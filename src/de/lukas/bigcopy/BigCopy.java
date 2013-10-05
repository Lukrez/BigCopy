package de.lukas.bigcopy;

import java.io.File;
import java.util.HashMap;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BigCopy extends JavaPlugin implements Listener {

	private static BigCopy instance;

	private HashMap<String, Project> projects;

	@Override
	public void onDisable() {
		// TODO: Close projects savely.
		// this.getServer().getScheduler().cancelTasks(this);
		for (Project project : this.projects.values()){
			this.closeProject(project);
		}
		this.getLogger().info("v" + this.getDescription().getVersion() + " disabled.");
	}

	@Override
	public void onEnable() {
		BigCopy.instance = this;
		this.projects = new HashMap<String, Project>();
		this.getServer().getPluginManager().registerEvents(this, this);

		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdir();

		this.getLogger().info("v" + this.getDescription().getVersion() + " enabled.");
	}

	public static BigCopy getInstance() {
		return instance;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {

			return true;
		}

		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("bigcopy")) {
			if (args.length == 0) {
				player.sendMessage("---Help of BigCopy---");
				player.sendMessage("/bigcopy create <project name>");
				player.sendMessage("/bigcopy open <project name>");
				player.sendMessage("/bigcopy close");
				player.sendMessage("/bigcopy list - List all existing Projects");
				player.sendMessage("/bigcopy status [project name]");
				player.sendMessage("/bigcopy pos1 - Set first position");
				player.sendMessage("/bigcopy pos2 - Set second position");				
				player.sendMessage("/bigcopy copy - Copy blocks into project folder, uses position and direction as reference");
				player.sendMessage("/bigcopy paste - Pastes the structure from project folder, uses position and direction as reference");
				player.sendMessage("/bigcopy stop - Stop Copyprocess");
				player.sendMessage("/bigcopy resume - Restart Copyprocess");
				player.sendMessage("/bigcopy marker <ON|OFF> - Toggle Positionmarker");
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length == 2) {
					player.sendMessage("Bitte Projektname angeben.");
					return true;
				}

				String projectName = "";
				for (int i = 2; i < args.length; i++) {
					projectName += args[i] + " ";
				}
				this.createProject(projectName, player);
			
				return true;
			}

			if (args[0].equalsIgnoreCase("open")) {
				if (args.length == 1) {
					player.sendMessage("Bitte Projektname angeben.");
					return true;
				}

				String projectName = "";
				for (int i = 1; i < args.length; i++) {
					projectName += args[i] + " ";
				}

				this.loadProject(projectName, player);

				return true;
			}

			if (args[0].equalsIgnoreCase("close")) {

				Project project = this.projects.get(player.getName());
				if (project != null) {
					this.closeProject(project);
					this.projects.remove(player.getName());
					player.sendMessage("Das Project wurde geschlossen.");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("status")) {
				
				if (this.projects.size() == 0){
					player.sendMessage("Es sind keine BigCopy Projekte geöffnet!");
					return true;
				}
				
				String projectName = "";
				for (int i = 1; i < args.length; i++) {
					projectName += args[i] + " ";
				}
				for (Project project : this.projects.values()) {
					if (project.getProjectName().equalsIgnoreCase(projectName)) {
						player.sendMessage(project.getStatus());
						return true;
					}
				}
				Project project = this.projects.get(player.getName());
				if (project != null){
					player.sendMessage(project.getStatus());
					return true;
				}
				
				player.sendMessage("Folgende BigCopy Projekte geöffnet:");
				for (Project projectAll : this.projects.values()){
					player.sendMessage(projectAll.getProjectName()+"\n");
				}
				return true;
			}
			
			Project project = this.projects.get(player.getName());
			if (project == null) {
				player.sendMessage("Du hast keine BigCopy Projekte offen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("pos1")) {
				// reset marker data
				project.setPos1(null);
				project.deleteMarker(MarkerType.Pos1);

				project.setSelectedPositionType(PositionType.POS1);
				player.sendMessage("Bitte pos1 auswählen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("pos2")) {
				// reset marker data
				project.setPos2(null);
				project.deleteMarker(MarkerType.Pos2);
				project.setSelectedPositionType(PositionType.POS2);
				player.sendMessage("Bitte pos2 auswählen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("copy")) {
				// check validity of config
				if (project.getPos1() == null){
					player.sendMessage("Location 1 ist nicht gesetzt");
					return true;
				}
				if (project.getPos2() == null){
					player.sendMessage("Location 2 ist nicht gesetzt");
					return true;
				}
				if (!project.getPos1().getWorld().equals(project.getPos2().getWorld()) || !player.getWorld().equals(project.getPos1().getWorld())) {
					player.sendMessage("Eine oder mehrere Locations sind in der falschen Welt.");
					return true;
				}
				
				// Get CopyCenterPosition and Playerdirection
				Location copyCenter = player.getLocation().subtract(0, 1, 0);
				project.setCopyCenter(copyCenter);
				project.setMarker(copyCenter, MarkerType.CopyCenter);
				//TODO: Calculate Positions
				
				Direction direction = Direction.parseDirection(player.getLocation());
				if (direction == Direction.UNDEFINED){
					player.sendMessage("Bitte wähle eine eindeutige Richtung zum Kopieren aus.");
					return true;
				}
				
				project.setCopyDirection(direction);

				//project.startCopyTask();
				player.sendMessage("Kopiervorgang gestartet.");
				return true;
			}

			if (args[0].equalsIgnoreCase("stop")) {
				if (project.getCopyTask() == null) { // TODO: stop paste
					player.sendMessage("In diesem Projekt läuft gerade kein Kopiervorgang.");
					return true;
				}
				project.stopCopyTask();
				player.sendMessage("Kopiervorgang wurde gestoppt.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("marker")) {
				if (args.length != 2){
					player.sendMessage("Bitte einen Markerstatus angeben (ON, OFF)");
					return true;
				}
				if (args[1].equals("ON")){
					project.toggleMarkers(true);
					player.sendMessage("Marker werden angezeigt.");
				} else if (args[1].equals("OFF")){
					project.toggleMarkers(false);
					player.sendMessage("Marker wurden durch Originalblöcke ersetzt.");
				} else {
					player.sendMessage("Bitte einen Markerstatus angeben (ON, OFF)");
				}
				return true;
			}
		}

		return false;
	}
	
	public void createProject(String projectName, Player player){
		File fileProject = new File(this.getDataFolder() + File.separator + projectName);
		if (fileProject.exists()){
			player.sendMessage("Projekt " + projectName + " existiert bereits.");
		}
		
		Project project = new Project(projectName, player);
		this.projects.put(player.getName(), project);
		player.sendMessage("Projekt " + projectName + " wurde erstellt und geöffnet.");
	}


	public void loadProject(String projectName, Player player) {
		for (Project project : this.projects.values()) {
			if (project.getProjectName().equalsIgnoreCase(projectName)) {
				player.sendMessage("Der User " + project.getUser() + " hat das Projekt schon geöffnet.");
				return;
			}
		}

		File fileProject = new File(this.getDataFolder() + File.separator + projectName);
		if (!fileProject.exists()) {
			player.sendMessage("Das Projekt " + projectName + " existiert nicht.");
			return;
		}

		Project project = new Project(projectName, player);
		this.projects.put(project.getUser(), project);
		player.sendMessage("Projekt " + project.getProjectName() + " wurde geladen.");
	}
	
	public void closeProject(Project project) {
		// TODO: stop  all tasks
		project.toggleMarkers(false);
		project.saveConfig();	
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block b = event.getClickedBlock();
		Action action = event.getAction();
		ItemStack item = player.getItemInHand();

		Project project = this.projects.get(player.getName());

		if (project != null && project.getSelectedPositionType() != PositionType.DEFAULT && project.getUser().equalsIgnoreCase(player.getName()) && item != null && item.getType() == Material.GOLD_AXE) {
			Location loc;
			if (action == Action.RIGHT_CLICK_AIR) {
				loc = player.getLocation();
			} else {
				loc = b.getLocation();
			}
			
			if (project.getSelectedPositionType() == PositionType.POS1) {
				project.setPos1(loc);
				project.setMarker(loc, MarkerType.Pos1);
			}

			if (project.getSelectedPositionType() == PositionType.POS2) {
				project.setPos2(loc);
				project.setMarker(loc, MarkerType.Pos2);
			}

			player.sendMessage(project.getSelectedPositionType().toString().toLowerCase() + " wurde ausgewählt.");
			project.setSelectedPositionType(PositionType.DEFAULT);

			event.setCancelled(true);
		}
	}
}
