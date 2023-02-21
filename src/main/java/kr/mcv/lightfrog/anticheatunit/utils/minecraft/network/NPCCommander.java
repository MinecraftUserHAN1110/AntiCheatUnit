package kr.mcv.lightfrog.anticheatunit.utils.minecraft.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityheadrotation.WrappedPacketOutEntityHeadRotation;
import io.github.retrooper.packetevents.packetwrappers.play.out.namedentityspawn.WrappedPacketOutNamedEntitySpawn;
import io.github.retrooper.packetevents.packetwrappers.play.out.playerinfo.WrappedPacketOutPlayerInfo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NPCCommander implements Listener {
    /* public static NPCCommander INSTANCE;
    private final List<String> nicknames = new ArrayList<>();
    public NPCCommander(JavaPlugin plugin) {
        this.INSTANCE = this;
    }

    public void onJoin(PlayerJoinEvent e) {
        nicknames.add(e.getPlayer().getName());
    }

    public String checkName() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int value = nicknames.toArray().length;

        String nick = nicknames.get(random.nextInt(value));

        return nick;
    }

    public EntityPlayer spawnNPC(Player target, World world) {
        String nickname = checkName();
        MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer ws = ((CraftWorld)world).getHandle();
        GameProfile profile = new GameProfile(target.getUniqueId(), nickname);
        String[] skin = getSkin(nickname);
        profile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        EntityPlayer npc = new EntityPlayer(ms, ws, profile);
        npc.g((EntityLiving) target);
        setVisible(npc, true);
        return npc;
    }

    public void setLocation(EntityPlayer ep, double x, double y, double z) {
        ep.g(x, y, z);
    }
    public void setLocation(Player target, EntityPlayer ep, double x, double y, double z, float yaw, float pitch) {
        ep.g(x, y, z);
        WrappedPacketOutEntityHeadRotation rot = new WrappedPacketOutEntityHeadRotation((Entity) ep, (byte) (yaw * 256 / 360));
        PacketEvents.get().getPlayerUtils().sendPacket(target, rot);
    }
    public void setVisible(Player target, EntityPlayer npc, boolean visible) {
        if(visible) {
            WrappedPacketOutPlayerInfo info = new WrappedPacketOutPlayerInfo(WrappedPacketOutPlayerInfo.PlayerInfoAction.ADD_PLAYER);
            WrappedPacketOutNamedEntitySpawn spawn = new WrappedPacketOutNamedEntitySpawn((Entity) npc);

            PacketEvents.get().getPlayerUtils().sendPacket(target, info);
            PacketEvents.get().getPlayerUtils().sendPacket(target, spawn);
        }else {
            WrappedPacketOutPlayerInfo info = new WrappedPacketOutPlayerInfo(WrappedPacketOutPlayerInfo.PlayerInfoAction.ADD_PLAYER);
            PacketEvents.get().getPlayerUtils().sendPacket(target, info);
        }
    }
    public void setVisible(EntityPlayer ep, boolean visible) {
        for(Player p : Bukkit.getOnlinePlayers())
            if(visible) {
                WrappedPacketOutPlayerInfo info = new WrappedPacketOutPlayerInfo(WrappedPacketOutPlayerInfo.PlayerInfoAction.ADD_PLAYER);
                WrappedPacketOutNamedEntitySpawn spawn = new WrappedPacketOutNamedEntitySpawn((Entity) ep);

                PacketEvents.get().getPlayerUtils().sendPacket(p, info);
                PacketEvents.get().getPlayerUtils().sendPacket(p, spawn);
            }else {
                WrappedPacketOutPlayerInfo info = new WrappedPacketOutPlayerInfo(WrappedPacketOutPlayerInfo.PlayerInfoAction.ADD_PLAYER);
                PacketEvents.get().getPlayerUtils().sendPacket(p, info);
            }
    }

    public String[] getSkin(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader r = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(r).getAsJsonObject().get("id").getAsString();
            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
                    + "?unsigned=false");
            InputStreamReader r2 = new InputStreamReader(url2.openStream());
            JsonObject obj = new JsonParser().parse(r2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String value = obj.get("value").getAsString();
            String signature = obj.get("signature").getAsString();
            return new String[] {value, signature};
        }catch(Exception e) {
            return new String[] {"ewogICJ0aW1lc3RhbXAiIDogMTY3NDM3ODYxMjQ5OCwKICAicHJvZmlsZUlkIiA6ICJmMmFmMzZkMjVlMjU0OWQyYjk4OThhMzMyN2M2NTBmYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJVTklWRVJTRUZSSUVORFMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNjYzI2ODk3NWM5ZGQ2M2ExNjM2YjNlMjg5M2ZmNzg3NDFkNmRkNTcxZTdhYmQ2ZDFjZTJkMWM0MjlhYjcwZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                    "co22ChhNppH8Y3rsMUOUgYrLWEXMrwEzlfYxYcus2ad3d0QbJCM71PBcGvT/7thQWZC/Wmj79VUYrujGCXzA8Mgex3alAUnVvCstGlgvwaybqZAeDoP339lINWWng38JGYoPRUkqSekl6MsB8v51bSxSax4zcux9Uhu5tAhi3dtFsJSshxErbT0zD7jd+OsUHrcTL9PdqvHCWvkvi9odCzXSpXBPaTr5GvJv/jrvV0rBqBpOrBdm8e1wfZRS6Bv7sYt9o44iPN6IcQy1htG8b48PAz1Zq+xxD4u/ebOQoHwUT18qevud3b5/UMzoRLRDxDXfv/J1v/ctWGsph6OLZlh5sNjhew1Qbxr0PvJ9iF/3WGfJp9AE723vgKxLiSdBB/3WgQ/FQwOKH6dnHS7/eWLP/dZv032H+raJg30v9GD7SE7OTJCmpIp4hPM3NUfEWqzDng0GtIzFf+VP+yylI8/vVHV1MhQZGPWua6KmCQ+Zxv9PqyjS4BxCahPNfz//GzUYHTIP839emthLchGKIXMmzYKzf3vH4PGGwEjlKqLzA2HqsGgS4ByApM1BkLRibdCxFdx2+3HDipNgHt43H5rD0Ec2Mdwh6CZW6xa6HJjCnhUVmTAm8vZyoRh46MZU2zicZ+pSJp02RGH44Bt7kPCdEfvnal1CCEeqflf5NaM="};
        }
    } */
}
