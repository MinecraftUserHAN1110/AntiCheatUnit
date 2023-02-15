package kr.mcv.lightfrog.anticheatunit.utils.math;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public class MathUtil {
    // From Grim
    public static final double MINIMUM_DIVISOR = (Math.pow(0.2f, 3) * 8) - 1e-3; // 1e-3 for float imprecision

    // From Grim
    public static double getGcd(double current, double previous) {
        if (current == 0) return 0;

        if (current < previous) {
            double temp = current;
            current = previous;
            previous = temp;
        }

        while (previous > MINIMUM_DIVISOR) {
            double temp = current - (Math.floor(current / previous) * previous);
            current = previous;
            previous = temp;
        }

        return current;
    }

    public static Double getMode(List<Double> collect) {
        double maxValue = 0;
        int maxCount = 0;

        for (int i = 0; i < collect.size(); ++i) {
            int count = 0;
            for (double t : collect) {
                if (t == collect.get(i)) ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = collect.get(i);
            }
        }

        return maxValue;
    }

    // Custom MathUtils by NikV2

    public static float getAngle(final Vector one, final Vector two) {

        final double dot = Math.min(Math.max(
                (one.getX() * two.getX() + one.getY() * two.getY() + one.getZ() * two.getZ())
                        / (one.length() * two.length()), -1.0), 1.0);

        return (float) Math.acos(dot);
    }

    public static Vector getDirection(final Location location) {

        Vector vector = new Vector();

        final double rotX = location.getYaw();
        final double rotY = location.getPitch();

        final double radiansRotY = Math.toRadians(rotY);

        vector.setY(-Math.sin(radiansRotY));

        final double xz = Math.cos(radiansRotY);

        final double radiansRotX = Math.toRadians(rotX);

        vector.setX(-xz * Math.sin(radiansRotX));
        vector.setZ(xz * Math.cos(radiansRotX));

        return vector;
    }
}
