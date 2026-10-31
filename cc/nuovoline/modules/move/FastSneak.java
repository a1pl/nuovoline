package cc.nuovoline.modules.move;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.MotionUpdateEvent;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FastSneak extends AbstractModule {

    public FastSneak(@NonNull ModuleManager nuovoline) {
        super(nuovoline, EnumModuleType.MOVEMENT, "FastSneak", "Fast Sneak");
    }

    @EventTarget
    public void onPre(MotionUpdateEvent event) {
        if (mc.player.movementInput().sneak() && mc.player.isMoving()) {
            if (event.getState().equals(MotionUpdateEvent.State.PRE)) {
                sendPacketNoEvent(new C0BPacketEntityAction(mc.player, C0BPacketEntityAction.Action.STOP_SNEAKING));
            } else {
                sendPacketNoEvent(new C0BPacketEntityAction(mc.player, C0BPacketEntityAction.Action.START_SNEAKING));
            }
        }
    }
}
