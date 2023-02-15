package kr.mcv.lightfrog.anticheatunit.utils.system;

public class Timer {
    private long time = System.currentTimeMillis();
    public static final Timer INSTANCE = new Timer();

    public void reset() {
        time = System.currentTimeMillis();
    }

    public boolean hasPassed(long time) {
        if (System.currentTimeMillis() - this.time >= time) {
            reset();
            return true;
        }
        return false;
    }

    public boolean hasPassedNoReset(long time) {
        if (System.currentTimeMillis() - this.time >= time) {
            return true;
        }
        return false;
    }

    public boolean hasPassedSecond(long time) {
        if (System.currentTimeMillis() - this.time >= (time * 1000)) {
            reset();
            return true;
        }
        return false;
    }

    public boolean hasPassedNoResetSecond(long time) {
        if (System.currentTimeMillis() - this.time >= (time * 1000)) {
            return true;
        }
        return false;
    }
}
