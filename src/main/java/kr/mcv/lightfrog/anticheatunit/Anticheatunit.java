package kr.mcv.lightfrog.anticheatunit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import kr.mcv.lightfrog.anticheatunit.checks.Check;
import kr.mcv.lightfrog.anticheatunit.checks.killaura.KillAuraCheck;
import kr.mcv.lightfrog.anticheatunit.checks.hitbox.HitBoxCheck;
import kr.mcv.lightfrog.anticheatunit.checks.move.MoveCheck;
import me.rerere.matrix.api.MatrixAPI;
import me.rerere.matrix.api.MatrixAPIProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Anticheatunit extends JavaPlugin {

    public static final String NAME = "ACU3 B4";
    public static Anticheatunit INSTANCE;
    private MatrixAPI matrix;
    private ProtocolManager protocolMananger;

    public ProtocolManager getProtocolManager() {
        return protocolMananger;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        System.out.println("-------------------------------------");
        System.out.println("      WELCOME TO ANTICHEATUNIT       ");
        System.out.println("-------------------------------------");

        saveDefaultConfig();

        PluginManager pm = Bukkit.getPluginManager();
        //new NPCCommander(this);

        protocolMananger = ProtocolLibrary.getProtocolManager();
        System.out.println("ProtocolLib was found!");

        matrix = MatrixAPIProvider.getAPI();
        System.out.println("Matrix was found!");

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
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("-------------------------------------");
        System.out.println("      GOOD BYE FROM ANTICHEATUNIT       ");
        System.out.println("-------------------------------------");
        HandlerList.unregisterAll();
        System.out.println("All checks uninjected.");
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
