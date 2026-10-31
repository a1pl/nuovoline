/*
package cc.nuovoline.modules.player;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.MotionUpdateEvent;
import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.IntProperty;
import cc.nuovoline.modules.configurations.property.object.PropertyFactory;
import cc.nuovoline.utils.ServerUtils;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.checkerframework.checker.nullness.qual.NonNull;
import viaversion.viafabric.ViaFabric;

public class FastUse extends AbstractModule {

    @Property("tick")
    private final IntProperty tick = PropertyFactory.createInt(4).maximum(20).minimum(1);

    public FastUse(@NonNull ModuleManager nuovoline) {
        super(nuovoline, EnumModuleType.PLAYER, "FastUse", "Fast Use");
        Manager.put(new Setting("FS_TICK", "Tick", SettingType.SLIDER, this, tick, 1));
    }

    @EventTarget
    public void onMotion(MotionUpdateEvent event) {
        if (event.getState().equals(MotionUpdateEvent.State.POST)) {
            if (mc.player.isUsingItem() && (mc.player.getItemInUse().getItem() instanceof ItemFood
                    || mc.player.getItemInUse().getItem() instanceof ItemPotion
                    || mc.player.getItemInUse().getItem() instanceof ItemBucketMilk)) {
                if (tick.get() == 1 || mc.player.getItemInUseDuration() == tick.get()) {
                    if (ServerUtils.isHypixel()) {
                        if (ViaFabric.clientSideVersion == nuovoline.viaVersion()) {
                            for (int i = 0; i < 30; i++) {
                                sendPacketNoEvent(new C03PacketPlayer(false));
                                sendPacketNoEvent(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
                            }
                        }

                    } else {
                        for (int i = 0; i < 20; i++) {
                            sendPacketNoEvent(new C03PacketPlayer(false));
                        }
                    }
                }
            }
        }
    }
}
*/
