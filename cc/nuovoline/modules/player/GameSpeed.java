package cc.nuovoline.modules.player;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.TickUpdateEvent;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import net.minecraft.potion.Potion;
import org.jetbrains.annotations.NotNull;

public class GameSpeed extends AbstractModule {

    public GameSpeed(@NotNull ModuleManager nuovoline) {
        super(nuovoline, EnumModuleType.PLAYER, "GameSpeed", "Game Speed");
    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {
        if (mc.player.isPotionActive(Potion.moveSpeed)) {
            mc.timer.timerSpeed = 1.3F + 0.2F * (mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        } else {
            mc.timer.timerSpeed = 1.3F;
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
    }
}
