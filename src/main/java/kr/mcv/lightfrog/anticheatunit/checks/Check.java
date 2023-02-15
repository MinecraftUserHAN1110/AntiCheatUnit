package kr.mcv.lightfrog.anticheatunit.checks;

import kr.mcv.lightfrog.anticheatunit.api.EventType;
import kr.mcv.lightfrog.anticheatunit.api.impl.ACUViolationEvent;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.server.Component;
import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Check {
    private static MatrixAPI matrixAPI;
    public static void register(MatrixAPI matrixAPI) {
        Check.matrixAPI = matrixAPI;
    }

    private HackType type;

    protected Check(HackType type) {
        this.type = type;
    }

    protected void flag(Player target, HackType type, String data, Component component, int violationLevel) {
        if (!(target.getGameMode() == GameMode.CREATIVE || target.getGameMode() == GameMode.SPECTATOR || target.isOp())) {
            ACUViolationEvent event = new ACUViolationEvent();
            event.setType(EventType.PRE);

            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            matrixAPI.flag(target, type, data, component.getData(), violationLevel);

            event.setType(EventType.POST);

            if (event.isCancelled()) {
                return;
            }
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public HackType getType() {
        return type;
    }

    public MatrixAPI getAPI() {
        return matrixAPI;
    }

    public void setType(HackType type) {
        this.type = type;
    }
}
