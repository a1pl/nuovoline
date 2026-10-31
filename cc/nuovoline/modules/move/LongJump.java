package cc.nuovoline.modules.move;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.*;
import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.DoubleProperty;
import cc.nuovoline.modules.configurations.property.object.IntProperty;
import cc.nuovoline.modules.configurations.property.object.PropertyFactory;
import cc.nuovoline.utils.MovementUtil;
import cc.nuovoline.utils.notifications.NotificationType;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LongJump extends AbstractModule {

    @Property("speed")
    private DoubleProperty speed = PropertyFactory.createDouble(5.0).minimum(2.0).maximum(6.0);
    @Property("jump-boost")
    private IntProperty jump_boost = PropertyFactory.createInt(2).minimum(0).maximum(4);

    public LongJump(@NotNull ModuleManager nuovoline) {
        super(nuovoline, EnumModuleType.MOVEMENT, "LongJump", "Long Jump");
        Manager.put(new Setting("LJ_SPEED", "Speed", SettingType.SLIDER, this, speed, 0.2));
        Manager.put(new Setting("LJ_JUMP_BOOST", "Jump Boost", SettingType.SLIDER, this, jump_boost, 1));
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            super.onDisable();
        }
        if (mc.player.isMoving()) {
            mc.player.motionY = 0.42;
            MovementUtil.Game.strafe(10f, true, null, 1.97);
        }
    }
}