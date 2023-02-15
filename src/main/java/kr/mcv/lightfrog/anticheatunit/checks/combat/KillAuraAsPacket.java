package kr.mcv.lightfrog.anticheatunit.checks.combat;

import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPIProvider;

public class KillAuraAsPacket extends PacketListenerAbstract {
    private boolean sent = false;
    private long lastFlying = 0L, lastPacket = 0L;
    private double buffer = 0.0d;
    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent e) {
        if (!e.getPlayer().isOp()) {
            killauraPacket:
            {
                killauraPacket_a: {
                    if (e.getPacketId() == PacketType.Play.Client.FLYING) {
                        final long now = System.currentTimeMillis();
                        final long delay = now - lastPacket;

                        if (sent) {
                            if (delay > 40L && delay < 100L) {
                                buffer += 0.25;

                                if (buffer > 0.5) {
                                    e.setCancelled(true);
                                    MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.KILLAURA, "buffer=(" + buffer + "), delay=(" + delay + ")",
                                            "killaura.anticheatunit.packet.a", 3);
                                }
                            } else {
                                buffer = Math.max(buffer - 0.025, 0);
                            }

                            sent = false;
                        }

                        this.lastFlying = now;
                    } else if (e.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
                        final WrappedPacketInUseEntity packet = new WrappedPacketInUseEntity(e.getNMSPacket());

                        if (packet.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                            break killauraPacket_a;
                        }

                        final long now = System.currentTimeMillis();
                        final long delay = now - lastFlying;

                        if (delay < 10L) {
                            lastPacket = now;
                            sent = true;
                        } else {
                            buffer = Math.max(buffer - 0.025, 0.0);
                        }
                    }
                }
            }
        }
    }
}
