package cc.nuovoline.modules.visual;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.Render2DEvent;
import cc.nuovoline.events.events.TickUpdateEvent;
import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import static cc.nuovoline.gui.screen.setting.SettingType.*;
import static cc.nuovoline.modules.EnumModuleType.VISUALS;
import static cc.nuovoline.modules.configurations.property.object.PropertyFactory.*;

public final class Animations extends AbstractModule {

    private float heightSmooth;

    /* properties @off */
    @Property("type")
    private final StringProperty type = createString("Swank").acceptableValues("Swank", "Swing", "Swang", "Swong", "Swaing", "Punch", "Stella", "Styles", "Slide", "Interia", "Ethereal", "1.7", "Sigma", "Exhibition", "Smooth", "Spinning");
    @Property("hit")
    private final StringProperty hit = createString("Vanilla").acceptableValues("Vanilla", "Smooth");
    @Property("slowdown")
    private final IntProperty slowdown = createInt(0).minimum(-4).maximum(10);
    @Property("downscale-factor")
    private final DoubleProperty downscaleFactor = createDouble(0.3d).minimum(0.0).maximum(0.5);
    @Property("hit-height")
    private final FloatProperty height = createFloat(10.0F).minimum(-10.0F).maximum(30.0F);
    @Property("block-height")
    private final FloatProperty block_height = createFloat(10.0F).minimum(0.0F).maximum(60.0F);
    @Property("rotating")
    private final BooleanProperty rotating = createBoolean(false);

    /* constructors @on */
    public Animations(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Animations", VISUALS, "AutoBlock/Hit animations");
        Manager.put(new Setting("Anim_Mode", "Block", COMBOBOX, this, type));
        Manager.put(new Setting("HIT_ANIM_MODE", "Hit", COMBOBOX, this, hit));
        Manager.put(new Setting("SWING_SLOW", "Slowdown", SLIDER, this, slowdown, 1));
        Manager.put(new Setting("SCALE_FACTOR", "Downscale Factor", SLIDER, this, downscaleFactor, 0.1));
        Manager.put(new Setting("HIT_HEIGHT", "Hit Height", SLIDER, this, height, 1.0F));
        Manager.put(new Setting("BLOCK_HEIGHT", "Block Height", SLIDER, this, block_height, 1.0F));
        Manager.put(new Setting("BLOCK_ROTATING", "Rotating", CHECKBOX, this, rotating));
    }

    /* events */
    @EventTarget
    public void shouldSlowSwing(TickUpdateEvent event) {
        setSuffix(type.get());
    }

    @EventTarget
    public void onRender(Render2DEvent event) {
        float heightSet = 20.0F - (mc.player.isSwingInProgress && mc.player.isBlocking() ? block_height.get() : mc.player.getHeldItem() == null ? 0.0F : height.get());

        if (heightSmooth < heightSet) {
            heightSmooth += 1.0F;
        } else if (heightSmooth > heightSet) {
            heightSmooth -= 1.0F;
        }
    }

    //region Lombok
    public StringProperty getAnim() {
        return type;
    }

    public IntProperty getSlowdown() {
        return slowdown;
    }

    public DoubleProperty getDownscaleFactor() {
        return downscaleFactor;
    }

    public StringProperty getHit() {
        return hit;
    }

    public float getHeight() {
        return heightSmooth;
    }

    public boolean getRotating() {
        return rotating.get();
    }
    //endregion

    @Override
    public void onEnable() {
        setSuffix(this.type.get());
    }
}
