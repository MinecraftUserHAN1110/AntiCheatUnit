package kr.mcv.lightfrog.anticheatunit.commands;

import kr.mcv.lightfrog.anticheatunit.Anticheatunit;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.network.NPCCommander;
import kr.mcv.lightfrog.anticheatunit.utils.system.Timer;
import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPIProvider;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AnticheatUnitCommand implements CommandExecutor {
    private FileConfiguration config = null;
    private File file = null;
    private void reload(CommandSender p) {
        try {
            if (file == null) {
                file = new File(Anticheatunit.INSTANCE.getDataFolder(), "config.yml");
            }

            config = YamlConfiguration.loadConfiguration(this.file);

            InputStream defaultStream = Anticheatunit.INSTANCE.getResource("config.yml");
            if (defaultStream == null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
                config.setDefaults(defaultConfig);
            }
            p.sendMessage(ChatColor.GREEN + "Sucucess load config!");
        } catch (Exception localException) {
            p.sendMessage(ChatColor.RED + "Failed to load config");
            localException.printStackTrace();
        }
    }

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param arg   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String arg, @NotNull String[] args) {
        if (sender.isOp()) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    reload(sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage(ChatColor.GREEN + "ACU: " + Anticheatunit.NAME);
                    return true;
                }
                if (args[0].equalsIgnoreCase("forceviolate")) {
                    if (args.length > 1) {
                        if (args.length > 2) {
                            Player target = this.getPlayer(args[1]);
                            if (target != null) {
                                try {
                                    int violation = Integer.parseInt(args[2]);
                                    MatrixAPIProvider.getAPI().flag(target, HackType.BADPACKETS, sender.getName() + " use anticheatunit's violation command]",
                                            "badpackets.anticheatunit.command", violation);
                                    sender.sendMessage(ChatColor.GREEN + "Sucuess " + violation + " to " + target + "as a badpackets!");
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(ChatColor.RED + "Please put only number! (integer)");
                                }
                                return true;
                            }
                            sender.sendMessage(ChatColor.RED + "AntiCheatUnit can't player find " + args[1]);
                            return true;
                        }
                        sender.sendMessage(ChatColor.RED + "Please set violation value");
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Please set player's name");
                    return true;
                }
            }

            sender.sendMessage(ChatColor.GREEN + "ANTICHEATUNIT: ADVANCED ANTICHEAT ADDON");
            sender.sendMessage(ChatColor.GRAY + "/anticheatunit reload");
            sender.sendMessage(ChatColor.GRAY + "reload anticheatunit (matrix is partial) config");
            sender.sendMessage(ChatColor.RED + "/anticheatunit version");
            sender.sendMessage(ChatColor.RED + "print anticheatunit version");
            sender.sendMessage(ChatColor.GOLD + "/anticheatunit forceviolate %player% %value%");
            sender.sendMessage(ChatColor.GOLD + "Add %value% Violation %player%");
            return true;
        }
    }

    private Player getPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
}
