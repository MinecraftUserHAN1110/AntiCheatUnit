package kr.mcv.lightfrog.anticheatunit.utils.minecraft.block;

import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockManager {
    public static final BlockManager INSTANCE = new BlockManager();

    public boolean isCarpet(Material type) {
        if (type == Material.BLACK_CARPET) {
            return true;
        } else if (type == Material.CYAN_CARPET) {
            return true;
        } else if (type == Material.BLUE_CARPET) {
            return true;
        } else if (type == Material.BROWN_CARPET) {
            return true;
        } else if (type == Material.GRAY_CARPET) {
            return true;
        } else if (type == Material.GREEN_CARPET) {
            return true;
        } else if (type == Material.LIGHT_BLUE_CARPET) {
            return true;
        } else if (type == Material.LIGHT_GRAY_CARPET) {
            return true;
        } else if (type == Material.LIME_CARPET) {
            return true;
        } else if (type == Material.MAGENTA_CARPET) {
            return true;
        } else if (type == Material.MOSS_CARPET) {
            return true;
        } else if (type == Material.ORANGE_CARPET) {
            return true;
        } else if (type == Material.PINK_CARPET) {
            return true;
        } else if (type == Material.PURPLE_CARPET) {
            return true;
        } else if (type == Material.RED_CARPET) {
            return true;
        } else if (type == Material.WHITE_CARPET) {
            return true;
        } else if (type == Material.YELLOW_CARPET) {
            return true;
        } else if (type == Material.LEGACY_CARPET) {
            return true;
        }
        return false;
    }
}
