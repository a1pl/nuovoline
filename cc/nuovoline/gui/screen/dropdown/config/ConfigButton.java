package cc.nuovoline.gui.screen.dropdown.config;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.modules.visual.HUD;
import cc.nuovoline.utils.OpenGLUtil;
import cc.nuovoline.utils.fonts.impl.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.function.Consumer;

import static cc.nuovoline.utils.fonts.impl.Fonts.SF.SF_18.SF_18;

public class ConfigButton extends Config {

    private final Consumer<String> actionPerformed;
    private float fraction = 0;

    public ConfigButton(String name, ConfigTab parent, Consumer<String> actionPerformed) {
        super(name, parent);

        this.actionPerformed = actionPerformed;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        HUD hud = Nuovoline.getInstance().getModuleManager().getModule(HUD.class);
        y = (int) (getParent().getPosY() + 15);

        for (Config config : getParent().getConfigs()) {
            if (config == this) {
                break;
            } else {
                y += config.getYPerConfig();
            }
        }

        if(isHovered(mouseX,mouseY) && fraction < 1){
            fraction+=0.0025 * (2000 / Minecraft.getInstance().getDebugFPS());
        }else if(fraction > 0){
            fraction-=0.0025 * (2000 / Minecraft.getInstance().getDebugFPS());
        }
        fraction = MathHelper.clamp_float(fraction,0,1);

        Gui.drawRect(getParent().getPosX(), y,getParent().getPosX() + 100, y + getYPerConfig(), new Color(40, 40, 40, 255).getRGB());
        Fonts.SF.SF_19.SF_19.drawString(getName(), getParent().getPosX() + 2, y + 4, OpenGLUtil.interpolateColor(new Color(255,255,255,255),new Color(hud.getColor().getRed(), hud.getColor().getGreen(), hud.getColor().getBlue(), 250),fraction));
    }



    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHovered(mouseX, mouseY) && mouseButton == 0) {
            if(getParent().getSelectedConfig() == null) {
                actionPerformed.accept("");
            } else {
                actionPerformed.accept(getParent().getSelectedConfig().getName());
            }
        }
    }

    public Consumer<String> getActionPerformed() {
        return actionPerformed;
    }
}
