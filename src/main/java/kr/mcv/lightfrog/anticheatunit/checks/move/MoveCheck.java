package kr.mcv.lightfrog.anticheatunit.checks.move;

import kr.mcv.lightfrog.anticheatunit.checks.Check;
import kr.mcv.lightfrog.anticheatunit.observable.Observable;
import kr.mcv.lightfrog.anticheatunit.utils.math.YMap;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.block.BlockManager;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.server.Component;
import kr.mcv.lightfrog.anticheatunit.utils.math.Distance;
import kr.mcv.lightfrog.anticheatunit.utils.system.Timer;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.entity.User;
import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.events.PlayerViolationEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static java.lang.Math.round;

public class MoveCheck extends Check implements Listener {
    public MoveCheck() {
        super(HackType.MOVE);
    }

    private double flyBbuffer = 0.0d;

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Distance d;
        User u = new User(e.getPlayer());
        Timer timer = Timer.INSTANCE;
        utilities: {
            distance: {
                d = new Distance(e);
            }

            time: {
                u.flying();

                if (timer.hasPassedSecond(5L)) {
                    u.decayFlying();
                }
            }
        }

        check:
        {
            speed:
            {
                if (getSpeedModifier(d, u) > 0.64) {
                    lagback(e);
                    flag(e.getPlayer(), getType(), "speed=(" + d.getSpeed() + ")",
                            new Component(new Observable<>("move.anticheatunit.speed").get()), 5);
                }
            }

            fly:
            {
                fly_a:
                {
                    if (u.isFlying()) {
                        lagback(e);
                        flag(e.getPlayer(), getType(), "speed=(" + d.getSpeed() + "), y=(" + d.getYDifference() + "), ground=(" +
                                        e.getPlayer().isOnGround() + ")", new Observable<Component>(new Component("move.anticheatunit.fly")).get(),
                                5);
                    }
                }
                fly_b:
                {
                    Location from = e.getFrom();
                    Location to = e.getTo();

                    final boolean clientGround = e.getPlayer().isOnGround();
                    final boolean serverGround = to.getY() % 0.015625 == 0.0 && from.getY() % 0.015625 == 0.0;

                    final boolean except = e.getPlayer().isInWater() && (e.getFrom().equals(to));

                    if (!except && clientGround != serverGround) {
                        if (++flyBbuffer > 4) {
                            lagback(e);
                            flag(e.getPlayer(), getType(), "speed=(" + d.getSpeed() + "), clientGround=(" + clientGround + "), serverGround=(" +
                                            serverGround + "), buffer=(" + flyBbuffer + ")",
                                    new Observable<>(new Component("move.anticheatunit.nofall")).get(), 10);
                        }
                    } else {
                        flyBbuffer = 0;
                    }
                }
                fly_c:
                {
                    if (!d.isGoingUp() || d.getYDifference() == 0) {
                        u.wasGoingUp = false;
                        u.oldYModifier = 0;
                        u.ticksUp = 0;
                        break fly_c;
                    }

                    u.wasGoingUp = true;
                    int ticksUp = u.ticksUp;
                    u.ticksUp++;
                    u.oldTicksUp = ticksUp;

                    final double speed = round(d.getYDifference());

                    int id = getYModifier(u);
                    if (id > u.oldYModifier)
                        u.oldYModifier = id;
                    id = u.oldYModifier;
                    YMap map = YMap.get(id);

                    if (d.isGoingUp() && d.isMovingHorizontally()) {
                        if (speed > .5) {
                            //return new CheckResult(true, CheckType.NORMALMOVEMENTS, "reason: step, type: " + (speed > .5 ? "high" : "low") + ", y: " + speed);
                            lagback(e);
                            flag(e.getPlayer(), getType(), "mode=(step), type=(" + (speed > .5 ? "high" : "low") + "), y=(" + speed + ")",
                                    new Observable<>(new Component("move.anticheatunit.fly")).get(), 5);
                            break fly_c;
                        }
                        break fly_c;
                    }

                    if (map == null) {
                        break fly_c;
                    }

                    if (!map.hasSpeed(ticksUp)) {
                        flag(e.getPlayer(), getType(), "mode=(longjump), ticks=(" + ticksUp + "), mapSize=(" + map.size() + ")",
                                new Observable<>(new Component("move.anticheatunit.fly")).get(), 3);
                        lagback(e);
                        break fly_c;
                    }

                    if (map.size() <= ticksUp) {
                        if (!(id != 0 && d.isMovingHorizontally() && map.size() == ticksUp && speed == map.getSpeed(ticksUp))) {
                            flag(e.getPlayer(), getType(), "mode=(highjump), ticks=(" + ticksUp + "), motY=(" + d.getYDifference() + ")",
                                    new Observable<Component>(new Component("move.anticheatunit.fly")).get(), 5);
                            lagback(e);
                            //break fly_c;
                        }
                    }

                    /* if (map.size() < ticksUp) {
                        break fly_c;
                    }

                    double expected = map.getSpeed(ticksUp);

                    if (expected != speed) {
                        //(speed: " + speed + ", expected: " + expected);
                        if (!u.getPlayer().isJumping() && !u.getPlayer().isOnGround()) {
                            flag(e.getPlayer(), getType(), "mode=(normal), type=(" + (expected < speed ? "high" : "low") + "), speed=(" + speed +
                                    "), excepted=(" + expected + ")", new Observable<>(new Component("move.anticheatunit.fly")).get(), 10);
                            lagback(e);
                        }
                    } */
                }
            }

            noslow: {
                if (e.getPlayer().isBlocking()) {
                    if (d.getSpeed() >= 0.27 && e.getPlayer().isOnGround()) {
                        flag(e.getPlayer(), getType(), "speed=(" + d.getSpeed() + ")",
                                new Observable<>(new Component("move.anticheatunit.noslow")).get(), 3);
                        e.setTo(e.getFrom());
                        e.setCancelled(true);
                    }
                }
            }

            inventory_move: {
                if (e.getPlayer().getOpenInventory().equals(e.getPlayer().getInventory())) {

                }
            }
        }
    }

    @EventHandler
    public void onViolationEvent(PlayerViolationEvent e) {
        if (e.getHackType() == HackType.MOVE) {
            if (BlockManager.INSTANCE.isCarpet(e.getPlayer().getLocation().getBlock().getType()) && e.getPlayer().isCollidable()) {
                e.setCancelled(true);
            }
        }
    }

    private void lagback(PlayerMoveEvent e) {
        if (!(e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR || e.getPlayer().isOp())) {
            e.setTo(e.getFrom());
        }
    }
    public int getYModifier(User user) {
        if (user.getPlayer().hasPotionEffect(PotionEffectType.JUMP))
            for (PotionEffect pe : user.getPlayer().getActivePotionEffects())
                if (pe.getType().equals(PotionEffectType.JUMP))
                    return pe.getAmplifier() + 1;
        return 0;
    }

    public double getSpeedModifier(Distance d, User user) {
        if (user.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
            for (PotionEffect pe : user.getPlayer().getActivePotionEffects())
                if (pe.getType().equals(PotionEffectType.SPEED))
                    return d.getSpeed() + (pe.getAmplifier() + 0.1);
        return d.getSpeed();
    }
}
