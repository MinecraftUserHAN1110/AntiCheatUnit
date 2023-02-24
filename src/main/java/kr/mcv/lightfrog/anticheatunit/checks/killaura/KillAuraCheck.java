package kr.mcv.lightfrog.anticheatunit.checks.killaura;

import kr.mcv.lightfrog.anticheatunit.Anticheatunit;
import kr.mcv.lightfrog.anticheatunit.checks.Check;
import kr.mcv.lightfrog.anticheatunit.checks.killaura.thread.AimAssistProtocol;
import kr.mcv.lightfrog.anticheatunit.observable.Observable;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.server.Component;
import kr.mcv.lightfrog.anticheatunit.utils.math.Distance;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.entity.User;
import me.rerere.matrix.api.HackType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KillAuraCheck extends Check implements Listener {
    public KillAuraCheck() {
        super(HackType.KILLAURA);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            if (!e.getDamager().isOp()) {
                User u = new User((Player) e.getDamager());
                if (Anticheatunit.INSTANCE.getConfig().getBoolean("checks.killaura.enable")) {
                    killaura:
                    {
                        killaura_a:
                        {
                            Distance d = new Distance(e.getDamager().getLocation(), e.getEntity().getLocation());

                            double x = d.getXDifference();
                            double z = d.getZDifference();
                            Player p = (Player) e.getDamager();

                            if (x == 0 || z == 0) {
                                break killaura_a;
                            }

                            if (d.getYDifference() >= .6) {
                                break killaura_a;
                            }

                            Location l = null;

                            if (x <= .5 && z >= 1) {
                                if (p.getLocation().getZ() > e.getEntity().getLocation().getZ()) {
                                    l = p.getLocation().clone().add(0, 0, -1);
                                } else {
                                    l = p.getLocation().clone().add(0, 0, 1);
                                }
                            } else if (z <= .5 && x >= 1) {
                                if (p.getLocation().getX() > e.getEntity().getLocation().getX()) {
                                    l = p.getLocation().clone().add(-1, 0, 0);
                                } else {
                                    l = p.getLocation().clone().add(-1, 0, 0);
                                }
                            }

                            boolean failed = false;

                            if (l != null) {
                                failed = l.getBlock().getType().isSolid() && l.clone().add(0, 1, 0).getBlock().getType().isSolid();
                            }

                            if (failed) {
                                flag(p, getType(), "tried to hit as throught a walls",
                                        new Observable<>(new Component("killaura.anticheatunit.walls")).get(), 5);
                            }
                        }
                    }
                    aimassist:
                    {
                        aimassist_a:
                        {
                            float speed = Math.abs(u.getYaw() - u.getLastYaw());
                            if (speed > 6f) {
                                e.setCancelled(true);
                                flag((Player) e.getDamager(), getType(), "player rotate so fast, speed=(" + speed + "), yaw=(" + u.getYaw() +
                                                "), lastYaw=(" + u.getLastYaw() + ")",
                                        new Observable<>(new Component("killaura.anticheatunit.rotation")).get(), 1);
                            }
                        }

                        aimassist_b:
                        {
                            if (Anticheatunit.INSTANCE.getConfig().getBoolean("checks.killaura.modules.rotations.check_protocol1")) {
                                new AimAssistProtocol(u, e).start();
                            }
                        }
                    }
                    clicker:
                    {
                        u.addHit();

                        int hits = u.getHits() * 2;

                        if (hits > 12) {
                            flag((Player) e.getDamager(), HackType.CLICK, "player tried to use autoclicker / killaura, speed=(" + hits + ")",
                                    new Observable<Component>(new Component("click.anticheatunit.auto")).get(), 3);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
