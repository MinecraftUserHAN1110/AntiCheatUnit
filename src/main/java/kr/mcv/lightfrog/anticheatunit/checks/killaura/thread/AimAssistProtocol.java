package kr.mcv.lightfrog.anticheatunit.checks.killaura.thread;

import kr.mcv.lightfrog.anticheatunit.observable.Observable;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.entity.User;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.server.Component;
import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPIProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AimAssistProtocol extends Thread {
    public User u;
    public EntityDamageByEntityEvent e;
    private int activeTicks = 0;
    private double llPitch;

    public AimAssistProtocol(User u, EntityDamageByEntityEvent e) {
        this.u = u;
        this.e = e;
    }

    @Override
    public void run() {
        /*
         * This is an extremely weird looking aim check (I have a switch addiction), but it seems effective.
         * This behavior is hard to reproduce legitimately and is a sign of bad kill auras.
         */
        final double pitch = Math.abs(u.getPitch());

        switch (activeTicks) {
            case 0:
                llPitch = pitch;
                activeTicks = 1;
                break;
            case 1:
                activeTicks = 2;
                break;
            case 2:
                final double lPitch = Math.abs(u.getLastPitch());

                // 3 15 3
                if (llPitch < 3f && lPitch > 15f && pitch < 3f) {
                    MatrixAPIProvider.getAPI().flag((Player) e.getDamager(), HackType.KILLAURA,
                            "lastPitch=(" + lPitch + "), pitch=(" + pitch + "), llPitch=(" +
                                    llPitch + ")", new Observable<>(new Component("killaura.anticheatunit.rotation")).get().getData(), 2);
                }

                llPitch = lPitch;
                break;
        }
    }
}
