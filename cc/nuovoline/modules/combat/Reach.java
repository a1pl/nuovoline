package cc.nuovoline.modules.combat;

import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.FloatProperty;
import org.checkerframework.checker.nullness.qual.NonNull;

import static cc.nuovoline.gui.screen.setting.SettingType.SLIDER;
import static cc.nuovoline.modules.configurations.property.object.PropertyFactory.createFloat;

public final class Reach extends AbstractModule {

    /* properties @off */
    @Property("range")
    private final FloatProperty range = createFloat(5.0F).minimum(3.0F).maximum(5.0F);

    private boolean hittingBlock;

    /* constructors @on */
    public Reach(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Reach", EnumModuleType.COMBAT, "Expands reach");
        Manager.put(new Setting("Reach_Distance", "Range", SLIDER, this, this.range, 0.1D));
    }

    public float getRange() {
        return range.get();
    }
}
