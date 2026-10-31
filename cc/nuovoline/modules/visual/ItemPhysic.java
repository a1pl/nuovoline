package cc.nuovoline.modules.visual;

import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;

public final class ItemPhysic extends AbstractModule {

    /* constructors */
    public ItemPhysic(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "ItemPhysic", "Item Physic", Keyboard.KEY_NONE, EnumModuleType.VISUALS,
                "Real items physic");
    }

}
