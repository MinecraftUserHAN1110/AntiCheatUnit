package kr.mcv.lightfrog.anticheatunit.utils.minecraft.entity;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private long time = 0;
    private ArrayList<Long> hits = new ArrayList<>();
    private HashMap<Long, Integer> entities = new HashMap<>();
    private long lastTimeHitsCleaned = 0, lastTimeEntitiesCleaned = 0;

    private Player p;
    public User(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

    public long getFlyingTime() {
        return time;
    }

    public void flying() {
        time++;
    }

    public boolean isFlying() {
        return time >= 60;
    }

    public void decayFlying() {
        time = time - 5;
    }

    public double oldY = 0;

    public int oldYModifier = 0, ticksUp = 0, oldTicksUp = 0;
    public boolean wasGoingUp = false;


    public void addHit() {
        hits.add(System.currentTimeMillis());
    }

    public int getHits() {
        long start = System.currentTimeMillis();
        ArrayList<Long> toRemove = new ArrayList<>();
        int result = 0;
        for (long l : hits)
            if (start - l > 1000L)
                toRemove.add(l);
            else
                result++;
        hits.removeAll(toRemove);
        toRemove.clear();
        lastTimeHitsCleaned = start;
        return result;
    }

    public void addEntity(int i) {
        entities.put(System.currentTimeMillis(), i);
    }

    public int getEntities() {
        long start = System.currentTimeMillis();
        ArrayList<Long> toRemove = new ArrayList<>();
        ArrayList<Integer> res = new ArrayList<>();
        int result = 0;
        for (long l : entities.keySet()) {
            int entityId = entities.get(l);
            if (start - l > 1000L)
                toRemove.add(l);
            else if (!res.contains(entityId)) {
                result++;
                res.add(entityId);
            }
        }
        hits.removeAll(toRemove);
        toRemove.clear();
        res.clear();
        lastTimeEntitiesCleaned = start;
        return result;
    }

    public long getLastTimeHitsCleaned() {
        return lastTimeHitsCleaned;
    }

    public long getLastTimeEntitiesCleaned() {
        return lastTimeEntitiesCleaned;
    }

    private float lastYaw;
    private float currentYaw;

    private float lastPitch;
    private float currentPitch;

    public float getLastYaw() {
        return lastYaw;
    }

    public void setYaw(float rot) {
        this.currentYaw = rot;
    }

    public void setLastYaw(float rot) {
        this.lastYaw = rot;
    }

    public float getYaw() {
        return currentYaw;
    }

    public ACUPlayer getACUPlayer() {
        return ACUPlayer.INSTANCE;
    }

    public void setLastPitch(float pitch) {
        this.lastPitch = pitch;
    }

    public void setPitch(float pitch) {
        this.currentPitch = pitch;
    }

    public float getPitch() {
        return currentPitch;
    }

    public float getLastPitch() {
        return lastPitch;
    }
}
