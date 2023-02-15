package kr.mcv.lightfrog.anticheatunit.checks.hitbox;

import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Entity;

public class HitBoxAsPacket extends PacketListenerAbstract {
    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent e) {
        if (e.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity p = new WrappedPacketInUseEntity(e.getNMSPacket());

            final Entity entity = p.getEntity();


        }
    }
}
