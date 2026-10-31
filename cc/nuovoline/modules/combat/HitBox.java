package cc.nuovoline.modules.combat;

import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.FloatProperty;
import org.checkerframework.checker.nullness.qual.NonNull;

import static cc.nuovoline.modules.configurations.property.object.PropertyFactory.createFloat;

public final class HitBox extends AbstractModule {

    /* properties @off */
    @Property("size")
    private final FloatProperty hitBoxSize = createFloat(0.3F).minimum(0.1F).maximum(1.0F);

    /* constructors @on */
    public HitBox(@NonNull ModuleManager moduleManager) {
        super(moduleManager, EnumModuleType.COMBAT, "HitBox", "Hit Box");
        Manager.put(new Setting("ENTITY_BOX", "Box Size", SettingType.SLIDER, this, this.hitBoxSize, 0.1F));
    }

    public FloatProperty getHitBoxSize() {
        return hitBoxSize;
    }
}

