package kr.mcv.lightfrog.anticheatunit.checks.hitbox;

import kr.mcv.lightfrog.anticheatunit.checks.Check;
import kr.mcv.lightfrog.anticheatunit.observable.Observable;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.server.Component;
import kr.mcv.lightfrog.anticheatunit.utils.math.Distance;
import me.rerere.matrix.api.HackType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HitBoxCheck extends Check implements Listener {
    public HitBoxCheck() {
        super(HackType.HITBOX);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            if (!e.getDamager().isOp()) {
                hitbox_a:
                {
                    Distance d = new Distance(e.getDamager().getLocation(), e.getEntity().getLocation());

                    double x = d.getXDifference();
                    double z = d.getZDifference();

                    double max = ((Player) e.getDamager()).getGameMode() == GameMode.CREATIVE ? 5.25 : 3.7;

                    if (x > max || z > max) {
                        e.setCancelled(true);
                        flag((Player) e.getDamager(), getType(), "", new Observable<>(new Component("hitbox.anticheatunit")).get(), 5);
                    }
                }
            }
        }
    }
}
