package kr.mcv.lightfrog.anticheatunit.utils.minecraft.entity;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class ACUPlayer {
    public static final ACUPlayer INSTANCE = new ACUPlayer();

    public boolean isNotOpOrGameMode(Player p) {
        if (p.getGameMode() == GameMode.CREATIVE) {
            return false;
        }

        if (p.getGameMode() == GameMode.SPECTATOR) {
            return false;
        }

        if (!p.isOp()) {
            return false;
        }
        return true;
    }
}
