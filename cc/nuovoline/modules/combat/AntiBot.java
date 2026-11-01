package cc.nuovoline.modules.combat;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.PacketEvent;
import cc.nuovoline.events.events.TickUpdateEvent;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.BooleanProperty;
import cc.nuovoline.modules.configurations.property.object.PropertyFactory;
import cc.nuovoline.modules.exploits.Blink;
import cc.nuovoline.modules.player.Freecam;
import cc.nuovoline.utils.ServerUtils;
import cc.nuovoline.utils.Servers;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.stream.Collectors;

import static cc.nuovoline.modules.EnumModuleType.COMBAT;

public final class AntiBot extends AbstractModule {

    @Property("visible-check")
    private final BooleanProperty visible_check = PropertyFactory.booleanFalse();

    /* constructors */
    public AntiBot(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "AntiBot", "Anti Bot", COMBAT, "Removes server-sided anti-cheat bots to prevent you from getting banned");
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (ServerUtils.isRewound()) {
            if (!ServerUtils.serverIs(Servers.LOBBY)) {
                if (event.getState().equals(PacketEvent.State.INCOMING)) {
                    if (event.getPacket() instanceof S18PacketEntityTeleport) {
                        S18PacketEntityTeleport packet = (S18PacketEntityTeleport) event.getPacket();
                        EntityPlayer entity = (EntityPlayer) mc.world.getEntityByID(packet.getEntityId());

                        if (entity instanceof EntityPlayer && entity.isInvisible() && entity.ticksExisted > 3 && mc.world.getPlayerEntities().contains(entity) && !isInTabList(entity)) {
                            mc.world.removeEntity(entity);
                        }
                    }

                    if (event.getPacket() instanceof S0CPacketSpawnPlayer) {
                        S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) event.getPacket();
                        EntityPlayer player = (EntityPlayer) mc.world.getEntityByID(packet.getEntityID());

                        double posX = packet.getX() / 32.0D, posY = packet.getY() / 32.0D, posZ = packet.getZ() / 32.0D;
                        double difX = mc.player.posX - posX, difY = mc.player.posY - posY, difZ = mc.player.posZ - posZ;
                        double dist = Math.sqrt(difX * difX + difY * difY + difZ * difZ);

                        if (mc.world.getPlayerEntities().contains(player) && dist <= 17.0D && !player.equals(mc.player)
                                && posX != mc.player.posX && posY != mc.player.posY && posZ != mc.player.posZ) {
                            mc.world.removeEntity(player);
                        }
                    }
                }
            }
        }
    }

    private boolean isInTabList(EntityPlayer player) {
        return mc.ingameGUI.getTabList().getList().contains(NetHandlerPlayClient.getPlayerInfo(player.getGameProfile().getId()));
    }

    private List<EntityPlayer> entityPlayersList() {
        return mc.world.getPlayerEntities().stream()
                .filter(e -> !e.equals(mc.player) &&
                        !e.equals(getModule(Blink.class).getBlinkEntity()) &&
                        !e.equals(getModule(Freecam.class).getFreecamEntity()))
                .collect(Collectors.toList());
    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {
        setSuffix("Rewound");
    }

    @Override
    public void onEnable() {
        setSuffix("Rewound");
    }
}