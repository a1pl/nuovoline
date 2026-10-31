package cc.nuovoline.modules.move;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.MotionUpdateEvent;
import cc.nuovoline.events.events.PacketEvent;
import cc.nuovoline.events.events.PlayerUpdateEvent;
import cc.nuovoline.events.events.TickUpdateEvent;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import org.jetbrains.annotations.NotNull;

public class PigFly extends AbstractModule {

    private Entity entity;

    public PigFly(@NotNull ModuleManager nuovoline) {
        super(nuovoline, EnumModuleType.MOVEMENT, "PigFly", "Pig Fly");
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getState().equals(PacketEvent.State.OUTGOING)) {
            if (event.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();

                if (packet.getAction() == C02PacketUseEntity.Action.INTERACT) {
                    if (packet.getEntityFromWorld(mc.world) instanceof EntityPig) {
                        entity = packet.getEntityFromWorld(mc.world);
                    }
                }
            }

        } else {

        }
    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {

    }

    @EventTarget
    public void onMotion(MotionUpdateEvent event) {
        if (entity != null && event.getState().equals(MotionUpdateEvent.State.PRE)) {
            sendPacketNoEvent(new CPacketVehicleMove(entity));
            event.setOnGround(false);
        }
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {

    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        entity = null;
    }
}
