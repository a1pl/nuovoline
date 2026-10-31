package cc.nuovoline.modules.misc;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.MotionUpdateEvent;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import net.minecraft.client.gui.GuiGameOver;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class Respawn extends AbstractModule {

    /* constructors */
    public Respawn(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Respawn", EnumModuleType.MISC, "Respawns you");
    }

    /* events */
    @EventTarget
    public void onUpdate(MotionUpdateEvent event) {
        if (event.getState().equals(MotionUpdateEvent.State.PRE)) {
            if (this.mc.currentScreen instanceof GuiGameOver) {
                this.mc.player.respawnPlayer();
            }
        }
    }

}
