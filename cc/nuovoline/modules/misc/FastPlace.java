package cc.nuovoline.modules.misc;

import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.BooleanProperty;
import cc.nuovoline.modules.configurations.property.object.IntProperty;
import cc.nuovoline.modules.configurations.property.object.PropertyFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import static cc.nuovoline.modules.configurations.property.object.PropertyFactory.createInt;

public final class FastPlace extends AbstractModule {

    /* properties @off */
    @Property("place-delay")
    public final IntProperty placeDelay = createInt(3).minimum(1).maximum(4);
    @Property("blocks-only")
    public final BooleanProperty blocksOnly = PropertyFactory.booleanTrue();

    /* constructors @on */
    public FastPlace(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "FastPlace", "Fast Place", EnumModuleType.MISC, "place blocks faster");
        Manager.put(new Setting("PLACE_DELAY", "Place Delay", SettingType.SLIDER, this, this.placeDelay, 1));
        Manager.put(new Setting("BLOCKS_ONLY", "Blocks Only", SettingType.CHECKBOX, this, this.blocksOnly));
    }
}
