package cc.nuovoline.modules.move;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.MotionUpdateEvent;
import cc.nuovoline.events.events.SlowdownEvent;
import cc.nuovoline.events.events.TickUpdateEvent;
import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.combat.KillAura;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.BooleanProperty;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.checkerframework.checker.nullness.qual.NonNull;

import static cc.nuovoline.modules.configurations.property.object.PropertyFactory.booleanFalse;

public final class NoSlow extends AbstractModule {

    /* properties @off */
    @Property("vanilla")
    private final BooleanProperty vanilla = booleanFalse();

    /* constructors @on */
    public NoSlow(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "NoSlow", "No Slow", EnumModuleType.MOVEMENT, "No slow down when using items");
        Manager.put(new Setting("NS_VANILLA", "Vanilla", SettingType.CHECKBOX, this, this.vanilla));
    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {
        setSuffix(vanilla.get() ? "Vanilla" : "NCP");
    }

    /* events */
    @EventTarget
    public void onBlock(MotionUpdateEvent event) {
        if (mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() instanceof ItemSword
                && mc.gameSettings.keyBindUseItem.isKeyDown() && !getModule(KillAura.class).shouldBlock()) {
            if (event.getState().equals(MotionUpdateEvent.State.PRE)) {
                sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            } else {
                sendPacket(new C08PacketPlayerBlockPlacement(mc.player.getHeldItem()));
            }
        }
    }

    @EventTarget
    public void onSlowDown(SlowdownEvent event) {
        event.setCancelled(true);
    }


    @Override
    public void onEnable() {
        setSuffix(vanilla.get() ? "Vanilla" : "NCP");
    }
}
