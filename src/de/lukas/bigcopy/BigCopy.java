package de.lukas.bigcopy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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

	/* String playerName;
	// private Location pos1;
	// private Location pos2;
	// private Location copymarker;
	// private Location pastemarker;
	// private int taskId;
		private CopyTask cp;*/

	@Override
	public void onDisable() {
		// TODO: Close projects savely.
		// this.getServer().getScheduler().cancelTasks(this);
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
				player.sendMessage("/bigcopy project create <project name>");
				player.sendMessage("/bigcopy project open <project name>");
				player.sendMessage("/bigcopy project close");
				player.sendMessage("/bigcopy status [project name]");
				player.sendMessage("/bigcopy copymarker - copy marker");
				player.sendMessage("/bigcopy pastemarker - paste marker");
				player.sendMessage("/bigcopy pos1 - Set first position");
				player.sendMessage("/bigcopy pos2 - Set second position");
				player.sendMessage("/bigcopy copy - Copy blocks into project folder");
				player.sendMessage("/bigcopy paste - Pastes the structure from project folder");
				return true;
			}

			if (args[0].equalsIgnoreCase("project") || args[0].equalsIgnoreCase("pj")) {
				if (args[1].equalsIgnoreCase("create")) {
					if (args.length == 2) {
						player.sendMessage("Bitte Projektname angeben.");
						return true;
					}

					String projectName = "";
					for (int i = 2; i < args.length; i++) {
						projectName += args[i] + " ";
					}

					Project project = new Project(projectName);
					this.saveProject(project);
					project.setUser(player.getName());
					this.projects.put(player.getName(), project);

					player.sendMessage("Projekt " + projectName + " wurde erstellt und ge�ffnet.");
					return true;
				}

				if (args[1].equalsIgnoreCase("open")) {
					if (args.length == 2) {
						player.sendMessage("Bitte Projektname angeben.");
						return true;
					}

					String projectName = "";
					for (int i = 2; i < args.length; i++) {
						projectName += args[i] + " ";
					}

					Project project = this.loadProject(projectName, player);
					if (project == null)
						return true;
					project.setUser(player.getName());

					player.sendMessage("Das Projekt ist nun offen.");
					return true;
				}

			}

			if (args[0].equalsIgnoreCase("close")) {

				Project project = this.projects.get(player.getName());
				if (project != null) {
					this.saveProject(project);
					this.projects.remove(player.getName());
					player.sendMessage("Das Project wurde geschlossen.");
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("status")) {
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
				player.sendMessage(this.projects.get(player.getName()).getStatus());
				return true;
			}

			Project project = this.projects.get(player.getName());
			if (project == null) {
				player.sendMessage("Du hast keine BigCopy Projekte offen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("copymarker")) {
				project.setSelectedPositionType(PositionType.COPYMARKER);
				player.sendMessage("Bitte copymarker ausw�hlen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("pastemarker")) {
				project.setSelectedPositionType(PositionType.PASTEMARKER);
				player.sendMessage("Bitte pastemarker ausw�hlen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("pos1")) {
				project.setSelectedPositionType(PositionType.POS1);
				player.sendMessage("Bitte pos1 ausw�hlen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("pos2")) {
				project.setSelectedPositionType(PositionType.POS2);
				player.sendMessage("Bitte pos2 ausw�hlen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("copy")) {
				// check validity of config
				if (project.getPos1() == null || project.getPos2() == null || project.getCopyMarker() == null) {
					player.sendMessage("Eine oder mehrere Locations sind nicht gesetzt.");
					return true;
				}
				if (!project.getPos1().getWorld().equals(project.getPos2().getWorld()) || !project.getPos1().getWorld().equals(project.getCopyMarker().getWorld())) {
					player.sendMessage("Eine oder mehrere Locations sind in der falschen Welt.");
					return true;
				}

				project.startCopyTask();
				player.sendMessage("Kopiervorgang gestartet.");
				return true;
			}

			if (args[0].equalsIgnoreCase("stop")) {
				if (project.getCopyTask() == null) {
					player.sendMessage("In diesem Projekt l�uft gerade kein Kopiervorgang.");
					return true;
				}
				project.stopCopyTask();
				player.sendMessage("Kopiervorgang wurde gestoppt.");
				return true;
			}
		}

		return false;
	}

	public void saveProject(Project project) {

	}

	public Project loadProject(String projectName, Player player) {
		for (Project project : this.projects.values()) {
			if (project.getProjectName().equalsIgnoreCase(projectName)) {
				player.sendMessage("Der User " + project.getUser() + " hat das Projekt schon ge�ffnet.");
				return null;
			}
		}

		File fileProject = new File(this.getDataFolder() + File.separator + projectName);
		if (!fileProject.exists()) {
			player.sendMessage("Das Projekt " + projectName + " existiert nicht.");
			return null;
		}

		File fileProjectConfig = new File(this.getDataFolder() + File.separator + projectName, "project.yml");
		if (!fileProjectConfig.exists()) {
			player.sendMessage("Das Projekt " + projectName + " existiert nicht.");
			return null;
		}

		YamlConfiguration yamlProjectConfig = new YamlConfiguration();
		try {
			yamlProjectConfig.load(fileProjectConfig);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		Project project = new Project(projectName);

		if (yamlProjectConfig.contains("pos1")) {
			project.setPos1(LocationParser.parseStringToLocation(yamlProjectConfig.getString("pos1")));
		}
		if (yamlProjectConfig.contains("pos2")) {
			project.setPos2(LocationParser.parseStringToLocation(yamlProjectConfig.getString("pos2")));
		}
		if (yamlProjectConfig.contains("copymarker")) {
			project.setCopyMarker(LocationParser.parseStringToLocation(yamlProjectConfig.getString("copymarker")));
		}

		if (yamlProjectConfig.contains("pastemarker")) {
			project.setPasteMarker(LocationParser.parseStringToLocation(yamlProjectConfig.getString("pastemarker")));
		}

		// delay
		if (yamlProjectConfig.contains("delay")) {
			project.setDelay(Integer.parseInt(yamlProjectConfig.getString("delay")));
		}
		project.setUser(player.getName());
		return project;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block b = event.getClickedBlock();
		Action action = event.getAction();
		ItemStack item = player.getItemInHand();

		Project project = this.projects.get(player.getName());

		if (project != null && project.getSelectedPositionType() != PositionType.DEFAULT && project.getUser().equalsIgnoreCase(player.getName()) && item != null && item.getType() == Material.GOLD_AXE) {
			if (action == Action.RIGHT_CLICK_BLOCK) {
				if (project.getSelectedPositionType() == PositionType.POS1) {
					project.setPos1(b.getLocation());
					b.setType(Material.WOOL);
					b.setData(DyeColor.RED.getWoolData());
				}

				if (project.getSelectedPositionType() == PositionType.POS2) {
					project.setPos2(b.getLocation());
					b.setType(Material.WOOL);
					b.setData(DyeColor.GREEN.getWoolData());
				}

				if (project.getSelectedPositionType() == PositionType.COPYMARKER) {
					project.setCopyMarker(b.getLocation());
					b.setType(Material.WOOL);
					b.setData(DyeColor.YELLOW.getWoolData());
				}

				if (project.getSelectedPositionType() == PositionType.PASTEMARKER) {
					project.setPasteMarker(b.getLocation());
					b.setType(Material.WOOL);
					b.setData(DyeColor.ORANGE.getWoolData());
				}
			} else if (action == Action.RIGHT_CLICK_AIR) {
				if (project.getSelectedPositionType() == PositionType.POS1) {
					project.setPos1(b.getLocation());
					player.getLocation().getBlock().setType(Material.WOOL);
					player.getLocation().getBlock().setData(DyeColor.RED.getWoolData());
				}

				if (project.getSelectedPositionType() == PositionType.POS2) {
					project.setPos2(b.getLocation());
					player.getLocation().getBlock().setType(Material.WOOL);
					player.getLocation().getBlock().setData(DyeColor.GREEN.getWoolData());
				}

				if (project.getSelectedPositionType() == PositionType.COPYMARKER) {
					project.setCopyMarker(b.getLocation());
					player.getLocation().getBlock().setType(Material.WOOL);
					player.getLocation().getBlock().setData(DyeColor.YELLOW.getWoolData());
				}

				if (project.getSelectedPositionType() == PositionType.PASTEMARKER) {
					project.setPasteMarker(b.getLocation());
					player.getLocation().getBlock().setType(Material.WOOL);
					player.getLocation().getBlock().setData(DyeColor.ORANGE.getWoolData());
				}
			}

			player.sendMessage(project.getSelectedPositionType().toString().toLowerCase() + " wurde ausgew�hlt.");
			project.setSelectedPositionType(PositionType.DEFAULT);

			event.setCancelled(true);
		}
	}
}
