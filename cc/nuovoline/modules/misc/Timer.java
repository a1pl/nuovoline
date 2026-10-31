package cc.nuovoline.modules.misc;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.SettingEvent;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.FloatProperty;
import cc.nuovoline.modules.configurations.property.object.PropertyFactory;
import org.lwjgl.input.Keyboard;


public final class Timer extends AbstractModule {
    private long lastMS = System.currentTimeMillis();

    // A property for controlling the timer speed
    @Property("timer-speed")
    private final FloatProperty timerSpeed = PropertyFactory.createFloat(1.0F).minimum(0.1F).maximum(10.0F);

    // Constructor
    public Timer(ModuleManager moduleManager) {
        super(moduleManager, "Timer", "Timer", Keyboard.KEY_T, EnumModuleType.MISC, "Adjusts the Minecraft timer speed");
        Manager.put(new Setting("TIMER_SPEED", "Timer Speed", SettingType.SLIDER, this, timerSpeed, 0.1F));
    }

    // Called when the module is enabled
    @Override
    public void onEnable() {
        mc.timer.timerSpeed = timerSpeed.get(); // Set the timer speed based on the property
        super.onEnable();
    }

    // Called when the module is disabled
    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F; // Reset the timer when the module is turned off
        super.onDisable();
    }

    // Event listener: Handle slider value changes
    @EventTarget
    public void onSetting(SettingEvent event) {
        if (event.getSettingName().equals("TIMER_SPEED")) {
            mc.timer.timerSpeed = timerSpeed.get(); // Dynamically update the timer speed when the slider changes
        }
    }

    public boolean hasTimePassed(long ms) {
        return System.currentTimeMillis() - lastMS >= ms;
    }

    public boolean hasJoinTimePassed() {
        return hasTimePassed(3000L);
    }


    public void reset() {
        lastMS = System.currentTimeMillis();
    }
}
