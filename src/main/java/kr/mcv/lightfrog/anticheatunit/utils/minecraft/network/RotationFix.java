package kr.mcv.lightfrog.anticheatunit.utils.minecraft.network;

import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import kr.mcv.lightfrog.anticheatunit.utils.math.MathUtil;
import kr.mcv.lightfrog.anticheatunit.utils.minecraft.entity.User;
import kr.mcv.lightfrog.anticheatunit.utils.system.EvictingList;
import org.bukkit.entity.Player;

public class RotationFix extends PacketListenerAbstract {
    private float yaw, pitch, lastYaw, lastPitch;
    private boolean a = true;
    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getLastYaw() { return lastYaw; }
    public float getLastPitch() { return lastPitch; }
    private double deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch;
    private double accelYaw, accelPitch, lastAccelYaw, lastAccelPitch;
    private double gcdYaw, gcdPitch;
    private double sensitivityYaw, sensitivityPitch, lastSensitivityYaw, lastSensitivityPitch;
    private final EvictingList<Double> yawSamples = new EvictingList<>(50);
    private final EvictingList<Double> pitchSamples = new EvictingList<>(50);
    private final double EXPANDER = Math.pow(2, 24);


    private void handleRotation(WrappedPacketInFlying wrapper, Player p) {
        lastYaw = yaw;
        lastPitch = pitch;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
        lastSensitivityYaw = sensitivityYaw;
        lastSensitivityPitch = sensitivityPitch;

        final float deltaYaw = Math.abs(wrapper.getYaw() - lastYaw);
        final float deltaPitch = Math.abs(wrapper.getPitch() - lastPitch);

        final double gcdYaw = MathUtil.getGcd((deltaYaw * EXPANDER), (lastDeltaYaw * EXPANDER));
        final double gcdPitch = MathUtil.getGcd((deltaPitch * EXPANDER), (lastDeltaPitch * EXPANDER));

        final double dividedYawGcd = gcdYaw / EXPANDER;
        final double dividedPitchGcd = gcdPitch / EXPANDER;

        if (gcdYaw > 90000 && gcdYaw < 2E7 && dividedYawGcd > 0.01f && deltaYaw < 8) {
            yawSamples.add(dividedYawGcd);
        }

        if (gcdPitch > 90000 && gcdPitch < 2E7 && deltaPitch < 8) {
            pitchSamples.add(dividedPitchGcd);
        }

        double modeYaw = 0.0;
        double modePitch = 0.0;

        if (pitchSamples.size() > 5 && yawSamples.size() > 5) {
            modeYaw = MathUtil.getMode(yawSamples);
            modePitch = MathUtil.getMode(pitchSamples);
        }

        sensitivityYaw = getSensitivityFromYawGCD(modeYaw);
        sensitivityPitch = getSensitivityFromPitchGCD(modePitch);

        this.gcdPitch = dividedPitchGcd;
        this.gcdYaw = dividedYawGcd;
        this.yaw = wrapper.getYaw();
        this.pitch = wrapper.getPitch();
        this.deltaYaw = Math.abs(yaw - lastYaw);
        this.deltaPitch = Math.abs(pitch - lastPitch);
        lastAccelYaw = accelYaw;
        lastAccelPitch = accelPitch;
        accelYaw = Math.abs(deltaYaw - lastDeltaYaw);
        accelPitch = Math.abs(deltaPitch - lastDeltaPitch);

        User u = new User(p);
        u.setYaw(yaw);
        u.setPitch(pitch);
        u.setLastYaw(lastYaw);
        u.setLastPitch(lastPitch);
    }

    /**
     * Credit: OverFlow 2.0
     */

    private static double getSensitivityFromPitchGCD(double gcd) {
        double stepOne = pitchToF3(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    /**
     * Credit: OverFlow 2.0
     */

    private static double getSensitivityFromYawGCD(double gcd) {
        double stepOne = yawToF2(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    /**
     * Credit: OverFlow 2.0
     */
    private static double pitchToF3(double pitchDelta) {
        int b0 = pitchDelta >= 0 ? 1 : -1; //Checking for inverted mouse.
        return pitchDelta / .15 / b0;
    }

    /**
     * Credit: OverFlow 2.0
     */
    private static double yawToF2(double yawDelta) {
        return yawDelta / .15;
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent e) {
        if (e.getPacketId() == PacketType.Play.Client.FLYING) {
            final WrappedPacketInFlying p = new WrappedPacketInFlying(e.getNMSPacket());
            handleRotation(p, e.getPlayer());
        }
        if(e.getPacketId() == PacketType.Play.Client.POSITION){
            Player p = e.getPlayer();
            double lastY = 0;
            double currentY;
            if(this.a){
                lastY = p.getLocation().getY();
                this.a = false;
                return;
            }
            currentY = p.getLocation().getY();
            this.a = true;
            p.sendMessage("" + ((currentY - lastY)-p.getLocation().getY()));

        }    
    }
}
