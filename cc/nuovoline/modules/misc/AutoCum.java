package cc.nuovoline.modules.misc;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class AutoCum extends AbstractModule {

    public AutoCum(@NonNull ModuleManager nuovoline) {
        super(nuovoline, EnumModuleType.MISC, "AutoCum", "Automatically cums for you");
    }

    @Override
    public void onEnable()  {
        System.out.println("AutoCum module enabled. Cumming...");
        mc.player.sendChatMessage("cum for mommy | ft. nuovoline!");
    }

    @Override
    public void onDisable() {
        System.out.println("AutoCum module disabled. Stopping...");
    }
}
