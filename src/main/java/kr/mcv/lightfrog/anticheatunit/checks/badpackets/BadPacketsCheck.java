package kr.mcv.lightfrog.anticheatunit.checks.badpackets;

import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.abilities.WrappedPacketInAbilities;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;
import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive;
import io.github.retrooper.packetevents.utils.player.Direction;
import kr.mcv.lightfrog.anticheatunit.utils.system.Pair;
import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPIProvider;
import org.bukkit.GameMode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BadPacketsCheck extends PacketListenerAbstract {
    // BadPackets A
    private int lastSlot = -1;

    // BadPackets D
    private boolean lastSprinting;

    // BadPackets G
    private Queue<Pair<Long, Long>> keepaliveMap = new LinkedList<>();

    // Disabler A
    private static final List<Float> validTurns = Arrays.asList(
            0.29400003f,
            0.98f,
            0f
    );

    public Queue<Pair<Long, Long>> getKeepAliveMap() {
        return keepaliveMap;
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent e) {
        if (!e.getPlayer().isOp()) {
            badpackets:
            {
                badpackets_a:
                {
                    if (e.getPacketId() == PacketType.Play.Client.HELD_ITEM_SLOT) {
                        WrappedPacketInHeldItemSlot packet = new WrappedPacketInHeldItemSlot(e.getNMSPacket());

                        int slot = packet.getCurrentSelectedSlot();

                        if (slot == lastSlot) {
                            e.setCancelled(true);
                            MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.BADPACKETS, "lastSlot=(" + lastSlot + "), slot=(" + slot + ")",
                                    "badpackets.anticheatunit.a", 5);
                        }

                        lastSlot = packet.getCurrentSelectedSlot();
                    }
                }

                badpackets_b:
                {
                    if (e.getPacketId() == PacketType.Play.Client.STEER_VEHICLE) {
                        WrappedPacketInSteerVehicle vehicle = new WrappedPacketInSteerVehicle(e.getNMSPacket());

                        float fowards = Math.abs(vehicle.getForwardValue());
                        float sideways = Math.abs(vehicle.getSideValue());

                        if (fowards > 0.98f || sideways > 0.98f) {
                            e.setCancelled(true);
                            MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.BADPACKETS, "fowards=(" + fowards + "), sideways=(" + sideways + ")",
                                    "badpackets.anticheatunit.b", 5);
                        }
                    }
                }

                badpackets_c:
                {
                    if (e.getPacketId() == PacketType.Play.Client.LOOK || e.getPacketId() == PacketType.Play.Client.POSITION_LOOK) {
                        WrappedPacketInFlying flying = new WrappedPacketInFlying(e.getNMSPacket());

                        if (flying.getPitch() > 90 && flying.getPitch() < -90) {
                            e.setCancelled(true);
                            MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.BADPACKETS, "pitch=(" + flying.getPitch() + ")",
                                    "badpackets.anticheatunit.c", 5);
                        }
                    }
                }

                badpackets_d:
                {
                    if (e.getPacketId() == PacketType.Play.Client.ENTITY_ACTION) {
                        WrappedPacketInEntityAction packet = new WrappedPacketInEntityAction(e.getNMSPacket());

                        if (packet.getAction() == WrappedPacketInEntityAction.PlayerAction.START_SPRINTING) {
                            if (lastSprinting) {
                                e.setCancelled(true);
                                MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.BADPACKETS, "sprinting=(" + lastSprinting + ")",
                                        "badpackets.anticheatunit.d", 5);
                            }

                            lastSprinting = true;
                        } else if (packet.getAction() == WrappedPacketInEntityAction.PlayerAction.STOP_SPRINTING) {
                            if (!lastSprinting) {
                                e.setCancelled(true);
                                MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.BADPACKETS, "sprinting=(" + lastSprinting + ")",
                                        "badpackets.anticheatunit.d", 5);
                            }

                            lastSprinting = false;
                        }
                    }
                }

                badpackets_e: {
                    if (e.getPacketId() == PacketType.Play.Client.SPECTATE) {
                        if (e.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                            e.setCancelled(true);
                            MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.BADPACKETS, "gamemode=(" + checkGameMode(e.getPlayer().getGameMode()) +
                                            ")",
                                    "badpackets.anticheatunit.e", 5);
                        }
                    }
                }

                badpackets_f: {
                    if (e.getPacketId() == PacketType.Play.Client.BLOCK_DIG) {
                        WrappedPacketInBlockDig packet = new WrappedPacketInBlockDig(e.getNMSPacket());

                        if (packet.getDigType() == WrappedPacketInBlockDig.PlayerDigType.RELEASE_USE_ITEM) {
                            if (packet.getDirection() != Direction.DOWN && packet.getBlockPosition().getX() != 0 && packet.getBlockPosition().getY() != 0 &&
                                    packet.getBlockPosition().getZ() != 0) {
                                e.setCancelled(true);
                                MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.BADPACKETS, "dir=(" + Direction.DOWN.name() + ")",
                                        "badpackets.anticheatunit.f", 3);
                            }
                        }
                    }
                }

                badpackets_g:  {
                    if (e.getPacketId() == PacketType.Play.Client.KEEP_ALIVE) {
                        WrappedPacketInKeepAlive packet = new WrappedPacketInKeepAlive(e.getNMSPacket());

                        long id = packet.getId();
                        boolean hasID = false;

                        for (Pair<Long, Long> iterator : keepaliveMap) {
                            if (iterator.getFirst() == id) {
                                hasID = true;
                                break;
                            }
                        }

                        if (!hasID) {
                            e.setCancelled(true);
                            MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.BADPACKETS, "id=(" + id + ")",
                                    "badpackets.anticheatunit.g", 2);
                        } else { // Found the ID, remove stuff until we get to it (to stop very slow memory leaks)
                            Pair<Long, Long> data;
                            do {
                                data = keepaliveMap.poll();
                                if (data == null) break;
                            } while(data.getFirst()!=id);
                        }
                    }
                }
            }

            disabler: {
                disabler_a: {
                    if (e.getPacketId() == PacketType.Play.Client.STEER_VEHICLE) {
                        final WrappedPacketInSteerVehicle packet = new WrappedPacketInSteerVehicle(e.getNMSPacket());

                        if (packet.isDismount()) {
                            final float forward = Math.abs(packet.getForwardValue()), side = Math.abs(packet.getSideValue());

                            if (!validTurns.contains(forward) || !validTurns.contains(side)) {
                                e.setCancelled(true);
                                MatrixAPIProvider.getAPI().flag(e.getPlayer(), HackType.BADPACKETS, "turn_fo=(" + forward + "), turn_si=(" + side + ")",
                                        "disabler.anticheatunit.a", 5);
                            }
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent e) {
        if (e.getPacketId() == PacketType.Play.Server.KEEP_ALIVE) {
            WrappedPacketOutKeepAlive packet = new WrappedPacketOutKeepAlive(e.getNMSPacket());
            keepaliveMap.add(new Pair<>(packet.getId(), System.nanoTime()));
        }
    }

    private String checkGameMode(GameMode gameMode) {
        String gameModeString;

        if (gameMode == GameMode.CREATIVE) {
            gameModeString = "Creative";
        } else if (gameMode == GameMode.SURVIVAL) {
            gameModeString = "Survival";
        } else if (gameMode == GameMode.SPECTATOR) {
            gameModeString = "Spectator";
        } else if (gameMode == GameMode.ADVENTURE) {
            gameModeString = "Adventure";
        } else {
            gameModeString = "Unknown";
        }

        return gameModeString;
    }
}
