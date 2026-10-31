package cc.nuovoline.modules.visual;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.PlayerUpdateEvent;
import cc.nuovoline.events.events.SettingEvent;
import cc.nuovoline.events.events.TickUpdateEvent;
import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.PropertyFactory;
import cc.nuovoline.modules.configurations.property.object.StringProperty;
import lombok.Getter;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.checkerframework.checker.nullness.qual.NonNull;

import static cc.nuovoline.modules.EnumModuleType.VISUALS;

public final class Brightness extends AbstractModule {

    private float oldGamma;
    
    @Getter
    @Property("mode")
    private final StringProperty mode = PropertyFactory.createString("Gamma").acceptableValues("Gamma", "Effect");

    public Brightness(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Brightness", VISUALS, "It's too dark in here");
        Manager.put(new Setting("BN_MODE", "Mode", SettingType.COMBOBOX, this, mode));
    }

    @Override
    public void onEnable() {
        updateSuffix();
        if ("Gamma".equals(mode.get())) {
            enableGamma();
        }
    }

    @Override
    public void onDisable() {
        if ("Effect".equals(mode.get())) {
            disableEffect();
        } else {
            disableGamma();
        }
    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {
        updateSuffix();
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (mc.player == null) return;

        if ("Effect".equals(mode.get())) {
            mc.player.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 5201, 1));
        } else if (mc.currentScreen instanceof GuiVideoSettings && mc.gameSettings.gammaSetting != 10_000.0F) {
            mc.gameSettings.gammaSetting = 10_000.0F;
        }
    }

    @EventTarget
    public void onSetting(SettingEvent event) {
        if (event.getSettingName().equals("BN_MODE")) {
            if ("Gamma".equals(mode.get())) {
                enableGamma();
            } else {
                disableGamma();
            }
        }
    }

    private void updateSuffix() {
        if (mode.get() != null) {
            setSuffix(mode.get());
        }
    }

    private void enableGamma() {
        if (mc.player != null && mc.player.isPotionActive(16)) {
            mc.player.removePotionEffect(16);
        }
        if (mc.gameSettings != null) {
            oldGamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 10_000.0F;
        }
    }

    private void disableGamma() {
        if (mc.gameSettings != null) {
            mc.gameSettings.gammaSetting = oldGamma;
        }
    }

    private void disableEffect() {
        if (mc.player != null) {
            mc.player.removePotionEffect(Potion.nightVision.getId());
        }
    }
}