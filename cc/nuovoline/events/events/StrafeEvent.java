package cc.nuovoline.events.events;

import cc.nuovoline.events.events.callables.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;


/**
 * @author yuxiangll
 * @package cc.nuovoline.events.events
 * don't mind
 * @date 2023/8/16 21:00
 */

@Getter
@Setter
@AllArgsConstructor
public class StrafeEvent extends CancellableEvent{
    private float forward;
    private float strafe;
    private float friction;
    private float yaw;

    public void setSpeed(final double speed, final double motionMultiplier) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        Minecraft.getInstance().player.motionX *= motionMultiplier;
        Minecraft.getInstance().player.motionZ *= motionMultiplier;
    }

    public void setSpeed(final double speed) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        stop();
    }

    /**
     * Stops the player from moving
     */
    public void stop() {
        Minecraft.getInstance().player.motionX = 0;
        Minecraft.getInstance().player.motionZ = 0;
    }


}
