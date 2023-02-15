package kr.mcv.lightfrog.anticheatunit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import kr.mcv.lightfrog.anticheatunit.checks.Check;
import kr.mcv.lightfrog.anticheatunit.checks.badpackets.BadPacketsCheck;
import kr.mcv.lightfrog.anticheatunit.checks.combat.KillAuraAsPacket;
import kr.mcv.lightfrog.anticheatunit.checks.combat.KillAuraCheck;
import kr.mcv.lightfrog.anticheatunit.checks.hitbox.HitBoxCheck;
import kr.mcv.lightfrog.anticheatunit.checks.move.MoveCheck;
import me.rerere.matrix.api.MatrixAPI;
import me.rerere.matrix.api.MatrixAPIProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Anticheatunit extends JavaPlugin {

    public static final String NAME = "ACU2 B2";
    public static Anticheatunit INSTANCE;
    private MatrixAPI matrix;
    private ProtocolManager protocolMananger;
    private PacketEvents packetEvent;
    @Override
    public void onEnable() {
        // Plugin startup logic
        if (checkPlugin()) {
            System.out.println("Plugin was disabled!");
            Bukkit.getPluginManager().disablePlugin(this);
            HandlerList.unregisterAll();
            return;
        }
        INSTANCE = this;
        System.out.println("-------------------------------------");
        System.out.println("      WELCOME TO ANTICHEATUNIT       ");
        System.out.println("-------------------------------------");

        saveDefaultConfig();

        if (checkLogin()) {
            System.out.println("Failed to login!");
            System.out.println("Plugin disabled!");
            Bukkit.getPluginManager().disablePlugin(this);
            HandlerList.unregisterAll();
            return;
        }

        PluginManager pm = Bukkit.getPluginManager();
        //new NPCCommander(this);

        protocolMananger = ProtocolLibrary.getProtocolManager();
        System.out.println("ProtocolLib was found!");

        matrix = MatrixAPIProvider.getAPI();
        System.out.println("Matrix was found!");

        PacketEvents.create(this);
        packetEvent = PacketEvents.get();
        packetEvent.registerListener(new BadPacketsCheck());
        packetEvent.registerListener(new KillAuraAsPacket());
        packetEvent.init();
        System.out.println("PacketEvents was found!");

        Check.register(matrix);
        pm.registerEvents(new MoveCheck(), this);
        System.out.println("Speed Check loaded!");
        System.out.println("Fly Check loaded!");
        pm.registerEvents(new HitBoxCheck(), this);
        System.out.println("HitBox Check loaded!");
        pm.registerEvents(new KillAuraCheck(), this);
        System.out.println("KillAura Check loaded!");


        System.out.println("Unloaded ALL Extensions!");

        System.out.println("Thanks for using this plugin");
    }

    @Override
    public void onLoad() {
        PacketEvents.create(this);
        if (checkVersion(ServerVersion.getVersion())) {
            System.out.println("Failed to load Plugin!");
            System.out.println("Reason: Server version is lower (min version is 1.8.8)");
            Bukkit.getPluginManager().disablePlugin(this);
            HandlerList.unregisterAll();
            return;
        }
        PacketEventsSettings settings = PacketEvents.get().getSettings();
        settings.fallbackServerVersion(ServerVersion.getVersion())
                .checkForUpdates(false)
                .bStats(true);
        PacketEvents.get().load();
    }

    private boolean checkVersion(ServerVersion ver) {
        if (ver.isOlderThan(ServerVersion.v_1_8_8)) {
            return true;
        }

        return ver.isNewerThanOrEquals(ServerVersion.v_1_9) && ver.isOlderThanOrEquals(ServerVersion.v_1_12_1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("-------------------------------------");
        System.out.println("      GOOD BYE FROM ANTICHEATUNIT       ");
        System.out.println("-------------------------------------");
        PacketEvents.get().terminate();
        HandlerList.unregisterAll();
        System.out.println("All checks uninjected.");
        System.out.println("Thanks for using this plugin");
    }

    private boolean checkLogin() {
        if (getConfig().getString("login.id").equalsIgnoreCase("HAN1110")) {
            return !getConfig().getString("login.password").equalsIgnoreCase("4569");
        } else if (getConfig().getString("login.id").equalsIgnoreCase("Frog")) {
            return !getConfig().getString("login.password").equalsIgnoreCase("5600");
        } else if (getConfig().getString("login.id").equalsIgnoreCase("Rejomy")) {
            return !getConfig().getString("login.password").equalsIgnoreCase("4993");
        } else if (getConfig().getString("login.id").equalsIgnoreCase("MISHA")) {
            return !getConfig().getString("login.password").equalsIgnoreCase("0045");
        }
        return true;
    }

    public boolean checkPlugin() {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            System.out.println("----------------------------------------");
            System.out.println("        FAILED TO LOAD ANTICHEATUNIT       ");
            System.out.println("-----------------------===--------------");
            Bukkit.getLogger().warning("");
            Bukkit.getLogger().warning("");
            Bukkit.getLogger().warning("REASON: ProtocolLib was disable.");
            Bukkit.getLogger().warning("");
            Bukkit.getLogger().warning("");
            Bukkit.getPluginManager().disablePlugin(this);
            return true;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled("Matrix")) {
            System.out.println("----------------------------------------");
            System.out.println("        FAILED TO LOAD ANTICHEATUNIT       ");
            System.out.println("-----------------------===--------------");
            Bukkit.getLogger().warning("");
            Bukkit.getLogger().warning("");
            Bukkit.getLogger().warning("REASON: Matrix was disable.");
            Bukkit.getLogger().warning("");
            Bukkit.getLogger().warning("");
            return true;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled("packetevents")) {
            System.out.println("----------------------------------------");
            System.out.println("        FAILED TO LOAD ANTICHEATUNIT       ");
            System.out.println("-----------------------===--------------");
            Bukkit.getLogger().warning("");
            Bukkit.getLogger().warning("");
            Bukkit.getLogger().warning("REASON: PacketEvents not loaded.");
            Bukkit.getLogger().warning("");
            Bukkit.getLogger().warning("");
            return true;
        }
        return false;
    }
}
