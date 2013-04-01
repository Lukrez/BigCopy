package de.lukas.bigcopy;

import org.bukkit.Bukkit;
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
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class BigCopy extends JavaPlugin implements Listener {

	private static BigCopy instance;

	String playerName;
	private Location pos1;
	private Location pos2;
	private Location copymarker;
	private Location pastemarker;
	private int taskId;
	private CopyTask cp;

	public static int counter = 0;

	private enum PositionTypes {
		POS1, POS2, COPYMARKER, PASTEMARKER, DEFAULT
	};

	private PositionTypes ptype;

	@Override
	public void onDisable() {
		this.getLogger().info("v" + this.getDescription().getVersion() + " disabled.");
	}

	@Override
	public void onEnable() {
		this.instance = this;
		this.playerName = "";
		this.ptype = PositionTypes.DEFAULT;
		this.getServer().getPluginManager().registerEvents(this, this);

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
				player.sendMessage("/bigcopy login");
				player.sendMessage("/bigcopy logout");
				player.sendMessage("/bigcopy status");
				player.sendMessage("/bigcopy copymarker - copy marker");
				player.sendMessage("/bigcopy pastemarker - paste marker");
				player.sendMessage("/bigcopy pos1 - Set first position");
				player.sendMessage("/bigcopy pos2 - Set second position");
				player.sendMessage("/bigcopy copy <file name> - Copy blocks into file");
				player.sendMessage("/bigcopy paste <file name> - Pastes the structure from file");
				return true;
			}

			if (args[0].equalsIgnoreCase("login")) {
				this.playerName = player.getName();
				player.sendMessage("You are now logged into bigcopy.");
				return true;
			}

			if (args[0].equalsIgnoreCase("logout")) {
				this.playerName = "";
				player.sendMessage("Du bist nun ausgeloggt.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("status")) {
				if (this.cp != null)
					player.sendMessage(this.cp.getStatus());
				return true;
			}

			if (!this.playerName.equalsIgnoreCase(player.getName())) {
				player.sendMessage("Bitte einloggen: /bigcopy login");
				return true;
			}

			if (args[0].equalsIgnoreCase("copymarker")) {
				this.ptype = PositionTypes.COPYMARKER;
				player.sendMessage("Bitte copymarker auswählen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("pastemarker")) {
				this.ptype = PositionTypes.PASTEMARKER;
				player.sendMessage("Bitte pastemarker auswählen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("pos1")) {
				this.ptype = PositionTypes.POS1;
				player.sendMessage("Bitte pos1 auswählen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("pos2")) {
				this.ptype = PositionTypes.POS2;
				player.sendMessage("Bitte pos2 auswählen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("copy")) {
				// check locations
				if (pos1 == null || pos2 == null || copymarker == null){
					player.sendMessage("eine oder mehrere Locations sind nicht gesetzt.");
					return true;
				}
				if (!pos1.getWorld().equals(pos2.getWorld()) || !pos1.getWorld().equals(copymarker.getWorld())){
					player.sendMessage("eine oder mehrere Locations sind in der falschen Welt.");
					return true;
				}
				this.cp = new CopyTask(playerName, pos1, pos2, copymarker, 5);
			
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, this.cp, 60);
				player.sendMessage("Kopiervorgang gestartet.");
				return true;
			}

			if (args[0].equalsIgnoreCase("stop")) {
				this.getServer().getScheduler().cancelTask(this.taskId);
				player.sendMessage("Task wurde gekillt.");
				return true;
			}
		}

		return false;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block b = event.getClickedBlock();
		Action action = event.getAction();
		ItemStack item = player.getItemInHand();

		if (this.ptype != PositionTypes.DEFAULT && this.playerName.equalsIgnoreCase(player.getName()) && item != null && item.getType() == Material.GOLD_AXE) {
			if (action == Action.RIGHT_CLICK_BLOCK) {
				if (this.ptype == PositionTypes.POS1) {
					this.pos1 = b.getLocation();
					b.setType(Material.WOOL);
					b.setData(DyeColor.RED.getWoolData());
				}

				if (this.ptype == PositionTypes.POS2) {
					this.pos2 = b.getLocation();
					b.setType(Material.WOOL);
					b.setData(DyeColor.GREEN.getWoolData());
				}

				if (this.ptype == PositionTypes.COPYMARKER) {
					b.setType(Material.WOOL);
					b.setData(DyeColor.YELLOW.getWoolData());
					this.copymarker = b.getLocation();
				}

				if (this.ptype == PositionTypes.PASTEMARKER) {
					b.setType(Material.WOOL);
					b.setData(DyeColor.ORANGE.getWoolData());
					this.pastemarker = b.getLocation();
				}
			} else if (action == Action.RIGHT_CLICK_AIR) {
				if (this.ptype == PositionTypes.POS1) {
					this.pos1 = player.getLocation();
					player.getLocation().getBlock().setType(Material.WOOL);
					player.getLocation().getBlock().setData(DyeColor.RED.getWoolData());
				}

				if (this.ptype == PositionTypes.POS2) {
					this.pos2 = player.getLocation();
					player.getLocation().getBlock().setType(Material.WOOL);
					player.getLocation().getBlock().setData(DyeColor.GREEN.getWoolData());
				}

				if (this.ptype == PositionTypes.COPYMARKER) {
					player.getLocation().getBlock().setType(Material.WOOL);
					player.getLocation().getBlock().setData(DyeColor.YELLOW.getWoolData());
					this.copymarker = player.getLocation();
				}

				if (this.ptype == PositionTypes.PASTEMARKER) {
					player.getLocation().getBlock().setType(Material.WOOL);
					player.getLocation().getBlock().setData(DyeColor.ORANGE.getWoolData());
					this.pastemarker = player.getLocation();
				}
			}

			player.sendMessage(this.ptype.toString().toLowerCase() + " wurde ausgewählt.");
			this.ptype = PositionTypes.DEFAULT;

			event.setCancelled(true);
		}
	}
}
